package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.DrawingContext;
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
    private final double ppx;
    private final double ppy;
    private final double tv;
    private final boolean xterm;


    public StraightLine( final DrawingContext _dc, final Position _start, final Position _end ) {
        super( _dc, _start, _end );

        // some setup...
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        int pts;
        xterm = (dx != 0);

        // anything other than a vertical line...
        if( xterm ) {

            // calculate our line's coefficients...
            m = dy / dx;
            b = start.getY() - m * start.getX();

            // setup for point production...
            double l = Math.hypot( dx, dy );
            pts = (int) Math.ceil( l / dc.getMaxPointDistance() );
            double h = l / pts;
            ppx = h * dx / l;
            ppy = h * dy / l;

            // terminate if we're within 10% of a dx of the line length...
            tv = end.getX() - 0.1 * ppx;
        }
        // special case if dx = 0 (a vertical line)...
        else {

            // calculate our line's coefficients...
            m = (dy < 0) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            b = start.getY();

            // setup for point production...
            pts = (int) Math.ceil( Math.abs(dy) / dc.getMaxPointDistance() );
            ppx = 0;
            ppy = dy / pts;

            // terminate if we're within 10% of a dx of the line length...
            tv = start.getY() - 0.1 * ppy;
        }

        generate();
    }


    /**
     * Returns the next point on the line.  Classes representing algorithmically generated lines must override this method to provide the points along
     * the line, including the end point (exactly), which will terminate the iteration of points.
     *
     * @param _current  a point along the line.
     * @return the next point along the line, at approximately the given distance or less.
     */
    @Override
    protected Position nextPoint( final Position _current ) {
        if( xterm ) {
            double nx = _current.getX() + ppx;
            if( isTerminal( ppx, nx, tv ) ) return end;
            double ny = m * nx + b;
            return new CartesianPosition( nx, ny, _current.getTurns() );
        }

        // we only get here if we have a vertical line, so x is constant...
        double ny = _current.getY() + ppy;
        if( isTerminal( ppy, ny, tv ) ) return end;
        return new CartesianPosition( _current.getX(), ny, _current.getTurns() );
    }
}
