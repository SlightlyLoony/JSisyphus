package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;

import static java.lang.Math.PI;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class OrderlyTransition extends ATrack {

    // givens...
    private static final double MAGIC_CIRCLE = 0.55191502449;  // per http://spencermortensen.com/articles/bezier-circle/
    private static final double RHO_PER_TURN = 0.02;
    private static final double INITIAL_SPIRAL_RHO = 0.1;
    private static final int TO_SQUARE_TURNS = 20;
    private static final int IN_SQUARE_TURNS = 5;
    private static final int TO_CIRCLE_TURNS = 20;

    public OrderlyTransition() {
        super( "OrderlyTransition" );
    }


    public void trace() throws IOException {

        // first we spiral out a bit...
        int turns = (int) Math.ceil( INITIAL_SPIRAL_RHO / RHO_PER_TURN );
        spiralTo( Point.fromRT( 0.1, 0 ), Point.fromRT( 0, 0 ), 0, 5 );

        // transition from circle to square...
        double theta = 0;
        double deltaTheta = (PI / 4) / (4 * TO_SQUARE_TURNS);
        Vert vert = new Vert( INITIAL_SPIRAL_RHO, RHO_PER_TURN / 4, deltaTheta, PI / 2 );
        theta = turn( theta, vert, TO_SQUARE_TURNS );

        // do some turns of pure squareness...
        deltaTheta = 0;
        vert = new Vert( dc.getCurrentRelativePosition().rho, RHO_PER_TURN / 4, deltaTheta, 3 * PI / 4 );
        theta = turn( theta, vert, IN_SQUARE_TURNS );

        // finally, transition back to circles...
        deltaTheta = -(PI / 4) / (4 * TO_CIRCLE_TURNS);
        vert = new Vert( dc.getCurrentRelativePosition().rho, RHO_PER_TURN / 4, deltaTheta, 3 * PI / 4 );
        theta = turn( theta, vert, TO_CIRCLE_TURNS );

        renderPNG( pngFileName );
        write( trackFileName );
    }


    private double turn( final double _theta, final Vert _vert, final int _turns ) {

        double theta = _theta;
        Vert vert = _vert;

        for( int i  = 0; i < _turns; i++ ) {

            // iterate over our four vertices...
            for( int v = 0; v < 4; v++ ) {

                // draw a curve...
                Vert newVert = vert.next();
                Point end = vectorTo( Point.fromRT( newVert.rho, theta + PI / 2 ) );
                Point cp1 = Point.fromRT( vert.cpRho, vert.cpTheta + theta );
                Point cp2 = Point.fromRT( newVert.cpRho, -newVert.cpTheta + theta + PI / 2 );
                curveTo( cp1, cp2, end );

                // move along...
                vert = newVert;
                theta += PI / 2;
            }
        }
        return _theta;
    }


    private static class Vert {
        private double rho;
        private double deltaRho;
        private double cpRho;
        private double cpTheta;
        private double deltaTheta;


        private Vert( final double _rho, final double _deltaRho, final double _deltaTheta, final double _cpTheta ) {
            rho = _rho;
            deltaRho = _deltaRho;
            cpRho = MAGIC_CIRCLE * rho;
            cpTheta = _cpTheta;
            deltaTheta = _deltaTheta;
        }


        private Vert next() {
            return new Vert( rho + deltaRho, deltaRho, deltaTheta, cpTheta + deltaTheta );
        }
    }
}
