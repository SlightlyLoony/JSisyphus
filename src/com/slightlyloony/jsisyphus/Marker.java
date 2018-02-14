package com.slightlyloony.jsisyphus;

/**
 * Instances of this class can produce Point instances that represent vectors from the current position to the marked position.  Instances of this class are
 * immutable and threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Marker {

    private final Point marker;
    private final DrawingContext dc;


    public Marker( final Point _marker, final DrawingContext _dc ) {
        marker = _marker;
        dc = _dc;
    }


    /**
     * Returns a {@link Point} instance that represents a vector from the drawing context's current position to the marked point.
     *
     * @return the vector to the marked position.
     */
    public Point vectorTo() {
        Point from = dc.getCurrentRelativePosition();
        Point to = marker;
        return from.vectorTo( to );
    }
}
