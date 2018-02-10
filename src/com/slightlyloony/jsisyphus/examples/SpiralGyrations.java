package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;

import static java.lang.Math.*;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SpiralGyrations extends ATrack {


    public SpiralGyrations() {
        super( "SpiralGyrations" );
    }


    public void trace() throws IOException {

        // givens...
        int iterations = 110;
        int turns = 10;
        double interval = 0.02;
        double deltaTheta = toRadians( 10 );

        // calculated...
        double radius = turns * interval;
        double m = 1.8 * radius / (2 * PI);
        double theta = 0;

        // do this a few times...
        for( int i = 0; i < iterations; i++ ) {

            // draw a spiral, clockwise...
            Point center = Point.fromXY( 0, 0 );
            Point end = Point.fromRT( radius, theta + PI/2 );
            dc.spiralTo( end, center, theta, turns );

            // get the next point our spiral...
            theta += ((i == 0) ? .5 * PI : deltaTheta);
            Point next = Point.fromRT( m * theta, theta );

            // draw a straight line there...
            dc.lineTo( dc.getCurrentRelativePosition().vectorTo( next ) );
        }

        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }
}
