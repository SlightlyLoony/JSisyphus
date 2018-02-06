package com.slightlyloony.jsisyphus;

/**
 * Enumerates the four possible Cartesian quadrants.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public enum CartesianQuadrant {

    PlusXPlusY, PlusXMinusY, MinusXMinusY, MinusXPlusY;

    public static CartesianQuadrant get( final double _x, final double _y ) {

        return (_x >= 0 ) ? ((_y >= 0) ? PlusXPlusY : PlusXMinusY ) : ((_y >= 0) ? MinusXPlusY : MinusXMinusY );
    }


    public static CartesianQuadrant get( final Point _point ) {
        return get( _point.x, _point.y );
    }
}
