package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;

import static java.lang.Math.PI;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class PolarValentine extends ATrack {


    private static final int STICKS = 3;               // the number of sticks with hearts...


    public PolarValentine() {
        super( "PolarValentine" );
    }


    public void trace() throws IOException {

        HeartDef heart = new HeartDef( dc );

        dc.spiralTo( Point.fromRT( 1, 0 ), Point.fromRT( 0,0 ), 0, 1 );
        dc.eraseTo( Point.fromRT( 1, PI ) );

        for( int i = 0; i < STICKS; i++ ) {

            dc.lineToRT( .4, 0 );

            double sf = 0.02;
            while( sf < 0.6 ) {

                heart.draw( "bottom", sf );
                double nsf = sf * 1.06;
                dc.lineToRT( (nsf - sf) / 2, PI );
                sf = nsf;
            }

            dc.home();
            dc.arcAroundTableCenter( 2 * PI / STICKS );
            dc.rotateBy( 2 * PI / STICKS );
        }

        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }
}
