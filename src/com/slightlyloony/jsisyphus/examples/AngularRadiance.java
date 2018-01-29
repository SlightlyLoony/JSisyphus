package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class AngularRadiance extends ATrack {


    public AngularRadiance( final String baseFileName ) {
        super( baseFileName );
    }


    public void trace() throws IOException {

        if( alreadyTraced() ) return;

        // inner and outer clear spaces...
        dc.eraseToRT(  .2, 0 );
        dc.lineToRT(   .8, 0 );
        dc.eraseToRT( -.2, 0 );

        // radial lines...
        int numLines = 199;
        int skip = 97;     // relatively prime to number of lines...
        double skipRotation = skip * Math.PI * 2 / numLines;
        boolean inStroke = true;

        for( int i = 0; i < numLines; i++ ) {
            //dc.lineToRT( inStroke ? -.6 : .6, Math.PI / (inStroke ? -2 : 2) );
            dc.lineToXY( inStroke ? -.2 : .2, inStroke ? -.8 : .8 );
//            dc.spiralToRT( 0, skipRotation );
            dc.rotateBy( skipRotation );
            inStroke = !inStroke;
        }
        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }
}
