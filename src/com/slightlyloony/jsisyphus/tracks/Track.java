package com.slightlyloony.jsisyphus.tracks;

import com.slightlyloony.jsisyphus.Pos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class represent a track that can be "played" on a Sisyphus table.  The track is simply a text list of vertice coordinates, one vertice
 * per line.  Each line has the angle (in radians) and the range (normalized to 1), separated by a space.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Track {

    private final String name;
    private final List<Pos> elements = new ArrayList<>();


    public Track( final String _name ) {
        name = _name;
    }


    public void add( final Pos _element ) {

        if( _element == null )
            return;

        // if we're adding the same element as the most recently added one, skip it...
        if( (elements.size() > 0) && (_element.equals( elements.get( elements.size()-1 ) )))
            return;

        elements.add( _element );
    }


    public void write() throws IOException {

        StringBuilder out = new StringBuilder();
        for( Pos el : elements ) {
            out.append( el.toVertice() );
            out.append( "\n" );
        }
        Path path = new File( name ).toPath();
        byte[] bytes = out.toString().getBytes();
        Files.write( path, bytes );
    }
}
