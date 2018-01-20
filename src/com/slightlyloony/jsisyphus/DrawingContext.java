package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.lines.ArithmeticSpiral;
import com.slightlyloony.jsisyphus.lines.CircularArc;
import com.slightlyloony.jsisyphus.lines.Line;
import com.slightlyloony.jsisyphus.lines.StraightLine;
import com.slightlyloony.jsisyphus.models.Model;
import com.slightlyloony.jsisyphus.positions.CartesianPosition;
import com.slightlyloony.jsisyphus.positions.PolarPosition;
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
    private double eraseSpacing;  // the erase spiral radial spacing in meters...
    private final Transformer transformer;


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
        transformer = new Transformer();
    }


    /**
     * Creates a new instance of this class that assumes the ball is at the center of the Sisyphus table.
     */
    public DrawingContext() {
        this( Position.CENTER );
    }


    /**
     * Erases from the current position to the given absolute position, spiraling either in or out depending on whether the "to" rho is less than or greater
     * than the current position's rho.  Note that if there are multiple turns involved in the erasure, the end position's number of turns will be adjusted
     * accordingly.  The X, Y position will be identical; only the number of turns may be adjusted.
     *
     * @param _absoluteEnd the absolute position to end the erase on.
     */
    public void eraseTo( final Position _absoluteEnd ) {

        // figure out how many turns to make...
        double drhopt = eraseSpacing / model.tableRadiusMeters();  // gives us the change in rho per turn...
        double drho = _absoluteEnd.getRho() - currentPosition.getRho();  // change in rho over the entire spiral...
        double turns = Math.ceil( Math.abs( drho / drhopt ));  // gives us the number of turns we'll actually need...
        boolean eraseClockwise = (drho < 0);
        double dtheta = Utils.normalizeTheta( _absoluteEnd.getTheta() ) - Utils.normalizeTheta( currentPosition.getTheta() ); // delta theta after turns...
        double endTheta = Math.PI * (eraseClockwise ? 2 : -2) * turns + currentPosition.getTheta() + dtheta;
        Position newEnd = new PolarPosition( _absoluteEnd.getRho(), endTheta );
        Line line = new ArithmeticSpiral( this, currentPosition, newEnd );
        draw( line );
    }


    /**
     * Erases from the current position to the given relative X, Y, turns position, spiraling either in or out depending on whether the "to" rho is less than
     * or greater than the current position's rho.
     *
     * @param dX the difference in x between the ending position and the current position.
     * @param dY the difference in y between the ending position and the current position.
     * @param dTurns the difference in turns between the ending position and the current position.
     */
    public void eraseToXYT( final double dX, final double dY, final int dTurns ) {
        eraseTo( new CartesianPosition( currentPosition.getX() + dX, currentPosition.getY() + dY, currentPosition.getTurns() + dTurns ) );
    }


    /**
     * Erases from the current position to the given relative rho, theta position, spiraling either in or out depending on whether the "to" rho is less than
     * or greater than the current position's rho.
     *
     * @param dRho the difference in rho between the ending position and the current position.
     * @param dTheta the difference in theta between the ending position and the current position.
     */
    public void eraseToRT( final double dRho, final double dTheta ) {
        eraseTo( new PolarPosition( currentPosition.getRho() + dRho, currentPosition.getTheta() + dTheta ) );
    }


    /**
     * Draws a straight line from the current position to the given absolute line end position.
     *
     * @param _absoluteEnd the relative position for the end of the new line.
     */
    public void lineTo( final Position _absoluteEnd ) {
        Line line = new StraightLine( this, currentPosition, _absoluteEnd );
        draw( line );
    }


    /**
     * Draws a straight line from the current position to the point at the given delta x, delta y, and delta turns from the current position.
     *
     * @param dX the difference in x between the ending position and the current position.
     * @param dY the difference in y between the ending position and the current position.
     * @param dTurns the difference in turns between the ending position and the current position.
     */
    public void lineToXYT( final double dX, final double dY, final int dTurns ) {
        lineTo( new CartesianPosition( currentPosition.getX() + dX, currentPosition.getY() + dY, currentPosition.getTurns() + dTurns ) );
    }


    /**
     * Draws a straight line from the current position to the point at the given delta rho and delta theta from the current position.
     *
     * @param dRho the difference in rho between the ending position and the current position.
     * @param dTheta the difference in theta between the ending position and the current position.
     */
    public void lineToRT( final double dRho, final double dTheta ) {
        lineTo( new PolarPosition( currentPosition.getRho() + dRho, currentPosition.getTheta() + dTheta ) );
    }


    /**
     * Draws an arithmetic spiral from the current position to the given absolute line end position.
     *
     * @param _absoluteEnd the relative position for the end of the new line.
     */
    public void spiralTo( final Position _absoluteEnd ) {
        Line line = new ArithmeticSpiral( this, currentPosition, _absoluteEnd );
        draw( line );
    }


    /**
     * Draws an arithmetic spiral from the current position to the point at the given delta x, delta y, and delta turns from the current position.
     *
     * @param dX the difference in x between the ending position and the current position.
     * @param dY the difference in y between the ending position and the current position.
     * @param dTurns the difference in turns between the ending position and the current position.
     */
    public void spiralToXYT( final double dX, final double dY, final int dTurns ) {
        spiralTo( new CartesianPosition( currentPosition.getX() + dX, currentPosition.getY() + dY, currentPosition.getTurns() + dTurns ) );
    }


    /**
     * Draws an arithmetic spiral from the current position to the point at the given delta rho and delta theta from the current position.
     *
     * @param dRho the difference in rho between the ending position and the current position.
     * @param dTheta the difference in theta between the ending position and the current position.
     */
    public void spiralToRT( final double dRho, final double dTheta ) {
        spiralTo( new PolarPosition( currentPosition.getRho() + dRho, currentPosition.getTheta() + dTheta ) );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position to the given absolute line end position.
     *
     * @param _absoluteEnd the relative position for the end of the new line.
     * @param _arcAngle the angular fraction of the arc to draw.
     */
    public void arcTo( final Position _absoluteEnd, final double _arcAngle ) {
        Line line = new CircularArc( this, currentPosition, _absoluteEnd, _arcAngle );
        draw( line );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position to the point at the given delta x, delta y, and delta turns from the current position.
     *
     * @param dX the difference in x between the ending position and the current position.
     * @param dY the difference in y between the ending position and the current position.
     * @param dTurns the difference in turns between the ending position and the current position.
     * @param _arcAngle the angular fraction of the arc to draw.
     */
    public void arcToXYT( final double dX, final double dY, final int dTurns, final double _arcAngle ) {
        arcTo( new CartesianPosition( currentPosition.getX() + dX, currentPosition.getY() + dY, currentPosition.getTurns() + dTurns ), _arcAngle );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position to the point at the given delta rho and delta theta from the current position.
     *
     * @param dRho the difference in rho between the ending position and the current position.
     * @param dTheta the difference in theta between the ending position and the current position.
     * @param _arcAngle the angular fraction of the arc to draw.
     */
    public void arcToRT( final double dRho, final double dTheta, final double _arcAngle ) {
        arcTo( new PolarPosition( currentPosition.getRho() + dRho, currentPosition.getTheta() + dTheta ), _arcAngle );
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


    public void renderPNG( final String _fileName ) throws IOException {

        int width  = 2 * pixelsPerRho;
        int height = 2 * pixelsPerRho;

        BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );

        Graphics2D g = bi.createGraphics();
        g.setColor( Color.lightGray );
        g.fillRect( 0, 0, width + 1, height + 1 );
        g.setColor( Color.WHITE );
        g.fillOval( 0, 0, width + 1, height + 1 );
        g.setColor( Color.BLACK );

        // draw a spiral line for each path we have...
        for( int i = 1; i < vertices.size(); i++ ) {

            // draw a spiral line for each pair of points within the Sisyphus line...
            Line line = new ArithmeticSpiral( this, vertices.get( i - 1 ), vertices.get( i ) );
            List<Position> points = line.getPoints();
            for( int j = 0; j < points.size() - 1; j++ ) {

                int x1 = pixelsPerRho + (int) Math.round( points.get( j ).getX() * pixelsPerRho );
                int y1 = pixelsPerRho + (int) Math.round( points.get( j ).getY() * -pixelsPerRho );
                int x2 = pixelsPerRho + (int) Math.round( points.get( j + 1 ).getX() * pixelsPerRho );
                int y2 = pixelsPerRho + (int) Math.round( points.get( j + 1 ).getY() * -pixelsPerRho );
                g.drawLine( x1, y1, x2, y2 );
            }
        }

        ImageIO.write(bi, "PNG", new File( _fileName ));
    }


    public void draw( final Line _line ) {
        Line line = transformer.transform( this, _line );
        SisyphusFitter fitter = new SisyphusFitter( line.getPoints(), this );
        fitter.generate();
        vertices.addAll( fitter.getVertices() );
        currentPosition = transformer.untransform( line.getEnd() );
    }


    /**
     * Rotate the canvas about the current center to the given angle in radians.
     *
     * @param _theta the angle to rotate the canvas to, in radians.
     */
    public void rotateTo( final double _theta ) {

        // calculate the delta theta and handle it through that method...
        rotateBy( _theta - transformer.getRotation() );
        transformer.setRotation( _theta );
    }


    /**
     * Rotate the canvas by the given angle in radians from its current rotation.  Positive angles rotate the canvas clockwise, negative angles anticlockwise.
     *
     * @param _deltaTheta the angle to rotate the canvas by.
     */
    public void rotateBy( final double _deltaTheta ) {

        // first rotate the canvas...
        transformer.setRotation( transformer.getRotation() + _deltaTheta );

        // then adjust the current position...
        currentPosition = new PolarPosition( currentPosition.getRho(), currentPosition.getTheta() - _deltaTheta );
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
}
