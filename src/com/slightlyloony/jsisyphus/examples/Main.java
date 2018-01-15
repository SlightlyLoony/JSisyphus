package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.DrawingContext;
import com.slightlyloony.jsisyphus.SisyphusFitter;
import com.slightlyloony.jsisyphus.lines.Line;
import com.slightlyloony.jsisyphus.lines.StraightLine;
import com.slightlyloony.jsisyphus.models.Model;
import com.slightlyloony.jsisyphus.positions.PolarPosition;

import java.io.IOException;

// TODO: write Bezier line class...
// TODO: write a concatenated line class...

public class Main {

    public static void main(String[] args) throws IOException {

        DrawingContext dc = new DrawingContext( Model.A16, 0.001 );
        Line l1 = new StraightLine( dc, new PolarPosition( .1, .1 ), new PolarPosition( .6, 1.5 ) );
        SisyphusFitter p1 = new SisyphusFitter( l1.getPoints(), dc );

        boolean f1 = p1.isOn( 0, l1.getPoints().size()-1 );

        p1.hashCode();

//        new NestedBubbles( "NestedBubbles" ).trace();
    }
}
