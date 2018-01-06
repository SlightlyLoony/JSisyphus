package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.positions.Position;

import java.util.List;

/**
 * Implemented by classes that represent a line of any shape on the Sisyphus table.  All lines have a beginning and an ending point, and a series of points
 * along the line that are no further apart than {@link com.slightlyloony.jsisyphus.Common#MIN_POINT_SPACING MIN_POINT_SPACING}.  We use a series of points
 * rather than an equation of the line in order to simplify both the writing of Line implementations and to allow for completely arbitrary line shapes (that
 * is, those not describable by an equation).
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Line {

    /**
     * Returns an ordered list of points on the line: [start, end].  These points are no further apart than
     * {@link com.slightlyloony.jsisyphus.Common#MIN_POINT_SPACING MIN_POINT_SPACING}.
     *
     * @return the list of this line's points.
     */
    List<Position> getPoints();


    /**
     * Returns the distance between the given point and the closest approach of this line, in normalized Sisyphus distance units.
     *
     * @param _point the point being tested.
     * @return the distance of closest approach in normalized Sisyphus distance units.
     */
    double getDistance( final Position _point );


    Position getStart();
    Position getEnd();
}
