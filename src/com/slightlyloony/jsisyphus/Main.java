package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.lines.Line;
import com.slightlyloony.jsisyphus.lines.StraightLine;
import com.slightlyloony.jsisyphus.positions.CartesianPosition;
import com.slightlyloony.jsisyphus.positions.PolarPosition;

import java.io.IOException;

// TODO: write README.MD file...
// TODO: write arc class...
// TODO: write a concatenated line class...

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
        int lines = 20;
        double dx = 2.0 / (lines - 1);
        Transformer t = new Transformer();
        t.setAutoTranslate( true );
        t.setReflectionAxis( Math.toRadians( 90 ) );
        t.setTranslation( new CartesianPosition( 1, 1, 0 ) );
        Line v = new StraightLine( new PolarPosition( 0, 0 ), new PolarPosition( 2, 0 ) );
        Line h = new StraightLine( new PolarPosition( 0, 0 ), new PolarPosition( dx, Math.toRadians( -90 ) ) );
        for( int i = 0; i < lines; i++ ) {
            t.setReflection( (i & 1) == 0 );
            dc.draw( v, t );
            dc.draw( h, t );
        }

        dc.renderPNG( "test2" );
        dc.write( "test2.thr" );
    }
}
