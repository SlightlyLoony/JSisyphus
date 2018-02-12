package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;

import static java.lang.Math.PI;
import static java.lang.Math.asin;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SpiralBezier extends ATrack {


    public SpiralBezier() {
        super( "SpiralBezier" );
    }


    public void trace() throws IOException {

        // givens...
        double rhoPerSpiralTurn = 0.25;  // radial interval between spiral turns...
        double intervalConstant = 0.02; // constant controlling the interval between curves...

        // setup...
        Spiral spiral = new Spiral( rhoPerSpiralTurn );
        double spiralTheta = 0;  // the implied spiral's current theta...
        boolean outStroke = true;  // we start with an out stroke...
        double deltaTheta = 0;
        double outerTheta;

        // first we iterate while the spiral theta is under 360 degrees (2pi) because we've only got one edge to curve to...
        while( spiralTheta < 2 * PI ) {

            // setup...
            deltaTheta = spiral.deltaTheta( spiralTheta, intervalConstant );
            outerTheta = spiralTheta + deltaTheta;
            Point end, cp1, cp2;

            // if we're stroking outward...
            if( outStroke ) {

                // calculate stuff for the out stroke and draw it...
                end = to( Point.fromRT( spiral.rhoAt( outerTheta ), outerTheta ) );
                cp1 = Point.fromRT( 0, spiral.slopeAt( spiralTheta ) );
                cp2 = Point.fromRT( 0, outerTheta + PI );
                curveTo( cp1, cp2, end );

                // move to the beginning of the in stroke...
                outerTheta += deltaTheta;
                end = to( Point.fromRT( spiral.rhoAt( outerTheta ), outerTheta ) );
                lineTo( end );
            }

            // if we're stroking inward...
            else {

                // calculate stuff for the in stroke and draw it...
                end = to( Point.fromRT( 0, 0 ) );
                cp1 = Point.fromRT( 0, outerTheta + PI );
                cp2 = Point.fromRT( 0, spiral.slopeAt( spiralTheta ) );
                curveTo( cp1, cp2, end );
            }

            // get ready for the next stroke...
            spiralTheta += deltaTheta;
            outStroke = !outStroke;
        }

        // reset our spiral theta, now that we've made the first turn...
        spiralTheta = spiralTheta - 2 * PI;

        // if the next stroke is outward, move to the first point we're drawing a curve from...
        if( outStroke )
            lineTo( to( Point.fromRT( spiral.rhoAt( spiralTheta ), spiralTheta ) ) );

        // then we iterate until we've spiralled past the edge of the table...
        while( spiral.rhoAt( spiralTheta ) < 1 ) {

            // setup...
            deltaTheta = spiral.deltaTheta( spiralTheta + 2 * PI, intervalConstant );
            outerTheta = spiralTheta + 2 * PI + deltaTheta;
            Point end, cp1, cp2;

            // if we're stroking outward...
            if( outStroke ) {

                // calculate stuff for the out stroke and draw it...
                end = to( Point.fromRT( spiral.rhoAt( outerTheta ), outerTheta ) );
                cp1 = Point.fromRT( .3, spiral.slopeAt( spiralTheta ) );
                cp2 = Point.fromRT( 0, outerTheta + PI );
                curveTo( cp1, cp2, end );

                // move to the beginning of the in stroke...
                outerTheta += deltaTheta;
                end = to( Point.fromRT( spiral.rhoAt( outerTheta ), outerTheta ) );
                lineTo( end );
            }

            // if we're stroking inward...
            else {

                // calculate stuff for the in stroke and draw it...
                end = to( Point.fromRT( spiral.rhoAt( spiralTheta ), spiralTheta ) );
                cp1 = Point.fromRT( 0, outerTheta + PI );
                cp2 = Point.fromRT( .3, spiral.slopeAt( spiralTheta ) );
                curveTo( cp1, cp2, end );

                // move to the beginning of the next out stroke...
                end = to( Point.fromRT( spiral.rhoAt( spiralTheta + deltaTheta ), spiralTheta + deltaTheta ) );
                lineTo( end );
            }

            spiralTheta += deltaTheta;
            outStroke = !outStroke;
        }

        renderPNG( pngFileName );
        write( trackFileName );
    }


    private static class Spiral {

        private final double m;


        private Spiral( final double _dRhoPerTurn ) {
            m = _dRhoPerTurn / (2 * PI);
        }


        private double rhoAt( final double _theta ) {
            return m * _theta;
        }


        /**
         * Returns the delta theta to the next curve from the last curve's theta.
         *
         * @param _lastTheta
         * @return
         */
        private double deltaTheta( final double _lastTheta, final double _intervalConstant ) {
            if( _lastTheta == 0 ) return _intervalConstant;
            double r = rhoAt( _lastTheta );
            if( _intervalConstant >= r )
                return PI/8;
            return 2 * asin( _intervalConstant / (2 * r) );
        }


        private double slopeAt( final double _theta ) {

            Point here = Point.fromRT( m * _theta, _theta );
            double dTheta = _theta + 0.0001;
            Point there = Point.fromRT( m * dTheta, dTheta );
            return here.thetaTo( there );
        }
    }
}
