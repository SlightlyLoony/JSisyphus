package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.Delta;
import com.slightlyloony.jsisyphus.Point;
import com.slightlyloony.jsisyphus.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.slightlyloony.jsisyphus.CartesianQuadrant.*;
import static java.lang.Math.*;

/**
 * Represents an arithmetic (Archimedian) spiral, which is a line with the polar form 𝚸 = m𝚹 + b, where "𝚸" is the distance from the center of the table
 * (normalized to 1) and 𝚹 is the angle (in radians) from the zero degree coordinate.  Note that the center of the spiral is assumed to be at the origin
 * in transformed coordinates.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ArithmeticSpiral extends ALine implements Line {


    /**
     * Creates a representation of an arithmetic spiral line with the given end point position, spiral center position, and number of turns to make.  The
     * deltas will be at most the maximum point distance apart.
     *
     * @param _maxPointDistance
     * @param _end
     * @param _center
     * @param _turns
     */
    public ArithmeticSpiral(  final double _maxPointDistance, final Point _end, final Point _center, final double _centerTheta, final int _turns ) {
        super( _maxPointDistance, getDeltas( _maxPointDistance, _end, _center, _centerTheta, _turns ) );
    }


    /* package */ static List<Delta> getDeltas( final double _maxPointDistance, final Point _end, final Point _center, final double _centerTheta, final int _turns ) {

        // convenience variables...
        boolean fromIsCenter = false;
        boolean toIsCenter = false;
        Point from = Point.fromRT( _center.rho, _center.theta + PI );
        Point to = from.sum( _end );
        if( _center.distanceFrom( from ) < 1E-10 ) {
            from = _center;
            fromIsCenter = true;
        }
        if( _center.distanceFrom( _end ) < 1E-10 ) {
            to = from.sum( _center );
            toIsCenter = true;
        }

        // calculate the spiral distances (rho)...
        double sRho = _center.rho;                        // the distance from the center to the start point...
        double eRho = _center.distanceFrom( _end );       // the distance from the center to the end point...
        double dRho = eRho - sRho;                        // the delta rho over the length of the spiral...

        // correct the turns if we're changing quadrants...
        int newTurns = _turns;
        if( !toIsCenter ) {
            if( (get( from ) == PlusXMinusY) && ((get( to ) == MinusXMinusY) || (get( to ) == MinusXPlusY)) ) newTurns++;
            if( (get( from ) == MinusXMinusY) && ((get( to ) == PlusXMinusY) || (get( to ) == PlusXPlusY)) ) newTurns--;
        }

        // calculate the spiral angles (theta)...
        double eTheta = toIsCenter   ? _centerTheta : _center.thetaTo( _end );   // the angle from the center to the end...
        double sTheta = fromIsCenter ? _centerTheta : _center.thetaTo( from );   // the angle from the center to the start...
        eTheta += newTurns * 2 * PI;                                             // correct for number of turns...
        double dTheta = eTheta - sTheta;                                         // the delta theta over the length of the spiral...

        // if we have a special case, handle them specially...
        // if we have a near-zero length spiral...
        if( (abs( dRho ) < 1.0E-10) && (abs( dTheta ) < 1.0E-10) ) {
            List<Delta> deltas = new ArrayList<>( 1 );
            deltas.add( new Delta( 0, 0 ) );
            return deltas;
        }

        // if we have a circular arc (constant rho)...
        if( abs( dRho ) < 1.0E-10 )   return CircularArc.getDeltasFromCenter( _maxPointDistance, _center.x, _center.y, dTheta);

        // if we have a radial (constant theta)...
        if( abs( dTheta ) < 1.0E-10 ) return StraightLine.getDeltas( _maxPointDistance, _end.x, _end.y );

        // handle a normal spiral...
        double sm = dRho / dTheta;
        double sb = sRho - sm * sTheta;
        boolean isClockwise = (dTheta >= 0);
        double curTheta = sTheta;
        double curRho = sRho;
        int estSize = (int) ceil( 1.5 * (abs( dTheta * max( sRho, eRho )) + abs( dRho )) / _maxPointDistance );
        List<Delta> deltas = new ArrayList<>( estSize );
        double lastX = 0;
        double lastY = 0;

        for( int iters = 0; iters < 100000; iters++ ) {

            // bail out if we iterate too much...
            if( iters >= 99999 )
                throw new IllegalStateException( "Arithmetic spiral deltas not terminating..." );

            // get the slope at our current point...
            double rs = getRadialSlope( sm, sb, curTheta );

            // calculate our new point's rho and theta...
            double npt;
            double npr;

            // if the slope over one, then we're near the center and we'll calculate with delta rho...
            if( abs( rs ) > 1 ) {

                // first we generate a rho for the next point, then get the theta from that...
                npr = curRho + Utils.sign( dRho ) * 0.7 * _maxPointDistance / min( 10, abs( rs ));  // dividing by the slope makes the points closer together near the center...
                npt = getThetaFromRho( sm, sb, npr );
            }

            // otherwise, we do it with delta theta...
            else {
                npt = curTheta + Utils.sign( dTheta ) * atan( 0.7 * _maxPointDistance / curRho );
                npr = getRhoFromTheta( sm, sb, npt );
            }

            // if we've reached the end, adjust and we're done...
            if( isClockwise ? npt >= eTheta : npt <= eTheta ) {
                deltas.add( new Delta( _end.x - lastX, _end.y - lastY ) );
                break;
            }

            // otherwise, stuff our new delta away and carry on...
            double npx = _center.x + npr * sin( npt );
            double npy = _center.y + npr * cos( npt );
            curTheta = npt;
            curRho = npr;
            double dx = npx - lastX;
            double dy = npy - lastY;
            deltas.add( new Delta( dx, dy ) );
            lastX = npx;
            lastY = npy;
        }

        return deltas;
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
