package com.slightlyloony.jsisyphus.examples;

import java.io.IOException;

// TODO: write Bezier line class...
// TODO: make fitter work with erase spirals...

public class Main {

    public static void main(String[] args) throws IOException {

        new LineTests().traceIfNeeded();

        new SimpleRadiance().traceIfNeeded();
        new AngularRadiance().traceIfNeeded();
        new Petalar().traceIfNeeded();
        new NestedBubbles(  ).trace();
    }
}
