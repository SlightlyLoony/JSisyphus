package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.lines.*;
import com.slightlyloony.jsisyphus.models.Model;
import com.slightlyloony.jsisyphus.positions.PolarPosition;
import com.slightlyloony.jsisyphus.positions.Position;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;
import static java.lang.Math.PI;

/**
 * Provides a drawing context for the Sisyphus table, supporting drawing with translation and rotation.
 *
 * Instances of this class are mutable and <i>not</i> threadsafe.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class DrawingContext {

    private static final double DEFAULT_MAX_FIT_ERROR_METERS = 0.0005; // in meters...
    private static final int    DEFAULT_PIXELS_PER_RHO       = 500;    // effectively the radius of the PNG in pixels...
    private static final double DEFAULT_ERASE_SPACING        = 0.004;  // in meters...

    private List<Position> vertices;  // holds all the vertices we've drawn...
    private double maxPointDistance;
    private Model model;
    private double maxFitErrorMeters;
    private double fitToleranceRho;
    private int pixelsPerRho;
    private Position currentPosition;
    private Point currentRelativePosition;
    private double currentRotation;
    private double eraseSpacing;  // the erase spiral radial spacing in meters...
    private boolean mute = false;


    /**
     * Creates a new instance of this class that assumes the given initial position of the ball on the Sisyphus table.
     *
     * @param _initialPosition The assumed initial position of the ball on the Sisyphus table.
     */
    public DrawingContext( final Position _initialPosition ) {
        vertices = new ArrayList<>();
        vertices.add( _initialPosition );  // for reasons unknown to us, the table seems to want the initial position twice...
        vertices.add( _initialPosition );
        currentPosition = _initialPosition;
        model = Model.GENERIC;
        maxFitErrorMeters = DEFAULT_MAX_FIT_ERROR_METERS;
        fitToleranceRho = maxFitErrorMeters / model.tableRadiusMeters();
        maxPointDistance = 0.01;  // approximately 2mm on A16 table...
        pixelsPerRho = DEFAULT_PIXELS_PER_RHO;
        eraseSpacing = DEFAULT_ERASE_SPACING;
        currentRotation = 0;
        currentRelativePosition = Point.fromXY( 0, 0 );
    }


    /**
     * Creates a new instance of this class that assumes the ball is at the center of the Sisyphus table.
     */
    public DrawingContext() {
        this( Position.CENTER );
    }


    /**
     * Erases from the current position to the given end point, spiraling either in or out depending on whether the given end point is inside or outside the
     * current position.  The number of turns made will ensure that the spacing is no more than the current erase spacing.
     *
     * @param _end the end point of the erasure.
     */
    public void eraseTo( final Point _end ) {

        // get the absolute end point...
        Point abEnd = _end.abs( this );

        // figure the delta rho and theta for the spiral...
        double dsRho = abEnd.rho - currentPosition.getRho();
        double dsTheta = abEnd.theta - currentPosition.getTheta();

        // figure out how many turns to make...
        double drhopt = eraseSpacing / model.tableRadiusMeters();        // gives us the change in rho per turn...
        int turns = (int) floor( abs( dsRho / drhopt ));       // gives us the number of turns we'll actually need...
        if( dsTheta < 0 ) turns = -turns;                                // correct for the direction...
        if( (_end.theta == 0) && (currentPosition.getRho() > 0) ) turns = -turns;

        Line line = new ArithmeticSpiral( maxPointDistance, _end, Point.fromXY( -currentPosition.getX(), -currentPosition.getY() ), 0, turns );
        draw( line );
    }


    /**
     * Erases from the current position to the given relative X, Y, turns position, spiraling either in or out depending on whether the "to" rho is less than
     * or greater than the current position's rho.  The number of turns made will ensure that the spacing is no more than the current erase spacing.
     *
     * @param _dX the difference in x between the end position and the current position.
     * @param _dY the difference in y between the end position and the current position.
     */
    public void eraseToXY( final double _dX, final double _dY ) {
        eraseTo( Point.fromXY( _dX, _dY ) );
    }


    /**
     * Erases from the current position to the given relative rho, theta position, spiraling either in or out depending on whether the "to" rho is less than
     * or greater than the current position's rho.  The number of turns made will ensure that the spacing is no more than the current erase spacing.  Note
     * that this number of turns may result in an ending theta that is different than that specified here, but only by a multiple of 2 * pi (360 degrees).
     *
     * @param _dRho the difference in rho between the ending position and the current position.
     * @param _dTheta the difference in theta between the ending position and the current position.
     */
    public void eraseToRT( final double _dRho, final double _dTheta ) {
        eraseTo( Point.fromRT( _dRho, _dTheta ) );
    }


    /**
     * Draws a straight line from the current position to the given point.  The given point's coordinates are considered relative to the current position, and
     * in the current rotation.
     *
     * @param _point the point to draw a line to, with coordinates relative to the current position.
     */
    public void lineTo( final Point _point ) {
        Line line = new StraightLine( maxPointDistance, _point.x, _point.y );
        draw( line );
    }


    /**
     * Draws a straight line from the current position to the point at the given delta x and delta y from the current position, in the current rotation.
     *
     * @param _dX the difference in x between the ending position and the current position.
     * @param _dY the difference in y between the ending position and the current position.
     */
    public void lineToXY( final double _dX, final double _dY ) {
        lineTo( Point.fromXY( _dX, _dY ) );
    }


    /**
     * Draws a straight line from the current position to the point at the given delta rho and delta theta from the current position, in the current
     * rotation.
     *
     * @param _dRho the difference in rho between the ending position and the current position.
     * @param _dTheta the difference in theta between the ending position and the current position.
     */
    public void lineToRT( final double _dRho, final double _dTheta ) {
        lineTo( Point.fromRT( _dRho, _dTheta ) );
    }


    /**
     * Draws an arithmetic spiral from the current position to the given end point.  The center of the spiral will be at the given center point.
     * The spiral will have the given number of complete turns plus (possibly) a partial turn after that to reach the specified end point.  Both the end
     * position and center position are relative to the current position, and in the current rotation.
     *
     * @param _end the end point of the spiral.
     * @param _center the center point of the spiral.
     * @param _centerTheta the theta for either the start or end of the spiral, if it hits the center.
     * @param _turns the number of complete turns (positive for clockwise, negative for anti-clockwise).
     */
    public void spiralTo( final Point _end, final Point _center, final double _centerTheta, final int _turns ) {
        Line line = new ArithmeticSpiral( maxPointDistance, _end, _center, _centerTheta, _turns );
        draw( line );
    }


    /**
     * Draws an arithmetic spiral from the current position to the given end point position.  The center of the spiral will be at the given center position.
     * The spiral will have the given number of complete turns plus (possibly) a partial turn after that to reach the specified end point.  Both the end
     * position and center position are relative to the current position, and in the current rotation.
     *
     * @param _dxEnd the delta x from the current position to the end position.
     * @param _dyEnd the delta y from the current position to the end position.
     * @param _dxCenter the delta x from the current position to the spiral center position.
     * @param _dyCenter the delta y from the current position to the spiral center position.
     * @param _turns the number of complete turns (positive for clockwise, negative for anti-clockwise).
     */
    public void spiralToXY( final double _dxEnd, final double _dyEnd, final double _dxCenter, final double _dyCenter, final int _turns ) {
        spiralTo( Point.fromXY( _dxEnd, _dyEnd ), Point.fromXY( _dxCenter, _dyCenter ), 0, _turns );
    }


    /**
     * Draws an arithmetic spiral from the current position to the given end point position.  The center of the spiral will be at the given center position.
     * The spiral will have the given number of complete turns plus (possibly) a partial turn after that to reach the specified end point.  Both the end
     * position and center position are relative to the current position, and in the current rotation.
     *
     * @param _drEnd the delta rho from the current position to the end position.
     * @param _dtEnd the delta theta from the current position to the end position.
     * @param _drCenter the delta rho from the current position to the spiral center position.
     * @param _dtCenter the delta theta from the current position to the spiral center position.
     * @param _turns the number of complete turns (positive for clockwise, negative for anti-clockwise).
     */
    public void spiralToRT( final double _drEnd, final double _dtEnd, final double _drCenter, final double _dtCenter, final int _turns ) {
        spiralTo( Point.fromRT( _drEnd, _dtEnd ), Point.fromRT( _drCenter, _dtCenter ), 0, _turns );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position around the given center point.  The center point's location is relative to
     * the current position, in the current rotation.  Positive arc angles are drawn clockwise, negative arc angles anti-clockwise.
     *
     * @param _center the center point of the arc, relative to the current position and in the current rotation.
     * @param _arcAngle the angle of the arc to be drawn.
     */
    public void arcAround( final Point _center, final double _arcAngle ) {
        Line line = CircularArc.fromCenter( maxPointDistance, _center.x, _center.y, _arcAngle );
        draw( line );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position around the center point at the given delta x and delta y from the current
     * position, in the current rotation.
     *
     * @param _dX the difference in x between the center position and the current position.
     * @param _dY the difference in y between the center position and the current position.
     * @param _arcAngle the angular fraction of the arc to draw.
     */
    public void arcAroundXY( final double _dX, final double _dY, final double _arcAngle ) {
        arcAround( Point.fromXY( _dX, _dY ), _arcAngle );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position around the center point at the given delta rho and delta theta from the current
     * position, in the current rotation.
     *
     * @param _dRho the difference in rho between the center position and the current position.
     * @param _dTheta the difference in theta between the center position and the current position.
     * @param _arcAngle the angular fraction of the arc to draw.
     */
    public void arcAroundRT( final double _dRho, final double _dTheta, final double _arcAngle ) {
        arcAround( Point.fromRT( _dRho, _dTheta ), _arcAngle );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position to the given point relative to the current position, in the current rotation.
     * Positive arc angles are drawn clockwise, negative arc angles anti-clockwise.
     *
     * @param _end
     * @param _arcAngle
     */
    public void arcTo( final Point _end, final double _arcAngle ) {
        Line line = CircularArc.fromEndPoint( maxPointDistance, _end.x, _end.y, _arcAngle );
        draw( line );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position to the point at the given delta x and delta y from the current position, in
     * the current rotation.
     *
     * @param _dX the difference in x between the ending position and the current position.
     * @param _dY the difference in y between the ending position and the current position.
     * @param _arcAngle the angular fraction of the arc to draw.
     */
    public void arcToXY( final double _dX, final double _dY, final double _arcAngle ) {
        arcTo( Point.fromXY( _dX, _dY ), _arcAngle );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position to the point at the given delta rho and delta theta from the current position,
     * in the current rotation.
     *
     * @param _dRho the difference in rho between the ending position and the current position.
     * @param _dTheta the difference in theta between the ending position and the current position.
     * @param _arcAngle the angular fraction of the arc to draw.
     */
    public void arcToRT( final double _dRho, final double _dTheta, final double _arcAngle ) {
        arcTo( Point.fromRT( _dRho, _dTheta ), _arcAngle );
    }


    /**
     * Draws a cubic Bézier curve from the current position to the given end point, with the curve controlled by the two given control point.  Control point 1
     * is relative to the current position, and controls the slope of the line as it leaves the current position.  Control point 2 is relative to the end
     * point, and controls the slope of the line as it approaches the end point.  The end point is relative to the current position.
     *
     * @param _cp1 control point 1, relative to the current position.
     * @param _cp2 control point 2, relative to the end point.
     * @param _end the end point, relative to the current position.
     */
    public void curveTo( final Point _cp1, final Point _cp2, final Point _end ) {

        // convert end-relative CP2 to be current position-relative...
        Point cp2 = _end.sum( _cp2 );
        Line line = new CubicBezierCurve( maxPointDistance, _cp1.x, _cp1.y, cp2.x, cp2.y, _end.x, _end.y );
        draw( line );
    }


    /**
     * Draws a cubic Bézier curve from the current position to the given end position, with the curve controlled by the two given control point positions.
     * All of the positions are relative to the current ball position, expressed as a delta x and delta y in the current rotation.  Control point 1
     * controls the slope of the line as it leaves the starting point.  Control point 2 controls the slope of the line as it approaches the end point.
     *
     * @param _cp1X the difference in x between control point 1 and the current position.
     * @param _cp1Y the difference in y between control point 1 and the current position.
     * @param _cp2X the difference in x between control point 2 and the ending position.
     * @param _cp2Y the difference in y between control point 2 and the ending position.
     * @param _endX the difference in x between the ending position and the current position.
     * @param _endY the difference in y between the ending position and the current position.
     */
    public void curveToXY( final double _cp1X, final double _cp1Y, final double _cp2X, final double _cp2Y, final double _endX, final double _endY ) {
        curveTo( Point.fromXY( _cp1X, _cp1Y ), Point.fromXY( _cp2X, _cp2Y ), Point.fromXY( _endX, _endY ) );
    }


    /**
     * Draws a cubic Bézier curve from the current position to the given end position, with the curve controlled by the two given control point positions.
     * All of the positions are relative to the current ball position, expressed as a delta rho and delta theta in the current rotation.  Control point 1
     * controls the slope of the line as it leaves the starting point.  Control point 2 controls the slope of the line as it approaches the end point.
     *
     * @param _cp1Rho   the difference in rho between control point 1 and the current position.
     * @param _cp1Theta the difference in theta between control point 1 and the current position.
     * @param _cp2Rho   the difference in rho between control point 2 and the ending position.
     * @param _cp2Theta the difference in theta between control point 2 and the ending position.
     * @param _endRho   the difference in rho between the ending position and the current position.
     * @param _endTheta the difference in theta between the ending position and the current position.
     */
    public void curveToRT( final double _cp1Rho, final double _cp1Theta, final double _cp2Rho, final double _cp2Theta,
                           final double _endRho, final double _endTheta ) {
        curveTo( Point.fromRT( _cp1Rho, _cp1Theta ), Point.fromRT( _cp2Rho, _cp2Theta ), Point.fromRT( _endRho, _endTheta ) );
    }


    /**
     * Draws a circle at the current rho for the given number of revolutions.  If the number of orbits is positive, they will be
     * in a clockwise direction, otherwise counterclockwise.
     *
     * @param _orbits the number of orbits to make.
     */
    public void orbit( final int _orbits ) {
        double theta = _orbits * 2 * PI;
        Position np = new PolarPosition( currentPosition.getRho(), theta );
        vertices.add( np );
        currentPosition = np;
    }


    /**
     * Draws a straight line from the current position to the table center at the same theta as the current position.
     */
    public void home() {
        Position np = new PolarPosition( 0, currentPosition.getTheta() );
        vertices.add( np );
        currentPosition = np;
    }


    /**
     * Arc around the table center by the given angle.  The rho is not changed.
     */
    public void arcAroundTableCenter( final double _theta ) {
        Position np = new PolarPosition( currentPosition.getRho(), currentPosition.getTheta() + _theta );
        vertices.add( np );
        currentPosition = np;
    }


    /**
     * Draws a straight line to the given theta (in the current rotation) on the edge of the table.
     *
     * @param _theta the position on the edge to draw a line to.
     */
    public void edge( final double _theta ) {
        lineToRT( 1, _theta );
    }


    // TODO: add log...
    public void write( final String _fileName ) throws IOException {

        massage();

        Position current = Position.CENTER;
        StringBuilder out = new StringBuilder();
        for( Position position : vertices ) {
            emit( out, position );
        }
        Path path = new File( _fileName ).toPath();
        byte[] bytes = out.toString().getBytes();
        Files.write( path, bytes );
    }


    // TODO: optimize by removing points along circle (esp. at rho == 1)...
    // do several things to make sure the .thr file is safe and optimal...
    private void massage() {

        // add two identical entries, forcing the rho to be either 0 or 1 (whichever is closer)
        // learned from Bruce Shapiro that NOT doing this could introduce positioning errors...
        Position last = vertices.get( vertices.size() - 1 );
        double endRho = (last.getRho() >= 0.5) ? 1 : 0;
        Position term = new PolarPosition( endRho, last.getTheta() );
        vertices.add( term );
        vertices.add( term );

        // clamp all vertice rho values to the range [0..1]...
        for( int i = 0; i < vertices.size(); i++ ) {

            Position vertice = vertices.get( i );
            double clampedRho = max( 0, min( 1, vertice.getRho() ) );
            if( vertice.getRho() == clampedRho )
                continue;
            vertices.set( i, new PolarPosition( clampedRho, vertice.getTheta() ) );
        }
    }


    private static final DecimalFormat THETA_FORMAT = new DecimalFormat( "#.########" );
    private static final DecimalFormat RHO_FORMAT   = new DecimalFormat( "#.########" );

    private void emit( final StringBuilder _out, final Position _vertice ) {
        _out.append( THETA_FORMAT.format( _vertice.getTheta() ) );
        _out.append( ' ' );
        _out.append( RHO_FORMAT.format( _vertice.getRho() ) );
        _out.append( '\n' );
    }


    private static final int BORDER_WIDTH = 100;

    public void renderPNG( final String _fileName ) throws IOException {

        int width  = 1 + 2 * BORDER_WIDTH + 2 * pixelsPerRho;
        int height = 1 + 2 * BORDER_WIDTH + 2 * pixelsPerRho;

        BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );

        Graphics2D g = bi.createGraphics();
        g.setColor( Color.lightGray );
        g.fillRect( 0, 0, width, height );
        g.setColor( Color.WHITE );
        g.fillOval( BORDER_WIDTH, BORDER_WIDTH, width - 2 * BORDER_WIDTH, height - 2 * BORDER_WIDTH );
        g.setColor( Color.BLACK );

        // draw a spiral line for each path we have...
        double cx = 0;
        double cy = 0;
        for( int i = 1; i < vertices.size(); i++ ) {

            // get our from and to, and delta theta...
            Position fromPos = vertices.get( i - 1 );
            Position toPos = vertices.get( i );
            Point from = Point.fromPosition( fromPos );
            Point to   = Point.fromPosition( toPos );
            double dTheta = toPos.getTheta() - fromPos.getTheta();

            if( to.rho > 0.48 )
                hashCode();

            // if the distance is zero and we don't have a full circle, just move to the next one...
            if( (from.distanceFrom( to ) < 0.0001) && ((dTheta == 0) || ( abs( Utils.normalizeTheta( dTheta ) ) > 0.0001 ) ) ) continue;

            // calculate our spiral's turns...
            int t = Utils.getTurnsFromTheta( dTheta );

            // figure out our center theta, if either the from or to points are at the center...
            double centerTheta = 0;  // the default...
            if( from.rho < 1E-10 ) centerTheta = Utils.normalizeTheta( from.theta );
            if( to.rho < 1E-10 ) centerTheta = Utils.normalizeTheta( to.theta );

            // draw a spiral line for each pair of points within the Sisyphus line...
            Point center = Point.fromRT( from.rho, from.theta + PI );
            Line line = new ArithmeticSpiral( 10.0/width, from.vectorTo( to ), center, centerTheta, t );
            List<Delta> deltas = line.getDeltas();
            int xFrom = pixelize( cx );
            int yFrom = pixelize( -cy );
            for( int j = 0; j < deltas.size(); j++ ) {

                Delta delta = deltas.get( j );
                cx += delta.x;
                cy += delta.y;
                int xTo = pixelize( cx );
                int yTo = pixelize( -cy );
                g.drawLine( xFrom, yFrom, xTo, yTo );
                xFrom = xTo;
                yFrom = yTo;
            }
        }

        ImageIO.write(bi, "PNG", new File( _fileName ));
    }


    private int pixelize( double _value ) {
        return BORDER_WIDTH + pixelsPerRho + (int) round( _value * pixelsPerRho );
    }


    /**
     * Draw the given line starting from the current position, and in the current rotation.
     *
     * @param _line the line to draw.
     */
    public void draw( final Line _line ) {

        // first we use the deltas in the line, the current position, and the current transform to produce a series of actual table points...
        List<Delta> deltas = _line.getDeltas();
        List<Position> points = new ArrayList<>( deltas.size() + 1 );
        points.add( currentPosition );

        // calculate all the actual table positions for the deltas in our line...
        double cumDX = 0;
        double cumDY = 0;
        for( Delta delta : deltas ) {

            // accumulate the delta x and y...
            cumDX += delta.x;
            cumDY += delta.y;

            // apply our rotation...
            double r = hypot( delta.x, delta.y );
            double t = Utils.getTheta( delta.x, delta.y ) + currentRotation;
            double x = r * sin( t );
            double y = r * cos( t );

            // update the current position and accumulate the point...
            currentPosition = currentPosition.fromDeltaXY( x, y );
            points.add( currentPosition );
        }

        // update the current relative position...
        currentRelativePosition = currentRelativePosition.sum( Point.fromXY( cumDX, cumDY ) );

        if( !mute ) {
            SisyphusFitter fitter = new SisyphusFitter( points, this );
            fitter.generateVertices();
            vertices.addAll( fitter.getVertices() );
        }
    }


    /**
     * Sets the current relative position to x,y 0,0.
     */
    public void zeroCurrentRelativePosition() {
        currentRelativePosition = Point.fromXY( 0, 0 );
    }


    /**
     * Sets the current relative position to the given point.
     *
     * @param _position the new current relative position.
     */
    public void setCurrentRelativePosition( final Point _position ) {
        currentRelativePosition = _position;
    }


    /**
     * Returns the current relative position.
     *
     * @return the current relative position.
     */
    public Point getCurrentRelativePosition() {
        return currentRelativePosition;
    }


    /**
     * Returns a vector from the current relative position to the given relative position.
     *
     * @param _destination the relative position to get a vector to.
     * @return the vector to the destination position.
     */
    public Point vectorTo( final Point _destination ) {
        return currentRelativePosition.vectorTo( _destination );
    }


    /**
     * Returns a marker for the current position.
     *
     * @return a marker for the current position.
     */
    public Marker marker() {
        return new Marker( currentRelativePosition, this );
    }


    /**
     * Rotate the canvas about the current center to the given angle in radians.
     *
     * @param _theta the angle to rotate the canvas to, in radians.
     */
    public void rotateTo( final double _theta ) {
        currentRotation = _theta;
    }


    /**
     * Rotate the canvas by the given angle in radians from its current rotation.  Positive angles rotate the canvas clockwise, negative angles anticlockwise.
     *
     * @param _deltaTheta the angle to rotate the canvas by.
     */
    public void rotateBy( final double _deltaTheta ) {
        currentRotation += _deltaTheta;
    }


    public void clear() {
        vertices.clear();
    }


    public double getMaxPointDistance() {
        return maxPointDistance;
    }


    public void setMaxPointDistance( final double _maxPointDistance ) {
        maxPointDistance = _maxPointDistance;
    }


    public double getFitToleranceRho() {
        return fitToleranceRho;
    }


    public Model getModel() {
        return model;
    }


    public void setModel( final Model _model ) {
        model = _model;
    }


    public double getMaxFitErrorMeters() {
        return maxFitErrorMeters;
    }


    public void setMaxFitErrorMeters( final double _maxFitErrorMeters ) {
        maxFitErrorMeters = _maxFitErrorMeters;
    }


    public int getPixelsPerRho() {
        return pixelsPerRho;
    }


    /**
     * Sets the number of pixels per rho unit for the generation of PNG files.  Effectively this sets the radius of the generated file in pixels.  For
     * instance, a value of 1000 pixels per rho would result in a 2000 x 2000 pixel PNG file.
     *
     * @param _pixelsPerRho the number of PNG file pixels in one rho unit.
     */
    public void setPixelsPerRho( final int _pixelsPerRho ) {
        pixelsPerRho = _pixelsPerRho;
    }


    /**
     * Returns the current absolute position of the ball on the Sisyphus table, as held by the drawing context.
     *
     * @return The current absolute position of the ball on the Sisyphus table.
     */
    public Position getCurrentPosition() {
        return currentPosition;
    }


    public double getEraseSpacing() {
        return eraseSpacing;
    }


    public void setEraseSpacing( final double _eraseSpacing ) {
        eraseSpacing = _eraseSpacing;
    }


    private static class TransformState {
        private double rotation;
        private Position translation;
    }


    public boolean isMute() {
        return mute;
    }


    public void setMute( final boolean _mute ) {
        mute = _mute;
    }


    public double getCurrentRotation() {
        return currentRotation;
    }

}
