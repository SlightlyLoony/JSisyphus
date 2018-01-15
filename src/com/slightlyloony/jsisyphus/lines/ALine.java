package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.DrawingContext;
import com.slightlyloony.jsisyphus.positions.Position;

import java.util.ArrayList;
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
    public final Position start;
    public final Position end;

    private final List<Position> points;


    /**
     * Creates a new instance of this class with algorithmically generated points.
     *
     * @param _start the start point of this line.
     * @param _end the end point of this line.
     */
    protected ALine( final DrawingContext _dc, final Position _start, final Position _end ) {
        dc = _dc;
        start = _start;
        end = _end;
        points = new ArrayList<>();

    }


    protected void generate() {
        // generate the points on our line...
        points.add( start );
        Position next;
        do {
            next = nextPoint( points.get( points.size() - 1 ) );
            points.add( next );
        } while( !next.equals( end ));
    }


    /**
     * Creates a new instance of this class with arbitrary sequence of points.
     *
     * @param _points the sequence of points comprising this line.
     */
    protected ALine( final DrawingContext _dc, final List<Position> _points ) {
        dc = _dc;
        start = _points.get( 0 );
        end = _points.get( _points.size() - 1);
        points = _points;
    }


    // Termination test; returns true if the given value equals or exceeds the terminal value, in the direction indicated by the sign of the given pp.


    /**
     * Returns true if the give current value exceeds the given terminal value in the direction of the given delta value.  For example, with a current value
     * of 2.2, a terminal value of 2.18, and a delta value of 0.43, it would return true.  With a current value of -3.45, a terminal value of -3.56, and a
     * delta value of -.029, it would return false.
     *
     * @param deltaValue the delta applied to the current value on each iteration of the getNextPoint() method.
     * @param _currentValue the computed current value for the next iteration.
     * @param _terminalValue the terminal value at the end of the line.
     * @return
     */
    protected boolean isTerminal( final double deltaValue, final double _currentValue, final double _terminalValue ) {
        return (deltaValue >= 0) ? (_currentValue >= _terminalValue) : (_currentValue <= _terminalValue);
    }


    /**
     * Returns the next point on the line.  Classes representing algorithmically generated lines must override this method to provide the points along
     * the line, including the end point (exactly), which will terminate the iteration of points.
     *
     * @param _current a point along the line.
     * @return the next point along the line at no more than the maximum point distance specified in the drawing context.
     */
    protected Position nextPoint( final Position _current ) {
        return end;
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
