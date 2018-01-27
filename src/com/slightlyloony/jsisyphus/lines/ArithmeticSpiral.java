package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.DrawingContext;
import com.slightlyloony.jsisyphus.Utils;
import com.slightlyloony.jsisyphus.positions.PolarPosition;
import com.slightlyloony.jsisyphus.positions.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an arithmetic (Archimedian) spiral, which is a line with the polar form 𝚸 = m𝚹 + b, where "𝚸" is the distance from the center of the table
 * (normalized to 1) and 𝚹 is the angle (in radians) from the zero degree coordinate.  Note that the center of the spiral is assumed to be at the origin
 * in transformed coordinates.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ArithmeticSpiral extends ALine implements Line {


    public ArithmeticSpiral( final DrawingContext _dc, final Position _end ) {
        super( _dc, getPoints( _dc, _end ) );
    }


    /* package */ static List<Position> getPoints(  final DrawingContext _dc, final Position _end ) {

        // some setup...
        Position current = _dc.getTransformer().untransform( _dc.getCurrentPosition() );
        List<Position> points = new ArrayList<>();
        points.add( _dc.getCurrentPosition() );
        double deltaTheta = _end.getTheta() - current.getTheta();  // delta theta over the entire line (may be multiple revolutions)...
        double deltaRho = _end.getRho() - current.getRho();        // delta rho over the entire line...
        boolean isRadial = (Math.abs( deltaTheta ) < 1.0E-12 );
        boolean isCircle = (Math.abs( deltaRho ) < 1.0E-12 );

        // handle circular arcs and radials specially...
        if( isCircle ) return CircularArc.getPointsFromCenter( _dc, Position.CENTER, deltaTheta );
        if( isRadial ) return StraightLine.getPoints( _dc, _end );

        // handle a normal spiral...
        double sm = deltaRho / deltaTheta;
        double sb = current.getRho() - sm * current.getTheta();
        boolean isClockwise = (deltaTheta >= 0);

        while( true ) {

            // get the slope at our current point...
            double rs = getRadialSlope( sm, sb, current.getTheta() );

            // calculate our new point's rho and theta...
            double npt;
            double npr;

            // if the slope over one, then we're near the center and we'll calculate with delta rho...
            if( Math.abs( rs ) > 1 ) {

                // first we generate a rho for the next point, then get the theta from that...
                npr = current.getRho() + Utils.sign( deltaRho ) * 0.7 * _dc.getMaxPointDistance();
                npt = getThetaFromRho( sm, sb, npr );
            }

            // otherwise, we do it with delta theta...
            else {
                npt = current.getTheta() + Utils.sign( deltaTheta ) * Math.atan( 0.7 * _dc.getMaxPointDistance() / current.getRho() );
                npr = getRhoFromTheta( sm, sb, npt );
            }

            // if we've reached the end, adjust and we're done...
            if( isClockwise ? npt >= _end.getTheta() : npt <= _end.getTheta() ) {
                points.add( _end );
                break;
            }

            // otherwise, stuff our new point away and carry on...
            current = _dc.getTransformer().transform( new PolarPosition( npr, npt ) );
            points.add( current );
        }

        return points;
    }


    private static double getRhoFromTheta( final double _m, final double _b, final double _theta ) {
        return _m * _theta + _b;
    }


    private static double getThetaFromRho( final double _m, final double _b, final double _rho) {
        return (_rho - _b) / _m;
    }


    private static double getRadialSlope( final double _m, final double _b, final double _theta ) {
        return _m / getRhoFromTheta( _m, _b, _theta );
    }
}
