package com.slightlyloony.jsisyphus;

import java.io.File;
import java.io.IOException;

/**
 * Base class for all tracks.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class ATrack {

    protected final String trackFileName;
    protected final String pngFileName;
    protected final DrawingContext dc;


    protected ATrack( final String baseFileName ) {
        trackFileName = baseFileName + ".thr";
        pngFileName = baseFileName + ".png";
        dc = new DrawingContext();
    }


    public void traceIfNeeded() throws IOException {
        if( alreadyTraced() )
            return;
        trace();
    }


    protected boolean alreadyTraced() {
        return new File( trackFileName ).exists() && new File( pngFileName ).exists();
    }


    protected abstract void trace() throws IOException;
}
