package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.DrawingContext;
import com.slightlyloony.jsisyphus.positions.Position;

import java.util.Collections;
import java.util.List;

/**
 * The base class for all lines.  Two constructors are provided: one for lines with algorithmically generated points, and the other for lines comprised of
 * arbitrary points.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
abstract class ALine implements Line {

    public final DrawingContext dc;
    private final List<Position> points;


    /**
     * Creates a new instance of this class with arbitrary sequence of points in actual coordinates.
     *
     * @param _points the sequence of points comprising this line.
     */
    protected ALine( final DrawingContext _dc, final List<Position> _points ) {

        // sanity check...
        if( (_dc == null) || (_points == null ) )
            throw new IllegalArgumentException( "Missing parameter(s) for ALine" );

        dc = _dc;
        points = _points;
    }


    @Override
    public Position getStart() {
        return points.get( 0 );
    }


    @Override
    public Position getEnd() {
        return points.size() == 0 ? null : points.get( points.size() - 1);
    }


    public List<Position> getPoints() {
        return Collections.unmodifiableList( points );
    }
}
