package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class RhoOffsetCalibration extends ATrack {


    public RhoOffsetCalibration() {
        super( "RhoOffsetCalibration" );
    }


    public void trace() throws IOException {

        // first we orbit the nominal center for 10 orbits, to remove as much sand as possible from immediately around the center...
        dc.orbit( 10 );

        // next we spiral very slowly out to .1 rho, to move more sand away from the center; this makes measurement easier...
        Point end = Point.fromRT( 0.1, 0 );
        Point center = Point.fromRT( 0, 0 );
        dc.spiralTo( end, center, 0, 40 );

        // now we orbit here for 25 orbits, so our human can make some measurements...
        dc.orbit( 25 );

        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }
}
