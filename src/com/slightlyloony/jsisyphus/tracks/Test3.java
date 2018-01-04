package com.slightlyloony.jsisyphus.tracks;

import com.slightlyloony.jsisyphus.Pos;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Test3 extends Track {

    public Test3( final String _name ) {
        super( _name );
    }


    public void write() throws IOException {

        Pos pos = new Pos( 0, 0 );
        double size = .75;
        while( pos.getAngle() < 2 * Math.PI ) {
            add( pos );
            pos = pos.add( 3, size );
            add( pos );
            pos = pos.add( .1, 0 );
            add( pos );
            pos = pos.add( -3, -size );
            add( pos );
        }
        super.write();
    }
}
