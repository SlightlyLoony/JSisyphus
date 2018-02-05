package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;

import static java.lang.Math.*;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class LineTests extends ATrack {


    public LineTests() {
        super( "LineTests" );
    }


    public void trace() throws IOException {

        // straight up...
        dc.lineToRT( .5, 0 );

        // a nice equilateral triangle...
        dc.rotateBy( toRadians( 150 ) );
        dc.lineToXY( 0, 1 );
        dc.rotateBy( toRadians( 120 ) );
        dc.lineToXY( 0, 1 );
        dc.rotateBy( toRadians( 120 ) );
        dc.lineToXY( 0, 1 );

        // make a pattern to test arcTo's crazy side variations...
        dc.rotateTo( 0 );
        for( int i = 0; i < 8; i++ ) {
            Point one = Point.fromXY( 0.25, 0 );
            Point minusOne = Point.fromXY( -0.25, 0 );
            dc.arcTo( one, PI / 2 );
            dc.arcTo( one, -PI / 2 );
            dc.arcTo( minusOne, PI / 2 );
            dc.arcTo( minusOne, -PI / 2 );
            dc.rotateBy( PI/4 );
        }

        // make a nice spiral...
        dc.spiralToRT( .35, -PI/2, .35, -PI/2, 4 );

        // how about a Bezier curve?
        dc.curveToXY( .3, -.4, .2, -.2, .3, .3 );

        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }
}
