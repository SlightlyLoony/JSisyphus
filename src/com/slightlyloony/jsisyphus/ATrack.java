package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.models.Model;

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
        dc = new DrawingContext( Model.A16, 0.001);
    }


    protected abstract void trace() throws IOException;
}
