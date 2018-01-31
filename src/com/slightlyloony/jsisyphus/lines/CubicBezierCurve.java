package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.Delta;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cubic Bezier curve (a Bezier curve with two control points).
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class CubicBezierCurve extends ALine implements Line {


    public CubicBezierCurve( final double _maxPointDistance,
                             final double _xControl1, final double _yControl1,  // the first control point (controls the slope from the start)...
                             final double _xControl2, final double _yControl2,  // the second control point (controls the slope from the end)...
                             final double _xEnd,      final double _yEnd ) {    // the end of the line...
        super( _maxPointDistance, getDeltas( _maxPointDistance, _xControl1, _yControl1, _xControl2, _yControl2, _xEnd, _yEnd ) );
    }

    /* package */ static List<Delta> getDeltas( final double _maxPointDistance,
                                                final double _xControl1, final double _yControl1,  // the first control point (controls the slope from the start)...
                                                final double _xControl2, final double _yControl2,  // the second control point (controls the slope from the end)... ) {
                                                final double _xEnd,      final double _yEnd ) {    // the end of the line...

        // initially estimate the number of points as twice the max point separation on the straight-line distance between the start and the end...
        // if that turns out to not be enough, we'll double it and try again...
        int numPoints = (int) Math.ceil( 2 * Math.hypot( _xEnd, _yEnd ) / _maxPointDistance );

        List<Delta> deltas = new ArrayList<>( numPoints );

        // iterate until we have all points within the max point distance...
        boolean done = false;
        while( !done ) {

            double lastX = 0;
            double lastY = 0;
            done = true;  // we presume it's going to work, and set it false if we detect otherwise...

            for( int t = 0; t <= numPoints; t++ ) {

                // calculate our next point...
                double tn = 1.0 * t / numPoints;
                double bx = poly( tn, _xControl1, _xControl2, _xEnd );
                double by = poly( tn, _yControl1, _yControl2, _yEnd );
                Delta delta = new Delta( bx - lastX, by - lastY );

                // if the distance to this exceeds max point distance, start over with twice as many points...
                if( Math.hypot( delta.x, delta.y ) > _maxPointDistance ) {
                    deltas.clear();
                    numPoints *= 2;
                    done = false;
                    break;
                }

                deltas.add( delta );
                lastX = bx;
                lastY = by;
            }
        }

        return deltas;
    }


    /**
     * Computes the basic cubic Bezier polynomial: (1 - t)^3 * p0 + 3 * (1 - t)^2 * p1 + 3 *(1 - t)^2 * p2 + t^3 * p3.  This is computed twice for each resulting
     * point: once for the x result, once for the y result.  The starting point is assumed: 0, 0.
     *
     * @param _t the interval along the curve, [0..1].
     * @param _p1 the x or y coordinate for the first control point.
     * @param _p2 the x or y coordinate for the second control point.
     * @param _p3 the x or y coordinate for the ending point.
     * @return the x or y coordinate at the given interval.
     */
    private static double poly( final double _t, final double _p1, final double _p2, final double _p3 ) {
        double ts = Math.pow( 1 - _t, 3 );
        double t1 = 3 * Math.pow( 1 - _t, 2 ) * _t;
        double t2 = 3 * (1 - _t) * Math.pow( _t, 2 );
        double te = Math.pow( _t, 3 );
        return t1 * _p1 + t2 * _p2 + te * _p3;
    }
}
