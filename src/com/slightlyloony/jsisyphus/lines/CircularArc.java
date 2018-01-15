package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.DrawingContext;
import com.slightlyloony.jsisyphus.positions.CartesianPosition;
import com.slightlyloony.jsisyphus.positions.Position;

/**
 * Represents the trace of an arc of a circle on the Sisyphus table.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class CircularArc extends ALine implements Line {

    private final Position center;
    private final double radius;
    private final double terminalValue;
    private final double deltaAngle;

    /**
     * Represents all or part of a circle on the Sisyphus table.  The circle is defined by the position of its center and its radius (specified in Sisyphus
     * table distance units).  The length of the arc drawn is defined by the starting and ending angles, in radians.  Note that this means circles may be
     * traced partially, completely, or even repeatedly.
     *
     * @param _center  the position of the center of the circle to be drawn.
     * @param _radius the radius of the circle to be drawn, in Sisyphus table distance units.
     * @param _startAngle  the angle (from the center) of the start of the circle to be drawn.
     * @param _endAngle  the angle (from the center) of the end of the circle to be drawn.
     */
    public CircularArc( final DrawingContext _dc, final Position _center, final double _radius, final double _startAngle, final double _endAngle ) {
        super( _dc, calcStart( _center, _radius, _startAngle ), calcEnd( _center, _radius, _endAngle ) );

        // some setup...
        center = _center;
        radius = _radius;
        double arclen = _radius * (_endAngle - _startAngle);
        int segs = (int) Math.ceil( Math.abs( arclen ) / dc.getMaxPointDistance() );
        deltaAngle = (_endAngle - _startAngle) / segs;
        terminalValue = _endAngle - 0.1 * deltaAngle;
    }


    /**
     * Returns the next point on the line.  Classes representing algorithmically generated lines must override this method to provide the points along
     * the line, including the end point (exactly), which will terminate the iteration of points.
     *
     * @param _current a point along the line.
     * @return the next point along the line at no more than the maximum point distance specified in the drawing context.
     */
    @Override
    protected Position nextPoint( final Position _current ) {
        double na = _current.getTheta() + deltaAngle;
        if( isTerminal( deltaAngle, na, terminalValue ) ) return end;
        double nx = center.getX() + radius * Math.sin( na );
        double ny = center.getY() + radius * Math.cos( na );
        return new CartesianPosition( nx, ny, _current.getTurns() );
    }


    private static Position calcStart( final Position _center, final double _radius, final double _startAngle ) {
        double x = _center.getX() + _radius * Math.sin( _startAngle );
        double y = _center.getY() + _radius * Math.cos( _startAngle );
        return new CartesianPosition( x, y, _center.getTurns() );
    }


    private static Position calcEnd( final Position _center, final double _radius, final double _endAngle ) {
        double x = _center.getX() + _radius * Math.sin( _endAngle );
        double y = _center.getY() + _radius * Math.cos( _endAngle );
        return new CartesianPosition( x, y, _center.getTurns() );
    }
}
