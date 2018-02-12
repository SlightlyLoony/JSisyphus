package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;

import static java.lang.Math.PI;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class BurstyBezier extends ATrack {


    public BurstyBezier() {
        super( "BurstyBezier" );
    }


    public void trace() throws IOException {

        // givens...
        int points = 9;         // number of points on our burst...
        double startCpRho = 0;  // starting value for the control point rho...
        double maxCpRho = 1.4;  // the maximum, terminating value for the control point rho...
        double offsetTheta = 0.2;

        // erase to the outside...
        eraseTo( Point.fromRT( 1, 0 ) );

        // setup...
        double cpRho = startCpRho;
        double deltaTheta = 2 * PI / points;
        double currentTheta = 0;

        // iterate until we hit our max control point rho...
        while( cpRho < maxCpRho ) {

            // iterate over all our points...
            for( int i = 0; i < points; i++ ) {

                // trace one of our Bezier curves...
                Point end = to( Point.fromRT( 1, currentTheta + deltaTheta ) );
                Point cp1 = Point.fromRT( cpRho, currentTheta + PI - offsetTheta );
                Point cp2 = Point.fromRT( cpRho, currentTheta + deltaTheta + PI + offsetTheta );
                curveTo( cp1, cp2, end );

                currentTheta += deltaTheta;
            }

            // update our rho value...
            cpRho += 0.02;
        }

        renderPNG( pngFileName );
        write( trackFileName );
    }
}
