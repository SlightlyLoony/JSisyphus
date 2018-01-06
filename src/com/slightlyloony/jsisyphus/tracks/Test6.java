package com.slightlyloony.jsisyphus.tracks;

import com.slightlyloony.jsisyphus.OrigLine;
import com.slightlyloony.jsisyphus.Pos;

import java.io.IOException;

/**
 * Draws slightly less than several straight lines per turn around the table, so that the vertices make a spiral pattern.  The lines are slightly closer to
 * the center at the ending point.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Test6 extends Track {

    private static final double VERTICES_PER_REVOLUTION = 7;
    private static final double LINES_PER_RADIUS = 70;
    private static final double OFFSET_PER_REVOLUTION = Math.toRadians( 3 );

    private static final double ANGULAR_LINE_LENGTH = (OFFSET_PER_REVOLUTION + Math.toRadians( 360 )) / VERTICES_PER_REVOLUTION;
    private static final int LINES = (int)(VERTICES_PER_REVOLUTION * LINES_PER_RADIUS);

    public Test6( final String _name ) {
        super( _name );
    }


    public void write() throws IOException {

        // our initial position...
        double h = 1 - Math.cos( ANGULAR_LINE_LENGTH / 2 );
        double k = 1/h;
        double sr = k / (k-1);

        // our radial line length...
        double rll = (sr/LINES_PER_RADIUS) / VERTICES_PER_REVOLUTION;

        Pos to = new Pos( 0, sr );
        Pos fm;

        // draw our lines...
        for( int i = 0; i < LINES; i++ ) {

            fm = to;
            to = fm.add( ANGULAR_LINE_LENGTH, -rll );
            new OrigLine( fm, to ).generate( this );
        }

        // produce our file...
        super.write();
    }
}
