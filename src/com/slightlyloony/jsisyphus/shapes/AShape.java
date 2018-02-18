package com.slightlyloony.jsisyphus.shapes;

import com.slightlyloony.jsisyphus.DrawingContext;
import com.slightlyloony.jsisyphus.Point;

import java.util.HashMap;
import java.util.Map;

/**
 * Instances of this class define a shape on the Sisyphus table with a set of named points relative to a specified anchor point, which is at the current
 * position on the table.  Instances of this class are used by an associated shape definition class, which knows how to draw the shape using these points.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class AShape {

    protected final Map<String, Point>  points  = new HashMap<>();
    protected final Map<String, Point>  vectors = new HashMap<>();
    protected final Map<String, Double> angles;
    protected final Map<String, Double> scalars = new HashMap<>();
    protected String anchor;
    protected DrawingContext dc;


    protected AShape( final AShapeDef _def, final double _scaleFactor, final double _rotation, final String _anchor ) {
        anchor = _anchor;
        dc = _def.dc;

        // get our anchor...
        Point anchor = _def.points.get( _anchor );

        // build our points by referencing and scaling and rotating all the definition's points...
        Map<String,Point> result = new HashMap<>( 3 * _def.points.size() );
        for( final Map.Entry<String, Point> entry : _def.points.entrySet() ) {
            Point scaled = anchor.vectorTo( entry.getValue() ).scale( _scaleFactor );
            Point rotated = scaled.rotate( _rotation );
            points.put( entry.getKey(), dc.getCurrentRelativePosition().sum( rotated ) );
        }

        // build our vectors by scaling and rotating all the definition's vectors...
        for( final Map.Entry<String, Point> entry : _def.vectors.entrySet() ) {
            vectors.put( entry.getKey(), entry.getValue().scale( _scaleFactor ).rotate( _rotation ) );
        }

        // build our scalars by scaling all the definition's scalars...
        for( final Map.Entry<String, Double> entry : _def.scalars.entrySet() ) {
            scalars.put( entry.getKey(), entry.getValue() * _scaleFactor );
        }

        // we just copy the angles, as there's nothing to scale...
        angles = _def.angles;
    }


    public Point use( final String _pointName ) {
        return points.get( _pointName );
    }


    public Point to( final String _pointName ) {
        return dc.getCurrentRelativePosition().vectorTo( points.get( _pointName ) );
    }


    public Point cp( final String _vectorName ) {
        return vectors.get( _vectorName );
    }


    public double scalar( final String _scalarName ) {
        return scalars.get( _scalarName );
    }


    public double angle( final String _angleName ) {
        return angles.get( _angleName );
    }

}
