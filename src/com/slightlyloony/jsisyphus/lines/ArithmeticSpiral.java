package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.DrawingContext;
import com.slightlyloony.jsisyphus.positions.CartesianPosition;
import com.slightlyloony.jsisyphus.positions.PolarPosition;
import com.slightlyloony.jsisyphus.positions.Position;

/**
 * Represents an arithmetic (Archimedian) spiral, which is a line with the polar form 𝚸 = m𝚹 + b, where "𝚸" is the distance from the center of the table
 * (normalized to 1) and 𝚹 is the angle (in radians) from the zero degree coordinate.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ArithmeticSpiral extends ALine implements Line {

    private final double m;
    private final double b;
    private final double deltaTheta;
    private final double deltaRho;

    public ArithmeticSpiral( final DrawingContext _dc, final Position _start, final Position _end ) {
        super( _dc, _start, _end );

        // calculate our line's coefficients...
        deltaTheta = end.getTheta() - start.getTheta();  // delta theta over the entire line (may be multiple revolutions)...
        deltaRho = end.getRho() - start.getRho();  // delta rho over the entire line...
        m = deltaRho / deltaTheta;
        b = start.getRho() - m * start.getTheta();

        // some setup for generating our points...
        Position current = start;
//        points.add( current );
//
//        // to improve accuracy, we'll do this in 1/8 revolution steps...
//        int steps = (int)((deltaTheta >= 0) ? Math.ceil( deltaTheta / (Math.PI / 4) ) : -Math.floor( deltaTheta / (Math.PI / 4 ) ));
//        double dtps = deltaTheta / steps;  // delta theta per step...
//        for( int step = 0; step < steps; step++ ) {
//
//            // find rho at our end point...
//            double epr = m * (current.getTheta() + dtps ) + b;
//
//            // find the length of this segment of our line (approximation as arc of circle at max rho and same delta theta)...
//            double seglen = Math.abs( (dtps / 2 * Math.PI) * Math.max( epr, current.getRho() ) );
//
//            // calculate the number of points on this segment, and the delta theta per point...
//            int pps = (int) Math.ceil( seglen / Common.VISUAL_RESOLUTION_SU );
//            double dtpp = dtps / pps;
//
//            // generate the points on this segment...
//            for( int i = 0; i < pps; i++ ) {
//
//                double pr = m * (current.getTheta() + dtpp ) + b;
//                double pt = current.getTheta() + dtpp;
//                current = new PolarPosition( pr, pt );
//                points.add( current );
//            }
//
//        }
//
//        // force the actual end point as the last point, to eliminate any accumulated error...
//        points.set( points.size() - 1, end );
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

        // calculate the slope angle at the current theta...
        double tm = _current.getTheta() - 0.001;
        double tp = tm + 0.002;
        double rm = m * tm + b;
        double rp = m * tp + b;
        Position ptm = new PolarPosition( rm, tm );
        Position ptp = new PolarPosition( rp, tp );
        double slopeAngle = new CartesianPosition( ptp.getX() - ptm.getX(), ptp.getY() - ptm.getY(), 0 ).getTheta();

        /*
            We're solving an SAS triangle defined as follows:
            a: the maximum length of the segment we creating
            b: rho at the current theta
            c: unknown and unneeded
            A: the value we need
            B: unknown and unneeded
            C: angle formed by the slope and current theta
         */

        // calculate the delta theta needed to get a segment of the desired length...
        double C = Math.PI + _current.getTheta() - slopeAngle;



        return super.nextPoint( _current );
    }
}
