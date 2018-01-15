package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.positions.Position;

import java.util.List;

/**
 * Implemented by classes that represent a line of any shape that can be traced on the Sisyphus table.  All lines have a beginning and an ending point, and a
 * series of points along the line that are no further apart than a distance specified when the instance is constructed.  We use a series of points
 * rather than an equation of the line in order to simplify both the writing of Line implementations and to allow for completely arbitrary line shapes (that
 * is, those not describable by an equation).
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Line {

    /**
     * Returns an ordered list of points on the line: [start, end].  These points are no further apart than the distance specified when the instance was
     * constructed.
     *
     * @return the list of this line's points.
     */
    List<Position> getPoints();


    /**
     * Returns the start point of this line.
     *
     * @return the start point of this line.
     */
    Position getStart();


    /**
     * Returns the end point of this line.
     *
     * @return the end point of this line.
     */
    Position getEnd();
}
