package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.lines.ArbitraryLine;
import com.slightlyloony.jsisyphus.lines.Line;
import com.slightlyloony.jsisyphus.positions.CartesianPosition;
import com.slightlyloony.jsisyphus.positions.PolarPosition;
import com.slightlyloony.jsisyphus.positions.Position;

import java.util.ArrayList;
import java.util.List;

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
    private boolean autoTranslate = false;
    private boolean noop = true;


    public Transformer() {
    }


    public Line transform( final DrawingContext _dc, final Line _line ) {

        // if we have no transformations, return with our argument unchanged...
        if( noop ) return _line;

        // iterate over all the points in our argument (could be in either direction)...
        int sp = _line.getPoints().size() - 1;
        List<Position> points = new ArrayList<>( _line.getPoints().size() );  // the place for our result...
        for( int p = 0; p <= sp; p++ ) {

            // transform the point...
            Position pos = transform( _line.getPoints().get( p ) );

            // stuff the result...
            points.add( pos );
        }

        if( autoTranslate )
            translation = points.get( points.size() - 1 );

        return new ArbitraryLine( _dc, points );
    }


    public Position transform( Position _pos ) {

        if( noop ) return _pos;

        // rotate...
        if( rotation != 0 ) {
            _pos = new PolarPosition( _pos.getRho(), _pos.getTheta() + rotation );
        }

        // translate...
        if( !translation.isCenter() ) {
            _pos = new CartesianPosition(
                    _pos.getX() + translation.getX(),
                    _pos.getY() + translation.getY(),
                    _pos.getTurns() + translation.getTurns() );
        }
        return _pos;
    }


    public Position untransform( Position _pos ) {

        if( noop ) return _pos;

        // rotate...
        if( rotation != 0 ) {
            _pos = new PolarPosition( _pos.getRho(), _pos.getTheta() - rotation );
        }

        // translate...
        if( !translation.isCenter() ) {
            _pos = new CartesianPosition(
                    _pos.getX() - translation.getX(),
                    _pos.getY() - translation.getY(),
                    _pos.getTurns() - translation.getTurns() );
        }
        return _pos;
    }


    private void checkNoop() {
        noop = translation.isCenter() && (rotation == 0);
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


    public boolean isAutoTranslate() {
        return autoTranslate;
    }


    public void setAutoTranslate( final boolean _autoTranslate ) {
        autoTranslate = _autoTranslate;
    }
}
