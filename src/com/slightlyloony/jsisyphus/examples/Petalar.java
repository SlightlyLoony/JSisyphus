package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Petalar extends ATrack {


    public Petalar() {
        super( "Petalar" );
    }


    public void trace() throws IOException {

        // some setup...
        int numPetals = 50;
        double petalAngle = Math.PI * 2 / numPetals;
        double outsideRadius = 1;
        double petalWidth = Math.PI + petalAngle;

        // erase and get ourselves to the outside...
        dc.eraseToRT( 1, 0 );

        // draw a ring of petals at our current outside radius...
        double insideRadius = getInsideRadius( petalAngle, outsideRadius );

        // draw rings until we're very close to the middle...
        while( insideRadius >= .2 ) {

            // compute our parameters for this turn around the table...
            double hx = insideRadius * Math.sin( petalAngle / 2 );
            double hy = -(outsideRadius - insideRadius * Math.cos( petalAngle / 2 ) );
            double fx = insideRadius * Math.sin( petalAngle );
            double fy = -(insideRadius - insideRadius * Math.cos( petalAngle ) );

            // first the half-petal to get us to the inside...
            dc.arcToXY( hx, hy, petalWidth / 2 );
            dc.rotateBy( petalAngle / 2 );

            // then all our whole petals...
            for( int i = 0; i < numPetals; i++ ) {
                dc.arcToXY( fx, fy, petalWidth );
                dc.rotateBy( petalAngle );
            }

            outsideRadius = insideRadius;
            insideRadius = getInsideRadius( petalAngle, outsideRadius );
        }

        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }


    private double getInsideRadius( final double _petalAngle, final double _outsideRadius ) {
        double spt = Math.sin( _petalAngle / 2 );
        double pr = spt * _outsideRadius / ( 1 + spt );
        return Math.sqrt( Math.pow(_outsideRadius - pr, 2) - Math.pow(pr, 2) );
    }
}
