package com.slightlyloony.jsisyphus.shapes;

import com.slightlyloony.jsisyphus.DrawingContext;
import com.slightlyloony.jsisyphus.Point;

import java.util.HashMap;
import java.util.Map;

/**
 * The base class for all shape definitions.  All shape definitions must include, at a minimum, a set of named points and a method to draw the shape using
 * those points.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class AShapeDef {

    protected final Map<String, Point> points = new HashMap<>();
    protected final Map<String, Point> vectors = new HashMap<>();
    protected final Map<String, Double> angles = new HashMap<>();
    protected final Map<String, Double> scalars = new HashMap<>();
    protected final DrawingContext dc;


    /**
     * Constructs a new instance of this class for use in the given {@link DrawingContext}.
     *
     * @param _dc the drawing context.
     */
    protected AShapeDef( final DrawingContext _dc ) {
        dc = _dc;
    }


    /**
     * Returns the point with the given name, or <i>null</i> if no such point exists.
     *
     * @param _pointName the name of the point to retrieve.
     * @return the point retrieved.
     */
    public Point getPoint( final String _pointName ) {
        return points.get( _pointName );
    }


    /**
     * Returns the vector with the given name, or <i>null</i> if no such vector exists.
     *
     * @param _vectorName the name of the vector to retrieve.
     * @return the vector retrieved.
     */
    public Point getVector( final String _vectorName ) {
        return vectors.get( _vectorName );
    }


    /**
     * Returns the angle with the given name, or <i>null</i> if no such angle exists.
     *
     * @param _angleName the name of the angle to retrieve.
     * @return the angle retrieved.
     */
    public Double getAngle( final String _angleName ) {
        return angles.get( _angleName );
    }


    /**
     * Returns the scalar with the given name, or <i>null</i> if no such scalar exists.
     *
     * @param _scalarName the name of the scalar to retrieve.
     * @return the scalar retrieved.
     */
    public Double getScalar( final String _scalarName ) {
        return scalars.get( _scalarName );
    }


    public abstract void draw( final String _anchorPoint, final double _scaleFactor, final double _rotation );
}
