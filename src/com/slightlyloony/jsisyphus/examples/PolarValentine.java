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
        double angle = 0;

        spiralTo( Point.fromRT( 1, 0 ), Point.fromRT( 0,0 ), 0, 1 );
        eraseTo( Point.fromRT( 1, PI ) );

        for( int i = 0; i < STICKS; i++ ) {

            lineToRT( .4, angle );

            double sf = 0.02;
            while( sf < 0.6 ) {

                heart.draw( "bottom", sf, angle );
                double nsf = sf * 1.06;
                lineToRT( (nsf - sf) / 2, angle + PI );
                sf = nsf;
            }

            home();
            arcAroundTableCenter( angle + 2 * PI / STICKS );
            angle += 2 * PI / STICKS;
        }

        renderPNG( pngFileName );
        write( trackFileName );
    }
}
