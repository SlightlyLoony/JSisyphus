package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;

import static java.lang.Math.*;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SineVsBezier extends ATrack {


    public SineVsBezier() {
        super( "SineVsBezier" );
    }


    public void trace() throws IOException {

        // givens...
        double rotationFraction = 0.0025;
        double angularVarianceK = 0.75;
        double minRho = 0.01;
        double cpRho = 0.5;

        // setup...
        int iterations = (int) ceil( 0.5 / rotationFraction );
        Point cp1 = Point.fromRT( cpRho, 0 );
        Point cp2 = Point.fromRT( cpRho, PI );

        // get to our starting point...
        dc.lineToRT( minRho, 0 );

        // draw our lines...
        for( int i = 0; i < iterations; i++ ) {

            // reset our current relative position to the current rho, but new theta...
            dc.setCurrentRelativePosition( Point.fromRT( dc.getCurrentRelativePosition().rho, 0 ) );

            // calculate our theta values for the in and out strokes...
            Calc outStroke = new Calc( i, rotationFraction, angularVarianceK, false );
            Calc inStroke = new Calc( i, rotationFraction, angularVarianceK, true );

            // draw our curve out...
            Point outside = dc.getCurrentRelativePosition().vectorTo( Point.fromRT( 1, outStroke.variance ) );
            dc.curveTo( cp1, cp2, outside );

            // arc over to the start of the in stroke...
            Point center = Point.fromRT( 1, outStroke.variance + PI );
            dc.arcAround( center, inStroke.deltaRotation - outStroke.variance + inStroke.variance );

            // draw our curve in...
            Point inside = dc.getCurrentRelativePosition().vectorTo( Point.fromRT( minRho, inStroke.deltaRotation ) );
            dc.curveTo( cp2, cp1, inside );

            // arc over to the start of the next out stroke...
            center = Point.fromRT( minRho, PI + inStroke.deltaRotation );
            dc.arcAround( center, inStroke.deltaRotation );

            // set our rotation for the next pair of strokes...
            dc.rotateTo( inStroke.rotation );
        }

        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }


    private static class Calc {
        double rotation;
        double deltaRotation;
        double variance;

        private Calc( final int _i, final double _rotationFraction, final double _angularVarianceK, final boolean _inStroke ) {
            rotation = ((_inStroke ? 2 : 1) + 2 * _i) * _rotationFraction * 2 * PI;
            deltaRotation = (_inStroke ? 1 : 0) * _rotationFraction * 2 * PI;
            variance = sin( rotation ) * _angularVarianceK;
        }
    }
}
