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
    private final boolean isRadial;
    private final boolean isCircle;


    public ArithmeticSpiral( final DrawingContext _dc, final Position _start, final Position _end ) {
        super( _dc, _start, _end );

        // calculate our line's coefficients...
        deltaTheta = end.getTheta() - start.getTheta();  // delta theta over the entire line (may be multiple revolutions)...
        deltaRho = end.getRho() - start.getRho();  // delta rho over the entire line...
        m = deltaRho / deltaTheta;
        b = start.getRho() - m * start.getTheta();
        isRadial = (Math.abs( deltaTheta ) < 1.0E-12 );
        isCircle = (Math.abs( deltaRho ) < 1.0E-12 );
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

        // calculate the radial slope theta...
        double rs = getRadialSlope( _current.getTheta() );

        // if we have a radial line, treat it specially...
        if( isRadial ) {
            boolean down = (deltaRho < 0);
            double nrho = _current.getRho() + ( down ? -dc.getMaxPointDistance() : dc.getMaxPointDistance() );
            if( (down && (nrho <= end.getRho())) || (!down && (nrho >= end.getRho()))) return end;
            return new PolarPosition( nrho, _current.getTheta() );
        }

        // if we have an arc of a circle, treat it specially...
        else if( isCircle ) {
            boolean anticlockwise = (deltaTheta < 0);
            double dtheta = dc.getMaxPointDistance() * Math.PI / _current.getRho();
            double ntheta = _current.getTheta() + (anticlockwise ? -dtheta : dtheta);
            if( (anticlockwise && (ntheta <= end.getTheta())) || (!anticlockwise && (ntheta >= end.getTheta()))) return end;
            return new PolarPosition( _current.getRho(), ntheta );
        }

        // if the slope is infinite, treat it specially - we're near the center and things are wonky...
        else if( Double.isInfinite( rs ) ) {
            boolean anticlockwise = (deltaTheta < 0);
            double npt = _current.getTheta() + (anticlockwise ? -.1 : .1);   // fixed tenth radian movement...
            double npr = getRhoFromTheta( npt );
            return new PolarPosition( npr, npt );
        }

        // otherwise, we do it with a bit of trig...
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
        double npt = _current.getTheta() + A;
        double npr = getRhoFromTheta( npt );

        // if we reached the end, just return the end point...
        if( isTerminal( A, npt, end.getTheta() ) ) return end;

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
