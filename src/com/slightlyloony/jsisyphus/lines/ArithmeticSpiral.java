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

        // calculate the spiral's specs...
        SpiralSpec spec = new SpiralSpec( _end, _center, _centerTheta, _turns );

        // if we have a special case, handle them specially...
        if( spec.isNearZeroLength ) {
            List<Delta> deltas = new ArrayList<>( 1 );
            deltas.add( new Delta( 0, 0 ) );
            return deltas;
        }

        // if we have a circular arc...
        if( spec.isCircle )   return CircularArc.getDeltasFromCenter( _maxPointDistance, _center.x, _center.y, spec.dTheta);

        // if we have a radial...
        if( spec.isRadial ) return StraightLine.getDeltas( _maxPointDistance, _end.x, _end.y );

        // handle a normal spiral...
        double curTheta = spec.sTheta;
        double curRho = spec.sRho;
        int estSize = (int) ceil( 1.5 * (abs( spec.dTheta * max( spec.sRho, spec.eRho )) + abs( spec.dRho )) / _maxPointDistance );
        List<Delta> deltas = new ArrayList<>( estSize );
        double lastX = 0;
        double lastY = 0;

        for( int iters = 0; iters < 100000; iters++ ) {

            // bail out if we iterate too much...
            if( iters >= 99999 )
                throw new IllegalStateException( "Arithmetic spiral deltas not terminating..." );

            // get the slope at our current point...
            double rs = spec.getRadialSlope( curTheta );

            // calculate our new point's rho and theta...
            double npt;
            double npr;

            // if the slope over one, then we're near the center and we'll calculate with delta rho...
            if( abs( rs ) > 1 ) {

                // first we generate a rho for the next point, then get the theta from that...
                npr = curRho + Utils.sign( spec.dRho ) * 0.7 * _maxPointDistance / min( 10, abs( rs ));  // dividing by the slope makes the points closer together near the center...
                npt = spec.getThetaFromRho( npr );
            }

            // otherwise, we do it with delta theta...
            else {
                npt = curTheta + Utils.sign( spec.dTheta ) * atan( 0.7 * _maxPointDistance / curRho );
                npr = spec.getRhoFromTheta( npt );
            }

            // if we've reached the end, adjust and we're done...
            if( spec.isClockwise ? npt >= spec.eTheta : npt <= spec.eTheta ) {
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


    /**
     * Instances of this class contain the complete specification of a spiral, as generated from the constructor's arguments.
     */
    public static class SpiralSpec {

        private boolean fromIsCenter;
        private boolean toIsCenter;
        private Point center;
        private Point from;
        private Point to;
        private double sRho;
        private double eRho;
        private double dRho;
        private double sTheta;
        private double eTheta;
        private double dTheta;
        private double m;
        private double b;
        private boolean isClockwise;
        private boolean isCircle;
        private boolean isRadial;
        private boolean isNearZeroLength;


        public SpiralSpec( final Point _end, final Point _center, final double _centerTheta, final int _turns ) {

            // compute center-referenced absolute points...
            center = Point.fromRT( 0, 0 );
            from = _center.invertXY();
            to = from.sum( _end );

            // convenience variables...
            fromIsCenter = ( center.distanceFrom( from ) < 1E-10 );
            toIsCenter   = ( center.distanceFrom( to )   < 1E-10 );
            if( fromIsCenter ) from = center;
            if( toIsCenter   ) to   = center;

            // calculate the spiral distances (rho)...
            sRho = from.rho;     // the distance from the center to the start point...
            eRho = to.rho;       // the distance from the center to the end point...
            dRho = eRho - sRho;  // the delta rho over the length of the spiral...

            // correct the turns if we're changing quadrants...
            int newTurns = _turns;
            if( !toIsCenter ) {
                if( (get( from ) == PlusXMinusY) && ((get( to ) == MinusXMinusY) || (get( to ) == MinusXPlusY)) ) newTurns++;
                if( (get( from ) == MinusXMinusY) && ((get( to ) == PlusXMinusY) || (get( to ) == PlusXPlusY)) ) newTurns--;
            }

            // calculate the spiral angles (theta)...
            eTheta = toIsCenter   ? _centerTheta : center.thetaTo( to   );   // the angle from the center to the end...
            sTheta = fromIsCenter ? _centerTheta : center.thetaTo( from );   // the angle from the center to the start...
            eTheta += newTurns * 2 * PI;                                     // correct for number of turns...
            dTheta = eTheta - sTheta;                                        // the delta theta over the length of the spiral...

            // check for special cases...
            isCircle         = (  abs( dRho )   < 1.0E-10 );
            isRadial         = (  abs( dTheta ) < 1.0E-10 );
            isNearZeroLength = isCircle && isRadial;
            if( isCircle || isRadial ) return;

            // calculate the spiral parameters (at last!)...
            m = dRho / dTheta;
            b = sRho - m * sTheta;
            isClockwise = (dTheta >= 0);
        }


        public double getRhoFromTheta( final double _theta ) {
            return m * _theta + b;
        }


        public double getThetaFromRho( final double _rho) {
            return (_rho - b) / m;
        }


        public double getRadialSlope( final double _theta ) {
            return m / getRhoFromTheta( _theta );
        }


        public boolean isFromIsCenter() {
            return fromIsCenter;
        }


        public boolean isToIsCenter() {
            return toIsCenter;
        }


        public Point getCenter() {
            return center;
        }


        public Point getFrom() {
            return from;
        }


        public Point getTo() {
            return to;
        }


        public double getsRho() {
            return sRho;
        }


        public double geteRho() {
            return eRho;
        }


        public double getdRho() {
            return dRho;
        }


        public double getsTheta() {
            return sTheta;
        }


        public double geteTheta() {
            return eTheta;
        }


        public double getdTheta() {
            return dTheta;
        }


        public double getM() {
            return m;
        }


        public double getB() {
            return b;
        }


        public boolean isClockwise() {
            return isClockwise;
        }


        public boolean isCircle() {
            return isCircle;
        }


        public boolean isRadial() {
            return isRadial;
        }


        public boolean isNearZeroLength() {
            return isNearZeroLength;
        }
    }
}
