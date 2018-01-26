package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.DrawingContext;
import com.slightlyloony.jsisyphus.Utils;
import com.slightlyloony.jsisyphus.positions.CartesianPosition;
import com.slightlyloony.jsisyphus.positions.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the trace of an arc of a circle on the Sisyphus table.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class CircularArc extends ALine implements Line {


    /**
     * Private constructor used by static factory methods.
     *
     * @param _dc the drawing context this line will be drawn on.
     * @param _points the points for this line.
     */
    private CircularArc( final DrawingContext _dc, final List<Position> _points ) {
        super( _dc, _points );
    }


    private static List<Position> getPoints( final DrawingContext _dc, final Position _start, final Position _end,
                                             final double _arcAngle, final Position _center, final double _radius ) {

        double arclen = _radius * 2 * Math.abs( _arcAngle );
        int numSegments = (int) Math.ceil( arclen / _dc.getMaxPointDistance() );
        double startAngle = Utils.getTheta( _start.deltaX( _center ), _start.deltaY( _center ) );
        Position lastPoint = _dc.getCurrentPosition();
        List<Position> points = new ArrayList<>( numSegments );

        for( int segment = 0; segment < numSegments; segment++ ) {

            // first we compute the new point in absolute coordinates from the angle and the center position...
            double currentAngle =  startAngle + 1.0 * segment / (numSegments - 1) * _arcAngle;
            double nx = _center.getX() + _radius * Math.sin( currentAngle );
            double ny = _center.getY() + _radius * Math.cos( currentAngle );
            Position newPoint = _dc.getTransformer().transform( new CartesianPosition( nx, ny, _center.getTurns() ) );

            // then we compute the deltas from the last point and apply them to get the new point correctly...
            lastPoint = lastPoint.fromDeltaXY( newPoint.deltaX( lastPoint ), newPoint.deltaY( lastPoint ) );
            points.add( lastPoint );
        }

        return points;
    }


    /* package */ static List<Position> getPointsFromEnd( final DrawingContext _dc, final Position _end, final double _arcAngle ) {

        Position start = _dc.getTransformer().untransform( _dc.getCurrentPosition() );  // current position is held as actual coordinates...

        // guard against the start and end points being too close together...
        double d = start.distanceFrom( _end );
        if( d < 0.01 )
            throw new IllegalStateException( "Start and end points of arc too close together!" );

        boolean isClockwise = (_arcAngle >= 0);
        boolean isLarge = (((int) Math.floor( Math.abs( _arcAngle ) / Math.PI )) & 1) == 1;
        boolean invert = isClockwise == isLarge;
        double naTheta = Math.abs( Utils.normalizeTheta( _arcAngle ) );
        double iTheta = (Math.PI - Math.abs( naTheta )) / 2;
        double dTheta = Utils.getTheta( _end.deltaX( start ), _end.deltaY( start ) );
        double radius = (d / 2) / Math.cos( iTheta );
        double xc = radius * Math.sin( dTheta + (invert ? -iTheta : iTheta) );
        double yc = radius * Math.cos( dTheta + (invert ? -iTheta : iTheta) );
        Position center = start.fromDeltaXY( xc, yc );

        return getPoints( _dc, start, _end, _arcAngle, center, radius );
    }


    /* package */ static List<Position> getPointsFromCenter( final DrawingContext _dc, final Position _center, final double _arcAngle ) {

        Position start = _dc.getTransformer().untransform( _dc.getCurrentPosition() );  // current position is held as actual coordinates...

        double radius = start.distanceFrom( _center );
        double startTheta = Utils.getTheta( start.deltaX( _center ), start.deltaY( _center ) );
        double endTheta = startTheta + _arcAngle;
        double endX = radius * Math.sin( endTheta );
        double endY = radius * Math.cos( endTheta );
        Position end = _center.fromDeltaXY( endX, endY );

        return getPoints( _dc, start, end, _arcAngle, _center, radius );
    }


    /**
     * Creates and returns a new instance of this class, representing all or part of a circle on the Sisyphus table.  The arc is defined by the given
     * start and end point, and the given angle of arc subtended (positive for clockwise, negative for anticlockwise).
     *
     * @param _dc the drawing context this line will be drawn on.
     * @param _end the end point for this line.
     * @param _arcAngle the circular angle of the arc to be drawn.
     * @return the new instance of this class.
     */
    public static CircularArc fromEndPoint( final DrawingContext _dc, final Position _end, final double _arcAngle ) {
        return new CircularArc( _dc, getPointsFromEnd( _dc, _end, _arcAngle ) );
    }


    /**
     * Creates and returns a new instance of this class, representing all or part of a circle on the Sisyphus table.  The arc is defined by the given
     * start and center, and the given angle of arc subtended (positive for clockwise, negative for anticlockwise).
     *
     * @param _dc the drawing context this line will be drawn on.
     * @param _center the center point for this line.
     * @param _arcAngle the circular angle of the arc to be drawn.
     * @return the new instance of this class.
     */
    public static CircularArc fromCenter( final DrawingContext _dc, final Position _center, final double _arcAngle ) {
        return new CircularArc( _dc, getPointsFromCenter( _dc, _center, _arcAngle ) );
    }
}
