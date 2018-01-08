package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.Common;
import com.slightlyloony.jsisyphus.positions.CartesianPosition;
import com.slightlyloony.jsisyphus.positions.Position;

/**
 * Represents a straight line on the Sisyphus table.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class StraightLine extends ALine implements Line {

    private final double m;
    private final double b;

    public StraightLine( final Position _start, final Position _end ) {
        super( _start, _end );

        // some setup...
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double ppx, ppy;
        int pts;

        // special case if dx = 0...
        if( dx == 0 ) {

            // calculate our line's coefficients...
            m = (dy < 0) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            b = start.getY();

            // setup for point production...
            pts = (int) Math.ceil( Math.abs(dy) / Common.VISUAL_RESOLUTION_SU );
            ppx = 0;
            ppy = dy / pts;
        }
        else {

            // calculate our line's coefficients...
            m = dy / dx;
            b = start.getY() - m * start.getX();

            // setup for point production...
            double l = Math.hypot( dx, dy );
            pts = (int) Math.ceil( l / Common.VISUAL_RESOLUTION_SU );
            double h = l / pts;
            ppx = h * dx / l;
            ppy = h * dy / l;
        }

        // produce our line's points...
        Position current = start;
        for( int i = 0; i < pts; i++ ) {
            points.add( current );
            current = new CartesianPosition( current.getX() + ppx, current.getY() + ppy, current.getTurns() );
        }
        points.add( end );
    }
}
