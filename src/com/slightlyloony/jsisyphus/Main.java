package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.lines.Line;
import com.slightlyloony.jsisyphus.lines.StraightLine;
import com.slightlyloony.jsisyphus.positions.PolarPosition;

import java.io.IOException;

// TODO: write README.MD file...
// TODO: write arc class...

public class Main {

    public static void main(String[] args) throws IOException {


        DrawingContext dc = new DrawingContext();
        double dt = 47 * Math.toRadians( 3.6 );
        Line line = new StraightLine( new PolarPosition( 1, 0 ), new PolarPosition( 1, dt ) );
//        for( int i = 0; i < 100; i++ ) {
//            dc.draw( line );
//            dc.stepRotation( dt );
//        }
//        dc.renderPNG( "test1" );
//        dc.write( "test1.thr" );

        dc.clear();
        line = new StraightLine( new PolarPosition( 1, 0 ), new PolarPosition( 1, Math.toRadians( 180 ) ) );
        for( int i = 0; i < 25; i++ ) {
            dc.setTranslation( new PolarPosition( 1 - i/25, Math.toRadians( 90 ) ) );
            dc.draw( line );
        }
        for( int i = 0; i < 25; i++ ) {
            dc.setTranslation( new PolarPosition( 1 - i/25, Math.toRadians( 270 ) ) );
            dc.draw( line );
        }
        dc.renderPNG( "test2" );
        dc.write( "test2.thr" );
    }
}
