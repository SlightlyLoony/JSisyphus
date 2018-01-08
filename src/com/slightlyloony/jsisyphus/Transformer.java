package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.lines.ArbitraryLine;
import com.slightlyloony.jsisyphus.lines.Line;
import com.slightlyloony.jsisyphus.positions.CartesianPosition;
import com.slightlyloony.jsisyphus.positions.PolarPosition;
import com.slightlyloony.jsisyphus.positions.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class transform lines through translation, rotation, scaling, or reflection.  The order of transformation operations is:
 * reflection (x, y), scaling, rotation, and translation.  Note that the only operation that moves the center is translation, and it is the last
 * operation performed.  The order of the remaining operations doesn't actually make any difference; they all involve operations about the origin (0, 0)
 * point.  Note this means that the lines being transformed are presumed to be referenced to the origin.
 *
 * If inversion is enabled, then a lines points are transformed in reverse order, from end to start.  This is not a conventional transformation (in the sense
 * of points being mathematically manipulated) but is very useful when drawing repetitive patterns.
 *
 * If auto-translation is enabled, then after each transform the translation point is updated to the last point of the transformed line.
 *
 * Instances of this class are mutable and <i>not</i> threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Transformer {

    public static final Transformer NOOP = new Transformer();

    private Position translation = Position.CENTER;
    private double reflectionAxis = 0;
    private boolean reflection = false;
    private double scale = 1.0;
    private double rotation = 0;

    private boolean inverted = false;

    private boolean autoTranslate = false;
    private boolean noop = true;


    public Transformer() {
    }


    public Line transform( final Line _line ) {

        // if we have no transformations, return with our argument unchanged...
        if( noop ) return _line;

        // iterate over all the points in our argument (could be in either direction)...
        int sp = _line.getPoints().size() - 1;
        int fp = inverted ? sp : 0;
        int dp = inverted ? -1 : 1;
        List<Position> points = new ArrayList<>( _line.getPoints().size() );  // the place for our result...
        for( int p = fp, ri = 0; (p >= 0) && (p <= sp); p += dp, ri++ ) {

            if( ri == 220 )
                hashCode();

            // some setup...
            Position pos = _line.getPoints().get( p );

            // reflect...
            if( reflection ) {
                pos = new PolarPosition( pos.getRho(), rotate( reflectionAxis, reflectionAxis - pos.getTheta() ) );
            }

            // scale...
            if( scale < 1 ) {
                pos = new PolarPosition( pos.getRho() * scale, pos.getTheta() );
            }

            // rotate...
            if( rotation != 0 ) {
                pos = new PolarPosition( pos.getRho(), rotate( pos.getTheta(), rotation ) );
            }

            // translate...
            if( !translation.isCenter() ) {
                pos = new CartesianPosition(
                        pos.getX() + translation.getX(),
                        pos.getY() + translation.getY(),
                        pos.getTurns() + translation.getTurns() );
            }

            // stuff the result...
            points.add( ri, pos );
        }

        if( autoTranslate ) {
            translation = points.get( points.size() - 1 );
        }

        return new ArbitraryLine( points );
    }


    // Rotates the given theta by the given delta, returning the rotated angle in the same turn (revolution) as the given theta.
    private double rotate( final double _theta, final double _delta ) {
        long turns = PolarPosition.getTurns( _theta );
        double result = _theta + _delta;
        long newTurns = PolarPosition.getTurns( result );
        return result - newTurns * Math.PI * 2;
    }


    private void checkNoop() {
        noop = translation.isCenter() && (rotation == 0) && (scale == 1) && !inverted && !reflection;
    }


    public Position getTranslation() {
        return translation;
    }


    public void setTranslation( final Position _translation ) {
        translation = _translation;
        checkNoop();
    }


    public double getRotation() {
        return rotation;
    }


    public void setRotation( final double _rotation ) {
        rotation = _rotation;
        checkNoop();
    }


    public double getScale() {
        return scale;
    }


    public void setScale( final double _scale ) {
        scale = _scale;
        checkNoop();
    }


    public boolean isInverted() {
        return inverted;
    }


    public void setInverted( final boolean _inverted ) {
        inverted = _inverted;
        checkNoop();
    }


    public double getReflectionAxis() {
        return reflectionAxis;
    }


    public void setReflectionAxis( final double _reflectionAxis ) {
        reflectionAxis = _reflectionAxis;
    }


    public boolean isReflection() {
        return reflection;
    }


    public void setReflection( final boolean _reflection ) {
        reflection = _reflection;
        checkNoop();
    }


    public boolean isAutoTranslate() {
        return autoTranslate;
    }


    public void setAutoTranslate( final boolean _autoTranslate ) {
        autoTranslate = _autoTranslate;
    }
}
