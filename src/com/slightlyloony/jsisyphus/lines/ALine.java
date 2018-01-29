package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.Delta;

import java.util.Collections;
import java.util.List;

/**
 * The base class for all lines.  Two constructors are provided: one for lines with algorithmically generated points, and the other for lines comprised of
 * arbitrary points.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
abstract class ALine implements Line {

    private final List<Delta> deltas;
    private final double maxPointDistance;


    /**
     * Creates a new instance of this class from the given values.
     *
     * @param _maxPointDistance the maximum point distance for the deltas in this line.
     * @param _deltas the deltas representing this line.
     */
    protected ALine( final double _maxPointDistance, final List<Delta> _deltas ) {

        // sanity check...
        if( (_deltas == null ) || (_deltas.size() == 0) || (_maxPointDistance <= 0) )
            throw new IllegalArgumentException( "Missing or invalid parameter(s) for ALine" );

        maxPointDistance = _maxPointDistance;
        deltas = _deltas;
    }


    public List<Delta> getDeltas() {
        return Collections.unmodifiableList( deltas );
    }
}
