package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.DrawingContext;
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
    private final boolean isClockwise;


    public ArithmeticSpiral( final DrawingContext _dc, final Position _start, final Position _end ) {
        super( _dc, _start, _end );

        // calculate our line's coefficients...
        deltaTheta = end.getTheta() - start.getTheta();  // delta theta over the entire line (may be multiple revolutions)...
        deltaRho = end.getRho() - start.getRho();  // delta rho over the entire line...
        m = deltaRho / deltaTheta;
        b = start.getRho() - m * start.getTheta();
        isClockwise = (deltaTheta >= 0);

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

        // some setup...
        double npr;
        double npt;

        // calculate the radial slope theta...
        double rs = getRadialSlope( _current.getTheta() );

        // if the slope is infinite, treat it specially...
        if( Double.isInfinite( rs ) ) {

            // our next point will be very near our radial...
            npt = _current.getTheta() + .1;   // fixed tenth radian movement...
            npr = getRhoFromTheta( npt );
        }

        // otherwise, we do it with a bit of trig...
        else {
            double slopeAngle = Math.atan( rs );

        /*
            We're solving an SAS triangle defined as follows:
            a: the maximum length of the segment we creating
            b: rho at the current theta
            c: unknown and unneeded
            A: the value we need
            B: unknown and unneeded
            C: angle formed by the slope and current theta
         */

            // our known elements...
            double C = Math.PI - (Math.PI / 2 - slopeAngle);
            double a = dc.getMaxPointDistance();
            double b = _current.getRho();

            // use law of cosines to find c...
            double c = Math.sqrt( a * a + b * b - 2 * a * b * Math.cos( C ) );

            // use law of cosines to find A, but limit it to a half-radian (about 30 degrees)...
            double A = Math.min( 0.5, Math.acos( (b * b + c * c - a * a) / (2 * b * c) ) );
            A = isClockwise ? A : -A;

            // now get our next point's location, tentatively...
            npt = _current.getTheta() + A;
            npr = getRhoFromTheta( npt );

            // if we reached the end, just return the end point...
            if( isTerminal( A, _current.getTheta(), end.getTheta() ) ) return end;
        }

        return new PolarPosition( npr, npt );
    }


    private double getRhoFromTheta( final double _theta ) {
        return m * _theta + b;
    }


    private double getThetaFromRho( final double _rho) {
        return (_rho - b) / m;
    }


    private double getRadialSlope( final double _theta ) {
        return m / getRhoFromTheta( _theta );
    }
}
