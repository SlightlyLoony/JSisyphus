package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.positions.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The base class for all lines.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
abstract class ALine implements Line {

    public final Position start;
    public final Position end;

    protected final List<Position> points;


    protected ALine( final Position _start, final Position _end ) {
        start = _start;
        end = _end;
        points = new ArrayList<>();
    }


    protected ALine( final List<Position> _points ) {
        start = _points.get( 0 );
        end = _points.get( _points.size() - 1);
        points = _points;
    }


    /**
     * Returns the distance between the given point and the closest approach of this line, in normalized Sisyphus distance units.
     *
     * @param _point the point being tested.
     * @return the distance of closest approach in normalized Sisyphus distance units.
     */
    @Override
    public double getDistance( final Position _point ) {

        double result = Double.POSITIVE_INFINITY;
        for( Position p: points ) {
            double d = Math.hypot( _point.getX() - p.getX(), _point.getY() - p.getY() );
            if( d < result ) result = d;
        }
        return result;
    }


    @Override
    public Position getStart() {
        return start;
    }


    @Override
    public Position getEnd() {
        return end;
    }


    public List<Position> getPoints() {
        return Collections.unmodifiableList( points );
    }
}
