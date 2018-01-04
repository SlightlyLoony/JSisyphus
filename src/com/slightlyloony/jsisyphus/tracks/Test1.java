package com.slightlyloony.jsisyphus.tracks;

import com.slightlyloony.jsisyphus.Pos;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Test1 extends Track {

    public Test1( final String _name ) {
        super( _name );
    }


    public void write() throws IOException {

        Pos pos = new Pos( 0, 1 );
        for( int i = 0; i < 500; i++ ) {
            add( pos );
            pos = pos.add( 0.7051, -0.0019 );
        }
        super.write();
    }
}
