package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.positions.Position;

/**
 * Instances of this class can produce Point instances that represent vectors from the current position to the marked position.  Instances of this class are
 * immutable and threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Marker {

    private final Position marker;
    private final DrawingContext dc;


    public Marker( final Position _marker, final DrawingContext _dc ) {
        marker = _marker;
        dc = _dc;
    }


    /**
     * Returns a {@link Point} instance that represents a vector from the drawing context's current position to the marked point.
     *
     * @return the vector to the marked position.
     */
    public Point vectorTo() {
        Point from = Point.fromPosition( dc.getCurrentPosition() );
        Point to = Point.fromPosition( marker );
        return from.vectorTo( to );
    }
}
