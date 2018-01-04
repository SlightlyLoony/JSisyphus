package com.slightlyloony.jsisyphus.tracks;

import com.slightlyloony.jsisyphus.Line;
import com.slightlyloony.jsisyphus.Pos;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Test5 extends Track {

    public Test5( final String _name ) {
        super( _name );
    }


    public void write() throws IOException {

        // our initial position...
        Pos to = new Pos( 0, 0 );
        Pos fm;

        // draw 144 lines, 2.5 degrees apart...
        for( int i = 0; i < 144; i++ ) {

            // starting at the center draw four lines to 0.25, 0.5, 0.75, and 1.0 radii, and back...
            fm = to;
            Pos to1 = fm.add( 0, .25 );
            Pos to2 = to1.add( Math.toRadians( 10 ), .25 );
            Pos to3 = to2.add( Math.toRadians( -20 ), .25 );
            Pos to4 = to3.add( Math.toRadians( 10 ), .25 );
            new Line(  fm, to1 ).generate( this );
            new Line( to1, to2 ).generate( this );
            new Line( to2, to3 ).generate( this );
            new Line( to3, to4 ).generate( this );
            new Line( to4, to3 ).generate( this );
            new Line( to3, to2 ).generate( this );
            new Line( to2, to1 ).generate( this );
            new Line( to1, fm  ).generate( this );

            // advance 2.5 degrees...
            to = fm.add( Math.toRadians( 2.5 ), 0 );
        }

        // produce our file...
        super.write();
    }
}
