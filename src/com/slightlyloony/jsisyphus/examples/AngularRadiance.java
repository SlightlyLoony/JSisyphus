package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class AngularRadiance extends ATrack {


    public AngularRadiance() {
        super( "AngularRadiance" );
    }


    public void trace() throws IOException {

        // inner and outer clear spaces...
        dc.eraseToRT(  .2, 0 );
        dc.lineToRT(   .8, 0 );
        dc.eraseToRT( -.2, 0 );

        // radial lines...
        int numLines = 199;
        int skip = 97;     // relatively prime to number of lines...
        double skipRotation = skip * Math.PI * 2 / numLines;
        boolean inStroke = true;

        // calculate our tangent line parameters...
        double ts = Math.acos( .2 / .8 );
        double tt = (Math.PI / 2) - ts;
        double l = .8 * Math.sin( ts );
        double dtd = Math.PI + tt;
        double utd = tt;
        double dtc = (Math.PI / 2) + tt;
        double utc = 0;

        for( int i = 0; i < numLines; i++ ) {
            dc.lineToRT( l, inStroke ? dtd : utd );
            dc.arcAroundRT( inStroke ? 0.2 : -0.8, inStroke ? dtc : utc, skipRotation );
            dc.rotateBy( skipRotation );
            inStroke = !inStroke;
        }
        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }
}
