package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.DrawingContext;
import com.slightlyloony.jsisyphus.positions.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a straight line on the Sisyphus table.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class StraightLine extends ALine implements Line {


    public StraightLine( final DrawingContext _dc, final Position _end ) {
        super( _dc, getPoints( _dc, _end ) );
    }

    /* package */ static List<Position> getPoints( final DrawingContext _dc, final Position _end ) {

        // some setup...
        Position start = _dc.getTransformer().untransform( _dc.getCurrentPosition() );
        double dx = _end.deltaX( start );
        double dy = _end.deltaY( start );
        double lineLength = Math.hypot( dx, dy );
        int numPoints = (int) Math.ceil( lineLength / _dc.getMaxPointDistance() );
        List<Position> points = new ArrayList<>( numPoints );

        // now the actual point generation...
        Position lastPoint = start;
        for( int p = 0; p < numPoints; p++ ) {

            // we calculate the fractional deltas and then a difference from the last point so as to avoid cumulative error from the simpler
            // approach of calculating each point as a delta from the last...
            double frac = 1.0 * p / (numPoints - 1.0);
            double nx = (start.getX() + frac * dx) - lastPoint.getX();
            double ny = (start.getY() + frac * dy) - lastPoint.getY();
            lastPoint = lastPoint.fromDeltaXY( nx, ny );
            points.add( _dc.getTransformer().transform( lastPoint ) );
        }

        return points;
    }
}
