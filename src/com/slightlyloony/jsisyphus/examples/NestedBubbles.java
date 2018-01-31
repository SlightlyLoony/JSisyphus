package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;

import java.io.IOException;

import static java.lang.Math.*;

/**
 * Draws a series of nested bubbles...
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class NestedBubbles extends ATrack {

    private static final double SIXTH      = PI / 3;
    private static final double THIRD      = 2 * PI / 3;
    private static final double HALF       = PI;
    private static final double TWO_THIRDS = 4 * PI / 3;
    private static final double FULL       = 2 * PI;


    public NestedBubbles() {
        super( "NestedBubbles" );
    }


    public void trace() throws IOException {

        // we start near the outside...
        dc.eraseToRT( 0.9, 0 );

        bubble( .9, true, 3 );

        dc.eraseToRT( 0.1, 0 );

        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }


    /**
     * Traces a circle at the given radius, then recurses into the seven enclosed circles if the given nesting state is greater than zero.  On entry, assumes
     * the current position is at the top center of the outer circle (in the current rotation) and that the ball is moving in the given direction.  Everything
     * else is computed from those three pieces of information.
     *
     * @param _radius the radius of the outer circle to be traced.
     * @param _clockwise true if the outer circle should be traced in a clockwise direction.
     * @param _nestingLevel the number of levels to nest bubbles.
     */
    private void bubble( final double _radius, final boolean _clockwise, final int _nestingLevel ) {

        // some setup...
        double outerRadius = _radius;
        double innerRadius = outerRadius / 3;
        boolean innerClockwise = _clockwise;

        // first we draw the outer circle...
        dc.arcAroundRT( -outerRadius, 0, innerClockwise ? FULL : -FULL );

        // if our nesting level is greater than zero, we recurse into the inner circles...
        if( _nestingLevel > 0 ) {

            // arc around the top inner circle to the point where it meets the next one...
            dc.arcAroundRT( -innerRadius, 0, _clockwise ? THIRD : -THIRD );

            // now we iterate over the six inner circles tangent to the outer circle...
            for( int i = 0; i < 6; i++ ) {

                // rotate so that the next circle is on top...
                dc.rotateBy( _clockwise ? SIXTH : -SIXTH );

                // flip our direction...
                innerClockwise = !innerClockwise;

                // arc around from the current position to the top...
                double dist = innerClockwise ^ _clockwise ? TWO_THIRDS : THIRD;
                dc.arcAroundRT( innerRadius, _clockwise ? SIXTH : -SIXTH, innerClockwise ? dist : -dist );

                // recurse to draw the actual tangent circle...
                bubble( innerRadius, innerClockwise, _nestingLevel - 1 );

                // now arc around to the point where it meets the next one...
                dc.arcAroundRT( -innerRadius, 0, innerClockwise ? dist : -dist );
            }

            // arc around the last tangent circle to get to the bottom of it...
            dc.arcAroundRT( innerRadius, innerClockwise ? -SIXTH : SIXTH, innerClockwise ? SIXTH : -SIXTH );

            // flip our direction...
            innerClockwise = !innerClockwise;

            // bubble to draw the actual inner circle...
            bubble( innerRadius, innerClockwise, _nestingLevel - 1 );

            // one last direction flip...
            innerClockwise = !innerClockwise;

            // arc up to the top center of the outer circle to extricate our ball...
            dc.arcAroundRT( innerRadius, 0, innerClockwise ? HALF : -HALF );
        }
    }
}
