package com.slightlyloony.jsisyphus.positions;

/**
 * Instances of this class represent positions on the Sisyphus table as specified in polar form (rho, theta).
 *
 * Instances of this class are immutable and threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class PolarPosition extends APosition implements Position {


    public PolarPosition( final double _rho, final double _theta ) {
        super( _rho, _theta, getX( _rho, _theta ), getY( _rho, _theta ), getTurns( _theta ) );
    }


    private static double getX( final double _rho, final double _theta ) {
        return _rho * Math.sin( _theta );
    }


    private static double getY( final double _rho, final double _theta ) {
        return _rho * Math.cos( _theta );
    }


    private static long getTurns( final double _theta ) {
        return (long) ((_theta >= 0) ? Math.floor( _theta / Math.toRadians( 360 ) ) : Math.ceil( _theta / Math.toRadians( 360 ) ));
    }
}
