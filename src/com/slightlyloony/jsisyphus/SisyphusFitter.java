package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.positions.PolarPosition;
import com.slightlyloony.jsisyphus.positions.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class find the series of spiral lines drawn natively by the Sisyphus table that will fit within the fit tolerance of an arbitrary line
 * represented by a series of points.  The result is a list of vertices.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SisyphusFitter {

    /**
     * Table encoding the possible outcomes resulting from measuring the distance between a point being tested and the four vertices of three Sisyphus line
     * segments.  The index to this table is a three bit number where each bit is a 1 for closer, 0 for further, and bit 2 is for vertice 1 vs vertice 0,
     * bit 1 is for vertice 2 vs vertice 1, and bit 0 is for vertice 3 vs vertice 2.  For example, a 6 means vertice 1 is closer to the test point then
     * vertice 0, vertice 2 is closer than vertice 1, and vertice 3 is further than vertice 2.  From that we can deduce (and the table encodes) that the
     * closest point on the Sisyphus line to the test point must lie somewhere between vertice 1 and vertice 3.  The error field allows detection of
     * impossible results.
     */
    private static final SegResult SEG_RESULTS[] = {        // index  1->0    2->1    3->2
            new SegResult( 0, 1, false ), // 0      further further further
            new SegResult( 0, 0, true  ), // 1      further further closer
            new SegResult( 0, 0, true  ), // 2      further closer  further
            new SegResult( 0, 0, true  ), // 3      further closer  closer
            new SegResult( 0, 2, false ), // 4      closer  further further
            new SegResult( 0, 0, true  ), // 5      closer  further closer
            new SegResult( 1, 3, false ), // 6      closer  closer  further
            new SegResult( 2, 3, false ), // 7      closer  closer  closer
    };

    private final List<Position> points;
    private final DrawingContext dc;
    private final List<Position> vertices;
    private final double fitTolerance;


    /**
     * Creates a new instance of this class.
     *
     * @param _points the list of points defining the arbitrary line that needs to have spiral lines fitted to it.
     * @param _dc the drawing context for this operation.
     */
    public SisyphusFitter( final List<Position> _points, final DrawingContext _dc ) {

        points = _points;
        dc = _dc;
        vertices = new ArrayList<>();
        fitTolerance = dc.getFitToleranceRho();
    }


    /**
     * Creates a list of positions representing vertices of a line to draw on the Sisyphus table.  For n lines there are n+1 vertices.
     */
    public void generate() {
        int last = points.size() - 1;
        int current = 0;  // start at the beginning!
        while( current < last ) {

            // get our start point...
            Position start = points.get( current );

            // do a binary search to find the longest segment we can draw as a Sisyphus line...
            boolean done = false;
            int probe = last;
            int highestCan = current + 1;
            int lowestCant = last + 1;
            while( !done ) {
                boolean canDraw = isOn( current, probe );
                if( canDraw ) {
                    highestCan = probe;
                    probe = probe + ((lowestCant - probe ) >> 1);
                }
                else {
                    lowestCant = probe;
                    probe = highestCan + ((probe - (highestCan + 1)) >> 1);
                }
                done = (lowestCant - highestCan == 1);
            }

            // emit the vertice...
            vertices.add( points.get( highestCan ) );

            // move to the next segment...
            current = highestCan;
        }

    }
    private static final int MAX_ITERATIONS = 25;
    /**
     * Returns true if the line defined by the start and end indices into the points held by this instance fits (within fit tolerance) the path that the
     * Sisyphus table would make from the same start and end points.
     *
     * @return true if the line fits.
     */
    public boolean isOn( final int _start, final int _end ) {

        // some setup...
        Position start = points.get( _start );
        Position end = points.get( _end );
        double ldt = end.getTheta() - start.getTheta();  // delta theta over the entire line (may be multiple revolutions)...
        double ldr = end.getRho() - start.getRho();      // delta rho over the entire line...
        boolean isRadial = (Math.abs( ldt ) < 1.0E-12 );
        boolean isCircle = (Math.abs( ldr ) < 1.0E-12 );
        boolean isClockwise = (ldt > 0);
        double m = ldr / ldt;
        double b = start.getRho() - m * start.getTheta();

        // where we keep our segment and sub-segment details...
        SegmentVertice st[] = {new SegmentVertice(), new SegmentVertice(), new SegmentVertice(), new SegmentVertice()};

        // iterate over all the points in this line, testing them in order from start to end...
        SegmentVertice lastFit = new SegmentVertice();
        lastFit.rho = points.get( _start ).getRho();
        lastFit.theta = points.get( _start ).getTheta();
        calcXY( lastFit );
        for( int p = _start + 1; p <= _end; p++ ) {

            Position testPoint = points.get( p );

            /*
                If this path is a radial, circle, or point a special method can find the closest point.  A relatively quick test can determine whether one of
                the end points is the closest point.
             */

            // if our line is actually a point, just see if the distance is within tolerance...
            if( isCircle && isRadial )
                if( Math.hypot( testPoint.getX() - start.getX(), testPoint.getY() - start.getY() ) <= fitTolerance )
                    continue;
                else
                    return logFail( p, 0 );

            // if our line is a circle, then the closest point is (by definition!) the point on the circle at the same theta as our point...
            if( isCircle ) {
                Position ct = getClosestTheta( isClockwise, start, end, m, b, testPoint.getTheta() );
                if( Math.hypot( testPoint.getX() - ct.getX(), testPoint.getY() - ct.getY() ) <= fitTolerance )
                    continue;
                else
                    return logFail( p, 0 );
            }

            // if our line is a radial, then we can use the straight line method to find the closest point...
            if( isRadial )
                if( Utils.distance( start, end, testPoint ) <= fitTolerance )
                    continue;
                else
                    return logFail( p, 0 );

            if( !fitsCurve( m, b, st, lastFit, p, testPoint ) )
                return false;
        }

        // if we get here, then we've successfully tested every point...
        return true;
    }


    private boolean fitsCurve( final double _m, final double _b, final SegmentVertice[] _st, final SegmentVertice _lastFit, final int _p, final Position _testPoint ) {
    /*
        If we get here, then we have the more difficult case - we have to see if there's a point on the spiral that is within the fit tolerance to our
        point.  The smaller the starting segment size, the fewer iterations will be required.  We use the last fit location as our start of segment,
        and calculate a point roughly two times the point spacing for the end.
     */
        double ss = _lastFit.theta;
        double se = getSegmentEnd( _m, _b, _p, _lastFit );

            /*
                Now we use successive segmentation of our line to narrow down the size of the segment that approaches the closest to our point.  If at any time
                we identify a point on the spiral that is within the fit tolerance distance from our point, we bail out with a positive finding (it fits!).  We
                keep this up until either we succeed or we determine that we can't possibly do so.

                The segmentation algorithm is fairly simple on each iteration.  It divides the remaining segment into rough thirds, then identifies either one
                or two of the sub-segments that might contain the closest point.  This identification is done by computing the delta distance to our point at
                each of the four vertices defining the three sub-segments, then calculating (in start-to-end direction) whether that distance increases or
                decreases in each segment.  From the pattern of those results we can determine which one or two sub-segments must contain the spiral's closest
                approach to our point.  There are eight possible patterns (given three segments' delta distance, each plus or minus), and we use a table to
                look up the result rather than computing it each time.
             */

        // initialize the starting segment's end points...
        _st[0].theta = ss;
        _st[0].rho = getRhoFromTheta( _m, _b, ss );
        _st[3].theta = se;
        _st[3].rho = getRhoFromTheta( _m, _b, se );

        for( int i = 0; i < MAX_ITERATIONS; i++ ) {

            // calculate our end points, checking for fit...
            if( calcVertice( _st[3], _testPoint, _lastFit ) ) return true;  // doing this one first helps at the origin of the spiral...
            if( calcVertice( _st[0], _testPoint, _lastFit ) ) return true;

            if( doesNotFit( _m, _b, _st, _testPoint, i ) ) return logFail( _p, i );

            // if we've iterated too much, then bail out, failing the fit...
            if( i == MAX_ITERATIONS - 1 )
                return logFail( _p, i );

            // generate sub-segments, checking for fit as we go...
            double dt = (_st[3].theta - _st[0].theta) / 3;
            _st[1].theta = _st[0].theta + dt;
            _st[1].rho = getRhoFromTheta( _m, _b, _st[1].theta );
            _st[2].theta = _st[1].theta + dt;
            _st[2].rho = getRhoFromTheta( _m, _b, _st[2].theta );

            // calculate our vertice points, checking for fit as we go...
            if( calcVertice( _st[1], _testPoint, _lastFit ) ) return true;
            if( calcVertice( _st[2], _testPoint, _lastFit ) ) return true;

            // calculate our delta distance pattern...
            int ddp = 0;
            if( _st[1].distance <= _st[0].distance ) ddp |= 4;
            if( _st[2].distance <= _st[1].distance ) ddp |= 2;
            if( _st[3].distance <= _st[2].distance ) ddp |= 1;

            // look up our new sub-segment...
            SegResult sr = SEG_RESULTS[ddp];
            if( sr.error ) {
                logState( "Impossible segment analysis result: " + ddp, _testPoint, _st );
                return logFail( _p, MAX_ITERATIONS );
            }
            _st[0].theta = _st[sr.start].theta;
            _st[0].rho = _st[sr.start].rho;
            _st[3].theta = _st[sr.end].theta;
            _st[3].rho = _st[sr.end].rho;
        }

        // we've iterated too much...
        logState( "Too many iterations!", _testPoint, _st );
        return logFail( _p, MAX_ITERATIONS );
    }


    /**
        Returns true if we can be certain that we DON'T have a fit, if the aperture (the difference in the angles from our test point to the
        two ends of our segment) is less than a certain amount.  We do this by calculating the distance between our test point and an imaginary line
        between the ends of this segment, plus or minus an adjustment for the convexity or concavity of our spiral.  If this distance is greater than
        the fit tolerance, than we know the point is not on the spiral.
     */
    private boolean doesNotFit( final double _m, final double _b, final SegmentVertice[] _st, final Position _testPoint, final int _i ) {
        // calculate the aperture; if it's under 10 degrees then we'll check for proof that we can't fit...
        double tp2s = Utils.getTheta( _st[0].x - _testPoint.getX(), _st[0].y - _testPoint.getY() );
        double tp2e = Utils.getTheta( _st[3].x - _testPoint.getX(), _st[3].y - _testPoint.getY() );
        double tpdt = Utils.deltaTheta( tp2s, tp2e );
        double aper = Math.abs( tpdt );
        if( aper < Math.toRadians( 10 ) ) {

            // see if the spiral curve, relative to our test point, is convex or concave...
            boolean positiveDeltaSlope = (getRadialSlope( _m, _b, _st[0].theta ) < getRadialSlope( _m, _b, _st[3].theta ));
            boolean positiveDeltaTheta = (tpdt >= 0);
            boolean isConvex = (positiveDeltaSlope != positiveDeltaTheta);

            // calculate the distance between our segment ends...
            double dv = Math.hypot( _st[3].x - _st[0].x, _st[3].y - _st[0].y );

            // calculate the height of the triangle (to the test point) formed by our segment ends and the test point...
            double x0 = (_st[3].distance * _st[3].distance - _st[0].distance * _st[0].distance - dv * dv) / (-2 * dv);
            double h = Math.sqrt( _st[0].distance * _st[0].distance - x0 * x0 );

            // adjust for convexity or concavity...
            double ha = x0 * Math.tan( aper );

            // if fit tolerance < adjusted height, then we know we do NOT have a fit...
            return fitTolerance < h + (isConvex ? ha : -ha);
        }
        return false;
    }


    // returns theta of the segment end...
    private double getSegmentEnd( final double _m, final double _b, final int _current, final SegmentVertice _lastFit ) {
        Position current = points.get( _current );
        double dt = current.getTheta() - _lastFit.theta;
        return _lastFit.theta + dt * 2;
    }


    private boolean logFail( final int _point, final int _iteration ) {
        Position point = points.get( _point );
        StringBuilder sb = new StringBuilder();
        sb.append( "isOn determined that the " );
        sb.append( _point );
        sb.append( "th point at (" );
        sb.append( point.getX() );
        sb.append( ", " );
        sb.append( point.getY() );
        sb.append( ") is not on the spiral.  It took ");
        sb.append( _iteration );
        sb.append( " iterations to determine this." );
        System.out.println( sb.toString() );
        return false;
    }


    private void logState( final String _msg, final Position _tp, final SegmentVertice _st[] ) {
        StringBuilder sb = new StringBuilder();
        sb.append( _msg );
        sb.append( '\n' );
        sb.append( "Test point: " );
        sb.append( _tp.toString() );
        sb.append( '\n' );
        for( int i = 0; i < 4; i++ ) {
            sb.append( "Segment vertice " );
            sb.append( i );
            sb.append( ": " );
            sb.append( _st[i].toString() );
            sb.append( '\n' );
        }
        System.out.print( sb.toString() );
    }


    // Computes x, y, and distance for the given vertice, putting the results in the given segment table array.  Returns true if this point is within
    // the fit tolerance, false otherwise.
    private boolean calcVertice( final SegmentVertice _vertice, final Position _testPoint, final SegmentVertice _lastFit ) {
        calcXY( _vertice );
        _vertice.distance = Math.hypot( _vertice.x - _testPoint.getX(), _vertice.y - _testPoint.getY() );
        boolean fits = _vertice.distance <= fitTolerance;
        if( fits ) {
            _lastFit.theta = _vertice.theta;
            _lastFit.rho = _vertice.rho;
        }
        return fits;
    }


    private void calcXY( final SegmentVertice _vertice ) {
        _vertice.x = Math.sin(_vertice.theta) * _vertice.rho;
        _vertice.y = Math.cos(_vertice.theta) * _vertice.rho;
    }


    // returns the point on this path with the theta closest to the given radial...
    private Position getClosestTheta( final boolean _isClockwise, final Position _start, final Position _end, final double _m, final double _b, final double _radial ) {

        double theta = 0;
        if( _isClockwise ) {
            if( _radial < _start.getTheta() )
                return _start;
            else if( _radial > _end.getTheta() )
                return _end;
            else
                theta = _radial;
        }
        else {
            if( _radial > _start.getTheta() )
                return _start;
            else if( _radial < _end.getTheta() )
                return _end;
            else
                theta = _radial;
        }

        return new PolarPosition( getRhoFromTheta( _m, _b, theta ), theta );
    }


    // returns the point on this path with the closest rho...
    private Position getClosestRho( final boolean _isClockwise, final Position _start, final Position _end, final double _m, final double _b, final double _rho ) {
        return getClosestTheta( _isClockwise, _start, _end, _m, _b, getThetaFromRho( _m, _b, _rho ) );
    }


    private double getRhoFromTheta( final double _m, final double _b, final double _theta ) {
        return _m * _theta + _b;
    }


    private double getThetaFromRho( final double _m, final double _b, final double _rho) {
        return (_rho - _b) / _m;
    }


    private double getRadialSlope( final double _m, final double _b, final double _theta ) {
        return _m / getRhoFromTheta( _m, _b, _theta );
    }


    public List<Position> getVertices() {
        return vertices;
    }


    /**
     * Represents a vertice of a Sisyphus line segement.
     */
    private static class SegmentVertice {
        private double rho;
        private double theta;
        private double x;
        private double y;
        private double distance;  // distance to the test point...


        @Override
        public String toString() {
            return "rho: " + rho + ", theta: " + theta + ", x: " + x + ", y: " + y + ", distance: " + distance;
        }
    }


    private static class SegResult {
        private int start;
        private int end;
        private boolean error;


        private SegResult( final int _start, final int _end, final boolean _error ) {
            start = _start;
            end = _end;
            error = _error;
        }

        @Override
        public String toString() {
            return "start: " + start + ", end: " + end + ", error: " + error;
        }
    }
}
