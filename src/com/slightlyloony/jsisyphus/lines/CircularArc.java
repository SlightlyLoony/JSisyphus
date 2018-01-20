package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.DrawingContext;
import com.slightlyloony.jsisyphus.Utils;
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
    private final double deltaAngle;

    private int segments;
    private int currentSegment;
    private double currentAngle;

    /**
     * Represents all or part of a circle on the Sisyphus table.  The arc is defined by the given start and end point, and the given angle of arc subtended.
     * Positive arc angles define clockwise arcs, negative arc angles anticlockwise.
     *
     * @param _dc the drawing context this line will be drawn on.
     * @param _start the start point for this line.
     * @param _end the end point for this line.
     * @param _arcAngle the circular angle of the arc to be drawn.
     */
    public CircularArc( final DrawingContext _dc, final Position _start, final Position _end, final double _arcAngle ) {
        super( _dc, _start, _end );

        // calculate our key elements...
        boolean isClockwise = (_arcAngle >= 0);
        boolean isLarge = (((int) Math.floor( Math.abs( _arcAngle ) / Math.PI )) & 1) == 1;
        boolean invert = isClockwise == isLarge;
        double naTheta = Math.abs( Utils.normalizeTheta( _arcAngle ) );
        double iTheta = (Math.PI - Math.abs( naTheta )) / 2;
        double dTheta = Utils.getTheta( end.deltaX( start ), end.deltaY( start ) );
        double d = start.distanceFrom( _end );
        radius = (d / 2) / Math.cos( iTheta );
        double xc = start.getX() + radius * Math.sin( dTheta + (invert ? -iTheta : iTheta) );
        double yc = start.getY() + radius * Math.cos( dTheta + (invert ? -iTheta : iTheta) );
        center = new CartesianPosition( xc, yc, start.getTurns() );

        double arclen = radius * Math.abs( _arcAngle ) / Math.PI;
        segments = (int) Math.ceil( arclen / dc.getMaxPointDistance() );
        deltaAngle = _arcAngle / segments;
        currentSegment = 0;
        currentAngle = Utils.getTheta( start.deltaX( center ), start.deltaY( center ) );

        generate();
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
        currentSegment++;
        if( currentSegment == segments ) return end;
        currentAngle += deltaAngle;
        double nx = center.getX() + radius * Math.sin( currentAngle );
        double ny = center.getY() + radius * Math.cos( currentAngle );
        return new CartesianPosition( nx, ny, center.getTurns() );
    }
}
