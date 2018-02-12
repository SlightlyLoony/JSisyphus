package com.slightlyloony.jsisyphus.examples;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {

        new LineTests()             .traceIfNeeded();

        new SimpleRadiance()        .traceIfNeeded();
        new AngularRadiance()       .traceIfNeeded();
        new Petalar()               .traceIfNeeded();
        new NestedBubbles(  )       .traceIfNeeded();
        new SwoopyRadiance()        .traceIfNeeded();
        new PolarValentine()        .traceIfNeeded();
        new BunchOfValentines()     .traceIfNeeded();
        new SineVsBezier()          .traceIfNeeded();
        new SineVsBezier2()         .traceIfNeeded();
        new SpiralGyrations()       .traceIfNeeded();
        new RhoOffsetCalibration()  .traceIfNeeded();
        new SpiralBezier()          .traceIfNeeded();
        new BurstyBezier()          .traceIfNeeded();
    }
}
