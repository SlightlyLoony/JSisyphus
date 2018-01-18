package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.DrawingContext;

import java.io.IOException;

// TODO: write Bezier line class...
// TODO: write a concatenated line class...

public class Main {

    public static void main(String[] args) throws IOException {



        DrawingContext dc = new DrawingContext();
        //dc.eraseToRT(  .2, 0 );
        dc.lineToRT( .2, 0);

        dc.lineToRT(   .8, 0 );
        //dc.eraseToRT( -.2, 0 );
        dc.lineToRT( -.2, 0 );

        for( int i = 0; i < 4; i++ ) {
            dc.lineToRT( -.6, 0 );
            dc.spiralToRT( 0, Math.PI / 8 );
            dc.lineToRT( .6, 0 );
            dc.spiralToRT( 0, Math.PI / 8 );
            dc.rotateBy( -Math.PI / 4 );
        }


//        Line l1 = new StraightLine( dc, new PolarPosition( .2, Math.PI/4 ), new PolarPosition( .6, 1.5 ) );
//        Line l2 = new ArithmeticSpiral( dc, new PolarPosition( .6, 1.5 ), new PolarPosition( 0, 15 ) );
//        dc.draw( l1 );
//        dc.draw( l2 );
        dc.renderPNG( "test.png" );
        dc.write( "test.thr" );


//        new NestedBubbles( "NestedBubbles" ).trace();
    }
}
