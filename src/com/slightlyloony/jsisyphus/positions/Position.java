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


    double getRho();
    double getTheta();
    double getX();
    double getY();
    long getTurns();
    boolean isCenter();
    Position add( final Position _step );
    String toVertice();
}
