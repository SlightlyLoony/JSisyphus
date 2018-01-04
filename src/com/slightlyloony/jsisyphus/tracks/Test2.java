package com.slightlyloony.jsisyphus.tracks;

import com.slightlyloony.jsisyphus.Pos;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Test2 extends Track {

    public Test2( final String _name ) {
        super( _name );
    }


    public void write() throws IOException {

        Pos pos = new Pos( 0, 0 );
        int points = 5;
        int passes = 10;
        double thick = Math.PI * 2.0 / points;
        double curdelta = 0.5;
        for( int i = 0; i < passes; i++ ) {
            for( int p = 0; p < points; p++ ) {
                pos = pos.add( -thick / 2, 0 );  // point the right way...
                add( pos );
                pos = pos.add( (0.5 - curdelta) * thick, .5 );
                add( pos );
                pos = pos.add( curdelta * thick, .5 );
                add( pos );
                pos = pos.add( curdelta * thick, -.5 );
                add( pos );
                pos = pos.add( (1 - curdelta) * thick, -.5 );
            }
            curdelta -= 0.5 / passes;
        }
        super.write();
    }
}
