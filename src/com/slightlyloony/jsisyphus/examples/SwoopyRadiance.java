package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SwoopyRadiance extends ATrack {


    private static final int NUM_SWOOPS = 5;
    private static final double FULL_THETA = 2 * Math.PI / NUM_SWOOPS;
    private static final double HALF_THETA = Math.PI / NUM_SWOOPS;
    private static final double INITIAL_CONTROL_POINT_RHO = 1.35;      // empirically determined; dependent on number of swoops...
    private static final double SWOOP_DECREMENT = 0.97;
    private static final double MIN_SWOOP_DECREMENT = .02;
    private static final double SWOOP_ORIGIN_RHO = 0.1;
    private static final double SWOOP_INNER_RHO = 0.15;
    private static final double END_RHO = 2 * SWOOP_ORIGIN_RHO * Math.sin( HALF_THETA );
    private static final double END_THETA = (Math.PI + FULL_THETA) / 2;
    private static final double INNER_ERASE_RHO = SWOOP_ORIGIN_RHO * 1.25;


    public SwoopyRadiance() {
        super( "SwoopyRadiance" );
    }


    public void trace() throws IOException {

        // erase in to the swoop's origin...
        dc.lineToRT( 1, 0 );
        dc.eraseToRT( -(1 - SWOOP_ORIGIN_RHO), 0 );

        // draw our swoops...
        for( int s = 0; s < NUM_SWOOPS; s++ ) {

            // draw our sub-swoops...
            double swoopLen = INITIAL_CONTROL_POINT_RHO;
            boolean clockwise = true;
            while( clockwise || swoopLen > SWOOP_INNER_RHO ) {

                // draw a swoop...
                if( clockwise )
                    dc.curveToRT( swoopLen, 0, swoopLen, FULL_THETA, END_RHO, END_THETA );
                else
                    dc.curveToRT( swoopLen, FULL_THETA, swoopLen, 0, END_RHO, END_THETA - Math.PI );

                // shorten the swoop a bit...
                swoopLen -= Math.max( MIN_SWOOP_DECREMENT, swoopLen * (1 - SWOOP_DECREMENT) );

                // flip direction...
                clockwise = !clockwise;
            }

            // rotate to the right place...
            dc.rotateBy( FULL_THETA );
        }

        // now erase the innards...
        dc.lineToRT( INNER_ERASE_RHO - SWOOP_ORIGIN_RHO, 0 );
        dc.eraseToRT( -INNER_ERASE_RHO, 0 );

        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }
}
