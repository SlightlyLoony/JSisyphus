package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.lines.ArithmeticSpiral;
import com.slightlyloony.jsisyphus.lines.CircularArc;
import com.slightlyloony.jsisyphus.lines.Line;
import com.slightlyloony.jsisyphus.lines.StraightLine;
import com.slightlyloony.jsisyphus.models.Model;
import com.slightlyloony.jsisyphus.positions.Position;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
    }


    /**
     * Creates a new instance of this class that assumes the ball is at the center of the Sisyphus table.
     */
    public DrawingContext() {
        this( Position.CENTER );
    }


    /**
     * Erases from the current position to the given relative X, Y, turns position, spiraling either in or out depending on whether the "to" rho is less than
     * or greater than the current position's rho.  The number of turns made will ensure that the spacing is no more than the current erase spacing.
     *
     * @param _dX the difference in x between the end position and the current position.
     * @param _dY the difference in y between the end position and the current position.
     */
    public void eraseToXY( final double _dX, final double _dY ) {
        double theta = Utils.getTheta( _dX, _dY );
        double rho = Math.hypot( _dX, _dY );
        eraseToRT( rho, theta );
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

        // figure out where our end point is, both as delta from current position and as actual...
        double x = _dRho * Math.sin( _dTheta );
        double y = _dRho * Math.cos( _dTheta );
        Position end = currentPosition.fromDeltaXY( x, y );

        // figure the delta rho and theta for the spiral...
        double dsRho = end.getRho() - currentPosition.getRho();
        double dsTheta = end.getTheta() - currentPosition.getTheta();

        // figure out how many turns to make...
        double drhopt = eraseSpacing / model.tableRadiusMeters();        // gives us the change in rho per turn...
        int turns = (int) Math.floor( Math.abs( dsRho / drhopt ));       // gives us the number of turns we'll actually need...
        if( dsTheta < 0 ) turns = -turns;                                // correct for the direction...

        Line line = new ArithmeticSpiral( maxPointDistance, x, y, -currentPosition.getX(), -currentPosition.getY(), turns );
        draw( line );
    }


    /**
     * Draws a straight line from the current position to the point at the given delta x and delta y from the current position, in the current rotation.
     *
     * @param _dX the difference in x between the ending position and the current position.
     * @param _dY the difference in y between the ending position and the current position.
     */
    public void lineToXY( final double _dX, final double _dY ) {
        Line line = new StraightLine( maxPointDistance, _dX, _dY );
        draw( line );
    }


    /**
     * Draws a straight line from the current position to the point at the given delta rho and delta theta from the current position, in the current
     * rotation.
     *
     * @param _dRho the difference in rho between the ending position and the current position.
     * @param _dTheta the difference in theta between the ending position and the current position.
     */
    public void lineToRT( final double _dRho, final double _dTheta ) {
        Line line = new StraightLine( maxPointDistance, _dRho * Math.sin( _dTheta ), _dRho * Math.cos( _dTheta ) );
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
        Line line = new ArithmeticSpiral( maxPointDistance, _dxEnd, _dyEnd, _dxCenter, _dyCenter, _turns );
        draw( line );
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
        double dxe = _drEnd * Math.sin( _dtEnd );
        double dye = _drEnd * Math.cos( _dtEnd );
        double dxc = _drCenter * Math.sin( _dtCenter );
        double dyc = _drCenter * Math.cos( _dtCenter );
        spiralToXY( dxe, dye, dxc, dyc, _turns );
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
        Line line = CircularArc.fromCenter( maxPointDistance, _dX, _dY, _arcAngle );
        draw( line );
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
        double dx = _dRho * Math.sin( _dTheta );
        double dy = _dRho * Math.cos( _dTheta );
        arcAroundXY( dx, dy, _arcAngle );
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
        Line line = CircularArc.fromEndPoint( maxPointDistance, _dX, _dY, _arcAngle );
        draw( line );
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
        double dx = _dRho * Math.sin( _dTheta );
        double dy = _dRho * Math.cos( _dTheta );
        arcToXY( dx, dy, _arcAngle );
    }


    /**
     * Draws a circle at the current rho for the given number of revolutions.  If the number of orbits is positive, they will be
     * in a clockwise direction, otherwise counterclockwise.
     *
     * @param _orbits the number of orbits to make.
     */
    public void orbit( final int _orbits ) {
        for( int i = 0; i < Math.abs( _orbits ); i++ ) {
            arcAroundXY( -currentPosition.getX(), -currentPosition.getY(), ((_orbits < 0) ? -2 : 2) * Math.PI );
        }
    }


    /**
     * Draws a straight line from the current position to the center.
     */
    public void home() {
        lineToXY( -currentPosition.getX(), -currentPosition.getY() );
    }


    /**
     * Draws a straight line to the given theta (in the current rotation) on the edge of the table.
     *
     * @param _theta the position on the edge to draw a line to.
     */
    public void edge( final double _theta ) {
        lineToRT( 1, _theta );
    }


    public void write( final String _fileName ) throws IOException {
        // TODO: clamp at rho == 1...
        // TODO: optimize by removing points along circle (esp. at rho == 1)...

        Position current = Position.CENTER;
        StringBuilder out = new StringBuilder();
        for( Position position : vertices ) {
            out.append( position.toVertice() );
        }
        out.append( vertices.get( vertices.size() - 1 ).toVertice() );  // repeat the last vertice; we don't know why this is needed...
        Path path = new File( _fileName ).toPath();
        byte[] bytes = out.toString().getBytes();
        Files.write( path, bytes );
    }


    // TODO: actually emulate the Sisyphus table's motion?
    public void renderPNG( final String _fileName ) throws IOException {

        int width  = 1 + 2 * pixelsPerRho;
        int height = 1 + 2 * pixelsPerRho;

        BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );

        Graphics2D g = bi.createGraphics();
        g.setColor( Color.lightGray );
        g.fillRect( 0, 0, width, height );
        g.setColor( Color.WHITE );
        g.fillOval( 0, 0, width, height );
        g.setColor( Color.BLACK );

        // draw a spiral line for each path we have...
        double cx = 0;
        double cy = 0;
        for( int i = 1; i < vertices.size(); i++ ) {

            // get our from and to, and delta theta...
            Position from = vertices.get( i - 1 );
            Position to = vertices.get( i );
            double dTheta = to.getTheta() - from.getTheta();

            // if the distance is zero, just move to the next one...
            if( from.distanceFrom( to ) < 0.001 ) continue;

            // calculate our spiral's parameters...
            int t = Utils.getTurnsFromTheta( dTheta );
            double ex = to.deltaX( from );
            double ey = to.deltaY( from );

            // draw a spiral line for each pair of points within the Sisyphus line...
            Line line = new ArithmeticSpiral( 10.0/width, ex, ey, -from.getX(), -from.getY(), t );
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
        return pixelsPerRho + (int) Math.round( _value * pixelsPerRho );
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
        for( Delta delta : deltas ) {

            // apply our rotation...
            double r = Math.hypot( delta.x, delta.y );
            double t = Utils.getTheta( delta.x, delta.y ) + currentRotation;
            double x = r * Math.sin( t );
            double y = r * Math.cos( t );

            // add the current position...
            currentPosition = currentPosition.fromDeltaXY( x, y );
            points.add( currentPosition );
        }

        if( !mute ) {
            SisyphusFitter fitter = new SisyphusFitter( points, this );
            fitter.generateVertices();
            vertices.addAll( fitter.getVertices() );
        }
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
}
