package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.tracks.Track;

/**
 * Represents a straight line between two points on the Sisyphus table.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class OrigLine {

    private static final double MAX_SEG_ANGLE = 0.05;  // radians...

    private final Pos fm;
    private final Pos to;


    public OrigLine( final Pos _fm, final Pos _to ) {
        fm = _fm;
        to = _to;
    }


    public void generate( final Track _track ) {

        double deltaAngle = to.getAngle() - fm.getAngle();
        int segs = (int) Math.ceil( Math.abs( deltaAngle ) / MAX_SEG_ANGLE );
        double segAngle = deltaAngle / segs;
        Pos start = fm;
        _track.add( start );  // just in case our caller didn't already move the table here...
        double curAngle = fm.getAngle();
        for( int i = 1; i <= segs; i++ ) {

            // if this is the last segment, we already know the end point, so just draw it...
            if( i == segs ) {
                _track.add( to );
                continue;
            }

            // otherwise we calculate intersection of line we're drawing and the radial...
            curAngle += segAngle;
            double xl = to.getX() - fm.getX();
            double yl = to.getY() - fm.getY();
            double ml = yl / xl;
            double bl = to.getY() - ml * to.getX();
            double xi, yi;
            if( (curAngle == 0) || (curAngle == Math.toRadians( 180 ) ) ) {
                xi = 0;
                yi = bl;
            } else {
                double xr = Math.sin( curAngle );
                double yr = Math.cos( curAngle );
                double mr = yr / xr;
                xi = bl / (mr - ml);
                yi = xi * mr;
            }
            Pos turnscalc = start.add( segAngle, 0 );
            Pos next = Pos.fromXY( xi, yi, turnscalc.getTurns() );

            // make sure we're moving in the right direction...
            if( (to.getAngle() > fm.getAngle()) && (next.getAngle() < start.getAngle()) )
                next = next.add( Math.toRadians( 360 ), 0 );
            if( (to.getAngle() < fm.getAngle()) && (next.getAngle() > start.getAngle()) )
                next = next.add( -Math.toRadians( 360 ), 0 );

            // normalize the angle to the from angle...
            while( Math.abs( next.getAngle() - start.getAngle() ) >= Math.toRadians( 360 ) ) {
                if( next.getAngle() > start.getAngle() )
                    next = next.add( -Math.toRadians( 360 ), 0 );
                else
                    next = next.add( Math.toRadians( 360 ), 0 );
            }
            _track.add( next );
            start = next;
        }
    }
}
