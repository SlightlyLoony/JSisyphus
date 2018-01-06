package com.slightlyloony.jsisyphus.positions;

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
    protected final long turns;


    public APosition( final double _rho, final double _theta, final double _x, final double _y, final long _turns ) {
        rho = _rho;
        theta = _theta;
        x = _x;
        y = _y;
        turns = _turns;
    }


    public boolean isCenter() {
        return (x == 0) && (y == 0);
    }


    public Position add( final Position _addend ) {
        return new CartesianPosition( x + _addend.getX(), y + _addend.getY(), turns + _addend.getTurns() );
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


    public long getTurns() {
        return turns;
    }


    public String toString() {
        return "Position: (x,y): (" +
                x +
                "," +
                y +
                ")  (rho,theta): (" +
                rho +
                "," +
                Math.toDegrees( theta ) +
                ")";
    }


    public String toVertice() {
        return theta + " " + Math.max( 0, Math.min( 1, rho ) );
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
