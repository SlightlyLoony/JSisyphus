package com.slightlyloony.jsisyphus.positions;

import com.slightlyloony.jsisyphus.Utils;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class CartesianPosition extends APosition implements Position {


    public CartesianPosition( final double _x, final double _y, final int _turns ) {
        super( getRho( _x, _y ), getTheta( _x, _y, _turns ), _x, _y, _turns );
    }


    private static double getRho( final double _x, final double _y ) {
        return Math.hypot( _x, _y );
    }


    private static double getTheta( final double _x, final double _y, final long _turns ) {

        // compute the angle for all positive _y...
        double theta = Utils.getTheta( _x, _y );

        // correct it for the number of turns...
        theta += _turns * Math.PI * 2;

        return theta;
    }
}
