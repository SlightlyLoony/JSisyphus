package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;
import java.util.Random;

import static java.lang.Math.PI;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class RandomlyCircular2 extends ATrack {

    private final Random random = new Random( System.currentTimeMillis() );


    public RandomlyCircular2() {
        super( "RandomlyCircular2" );
    }


    public void trace() throws IOException {

        // givens...
        double minArcWidth     = 0.5;
        double maxArcWidth     = 2.5;
        double minTransWidth   = 0.5;
        double maxTransWidth   = 1.0;
        int    arcLevels       = 100;
        int    maxDeltaLevel   = 5;
        double minLevel        = 0.1;
        double maxLevel        = 0.9;
        int    iterations      = 1000;
        double controlK        = 0.4;

        // first we erase out to the min level...
        eraseTo( Point.fromRT( minLevel, 0 ) );

        // commence iterating!
        double angle = 0;
        int level = 0;
        for( int i = 0; i < iterations; i++ ) {

            // first we arc for a bit...
            double arcWidth = randomRange( minArcWidth, maxArcWidth );
            arcAround( toCenter(), arcWidth );
            angle += arcWidth;

            // decide on a new level and calculate its rho...
            int newLevel;
            //int bias = (maxDeltaLevel - 2 * maxDeltaLevel * level / arcLevels) /2;
            do {
                newLevel = level + /*bias +*/ (random.nextBoolean() ? 1 : -1) * (1 + random.nextInt( maxDeltaLevel ));
            } while( (newLevel < 0) || (newLevel >= arcLevels) );
            level = newLevel;
            double levelRho = minLevel + level * (maxLevel - minLevel) / (arcLevels - 1);

            // draw a transition to the new level...
            double transitionWidth = randomRange( minTransWidth, maxTransWidth );
            Point end = Point.fromRT( levelRho, angle + transitionWidth );
            Point cp1 = Point.fromRT( getCurrentRelativePosition().rho * controlK, angle + PI/2 );
            Point cp2 = Point.fromRT( end.rho * controlK, angle + transitionWidth - PI/2 );
            curveTo( cp1, cp2, vectorTo( end ) );
            angle += transitionWidth;
        }

        renderPNG( pngFileName );
        write( trackFileName );
    }


    private double randomRange( final double _min, final double _max ) {
        return _min + random.nextDouble() * (_max - _min);
    }
}
