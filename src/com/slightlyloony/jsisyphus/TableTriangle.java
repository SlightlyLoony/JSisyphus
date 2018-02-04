package com.slightlyloony.jsisyphus;

/**
 * Instances of this class represent a triangle on the Sisyphus table.  It extends the {@link Triangle} class to add a point for each vertice, as well as
 * methods for getting information from the triangle.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class TableTriangle extends Triangle {

    public final Point verticeA;
    public final Point verticeB;
    public final Point verticeC;


    private TableTriangle( final Point _verticeA, final Point _verticeB, final Point _verticeC, final Triangle _triangle ) {
        super( _triangle.sideA, _triangle.sideB, _triangle.sideC, _triangle.angleA, _triangle.angleB, _triangle.angleC  );
        verticeA = _verticeA;
        verticeB = _verticeB;
        verticeC = _verticeC;
    }


    /**
     * Returns a new instance of this class that has been moved (without rotation or scaling) so that vertice A is at the given point.
     *
     * @param _newA the new location for vertice A.
     * @return the moved instance.
     */
    public TableTriangle moveAto( final Point _newA ) {
        return moveTo( verticeA.vectorTo( _newA ) );
    }


    /**
     * Returns a new instance of this class that has been moved (without rotation or scaling) so that vertice B is at the given point.
     *
     * @param _newB the new location for vertice B.
     * @return the moved instance.
     */
    public TableTriangle moveBto( final Point _newB ) {
        return moveTo( verticeB.vectorTo( _newB ) );
    }


    /**
     * Returns a new instance of this class that has been moved (without rotation or scaling) so that vertice C is at the given point.
     *
     * @param _newC the new location for vertice C.
     * @return the moved instance.
     */
    public TableTriangle moveCto( final Point _newC ) {
        return moveTo( verticeC.vectorTo( _newC ) );
    }


    /**
     * Returns a new instance of this class that is moved from this instance by the given vector.
     *
     * @param _vector the vector by which to move this instance.
     * @return the new instance, moved from this instance by the given vector.
     */
    public TableTriangle moveTo( final Point _vector ) {
        Point newVerticeA = verticeA.sum( _vector );
        Point newVerticeB = verticeB.sum( _vector );
        Point newVerticeC = verticeC.sum( _vector );

        return TableTriangle.fromVVV( newVerticeA, newVerticeB, newVerticeC );
    }


    /**
     * Returns a new instance of this class that has been scaled by the given scale factor, anchored to vertice A (that is, vertice A does not move as a
     * result of the scaling).  Vertices B and C will be moved such that the sides of the triangle are the scaled length, and no angles are changed.
     *
     * @param _scaleFactor the factor by which to scale the sides of this triangle.
     * @return the scaled new instance of this class.
     */
    public TableTriangle scaleToA( final double _scaleFactor ) {
        Point newVerticeB = scaleVertice( verticeA, verticeB, _scaleFactor );
        Point newVerticeC = scaleVertice( verticeA, verticeC, _scaleFactor );
        return fromVVV( verticeA, newVerticeB, newVerticeC );
    }


    /**
     * Returns a new instance of this class that has been scaled by the given scale factor, anchored to vertice A (that is, vertice A does not move as a
     * result of the scaling).  Vertices B and C will be moved such that the sides of the triangle are the scaled length, and no angles are changed.
     *
     * @param _scaleFactor the factor by which to scale the sides of this triangle.
     * @return the scaled new instance of this class.
     */
    public TableTriangle scaleToB( final double _scaleFactor ) {
        Point newVerticeA = scaleVertice( verticeB, verticeA, _scaleFactor );
        Point newVerticeC = scaleVertice( verticeB, verticeC, _scaleFactor );
        return fromVVV( newVerticeA, verticeB, newVerticeC );
    }


    /**
     * Returns a new instance of this class that has been scaled by the given scale factor, anchored to vertice A (that is, vertice A does not move as a
     * result of the scaling).  Vertices B and C will be moved such that the sides of the triangle are the scaled length, and no angles are changed.
     *
     * @param _scaleFactor the factor by which to scale the sides of this triangle.
     * @return the scaled new instance of this class.
     */
    public TableTriangle scaleToC( final double _scaleFactor ) {
        Point newVerticeB = scaleVertice( verticeC, verticeB, _scaleFactor );
        Point newVerticeA = scaleVertice( verticeC, verticeA, _scaleFactor );
        return fromVVV( newVerticeA, newVerticeB, verticeC );
    }


    /**
     * Scale the given old vertice to the given anchor by the given scale factor.
     *
     * @param _anchor the vertice that doesn't move.
     * @param _old a vertice that is going to move.
     * @param _scaleFactor the scale factor.
     * @return the new, moved vertice.
     */
    private Point scaleVertice( final Point _anchor, final Point _old, final double _scaleFactor ) {
        Point vectorTo = _anchor.vectorTo( _old );
        return _anchor.sum( Point.fromXY( vectorTo.x * _scaleFactor, vectorTo.y * _scaleFactor ) );
    }


    /**
     * Returns the angle (in table coordinates) from vertice A to vertice B.
     *
     * @return the angle from vertice A to vertice B.
     */
    public double angleAB() {
        return verticeA.thetaTo( verticeB );
    }


    /**
     * Returns the angle (in table coordinates) from vertice A to vertice c.
     *
     * @return the angle from vertice A to vertice C.
     */
    public double angleAC() {
        return verticeA.thetaTo( verticeC );
    }


    /**
     * Returns the angle (in table coordinates) from vertice B to vertice A.
     *
     * @return the angle from vertice B to vertice A.
     */
    public double angleBA() {
        return verticeB.thetaTo( verticeA );
    }


    /**
     * Returns the angle (in table coordinates) from vertice B to vertice C.
     *
     * @return the angle from vertice B to vertice C.
     */
    public double angleBC() {
        return verticeB.thetaTo( verticeC );
    }


    /**
     * Returns the angle (in table coordinates) from vertice C to vertice A.
     *
     * @return the angle from vertice C to vertice A.
     */
    public double angleCA() {
        return verticeC.thetaTo( verticeA );
    }


    /**
     * Returns the angle (in table coordinates) from vertice C to vertice B.
     *
     * @return the angle from vertice C to vertice B.
     */
    public double angleCB() {
        return verticeC.thetaTo( verticeB );
    }


    /**
     * Returns a new instance of this class with the given vertices (VVV).
     *
     * @param _verticeA one vertice of the triangle.
     * @param _verticeB another vertice of the triangle.
     * @param _verticeC another vertice of the triangle.
     * @return a new instance of this class with the given vertices (VVV).
     */
    public static TableTriangle fromVVV( final Point _verticeA, final Point _verticeB, final Point _verticeC ) {

        // compute our side lengths from the vertices...
        double sideA = _verticeB.distanceFrom( _verticeC );
        double sideB = _verticeA.distanceFrom( _verticeC );
        double sideC = _verticeA.distanceFrom( _verticeB );

        // get our triangle...
        Triangle triangle = Triangle.fromSSS( sideA, sideB, sideC );

        return new TableTriangle( _verticeA, _verticeB, _verticeC, triangle );
    }


    /**
     * Returns a new instance of this class from the given two vertices, a given side, and a given included angle.  The two vertices define the endpoints
     * of the triangle's side C.  The given angle B is the angle included between sides A and C.  If the included angle is positive, it describes an angle
     * clockwise from side A to side C, and the opposite if it is negative.
     *
     * @param _verticeA a given vertice.
     * @param _verticeB a second given vertice, defining the position and length of side C.
     * @param _angleB the included angle, side A to side C.
     * @param _sideA the length of side A.
     * @return a new instance of this class calculated from the given information.
     */
    public static TableTriangle fromVVAS( final Point _verticeA, final Point _verticeB, final double _angleB, final double _sideA ) {

        // first we calculate the length of side C...
        double sideC = _verticeA.distanceFrom( _verticeB );

        // then we calculate our abstract triangle...
        Triangle triangle = Triangle.fromSAS( _sideA, Math.abs( _angleB ), sideC );

        // finally, we compute the location of vertice C...
        double baTheta = _verticeB.thetaTo( _verticeA );
        double bcTheta = baTheta + _angleB;
        Point verticeC = _verticeB.sum( Point.fromRT( _sideA, bcTheta ) );

        return new TableTriangle( _verticeA, _verticeB, verticeC, triangle );
    }


    public static void main( String[] args ) {


        TableTriangle tt1, tt2;
        tt1 = TableTriangle.fromVVAS( Point.fromXY( 0, 1 ), Point.fromXY( 1, 1 ), 1, 1 );
        tt2 = TableTriangle.fromVVAS( Point.fromXY( 0, 1 ), Point.fromXY( 1, 1 ), -1, 1 );

        tt1.hashCode();
    }
}
