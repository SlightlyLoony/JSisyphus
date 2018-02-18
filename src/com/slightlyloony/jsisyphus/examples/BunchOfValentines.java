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

        eraseTo( Point.fromRT( 1, toRadians( 150 ) ) );

        // draw the ground line...
        lineToRT( ground.sideB, -PI / 2 );

        // draw the first one...
        lineToRT( .05, PI/2 );
        curveToRT( .1, 0, .1, V1A + PI, .2, toRadians( -10 ) );
        Marker marker = marker();
        lineToRT( .5, V1A );
        drawHeart( .04, .4, V1A );
        lineTo( marker.vectorTo() );
        curveToRT( .1, V1A + PI, .1, 0, .2, toRadians( -10 ) + PI );

        // draw the second one...
        lineToRT( .15, PI/2 );
        lineToRT( .3, 0 );
        Marker marker2 = marker();
        curveToRT( .1, 0, .1, V2A + PI, .5, toRadians( 5 ) );
        marker = marker();
        lineToRT( .5, V2A );
        drawHeart( .04, .5, V2A );
        lineTo( marker.vectorTo() );
        curveToRT( .1, V2A + PI, .1, 0, marker2.vectorTo().rho, marker2.vectorTo().theta );
        lineToRT( .3, PI );

        // draw the third one...
        lineToRT( .6, PI/2 );
        marker = marker();
        lineToRT( .4, 0 );
        drawHeart( .04, .6, 0 );
        lineTo( marker.vectorTo() );


        renderPNG( pngFileName );
        write( trackFileName );
    }


    private void drawHeart( final double _initialSF, final double _finalSF, final double _rotation ) {
        double sf = _initialSF;
        while( sf < _finalSF ) {

            heart.draw( "bottom", sf, _rotation );
            double nsf = sf * 1.06;
            lineToRT( (nsf - sf) / 2, _rotation + PI );
            sf = nsf;
        }
    }
}
