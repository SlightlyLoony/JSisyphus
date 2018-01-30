package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SimpleRadiance extends ATrack {


    public SimpleRadiance() {
        super( "SimpleRadiance" );
    }


    public void trace() throws IOException {

        // inner and outer clear spaces...
        dc.eraseToRT(  .2, 0 );
        dc.lineToRT(   .8, 0 );
        dc.eraseToRT(  .2, Math.PI );

        // radial lines...
        int numLines = 199;
        int skip = 97;     // relatively prime to number of lines...
        double skipRotation = skip * Math.PI * 2 / numLines;
        boolean inStroke = true;

        for( int i = 0; i < numLines; i++ ) {
            dc.lineToRT( inStroke ? -0.6 : 0.6, 0 );
            dc.arcAroundRT( inStroke ? -0.2 : -0.8, 0, skipRotation );
            dc.rotateBy( skipRotation );
            inStroke = !inStroke;
        }
        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }
}
