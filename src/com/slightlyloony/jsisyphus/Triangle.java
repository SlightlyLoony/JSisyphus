package com.slightlyloony.jsisyphus;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

// TODO: add sanity checks for angle parameters on factory methods...

/**
 * Instances of this class represent abstract triangles, with the lengths of all three sides, and the interior angles for all three vertices.  The three
 * sides are called A, B, and C.  Angle A is the angle between sides B and C, angle B the angle between sides A and C, and angle C the angle between sides
 * A and B.  Methods are provided to create instances of this class from any sufficient combination of information about the triangle.
 *
 * Instances of this class are immutable and threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Triangle {

    final public double sideA;
    final public double sideB;
    final public double sideC;
    final public double angleA;
    final public double angleB;
    final public double angleC;


    protected Triangle( final double _sideA, final double _sideB, final double _sideC, final double _angleA, final double _angleB, final double _angleC ) {
        sideA = _sideA;
        sideB = _sideB;
        sideC = _sideC;
        angleA = _angleA;
        angleB = _angleB;
        angleC = _angleC;
    }


    /**
     * Returns a new triangle that is a scaled version of this triangle.  The angles will be unchanged, but the lengths of the sides will be multiplied by
     * the given scale factor.
     *
     * @param _scaleFactor the scale factor to scale this triangle by.
     * @return a new instance with the scaled triangle.
     */
    public Triangle scale( final double _scaleFactor ) {
        return fromSSS( sideA * _scaleFactor, sideB * _scaleFactor, sideC * _scaleFactor );
    }


    /**
     * Returns the exterior angle A, which is just 2π - angle A, or 360º - angle A.
     *
     * @return the exterior angle A.
     */
    public double exteriorAngleA() {
        return 2 * PI - angleA;
    }


    /**
     * Returns the exterior angle B, which is just 2π - angle B, or 360º - angle B.
     *
     * @return the exterior angle B.
     */
    public double exteriorAngleB() {
        return 2 * PI - angleB;
    }


    /**
     * Returns the exterior angle C, which is just 2π - angle C, or 360º - angle C.
     *
     * @return the exterior angle C.
     */
    public double exteriorAngleC() {
        return 2 * PI - angleC;
    }


    /**
     * Returns a new instance of this class calculated from the given angles and the side between them (ASA).
     *
     * @param _angleA one known angle.
     * @param _sideB the side adjacent to both given angles.
     * @param _angleC the other known angle.
     * @return a new instance of this class calculated from the given angles and the side between them.
     */
    public static Triangle fromASA( final double _angleA, final double _sideB, final double _angleC ) {

        // first we calculate angle B by simple difference...
        double angleB = PI - (_angleA + _angleC);

        // then we use the law of sines to calculate side A...
        double sideA = _sideB * sin( _angleA ) / sin( angleB );

        // finally, we use the law of sines again to calculate side C...
        double sideC = _sideB * sin( _angleC ) / sin( angleB );

        return new Triangle( sideA, _sideB, sideC, _angleA, angleB, _angleC );
    }


    /**
     * Returns a new instance of this class calculated from the given angles and the side not between them (AAS).
     *
     * @param _angleA one known angle.
     * @param _angleB the other known angle.
     * @param _sideB the side adjacent to the first given angle, but not the second.
     * @return a new instance of this class calculated from the given angles and the side between them.
     */
    public static Triangle fromAAS( final double _angleA, final double _angleB, final double _sideB ) {

        // first we calculate angle C by simple difference...
        double angleC = PI - (_angleA + _angleB);

        // then we treat it as ASA...
        return fromASA( _angleA, _sideB, angleC );
    }


    /**
     * Returns a new instance of this class calculated from the given sides and their included angle (SAS).
     *
     * @param _sideA one known side.
     * @param _angleB the known angle, included between the two known sides.
     * @param _sideC the second known side.
     * @return a new instance of this class calculated from the given sides and their included angle (SAS).
     */
    public static Triangle fromSAS( final double _sideA, final double _angleB, final double _sideC ) {

        // sanity check on the given angle...
        if( _angleB >= PI )
            throw new IllegalArgumentException( "Angle must be less than pi radians (180º)" );

        // first we use the law of cosines to calculate the length of side B...
        double sideB = sqrt( _sideA * _sideA + _sideC * _sideC - 2 * _sideA * _sideC * cos( _angleB ) );

        // next we use the law of cosines to calculate angle A...
        double angleA = acos( (_sideC * _sideC + sideB * sideB - _sideA * _sideA) / (2 * _sideC * sideB) );

        // finally, we calculate angle C by simple difference...
        double angleC = PI - (angleA + _angleB);

        return new Triangle( _sideA, sideB, _sideC, angleA, _angleB, angleC );
    }


    /**
     * Returns a new instance of this class calculated from the given three sides (SSS).
     *
     * @param _sideA one known side.
     * @param _sideB a second known side.
     * @param _sideC the third known side.
     * @return a new instance of this class calculated from the given three sides.
     */
    public static Triangle fromSSS( final double _sideA, final double _sideB, final double _sideC ) {

        // first we use the law of cosines to calculate angle A...
        double angleA = acos( (_sideB * _sideB + _sideC * _sideC - _sideA * _sideA) / (2 * _sideB * _sideC) );

        // then we use the law of cosines again to calculate angle B...
        double angleB = acos( (_sideA * _sideA + _sideC * _sideC - _sideB * _sideB) / (2 * _sideA * _sideC) );

        // finally, we calculate angle C by simple difference...
        double angleC = PI - (angleA + angleB);

        return new Triangle( _sideA, _sideB, _sideC, angleA, angleB, angleC );
    }


    /**
     * Returns a new instance of this class calculated from the given sides and an excluded angle (SSA).  Note that the excluded angle is adjacent to the
     * second given side.  This method may only be used if the second given side is not longer than the first given side, as only in that case is it
     * guaranteed that there will be a single solution.  If the second given side is longer than the first given side there could be 0, 1, or 2 possible
     * solutions, and an exception will be thrown.  To solve SSA triangles where the second given side is possibly longer than the first given side, see
     * the {@link #allFromSSA(double, double, double)} method.
     *
     * @param _sideA one known side.
     * @param _sideB the second known side, not longer than the first known side.
     * @param _angleA the known angle, adjacent to the second known side but not the first.
     * @return a new instance of this class calculated from the given sides and their excluded angle.
     */
    public static Triangle fromSSA( final double _sideA, final double _sideB, final double _angleA ) {

        // sanity check on the given angle...
        if( _angleA >= PI )
            throw new IllegalArgumentException( "Angle must be less than pi radians (180º)" );

        // make sure that we have a single solution...
        if( _sideB > _sideA ) throw new IllegalArgumentException( "Side B must not be longer than side A" );

        // first we use the law of sines calculate angle B...
        double angleB = asin( sin( _angleA ) * _sideB / _sideA );

        // then we calculate angle C by simple difference...
        double angleC = PI - (_angleA + angleB);

        // finally, we use the law of sines to calculate side C...
        double sideC = _sideA * sin( angleC ) / sin( _angleA );

        return new Triangle( _sideA, _sideB, sideC, _angleA, angleB, angleC );
    }


    /**
     * Returns 0, 1, or 2 new instances of this class calculated from the given sides and an excluded angle (SSA).  Note that the excluded angle is adjacent
     * to the second given side.  If the second given side is known to be no longer than the first given side, then see the
     * {@link #fromSSA(double, double, double)}, which will return a single unambiguous solution.
     *
     * @param _sideA one known side.
     * @param _sideB the second known side.
     * @param _angleA the known angle, adjacent to the second known side but not the first.
     * @return a new instance of this class calculated from the given sides and their excluded angle.
     */
    public static List<Triangle> allFromSSA( final double _sideA, final double _sideB, final double _angleA ) {

        List<Triangle> result = new ArrayList<>( 2 );

        // calculate the sine of the other excluded angle, from which we may make some decisions...
        double sineB = sin( _angleA ) * _sideB / _sideA;

        // if sineB greater than 1, then there are no solutions...
        if( sineB > 1 ) return result;

        // then we get angle B...
        double angleB = asin( sineB );

        // then we calculate angle C by simple differnce...
        double angleC = PI - (_angleA + angleB);

        // finally, we use the law of sines to calculate side C...
        double sideC = _sideA * sin( angleC ) / sin( _angleA );

        // stuff this solution into our results...
        result.add( new Triangle( _sideA, _sideB, sideC, _angleA, angleB, angleC ) );

        // if sine B is exactly equal to 1, or if side B <= side A, then there is just one solution...
        if( (sineB == 1) || (_sideB <= _sideA) ) return result;

        // calculate the second possible angle B...
        angleB = PI - angleB;

        // then we calculate angle C by simple differnce...
        angleC = PI - (_angleA + angleB);

        // finally, we use the law of sines to calculate side C...
        sideC = _sideA * sin( angleC ) / sin( _angleA );

        // stuff this second solution into our results...
        result.add( new Triangle( _sideA, _sideB, sideC, _angleA, angleB, angleC ) );

        return result;
    }


    @Override
    public String toString() {
        return "Triangle: " + sideA + ", " + sideB + ", " + sideC;
    }
}
