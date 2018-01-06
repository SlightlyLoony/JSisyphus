package com.slightlyloony.jsisyphus.tracks;

import com.slightlyloony.jsisyphus.OrigLine;
import com.slightlyloony.jsisyphus.Pos;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Test4 extends Track {

    public Test4( final String _name ) {
        super( _name );
    }


    public void write() throws IOException {

        // our initial position...
        Pos to = new Pos( 0, 1 );

        // draw diagonal rings until we're less that 0.3 range on the inside...
        while( to.getRange() >= 0.3 ) {

            // draw a series of squares at 5 degree intervals...
            Pos fm;
            for( int s = 0; s < 18; s++ ) {

                // draw a square from the current position...
                for( int i = 0; i < 4; i++ ) {
                    fm = to;
                    to = fm.add( Math.toRadians( 90 ), 0 );
                    OrigLine line = new OrigLine( fm, to );
                    line.generate( this );
                }

                // advance 5 degrees if this wasn't the last square...
                if( s < 17 ) {
                    to = to.add( Math.toRadians( 5 ), 0 );
                    add( to );
                }
            }

            // advance to center of the current line...
            fm = to;
            double nr = Math.sqrt( Math.pow( fm.getRange(), 2) / 2 );
            to = fm.add( Math.toRadians( 45 ), nr - fm.getRange() );
            OrigLine line = new OrigLine( fm, to );
            line.generate( this );
        }

        // now draw radii at 5 degree intervals at the current range...

        // start by moving to the center...
        double dr = to.getRange();
        to = new Pos( to.getAngle(), 0 );
        add( to );

        // now draw 36 strokes out, then back in...
        for( int i = 0; i < 36; i++ ) {

            // first a line to the radius...
            to = new Pos( to.getAngle(), dr );
            add( to );

            // rotate five degrees...
            to = to.add( Math.toRadians( 5 ), 0 );
            add( to );

            // then a line back back...
            to = new Pos( to.getAngle(), 0 );
            add( to );

            // rotate five degrees...
            to = to.add( Math.toRadians( 5 ), 0 );
            add( to );
        }

        // produce our file...
        super.write();
    }
}
