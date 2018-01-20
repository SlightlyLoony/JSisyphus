package com.slightlyloony.jsisyphus.examples;

import java.io.IOException;

// TODO: write Bezier line class...
// TODO: write a concatenated line class...

public class Main {

    public static void main(String[] args) throws IOException {

        new SimpleRadiance( "SimpleRadiance" ).trace();
        new AngularRadiance( "AngularRadiance" ).trace();
        new Petalar( "Petalar" ).trace();
    }
}
