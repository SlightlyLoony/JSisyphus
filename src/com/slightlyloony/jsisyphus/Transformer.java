package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.positions.CartesianPosition;
import com.slightlyloony.jsisyphus.positions.Position;

/**
 * Instances of this class transform lines through any combination of translation or rotation.  The order of transformation operations is
 * rotation then translation.  Note that the only operation that moves the center is translation, and it is the last
 * operation performed.  The order of the remaining operations doesn't actually make any difference; they all involve operations about the origin (0, 0)
 * point.  Note this means that the lines being transformed are presumed to be referenced to the origin.
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
    private double rotation = 0;
    private boolean noop = true;


    public Transformer() {
    }


    public Position transform( final Position _pos ) {

        if( noop ) return _pos;

        double x = _pos.getX();
        double y = _pos.getY();

        // rotate...
        double r = Math.hypot( x, y );
        double t = Utils.getTheta( x, y ) + rotation;
        x = r * Math.sin( t );
        y = r * Math.cos( t );

        // translate...
        x += translation.getX();
        y += translation.getY();

        return new CartesianPosition( x, y, 0 );
    }


    public Position untransform( final Position _pos ) {

        if( noop ) return _pos;

        double x = _pos.getX();
        double y = _pos.getY();

        // translate...
        x -= translation.getX();
        y -= translation.getY();

        // rotate...
        double r = Math.hypot( x, y );
        double t = Utils.getTheta( x, y ) - rotation;
        x = r * Math.sin( t );
        y = r * Math.cos( t );

        return new CartesianPosition( x, y, 0 );
    }


    private void checkNoop() {
        noop = translation.isCenter() && (rotation == 0);
    }


    public Position getTranslation() {
        return translation;
    }


    public void setTranslation( final Position _translation ) {
        // ensure that the translation is always on turn 0...
        translation = new CartesianPosition( _translation.getX(), _translation.getY(), 0 );
        checkNoop();
    }


    public double getRotation() {
        return rotation;
    }


    public void setRotation( final double _rotation ) {
        rotation = _rotation;
        checkNoop();
    }
}
