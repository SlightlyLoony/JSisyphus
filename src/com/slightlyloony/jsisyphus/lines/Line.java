package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.Delta;

import java.util.List;

/**
 * Implemented by classes that represent a line of any shape.  A line consists of a sequence of deltas in Cartesian coordinates, each of which represents
 * a point along the line.  The Cartesian space used is assumed to have the dimensions [-1..1], [-1..1], though nothing prohibits lines from generating
 * deltas outside that space.  A line is created in a virtual space with it's starting point assumed to be 0,0, and no transformations (translation, rotation,
 * etc.) are assumed.  The sequence of deltas is used by the drawing context to generate actual Sisyphus table positions, though this is actually irrelevant
 * to the line generation.  The deltas generated may not exceed a maximum point distance (in the Cartesian space) that is specified when the line is
 * constructed.  We use a series of points rather than an equation of the line in order to simplify both the writing of Line implementations and to allow for
 * completely arbitrary line shapes (that is, those not practicably describable by an equation).
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Line {


    /**
     * Returns an immutable list of the delta positions representing this line.
     *
     * @return the immutable list of delta positions.
     */
    List<Delta> getDeltas();
}
