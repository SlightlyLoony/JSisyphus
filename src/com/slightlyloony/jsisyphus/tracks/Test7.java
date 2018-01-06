package com.slightlyloony.jsisyphus.tracks;

import com.slightlyloony.jsisyphus.OrigLine;
import com.slightlyloony.jsisyphus.Pos;

import java.io.IOException;

/**
 * Draws a bunch of chords of the same length.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Test7 extends Track {

    private static final double VERTICES = 127;  // prime number makes this easier...
    private static final double ANGLE_BETWEEN_VERTICES = Math.toRadians( 360 ) / VERTICES;
    private static final double CHORD_VERTICE_OFFSET = 59;  // relatively prime to VERTICES to ensure coverage...
    private static final double CHORD_ANGLE = ANGLE_BETWEEN_VERTICES * CHORD_VERTICE_OFFSET;


    public Test7( final String _name ) {
        super( _name );
    }


    public void write() throws IOException {

        Pos to, fm;

        // erase...
        to = new Pos( 60 * Math.toRadians( 360 ), 1 );
        add( new Pos( 0, 0) );
        add( to );

        // starting position...
        fm = to;

        // draw our lines...
        for( int i = 0; i < VERTICES; i++ ) {

            fm = to;
            to = fm.add( CHORD_ANGLE, 0 );
            new OrigLine( fm, to ).generate( this );
        }

        // produce our file...
        super.write();
    }
}
