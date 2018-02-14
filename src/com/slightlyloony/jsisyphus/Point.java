package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.positions.Position;

import static java.lang.Math.*;

/**
 * Instances of this class represent a point in either Cartesian or polar coordinates.  Note that instances may be used <i>either</i> as absolute coordinates
 * of a point on the Sisyphus table, or as relative coordinates.  The Cartesian coordinates are conventional; the x axis is horizontal with positive values
 * to the right of the origin, and the y axis is vertical with positive values above the origin.  The polar coordinates are unconventional.  The zero radial
 * is coincident with the positive y axis, and positive angles increase clockwise from there, negative angles anti-clockwise.<br><br>
 *
 * Instances of this class are immutable and threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Point {

    public final double x;
    public final double y;
    public final double rho;
    public final double theta;


    private Point( final double _x, final double _y, final double _rho, final double _theta ) {
        x = _x;
        y = _y;
        rho = _rho;
        theta = _theta;
    }


    /**
     * Returns the distance from the given point to this point.
     *
     * @param _from the point to measure the distance from.
     * @return the distance from the given point to this point.
     */
    public double distanceFrom( final Point _from ) {
        return hypot( x - _from.x, y - _from.y );
    }


    /**
     * Returns the angle from this point to the given point.
     *
     * @param _to the point to calculate the angle to.
     * @return the angle from this point to the given point.
     */
    public double thetaTo( final Point _to ) {
        return vectorTo( _to ).theta;
    }


    /**
     * Returns a new instance of this class that is the vector sum of this instance and the given instance.
     *
     * @param _a the point to vector sum with this instance.
     * @return a new instance of this class that is the vector sum of this instance and the given instance.
     */
    public Point sum( final Point _a ) {
        return fromXY( x + _a.x, y + _a.y );
    }


    /**
     * Returns a new instance of this class that is the vector from this instance to the given instance.
     *
     * @param _a the point to calculate the vector to, from this point.
     * @return a new instance of this class that is the vector from this instance to the given instance.
     */
    public Point vectorTo( final Point _a ) {
        return fromXY( _a.x - x, _a.y - y );
    }


    public Point invertX() {
        return Point.fromXY( -x, y );
    }


    public Point invertY() {
        return Point.fromXY( x, -y );
    }


    public Point invertXY() {
        return Point.fromXY( -x, -y );
    }


    public Point invertRho() {
        return Point.fromRT( -rho, theta );
    }


    public Point invertTheta() {
        return Point.fromRT( rho, -theta );
    }


    public Point oppositeTheta() {
        return Point.fromRT( rho, theta + PI );
    }


    public Point scale( final double _scaleFactor ) {
        return Point.fromXY( x * _scaleFactor, y * _scaleFactor );
    }


    /**
     * Returns a new instance of this class that is rotated by the given angle from this instance.
     *
     * @param _theta the angle to rotate by.
     * @return the new, rotated instace.
     */
    public Point rotate( final double _theta ) {
        return Point.fromRT( rho, theta + _theta );
    }


    /**
     * Returns a new instance of this class that represents the equivalent absolute coordinates of this instance (presumed to be relative to the current
     * position) in the given drawing context, by rotating this instance to match the drawing context's rotation and computing the vector sum from the
     * current position.
     *
     * @param _dc the drawing context.
     * @return a new instance with the absolute equivalent of the given current position-relative point.
     */
    public Point abs( final DrawingContext _dc ) {
        return fromPosition( _dc.getCurrentPosition() ).sum( rotate( _dc.getCurrentRotation() ) );
    }


    /**
     * Creates a new instance of this class with the same coordinates as the given position.
     *
     * @param _position the position to create a new instance of this class from.
     * @return a new instance of this class with the same coordinates as the given position.
     */
    public static Point fromPosition( final Position _position ) {
        return fromRT( _position.getRho(), _position.getTheta() );
    }


    /**
     * Creates a new instance of this class from the given x and y Cartesian coordinates.
     *
     * @param _x the x coordinate.
     * @param _y the y coordinate.
     * @return a new instance of this class with the given x,y Cartesian coordinates.
     */
    public static Point fromXY( final double _x, final double _y ) {

        // handle special case of 0,0 point...
        if( (_x == 0) && (_y == 0) )
            return new Point( 0, 0, 0, 0 );

        double rho = hypot( _x, _y );
        double theta = asin( _x / rho );


        // correct the angle if _dy is actually negative...
        if( _y < 0 ) theta = Utils.sign( theta ) * Math.PI - theta;

        return new Point( _x, _y, rho, theta );
    }


    /**
     * Creates a new instance of this class from the given rho and theta polar coordinates.
     *
     * @param _rho the rho coordinate.
     * @param _theta the theta coordinate.
     * @return a new instance of this class with the given rho and theta polar coordinates.
     */
    public static Point fromRT( final double _rho, final double _theta ) {

        double x = _rho * sin( _theta );
        double y = _rho * cos( _theta );
        return new Point( x, y, _rho, _theta );
    }


    @Override
    public String toString() {
        return "Point: (x,y): (" + x + "," + y + "), [rho,theta]: [" + rho + "," + theta + "]";
    }
}
