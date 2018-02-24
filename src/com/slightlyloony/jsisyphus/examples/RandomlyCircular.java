package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;
import java.util.Random;

import static java.lang.Math.PI;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class RandomlyCircular extends ATrack {


    public RandomlyCircular() {
        super( "RandomlyCircular" );
    }


    public void trace() throws IOException {

        // givens...
        Random random = new Random( 1 );
        double arcWidth        = 1.5;
        double transitionWidth = 1.5;
        int    arcLevels       = 100;
        double minLevel        = 0.1;
        double maxLevel        = 0.9;
        int    iterations      = 1000;
        double controlK        = 0.2;

        // first we erase out to the min level...
        eraseTo( Point.fromRT( minLevel, 0 ) );

        // commence iterating!
        double angle = 0;
        for( int i = 0; i < iterations; i++ ) {

            // first we arc for a bit...
            arcAround( toCenter(), arcWidth );
            angle += arcWidth;

            // decide on a new level and calculate its rho...
            int level = random.nextInt( arcLevels );
            double levelRho = minLevel + level * (maxLevel - minLevel) / (arcLevels - 1);

            // draw a transition to the new level...
            Point end = Point.fromRT( levelRho, angle + transitionWidth );
            Point cp1 = Point.fromRT( getCurrentRelativePosition().rho * controlK, angle + PI/2 );
            Point cp2 = Point.fromRT( end.rho * controlK, angle + transitionWidth - PI/2 );
            curveTo( cp1, cp2, vectorTo( end ) );
            angle += transitionWidth;
        }

        renderPNG( pngFileName );
        write( trackFileName );
    }
}
