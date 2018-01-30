package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.Delta;
import com.slightlyloony.jsisyphus.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.slightlyloony.jsisyphus.CartesianQuadrant.*;
import static com.slightlyloony.jsisyphus.CartesianQuadrant.PlusXPlusY;
import static com.slightlyloony.jsisyphus.CartesianQuadrant.get;

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
     * @param _ex
     * @param _ey
     * @param _cx
     * @param _cy
     * @param _turns
     */
    public ArithmeticSpiral( final double _maxPointDistance, final double _ex, final double _ey, final double _cx, final double _cy, final int _turns ) {
        super( _maxPointDistance, getDeltas( _maxPointDistance, _ex, _ey, _cx, _cy, _turns ) );
    }


    /* package */ static List<Delta> getDeltas( final double _maxPointDistance, final double _ex, final double _ey, final double _cx, final double _cy, final int _turns ) {

        // convenience variables...
        double fmX = -_cx;
        double fmY = -_cy;
        double toX = _ex - _cx;
        double toY = _ey - _cy;

        // calculate the spiral distances (rho)...
        double sRho = Math.hypot( fmX, fmY );                             // the distance from the center to the start point...
        double eRho = Math.hypot( toX, toY );                             // the distance from the center to the end point...
        double dRho = eRho - sRho;                                        // the delta rho over the length of the spiral...

        // correct the turns if we're changing quadrants...
        int newTurns = _turns;
        if( (get( fmX, fmY ) == PlusXMinusY)  && ((get( toX, toY ) == MinusXMinusY) || (get( toX, toY ) == MinusXPlusY)) ) newTurns++;
        if( (get( fmX, fmY ) == MinusXMinusY) && ((get( toX, toY ) == PlusXMinusY ) || (get( toX, toY ) == PlusXPlusY )) ) newTurns--;

        // calculate the spiral angles (theta)...
        double sTheta = Utils.getTheta( fmX, fmY );                       // the angle from the center to the start...
        double eTheta = Utils.getTheta( toX, toY );                       // the angle from the center to the end...
        eTheta += newTurns * 2 * Math.PI;                                 // correct for number of turns...
        double dTheta = eTheta - sTheta;                                  // the delta theta over the length of the spiral...

        // if we have a special case, handle them specially...
        if( Math.abs( dRho ) < 1.0E-10 )   return CircularArc.getDeltasFromCenter( _maxPointDistance, _cx, _cy, dTheta);
        if( Math.abs( dTheta ) < 1.0E-10 ) return StraightLine.getDeltas( _maxPointDistance, _ex, _ey );

        // handle a normal spiral...
        double sm = dRho / dTheta;
        double sb = sRho - sm * sTheta;
        boolean isClockwise = (dTheta >= 0);
        double curTheta = sTheta;
        double curRho = sRho;
        int estSize = (int) Math.ceil( 1.5 * (Math.abs( dTheta * Math.max( sRho, eRho )) + Math.abs( dRho )) / _maxPointDistance );
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
            if( Math.abs( rs ) > 1 ) {

                // first we generate a rho for the next point, then get the theta from that...
                npr = curRho + Utils.sign( dRho ) * 0.7 * _maxPointDistance / Math.min( 10, Math.abs( rs ));  // dividing by the slope makes the points closer together near the center...
                npt = getThetaFromRho( sm, sb, npr );
            }

            // otherwise, we do it with delta theta...
            else {
                npt = curTheta + Utils.sign( dTheta ) * Math.atan( 0.7 * _maxPointDistance / curRho );
                npr = getRhoFromTheta( sm, sb, npt );
            }

            // if we've reached the end, adjust and we're done...
            if( isClockwise ? npt >= eTheta : npt <= eTheta ) {
                deltas.add( new Delta( _ex - lastX, _ey - lastY ) );
                break;
            }

            // otherwise, stuff our new delta away and carry on...
            double npx = _cx + npr * Math.sin( npt );
            double npy = _cy + npr * Math.cos( npt );
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
