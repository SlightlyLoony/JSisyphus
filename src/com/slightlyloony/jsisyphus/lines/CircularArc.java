package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.Delta;
import com.slightlyloony.jsisyphus.Utils;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

/**
 * Represents an arc of a circle.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class CircularArc extends ALine implements Line {


    /**
     * Private constructor used by static factory methods.
     *
     * @param _maxPointDistance the maximum distance between points on this line.
     * @param _deltas the sequence of deltas for this line.
     */
    private CircularArc( final double _maxPointDistance, final List<Delta> _deltas ) {
        super( _maxPointDistance, _deltas );
    }


    private static List<Delta> getDeltas( final double _maxPointDistance, final double _arcAngle,
                                          final double _xCenter, final double _yCenter, final double _radius ) {

        int turns = Utils.getTurnsFromTheta( _arcAngle );
        double arclen = _radius * abs( _arcAngle ) + 2 * PI * abs( turns);
        int numSegments = (int) ceil( arclen / _maxPointDistance );
        double startAngle = Utils.getTheta( -_xCenter, -_yCenter );
        double lastX = 0;
        double lastY = 0;
        List<Delta> deltas = new ArrayList<>( numSegments );

        for( int segment = 1; segment <= numSegments; segment++ ) {

            double currentAngle =  startAngle + (1.0 * segment / numSegments) * _arcAngle;
            double nx = _xCenter + _radius * sin( currentAngle );
            double ny = _yCenter + _radius * cos( currentAngle );
            deltas.add( new Delta( nx - lastX, ny - lastY ) );
            lastX = nx;
            lastY = ny;
        }

        return deltas;
    }


    /**
     * Returns the deltas for an arc of the given angle to the given end point location.  Positive arc angles indicate a clockwise arc from the starting
     * point; negative arc angles and anti-clockwise arc.  Note that the arc angle's range is not limited, though angles with an absolute value greater than
     * 2*pi are treated as though they were modulo 2*pi.  For example, an arc angle of 7 radians would be treated identically to an arc angle of (7 - 2*pi)
     * radians.
     *
     * @param _maxPointDistance the maximum point distance for this line.
     * @param _x the x offset of the end point from the start point.
     * @param _y the y offset of the end point from the start point.
     * @param _arcAngle the circular angle of the arc to be drawn.
     * @return the deltas for this line.
     */
    /* package */ static List<Delta> getDeltasFromEnd( final double _maxPointDistance, final double _x, final double _y, final double _arcAngle ) {

        // guard against the start and end points being too close together (can't compute the center accurately then)...
        double d = hypot( _x, _y );
        if( d < 0.01 )
            throw new IllegalStateException( "Start and end points of arc too close together!" );

        /*
           The (as yet unknown) radius to the start and end point, together with a straight line between the start and end point, form an equilateral
           triangle.  Here we compute the radius using the height triangle and a bit of trigonometry.
        */

        // normalize arc angle to the range [-2pi..2pi]...
        double nat = _arcAngle;
        if( abs( nat ) > 2 * PI ) nat += Utils.sign( nat ) * -2 * PI * floor( abs( nat ) / (2 * PI) );

        // compute the angle at the arc's start and end points...
        double at = abs( (PI - abs( nat ) ) / 2 );

        // compute the radius...
        double radius = abs( (d / 2) / cos( at ) );

        /*
           Now we figure out where the center is.  There are two possibilities remaining with the calculation so far, as the center of the arc could be on
           either side of the straight line between the start and end points.  Here we figure out which side, and calculate its exact position.
         */

        // if the arc is < 180 degrees, the center will be on the other side of the start -> end line from the arc.
        // if the arc is >= 180 degrees, the center will be on the same side of the start -> end line as the arc.
        boolean isOtherSide = ( abs( nat ) < PI );

        // the center will be on different sides depending on whether we're drawing clockwise or anti-clockwise...
        boolean isClockwise = (_arcAngle >= 0);

        // finally we can decide whether we need to invert the center's location...
        boolean invert = isClockwise ^ isOtherSide;

        // now we compute where the center actually is...
        double et = Utils.getTheta( _x, _y );                   // the angle of the end point from the start point...
        double xc = radius * sin( et + (invert ? -at : at) );  // the x location of the center from the start point...
        double yc = radius * cos( et + (invert ? -at : at) );  // the y location of the center from the start point...

        return getDeltas( _maxPointDistance, _arcAngle, xc, yc, radius );
    }


    /**
     * Returns the deltas for an arc of the given angle around the given center.  Positive arc angles indicate a clockwise arc from the starting
     * point; negative arc angles and anti-clockwise arc.  Note that the arc angle's range is not limited, though angles with an absolute value greater than
     * 2*pi are treated as though they were modulo 2*pi.  For example, an arc angle of 7 radians would be treated identically to an arc angle of (7 - 2*pi)
     * radians.
     *
     * @param _maxPointDistance the maximum point distance for this line.
     * @param _x the x offset of the center from the start point.
     * @param _y the y offset of the center from the start point.
     * @param _arcAngle the circular angle of the arc to be drawn.
     * @return the deltas for this line.
     */
    /* package */ static List<Delta> getDeltasFromCenter( final double _maxPointDistance, final double _x, final double _y, final double _arcAngle ) {

        // normalizes to [0..2*pi]...
        double nt = Utils.normalizeTheta( _arcAngle );    // normalizes to [-pi..pi]...
        if( nt < 0 ) nt += 2 * PI;                   // fixes it to [0..2*pi]...

        // start -> center length is the radius...
        double radius = hypot( _x, _y );

        return getDeltas( _maxPointDistance, _arcAngle, _x, _y, radius );
    }


    /**
     * Creates and returns a new instance of this class, representing all or part of a circle.  The arc is defined by the given coordinates to the end
     * and the given angle of arc subtended (positive for clockwise, negative for anticlockwise).
     *
     * @param _maxPointDistance the maximum point distance for this line.
     * @param _x the x location of the end point.
     * @param _y the y location of the end point.
     * @param _arcAngle the circular angle of the arc to be drawn.
     * @return the new instance of this class.
     */
    public static CircularArc fromEndPoint( final double _maxPointDistance, final double _x, final double _y, final double _arcAngle ) {
        return new CircularArc( _maxPointDistance, getDeltasFromEnd( _maxPointDistance, _x, _y, _arcAngle ) );
    }


    /**
     * Creates and returns a new instance of this class, representing all or part of a circle on the Sisyphus table.  The arc is defined by the given
     * start and center, and the given angle of arc subtended (positive for clockwise, negative for anticlockwise).
     *
     * @param _maxPointDistance the maximum point distance for this line.
     * @param _x the x location of the center.
     * @param _y the y location of the center.
     * @param _arcAngle the circular angle of the arc to be drawn.
     * @return the new instance of this class.
     */
    public static CircularArc fromCenter( final double _maxPointDistance, final double _x, final double _y, final double _arcAngle ) {
        return new CircularArc( _maxPointDistance, getDeltasFromCenter( _maxPointDistance, _x, _y, _arcAngle ) );
    }
}
