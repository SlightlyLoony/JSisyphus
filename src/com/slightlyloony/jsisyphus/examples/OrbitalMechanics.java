package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;

import static java.lang.Math.PI;
import static java.lang.Math.ceil;
import static java.lang.Math.toRadians;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class OrbitalMechanics extends ATrack {


    public OrbitalMechanics() {
        super( "OrbitalMechanics" );
    }


    public void trace() throws IOException {

        // givens...
        double apogee = 0.85;
        double perigee = 0.05;
        double width = 0.5;
        double precession = toRadians( 2.5 );
        int orbits = (int) ceil( 2 * PI / precession );

        // first erase to the perigee...
        eraseTo( Point.fromRT( perigee, PI ) );

        // then we orbit away...
        double angle = 0;
        for( int i = 0; i < orbits; i++ ) {

            // one orbit...
            Point end = Point.fromRT( perigee + apogee, angle );
            Point cp1 = Point.fromRT( width, angle - PI/2 );
            Point cp2 = Point.fromRT( width, angle - PI/2 );
            curveTo( cp1, cp2, end );
            curveTo( cp1.oppositeTheta(), cp2.oppositeTheta(), end.oppositeTheta() );


            // cheat for our precession...
            arcAround( getCurrentRelativePosition().invertXY(), precession );
            angle += precession;

        }
        renderPNG( pngFileName );
        write( trackFileName );
    }
}
