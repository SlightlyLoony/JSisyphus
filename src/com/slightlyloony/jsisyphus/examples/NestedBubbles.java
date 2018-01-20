package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.lines.Line;
import com.slightlyloony.jsisyphus.lines.StraightLine;
import com.slightlyloony.jsisyphus.positions.CartesianPosition;
import com.slightlyloony.jsisyphus.positions.Position;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class NestedBubbles extends ATrack {


    public NestedBubbles( final String baseFileName ) {
        super( baseFileName );
    }


    public void trace() throws IOException {

        bubble( Position.CENTER, 1 );

        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }


    // Assumes ball is at the given center.  Ball ends up 60 degrees clockwise on outer radius.
    private void bubble( final Position _center, final double _outerRadius ) {

        // some setup...
        double innerRadius = _outerRadius / 3;

        // move to inner radius...
        Line rl = new StraightLine( dc, _center, new CartesianPosition( _center.getX(), _center.getY() + innerRadius, _center.getTurns() ) );
        dc.draw( rl );

        // trace the inner circle...
//        Line ic = new CircularArc( dc, _center, innerRadius, 0, Math.PI * 2 );
//        dc.draw( ic );

        // trace the ring of six inner circles...
        double ar = 0;  // angle of radial...
        double start = 0;
        double end = 0;
        double rcx = 0;
        double rcy = 0;
        Line rc = null;
//        for( int c = 0; c < 5; c++ ) {
//
//            // draw one circle...
//            start = Utils.normalize( Math.PI + ar );
//            end = Utils.normalize( Math.PI * 10 / 3 + ar );
//            rcx = _center.getX() + Math.sin(ar) * 2 * innerRadius;
//            rcy = _center.getY() + Math.cos(ar) + 2 * innerRadius;
//            rc = new CircularArc( new CartesianPosition( rcx, rcy, _center.getTurns() ), innerRadius, start, end );
//            dc.draw( rc );
//
//            // update our radial angle...
//            ar -= Math.PI / 3;
//        }
//
//        // get to an outer point on the last circle...
//        rc = new CircularArc( new CartesianPosition( rcx, rcy, _center.getTurns() ), innerRadius, end, Math.PI / 3 );
//        dc.draw( rc );
//
//        // trace the outer ring...
//        Line oc = new CircularArc( _center, _outerRadius, Math.PI / 3, Math.PI * 7 / 3  );
//        dc.draw( oc );
    }
}
