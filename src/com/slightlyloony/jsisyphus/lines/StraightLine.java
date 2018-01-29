package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.Delta;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a straight line.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class StraightLine extends ALine implements Line {


    public StraightLine( final double _maxPointDistance, final double _x, final double _y ) {
        super( _maxPointDistance, getDeltas( _maxPointDistance, _x, _y ) );
    }

    /* package */ static List<Delta> getDeltas( final double _maxPointDistance, final double _x, final double _y ) {

        // some setup...
        double lineLength = Math.hypot( _x, _y );
        int numDeltas = (int) Math.ceil( lineLength / _maxPointDistance );
        List<Delta> deltas = new ArrayList<>( numDeltas );

        // now the actual delta generation...
        double lx = 0;
        double ly = 0;
        for( int d = 1; d <= numDeltas; d++ ) {

            // we calculate the fractional deltas and then a difference from the last point so as to avoid cumulative error from the simpler
            // approach of calculating each point as a delta from the last...
            double frac = 1.0 * d / numDeltas;
            double nx = frac * _x;
            double ny = frac * _y;
            deltas.add( new Delta( nx - lx, ny - ly ) );
            lx = nx;
            ly = ny;
        }

        return deltas;
    }
}
