package com.slightlyloony.jsisyphus.examples;

import com.slightlyloony.jsisyphus.ATrack;

import java.io.IOException;

/**
 * @author Tom Dilatush  tom@dilatush.com
 */
public class PolarValentine extends ATrack {


    private static final double WIDTH = 0.7;
    private static final double LOBE_HEIGHT = 0.7;

    private static final double LOBE_DROP_THETA = Math.atan( WIDTH / (1 - LOBE_HEIGHT) );

//    private static final double CP1_RHO = 1;
//    private static final double CP1_THETA = ;
//    private static final double CP2_RHO = 1;
//    private static final double CP2_THETA = 1;
//    private static final double CP3_RHO = 1;
//    private static final double CP3_THETA = 0.8;
//    private static final double CP4_RHO = 1;
//    private static final double CP4_THETA = 0.8;
//    private static final double TOP_END_RHO = Math.PI - LOBE_DROP_THETA;
//    private static final double TOP_END_THETA = WIDTH / Math.sin( LOBE_DROP_THETA );
//    private static final double BOT_END_RHO = ;
//    private static final double BOT_END_THETA = ;


    public PolarValentine() {
        super( "PolarValentine" );
    }


    public void trace() throws IOException {
//
//        dc.lineToRT( .5, 0 );
//
//        dc.curveToRT( CP1_RHO, CP1_THETA, CP2_RHO, CP2_THETA, TOP_END_RHO, TOP_END_THETA );
//        dc.curveToRT( CP3_RHO, CP3_THETA, CP4_RHO, CP4_THETA, BOT_END_RHO, BOT_END_THETA );
//
//        dc.renderPNG( pngFileName );
//        dc.write( trackFileName );
    }
}
