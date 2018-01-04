package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.tracks.*;

import java.io.IOException;

// TODO: have a position interface, with a Cartesian and a Polar implementation...
// TODO: come up with Cartesian equation of Sisyphus line...
// TODO: write README.MD file...
// TODO: clamp write to [0,1] for range...
// TODO: calculate error equation for line/arc/curve segments...
// TODO: write arc class...
// TODO: make a couple more straight-line patterns...

public class Main {

    public static void main(String[] args) throws IOException {

        // test tracks...
        Track file = new Test1( "t01.thr" );
        file.write();
        file = new Test2( "t02.thr" );
        file.write();
        file = new Test3( "t03.thr" );
        file.write();
        file = new Test4( "t04.thr" );
        file.write();
        file = new Test5( "t05.thr" );
        file.write();
        file = new Test6( "t06.thr" );
        file.write();
        file = new Test7( "t07.thr" );
        file.write();
    }
}
