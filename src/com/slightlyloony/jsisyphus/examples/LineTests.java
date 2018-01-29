package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class LineTests extends ATrack {


    public LineTests( final String baseFileName ) {
        super( baseFileName );
    }


    public void trace() throws IOException {

        // straight up...
        dc.lineToRT( .5, 0 );

        // a nice equilateral triangle...
        dc.rotateBy( Math.toRadians( 150 ) );
        dc.lineToXY( 0, 1 );
        dc.rotateBy( Math.toRadians( 120 ) );
        dc.lineToXY( 0, 1 );
        dc.rotateBy( Math.toRadians( 120 ) );
        dc.lineToXY( 0, 1 );

        // make a lens...
        dc.rotateTo( 0 );
        dc.arcToRT( .5,  Math.PI / 2, Math.PI / 2 );
        dc.arcToRT( .5, -Math.PI / 2, Math.PI / 2 );
        dc.arcToRT( .5,  Math.PI / 2, Math.PI / 3 );
        dc.arcToRT( .5, -Math.PI / 2, Math.PI / 3 );
        dc.arcToRT( .5,  Math.PI / 2, 4 * Math.PI / 3 );
        dc.arcToRT( .5, -Math.PI / 2, 4 * Math.PI / 3 );

        // make a nice spiral...
        dc.spiralToRT( .35, -Math.PI/2, .35, -Math.PI/2, 4 );

        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }
}
