package com.slightlyloony.jsisyphus.positions;

import static com.slightlyloony.jsisyphus.CartesianQuadrant.*;

/**
 * The base class for all positions.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class APosition implements Position {

    protected final double rho;
    protected final double theta;
    protected final double x;
    protected final double y;
    protected final int turns;


    public APosition( final double _rho, final double _theta, final double _x, final double _y, final int _turns ) {
        rho = _rho;
        theta = _theta;
        x = _x;
        y = _y;
        turns = _turns;
    }


    public boolean isCenter() {
        return (x == 0) && (y == 0);
    }


    public double getRho() {
        return rho;
    }


    public double getTheta() {
        return theta;
    }


    public double getX() {
        return x;
    }


    public double getY() {
        return y;
    }


    public int getTurns() {
        return turns;
    }


    @Override
    public double deltaX( final Position _from ) {
        return x - _from.getX();
    }


    @Override
    public double deltaY( final Position _from ) {
        return y - _from.getY();
    }


    @Override
    public double angleFrom( final Position _from ) {
        return getTheta( deltaX( _from ), deltaY( _from ) );
    }


    @Override
    public double distanceFrom( final Position _from ) {
        return Math.hypot( x - _from.getX(), y - _from.getY() );
    }


    /**
     * Returns a new instance implementing this interface that is located at the given dX, dY from this instance.  This method assumes that the new instance
     * is relative to the current instance, with polar angles and Cartesian turns set properly.  For instance, if this instance was at theta 175 degrees,
     * and the new instance is 15 degrees clockwise from it, the new instance will have a theta of 190 degrees (not -170 degrees), and a turn count that
     * is one greater than that of this instance.
     *
     * @param _dX the delta x to add to this instance's position.
     * @param _dY the delta y to add to this instance's position.
     * @return the new instance.
     */
    @Override
    public Position fromDeltaXY( final double _dX, final double _dY ) {

        // first the easy part - compute our new XY position...
        double newX = x + _dX;
        double newY = y + _dY;

        // figure out our turns...
        int newTurns = turns;
        if( (get( x, y ) == PlusXMinusY)  && (get( newX, newY ) == MinusXMinusY) ) newTurns++;
        if( (get( x, y ) == MinusXMinusY) && (get( newX, newY ) == PlusXMinusY) )  newTurns--;

        return new CartesianPosition( newX, newY, newTurns );
    }


    // returns the angle, [-pi..pi], represented by the given delta x and delta y values...
    protected static double getTheta( final double _dx, final double _dy ) {

        // handle the special case of no distance...
        if( (_dx == 0) && (_dy == 0) ) return 0;

        // compute the angle for all positive _dy...
        double theta = Math.asin( _dx / Math.hypot( _dx, _dy ) );

        // correct the angle if _dy is actually negative...
        if( _dy < 0 ) theta = Math.signum( theta) * Math.PI - theta;

        return theta;
    }


    public String toString() {
        return "Position: (x,y,t): (" +
                x +
                "," +
                y +
                "," +
                turns +
                ")  (rho,theta): (" +
                rho +
                "," +
                theta +
                " radians, " +
                Math.toDegrees( theta ) +
                " degrees)";
    }


    public String toVertice() {
        return theta + " " + rho + "\n";
    }


    @Override
    public boolean equals( final Object _o ) {
        if( this == _o ) return true;
        if( _o == null || getClass() != _o.getClass() ) return false;

        APosition aPosition = (APosition) _o;

        if( Double.compare( aPosition.rho, rho ) != 0 ) return false;
        if( Double.compare( aPosition.theta, theta ) != 0 ) return false;
        if( Double.compare( aPosition.x, x ) != 0 ) return false;
        if( Double.compare( aPosition.y, y ) != 0 ) return false;
        return turns == aPosition.turns;
    }


    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits( rho );
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits( theta );
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits( x );
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits( y );
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (turns ^ (turns >>> 32));
        return result;
    }
}
