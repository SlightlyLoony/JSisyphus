package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.DrawingContext;
import com.slightlyloony.jsisyphus.lines.ArithmeticSpiral;
import com.slightlyloony.jsisyphus.lines.Line;
import com.slightlyloony.jsisyphus.lines.StraightLine;
import com.slightlyloony.jsisyphus.models.Model;
import com.slightlyloony.jsisyphus.positions.PolarPosition;

import java.io.IOException;

// TODO: write Bezier line class...
// TODO: write a concatenated line class...

public class Main {

    public static void main(String[] args) throws IOException {

        DrawingContext dc = new DrawingContext( Model.A16, 0.001, 1000 );
        dc.eraseOut( new PolarPosition( .2, Math.PI/4 ) );
        Line l1 = new StraightLine( dc, new PolarPosition( .2, Math.PI/4 ), new PolarPosition( .6, 1.5 ) );
        Line l2 = new ArithmeticSpiral( dc, new PolarPosition( .6, 1.5 ), new PolarPosition( 0, 15 ) );
        dc.draw( l1 );
        dc.draw( l2 );
        dc.renderPNG( "test.png" );
        dc.write( "test.thr" );


//        new NestedBubbles( "NestedBubbles" ).trace();
    }
}
