package com.slightlyloony.jsisyphus.positions;

/**
 * Instances of classes implementing this interface hold a Sisyphus table position in two equivalent forms.  First, as a distance from the table's center,
 * normalized to 1.0 (rho) and as an angle clockwise from the zero point (theta).  Second, as Cartesian coordinates where the origin is the center of the
 * table, with a size in both coordinates (x, y) normalized to 1.  A theta of zero corresponds to the positive Y axis, so (x,y) coordinates of (0,1) = 0
 * degrees, 1 distance, (1,0) = 90 degrees, 1 distance, (0,-1) = 180 degrees, 1 distance, and (-1,0) = 270 degrees, 1 distance.
 *
 * There is an extra property for the Cartesian representation that allows full equivalence to positions specified in polar form.  This property ("turns")
 * represents how many complete revolutions (positive for clockwise, negative for counter-clockwise) should be added to the Cartesian form to make a complete
 * polar representation.  For instance, consider a polar form specification of (1.0, 450º).  In the usual Cartesian form (1, 0) this value could not be
 * converted back to the given polar form - instead it would convert to the (normally) equivalent (1.0, 90º).  On the Sisyphus table, however, those two
 * polar values are <i>not</i> equivalent - they might result in motion in the wrong direction.  The "turns" property solves this problem.  With it, the
 * polar (1.0, 450º) is equivalent to the Cartesian form (1, 0, 1) (for x, y, turns) - and that <i>is</i> convertible back to the given polar form.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Position {

    Position CENTER = new CartesianPosition( 0, 0, 0 );

    double getRho();
    double getTheta();
    double getX();
    double getY();
    int getTurns();
    boolean isCenter();
    String toVertice();

    double deltaX( final Position _from );
    double deltaY( final Position _from );
    double angleFrom( final Position _from );
    double distanceFrom( final Position _from );

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
    Position fromDeltaXY( final double _dX, final double _dY );
}
