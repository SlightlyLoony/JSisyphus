package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Marker;
import com.slightlyloony.jsisyphus.Point;

import java.io.IOException;

import static java.lang.Math.*;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class BinarySupport extends ATrack {

    // givens...
    private static final int TREES = 4;         // number of trees...
    private static final int MAX_LEVEL = 6;     // maximum level (2^n leaves on final level)...
    private static final boolean CURVY = true;  // true to use Bezier connectors instead of straight lines...


    public BinarySupport() {
        super( "BinarySupport" );
    }


    public void trace() throws IOException {

        // erase from the outside...
        spiralTo( Point.fromRT( 1, 0 ), Point.fromRT( 0, 0 ), 0, 3 );
        eraseTo( Point.fromRT( -1, 0 ) );

        // iterate over our trees...
        for( int t = 0; t < TREES; t++ ) {

            // draw a tree recursively, starting at level 0, leaf 0...
            drawTree( 0, 0 );

            // rotate for the next tree...
            rotateBy( 2 * PI / TREES );
        }

        renderPNG( pngFileName );
        write( trackFileName );
    }


    private void drawTree( final int _level, final int _leaf ) {

        // remember where we are, so we can get back here...
        Marker marker = marker();

        // calculate where to put this leaf...
        double treeSpace = 2 * PI / TREES;
        int leavesInLevel = (int) round( pow( 2, _level ) );
        double leafOffset = 0.5 * treeSpace / leavesInLevel;
        double levelRho = 0.8 * (1.0 + _level) / (MAX_LEVEL + 1);
        double leafTheta = (_level == 0) ? 0 : getCurrentRelativePosition().theta + ((_leaf == 0) ? -leafOffset : leafOffset );
        Point end = vectorTo( Point.fromRT( levelRho, leafTheta ) );

        // if we're curvy, calculate our control points...
        Point cp1, cp2;
        if( CURVY ) {
            if( _level == 0 )
                cp1 = Point.fromRT( .5 * end.rho, 0 );
            else
                cp1 = Point.fromRT( .5 * end.rho, getCurrentRelativePosition().theta );
            cp2 = Point.fromRT( .5 * end.rho, leafTheta + PI );
        }

        // draw our line...
        if( CURVY )
            curveTo( cp1, cp2, end );
        else
            lineTo( end );

        // see if we have sub-leaves...
        int nextLevel = _level + 1;
        if( nextLevel <= MAX_LEVEL ) {

            // handle our two sub-leaves...
            drawTree( _level + 1, 0 );
            drawTree( _level + 1, 1 );
        }

        // move back to our beginning...
        if( CURVY )
            curveTo( cp2, cp1, marker.vectorTo() );
        else
            lineTo( marker.vectorTo() );
    }
}
