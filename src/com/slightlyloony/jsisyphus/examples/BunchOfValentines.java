package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;
import com.slightlyloony.jsisyphus.Marker;
import com.slightlyloony.jsisyphus.Point;
import com.slightlyloony.jsisyphus.Triangle;

import java.io.IOException;

import static java.lang.Math.PI;
import static java.lang.Math.toRadians;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class BunchOfValentines extends ATrack {


    private static final double GA = toRadians( 150 );  // angle of right side of ground plane...
    private static final double V1A = toRadians( -10 );
    private static final double V2A = toRadians( 25 );

    private final HeartDef heart;


    public BunchOfValentines() {
        super( "BunchOfValentines" );
        heart = new HeartDef( dc );
    }


    public void trace() throws IOException {

        Triangle ground = Triangle.fromSAS( 1, 2 * PI - 2 * GA, 1 );

        dc.eraseTo( Point.fromRT( 1, toRadians( 150 ) ) );

        // draw the ground line...
        dc.lineToRT( ground.sideB, -PI / 2 );

        // draw the first one...
        dc.lineToRT( .05, PI/2 );
        dc.curveToRT( .1, 0, .1, V1A + PI, .2, toRadians( -10 ) );
        Marker marker = dc.marker();
        dc.lineToRT( .5, V1A );
        dc.rotateBy( V1A );
        drawHeart( .04, .4 );
        dc.rotateBy( -V1A );
        dc.lineTo( marker.vectorTo() );
        dc.curveToRT( .1, V1A + PI, .1, 0, .2, toRadians( -10 ) + PI );

        // draw the second one...
        dc.lineToRT( .15, PI/2 );
        dc.lineToRT( .3, 0 );
        Marker marker2 = dc.marker();
        dc.curveToRT( .1, 0, .1, V2A + PI, .5, toRadians( 5 ) );
        marker = dc.marker();
        dc.lineToRT( .5, V2A );
        dc.rotateBy( V2A );
        drawHeart( .04, .5 );
        dc.rotateBy( -V2A );
        dc.lineTo( marker.vectorTo() );
        dc.curveToRT( .1, V2A + PI, .1, 0, marker2.vectorTo().rho, marker2.vectorTo().theta );
        dc.lineToRT( .3, PI );

        // draw the third one...
        dc.lineToRT( .6, PI/2 );
        marker = dc.marker();
        dc.lineToRT( .4, 0 );
        drawHeart( .04, .6 );
        dc.lineTo( marker.vectorTo() );


        dc.renderPNG( pngFileName );
        dc.write( trackFileName );
    }


    private void drawHeart( final double _initialSF, final double _finalSF ) {
        double sf = _initialSF;
        while( sf < _finalSF ) {

            heart.draw( "bottom", sf );
            double nsf = sf * 1.06;
            dc.lineToRT( (nsf - sf) / 2, PI );
            sf = nsf;
        }
    }
}
