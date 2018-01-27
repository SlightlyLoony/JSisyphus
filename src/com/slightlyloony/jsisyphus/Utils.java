package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.positions.Position;

/**
 * Container class for utility methods.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Utils {


    /**
     * Returns the difference (b - a) between the two given angles ([-pi..pi]) as a result ([-pi..pi]).
     * @param _a the first angle.
     * @param _b the second angle.
     * @return the difference between the two angles.
     */
    public static double deltaTheta( final double _a, final double _b ) {
        double dif = _b - _a;
        return (dif < -Math.PI ) ? dif + Math.PI * 2 : ((dif > Math.PI) ? dif - Math.PI * 2 : dif);
    }


    /**
     * Returns 1 if the given number is positive, -1 if it is negative.
     *
     * @param _a the number to test.
     * @return if the given number is positive, 1; otherwise, -1.
     */
    public static int sign( final double _a ) {
        return (_a < 0 ) ? -1 : 1;
    }


    /**
     * Returns the angle [-pi..pi] represented by the given (dx,dy) pair.
     *
     * @param _dx the delta x.
     * @param _dy the delta y.
     * @return The angle [-pi..pi] represented by the given (dx,dy) pair.
     */
    public static double getTheta( final double _dx, final double _dy ) {

        if( (_dx == 0) && (_dy == 0) ) return 0;  // handle the special case of zero dx and dy...

        // compute the angle for all positive dy...
        double theta = Math.asin( _dx / Math.hypot( _dx, _dy ) );

        // correct the angle if _dy is actually negative...
        return ( _dy < 0 ) ? sign( theta ) * Math.PI - theta : theta;
    }


    /**
     * Returns the normalized form of the given angle, where "normalized" means between -pi and +pi.
     *
     * @param _theta  the angle to be normalized
     * @return the normalized angle
     */
    public static double normalizeTheta( final double _theta ) {
        return _theta - getTurnsFromTheta( _theta ) * Math.PI * 2;
    }


    /**
     * Returns the number of turns in the given angle.
     *
     * @param _theta the angle (in radians) to calculate number of turns from.
     * @return the number of turns in the given angle.
     */
    public static int getTurnsFromTheta( final double _theta ) {
        int turns = (1 + (int) Math.floor( Math.abs( _theta ) / Math.PI )) >> 1;
        return (_theta < 0) ? -turns : turns;
    }


    /**
     * The given start and end points define a straight line.  This method returns the distance to the point on that line closest to the given point.
     *
     * @param _start start point of the straight line.
     * @param _end end point of the straight line.
     * @param _point a point (presumably off the line).
     * @return the distance to the point on the line closest to the given point.
     */
    public static double distance( final Position _start, final Position _end, final Position _point ) {

        // special case if our line is a point...
        if( _start.equals( _end ) ) {
            return Math.hypot( _point.getX() - _start.getX(), _point.getY() - _start.getY() );
        }

        // get coefficients for the line...
        double dy = _end.getY() - _start.getY();
        double dx = _end.getX() - _start.getX();

        // if dx == 0 (vertical line), we have a special case...
        if( dx == 0 ) return Math.abs( _point.getX() - _start.getX() );

        // if dy == 0 (horizontal line, we have a special case...
        if( dy == 0 ) return Math.abs( _point.getY() - _start.getY() );

        // otherwise, we carry on...
        double ml = dy / dx;
        double bl = _start.getY() - ml * _start.getX();

        // get coefficients for the intercept...
        double mi = -1 / ml;
        double bi = _point.getY() - mi * _point.getX();

        // find the x of the intercept...
        double xi = (bi - bl) / ( ml - mi);

        // clamp it to our line's endpoints...
        if( dx >= 0 )
            xi = Math.min( Math.max( xi, _start.getX() ), _end.getX() );
        else
            xi = Math.max( Math.min( xi, _start.getX() ), _end.getX() );

        // get the y intercept...
        double yi = ml * xi + bl;

        // calculate the distance...
        return Math.hypot( _point.getX() - xi, _point.getY() - yi );
    }


    /**
     * Returns a "pretty string" (such as "15th", "22nd", etc.) for the given integer (such as 15, 22, etc.).
     *
     * @param _number the number to get a pretty string for.
     * @return the pretty string.
     */
    public static String prettyIteration( final int _number ) {

        int lastDigit = _number % 10;
        switch( lastDigit ) {
            case 1: return _number + "st";
            case 2: return _number + "nd";
            case 3: return _number + "rd";
            default: return _number + "th";
        }
    }


    /**
     * Logs the given string to the system console.  This method may be modified for a different logging destination.
     *
     * @param _msg the message to log.
     */
    public static void log( final String _msg ) {
        System.out.println( _msg );
    }
}
