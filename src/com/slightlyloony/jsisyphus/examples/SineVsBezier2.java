package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;

import static java.lang.Math.*;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SineVsBezier2 extends ATrack {


    public SineVsBezier2() {
        super( "SineVsBezier2" );
    }


    public void trace() throws IOException {

        // givens...
        double rotationFraction = 0.005;
        double angularVarianceK = 1;
        double rhoVarianceK = 0.4;
        double rhoVarianceBase = 0.2;
        double minRho = 0.01;

        // setup...
        int iterations = (int) ceil( 0.5 / rotationFraction );
        double dRot = rotationFraction * 2 * PI;

        // get to our starting point...
        dc.lineToRT( minRho, 0 );

        // draw our lines...
        for( int i = 0; i < iterations; i++ ) {

            // setup...
            double rot = dc.getCurrentRotation();

            // reset our current relative position to the current rho, but new theta...
            dc.setCurrentRelativePosition( Point.fromRT( dc.getCurrentRelativePosition().rho, 0 ) );

            // calculate our CP values for the out stroke...
            Point cp1 = Point.fromRT( rhoVarianceBase + rhoVarianceK * abs( sin( rot ) ), sin( rot ) * angularVarianceK );
            Point cp2 = Point.fromRT( cp1.rho, cp1.theta + PI );

            // draw our curve out...
            Point outside = dc.getCurrentRelativePosition().vectorTo( Point.fromRT( 1, 0 ) );
            dc.curveTo( cp1, cp2, outside );

            // arc over to the start of the in stroke...
            Point center = Point.fromRT( 1, PI );
            dc.arcAround( center, dRot );

            // calculate our CP values for the in stroke...
            rot += dRot;
            cp1 = Point.fromRT( rhoVarianceBase + rhoVarianceK * abs( sin( rot ) ), sin( rot ) * angularVarianceK );
            cp2 = Point.fromRT( cp1.rho, cp1.theta + PI );

            // draw our curve in...
            Point inside = dc.getCurrentRelativePosition().vectorTo( Point.fromRT( minRho, dRot ) );
            dc.curveTo( cp2, cp1, inside );

            // arc over to the start of the next out stroke...
            center = Point.fromRT( minRho, PI + dRot );
            dc.arcAround( center, dRot );

            // set our rotation for the next pair of strokes...
            dc.rotateBy( dRot * 2 );
        }

        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }
}
