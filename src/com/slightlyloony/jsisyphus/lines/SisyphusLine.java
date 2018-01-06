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
        m = (end.getRho() - start.getRho()) / (end.getTheta() - start.getTheta() );
        b = start.getRho() - m * start.getTheta();

        // some setup...
        Position current = start;
        points.add( current );  // always add our starting point...
        double ad = Math.signum( end.getTheta() - start.getTheta() );
        double dd = Math.signum( end.getRho() - start.getRho() );
        boolean done = false;

        while( !done ) {

            // find our next point...
            double K = 1.2;  // our fudge factor...
            double P = current.getRho();
            double dt;
            if( dd <= 0 ) {
                dt = Common.MIN_POINT_SPACING / (K * (P + Math.abs(m)) );
            }
            else {
                double a = Math.abs(m);
                double b = P + Math.abs(m);
                double c = -Common.MIN_POINT_SPACING/K;
                dt = (-b + Math.sqrt( Math.pow( b, 2 ) - 4 * a * c )) / (2 * a);
                double dt2 = (-b - Math.sqrt( Math.pow( b, 2 ) - 4 * a * c )) / (2 * a);
            }

            double nt = current.getTheta() + ad * dt;
            if( ((ad >= 0) && (nt >= end.getTheta())) || ((ad < 0) && (nt <= end.getTheta())) ) {
                    current = end;
                    done = true;
            }
            else
                current = new PolarPosition( m * nt + b, nt );

            points.add( current );
        }

    }
}
