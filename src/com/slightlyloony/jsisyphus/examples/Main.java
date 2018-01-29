package com.slightlyloony.jsisyphus.examples;

import java.io.IOException;

// TODO: write Bezier line class...
// TODO: make fitter work with erase spirals...

public class Main {

    public static void main(String[] args) throws IOException {

        //new LineTests( "LineTests" ).trace();

        //new SimpleRadiance( "SimpleRadiance" ).trace();
        new AngularRadiance( "AngularRadiance" ).trace();
        new Petalar( "Petalar" ).trace();
        new NestedBubbles( "NestedBubbles" ).trace();
    }
}
