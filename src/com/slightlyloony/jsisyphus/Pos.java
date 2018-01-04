package com.slightlyloony.jsisyphus;

/**
 * Instances of this class hold a Sisyphus table position as an angle clockwise from the zero point, and range from the center, normalized to 1 at the outside
 * edge of the table.  This class provides convenience methods to convert to and from Cartesian coordinates, where (0,1) = 0 degrees, 1 distance, (1,0) =
 * 90 degrees, 1 distance, (0,-1) = 180 degrees, 1 distance, and (-1,0) = 270 degrees, 1 distance.
 *
 * This class also provides convenience methods for producing new instances from an existing instance added to an angle, keeping track of how many complete
 * turns around the table have been made.  This allows computations that produce the correct direction of motion on the Sisyphus table.
 *
 * Instances of this class are immutable and threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Pos {

    private final double angle;
    private final double range;
    private final int turns;


    public Pos( final double _angle, final double _range ) {

        // first we normalize the angle to +/- 360 and a number of turns...
        if( _angle >= Math.toRadians( 360 ) ) {
            turns = (int) Math.floor( _angle / Math.toRadians( 360 ) );
            angle = _angle - turns * Math.toRadians( 360 );
        }
        else if( _angle <= Math.toRadians( -360 ) ) {
            turns = (int) Math.ceil( _angle / Math.toRadians( 360 ) );
            angle = _angle - turns * Math.toRadians( 360 );
        }
        else {
            turns = 0;
            angle = _angle;
        }
        range = _range;
    }


    public static Pos fromXY( final double _x, final double _y ) {
        return fromXY( _x, _y, 0 );
    }


    public static Pos fromXY( final double _x, final double _y, final int _turns ) {

        double range = Math.sqrt( _x * _x + _y * _y );

        double angle = Math.asin( _x / range );
        if( _y < 0 ) {
            angle = Math.toRadians( 180 ) - angle;
        }
        else if( _x < 0 ) {
            angle = Math.toRadians( 360 ) + angle;
        }
        angle += _turns * Math.toRadians( 360 );

        return new Pos( angle, range );
    }


    public Pos add( double _deltaAngle, double _deltaRange ) {
        return new Pos( getAngle() + _deltaAngle, range + _deltaRange );
    }


    public double getX() {
        return range * Math.sin( angle );
    }


    public double getY() {
        return range * Math.cos( angle );
    }


    public int getTurns() {
        return turns;
    }


    public double getAngle() {
        return angle + turns * Math.toRadians( 360 );
    }


    public double getRange() {
        return range;
    }


    public String toString() {
        return Math.toDegrees( getAngle() ) + " degrees, " + range + " distance";
    }


    public String toVertice() {
        return getAngle() + " " + Math.max( 0, Math.min( 1, range ) );
    }


    @Override
    public boolean equals( final Object _o ) {
        if( this == _o ) return true;
        if( _o == null || getClass() != _o.getClass() ) return false;

        Pos pos = (Pos) _o;

        return Double.compare( pos.angle, angle ) == 0 && Double.compare( pos.range, range ) == 0;
    }


    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits( angle );
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits( range );
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
