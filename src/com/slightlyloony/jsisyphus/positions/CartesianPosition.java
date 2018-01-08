package com.slightlyloony.jsisyphus.positions;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class CartesianPosition extends APosition implements Position {


    public CartesianPosition( final double _x, final double _y, final long _turns ) {
        super( getRho( _x, _y ), getTheta( _x, _y, _turns ), _x, _y, _turns );
    }


    private static double getRho( final double _x, final double _y ) {
        return Math.hypot( _x, _y );
    }


    private static double getTheta( final double _x, final double _y, final long _turns ) {

        double theta = Math.asin( _x / Math.hypot( _x, _y ) );

        if( _y < 0 ) {
            theta = Math.signum( theta) * Math.toRadians( 180 ) - theta;
        }
        theta += _turns * Math.toRadians( 360 );

        return theta;
    }
}
