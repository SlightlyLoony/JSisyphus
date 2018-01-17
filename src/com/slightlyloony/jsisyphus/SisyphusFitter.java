package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.positions.PolarPosition;
import com.slightlyloony.jsisyphus.positions.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class find the series of spiral lines drawn natively by the Sisyphus table that will fit within the fit tolerance of the arbitrary line
 * represented by a series of points.  The result is a list of vertices.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class SisyphusFitter {

//    private final Position start;
//    private final Position end;
//    private final double m;
//    private final double b;
//    private final boolean isCircle;
//    private final boolean isRadial;
//    private final boolean isClockwise;
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
//        start = points.get( 0 );
//        end = points.get( points.size() - 1);

        // calculate our line's coefficients...
//        double dt = end.getTheta() - start.getTheta();  // delta theta over the entire line (may be multiple revolutions)...
//        double dr = end.getRho() - start.getRho();      // delta rho over the entire line...
//        isRadial = (dt == 0);
//        isCircle = (dr == 0);
//        isClockwise = (dt > 0);
//        m = dr / dt;
//        b = start.getRho() - m * start.getTheta();

        vertices = new ArrayList<>();
        fitTolerance = dc.getFitToleranceRho();
    }


    /**
     * Returns a list of positions, each of which represents a vertice of a line to draw on the Sisyphus table.
     * @return
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


    private static class Vertice {
        public double rho;
        public double theta;
        public double x;
        public double y;
        public double distance;
    }

    private static class SegResult {
        public int start;
        public int end;
        public boolean error;

        public SegResult( final int _start, final int _end, final boolean _error ) {
            start = _start;
            end = _end;
            error = _error;
        }
    }

    private static final SegResult SEG_RESULTS[] = {
            new SegResult( 0, 1, false ),
            new SegResult( 0, 0, true  ),
            new SegResult( 0, 0, true  ),
            new SegResult( 0, 0, true  ),
            new SegResult( 0, 2, false ),
            new SegResult( 0, 0, true  ),
            new SegResult( 1, 3, false ),
            new SegResult( 2, 3, false ),
    };
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
        boolean isRadial = (ldt == 0);
        boolean isCircle = (ldr == 0);
        boolean isClockwise = (ldt > 0);
        double m = ldr / ldt;
        double b = start.getRho() - m * start.getTheta();

        // iterate over all the points in this line, testing them in order from start to end...
        Vertice lastFit = new Vertice();
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

            /*
                If we get here, then we have the more difficult case - we have to see if there's a point on the spiral that is within the fit tolerance to our
                point.  The smaller the starting segment size, the fewer iterations will be required.  We use the last fit location as our start of segment,
                and calculate a point roughly two times the point spacing for the end.
             */
            double ss = lastFit.theta;
            double se = getSegmentEnd( m, b, p, lastFit );

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

            // where we keep our segment and sub-segment details...
            Vertice st[] = {new Vertice(), new Vertice(), new Vertice(), new Vertice()};

            // initialize the starting segment's end points...
            st[0].theta = ss;
            st[0].rho = getRhoFromTheta( m, b, ss );
            st[3].theta = se;
            st[3].rho = getRhoFromTheta( m, b, se );

            for( int i = 0; i < MAX_ITERATIONS; i++ ) {

                // make sure we didn't iterate too much!
                if( i == MAX_ITERATIONS - 1 )
                    throw new IllegalStateException( "Too many iterations!" );

                // calculate our end points, checking for fit...
                if( calcVertice( st[3], testPoint, lastFit ) ) break;  // doing this one first helps at the origin of the spiral...
                if( calcVertice( st[0], testPoint, lastFit ) ) break;

                /*
                    Here we check to see if we can be certain that we DON'T have a fit, if the aperture (the difference in the angles from our test point to the
                    two ends of our segment) is less than a certain amount.  We do this by calculating the distance between our test point and an imaginary line
                    between the ends of this segment, plus or minus an adjustment for the convexity or concavity of our spiral.  If this distance is greater than
                    the fit tolerance, than we know the point is not on the spiral.
                 */

                // calculate the aperture; if it's under 10 degrees then we'll check for proof that we can't fit...
                double tp2s = Utils.getTheta( st[0].x - testPoint.getX(), st[0].y - testPoint.getY() );
                double tp2e = Utils.getTheta( st[3].x - testPoint.getX(), st[3].y - testPoint.getY() );
                double tpdt = Utils.deltaTheta( tp2s, tp2e );
                double aper = Math.abs( tpdt );
                if( aper < Math.toRadians( 10 ) ) {

                    // see if the spiral curve, relative to our test point, is convex or concave...
                    boolean positiveDeltaSlope = (getRadialSlope( m, b, st[0].theta ) < getRadialSlope( m, b, st[3].theta ));
                    boolean positiveDeltaTheta = (tpdt >= 0);
                    boolean isConvex = (positiveDeltaSlope != positiveDeltaTheta);

                    // calculate the distance between our segment ends...
                    double dv = Math.hypot( st[3].x - st[0].x, st[3].y - st[0].y );

                    // calculate the height of the triangle (to the test point) formed by our segment ends and the test point...
                    double x0 = (st[3].distance * st[3].distance - st[0].distance * st[0].distance - dv * dv) / (-2 * dv);
                    double h = Math.sqrt( st[0].distance * st[0].distance - x0 * x0 );

                    // adjust for convexity or concavity...
                    double ha = x0 * Math.tan( aper );

                    // if fit tolerance < adjusted height, then we know we do NOT have a fit...
                    if( fitTolerance < h + (isConvex ? ha : -ha) ) {
                        return logFail( p, i );
                    }
                }

                // generate sub-segments, checking for fit as we go...
                double dt = (st[3].theta - st[0].theta) / 3;
                st[1].theta = st[0].theta + dt;
                st[1].rho = getRhoFromTheta( m, b, st[1].theta );
                st[2].theta = st[1].theta + dt;
                st[2].rho = getRhoFromTheta( m, b, st[2].theta );

                // calculate our vertice points, checking for fit as we go...
                if( calcVertice( st[1], testPoint, lastFit ) ) break;
                if( calcVertice( st[2], testPoint, lastFit ) ) break;

                // calculate our delta distance pattern...
                int ddp = 0;
                if( st[1].distance <= st[0].distance ) ddp |= 4;
                if( st[2].distance <= st[1].distance ) ddp |= 2;
                if( st[3].distance <= st[2].distance ) ddp |= 1;

                // look up our new sub-segment...
                SegResult sr = SEG_RESULTS[ddp];
                if( sr.error )
                    throw new IllegalStateException( "Impossible segment distance result: " + ddp );
                st[0].theta = st[sr.start].theta;
                st[0].rho = st[sr.start].rho;
                st[3].theta = st[sr.end].theta;
                st[3].rho = st[sr.end].rho;
            }

            // we get here if the point fit within our specified number of iterations (thorugh a "break")...
        }

        // if we get here, then we've successfully tested every point...
        return true;
    }


    // returns theta of the segment end...
    private double getSegmentEnd( final double _m, final double _b, final int _current, final Vertice _lastFit ) {
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


    // Computes x, y, and distance for the given vertice, putting the results in the given segment table array.  Returns true if this point is within
    // the fit tolerance, false otherwise.
    private boolean calcVertice( final Vertice _vertice, final Position _testPoint, final Vertice _lastFit ) {
        calcXY( _vertice );
        _vertice.distance = Math.hypot( _vertice.x - _testPoint.getX(), _vertice.y - _testPoint.getY() );
        boolean fits = _vertice.distance <= fitTolerance;
        if( fits ) {
            _lastFit.theta = _vertice.theta;
            _lastFit.rho = _vertice.rho;
        }
        return fits;
    }


    private void calcXY( final Vertice _vertice ) {
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
}
