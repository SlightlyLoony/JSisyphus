package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.Common;
import com.slightlyloony.jsisyphus.positions.PolarPosition;
import com.slightlyloony.jsisyphus.positions.Position;

/**
 * Represents a line on the Sisyphus table, which is a spiral line with the polar form 𝚸 = m𝚹 + b, where "𝚸" is the distance from the center of the table
 * (normalized to 1) and 𝚹 is the angle (in radians) from the zero degree coordinate.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SisyphusLine extends ALine implements Line {

    private final double m;
    private final double b;

    public SisyphusLine( final Position _start, final Position _end ) {
        super( _start, _end );

        // calculate our line's coefficients...
        double dt = end.getTheta() - start.getTheta();  // delta theta over the entire line (may be multiple revolutions)...
        double dr = end.getRho() - start.getRho();  // delta rho over the entire line...
        m = dr / dt;
        b = start.getRho() - m * start.getTheta();

        // some setup for generating our points...
        Position current = start;
        points.add( current );

        // to improve accuracy, we'll do this in 1/8 revolution steps...
        int steps = (int)((dt >= 0) ? Math.ceil( dt / (Math.PI / 4) ) : -Math.floor( dt / (Math.PI / 4 ) ));
        double dtps = dt / steps;  // delta theta per step...
        for( int step = 0; step < steps; step++ ) {

            // find rho at our end point...
            double epr = m * (current.getTheta() + dtps ) + b;

            // find the length of this segment of our line (approximation as arc of circle at max rho and same delta theta)...
            double seglen = Math.abs( (dtps / 2 * Math.PI) * Math.max( epr, current.getRho() ) );

            // calculate the number of points on this segment, and the delta theta per point...
            int pps = (int) Math.ceil( seglen / Common.VISUAL_RESOLUTION_SU );
            double dtpp = dtps / pps;

            // generate the points on this segment...
            for( int i = 0; i < pps; i++ ) {

                double pr = m * (current.getTheta() + dtpp ) + b;
                double pt = current.getTheta() + dtpp;
                current = new PolarPosition( pr, pt );
                points.add( current );
            }

        }

        // force the actual end point as the last point, to eliminate any accumulated error...
        points.set( points.size() - 1, end );
    }
}
