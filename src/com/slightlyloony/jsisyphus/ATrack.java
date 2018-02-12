package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.lines.Line;
import com.slightlyloony.jsisyphus.models.Model;
import com.slightlyloony.jsisyphus.positions.Position;

import java.io.File;
import java.io.IOException;

/**
 * Base class for all tracks.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public abstract class ATrack {

    protected final String trackFileName;
    protected final String pngFileName;
    protected final DrawingContext dc;


    protected ATrack( final String baseFileName ) {
        trackFileName = baseFileName + ".thr";
        pngFileName = baseFileName + ".png";
        dc = new DrawingContext();
    }


    public void traceIfNeeded() throws IOException {
        if( alreadyTraced() )
            return;
        trace();
    }


    protected boolean alreadyTraced() {
        return new File( trackFileName ).exists() && new File( pngFileName ).exists();
    }


    protected abstract void trace() throws IOException;


    /**
     * Erases from the current position to the given end point, spiraling either in or out depending on whether the given end point is inside or outside the
     * current position.  The number of turns made will ensure that the spacing is no more than the current erase spacing.
     *
     * @param _end the end point of the erasure.
     */
    public void eraseTo( final Point _end ) {
        dc.eraseTo( _end );
    }


    /**
     * Erases from the current position to the given relative X, Y, turns position, spiraling either in or out depending on whether the "to" rho is less than
     * or greater than the current position's rho.  The number of turns made will ensure that the spacing is no more than the current erase spacing.
     *  @param _dX the difference in x between the end position and the current position.
     * @param _dY the difference in y between the end position and the current position.
     */
    public void eraseToXY( final double _dX, final double _dY ) {
        dc.eraseToXY( _dX, _dY );
    }


    /**
     * Erases from the current position to the given relative rho, theta position, spiraling either in or out depending on whether the "to" rho is less than
     * or greater than the current position's rho.  The number of turns made will ensure that the spacing is no more than the current erase spacing.  Note
     * that this number of turns may result in an ending theta that is different than that specified here, but only by a multiple of 2 * pi (360 degrees).
     *  @param _dRho the difference in rho between the ending position and the current position.
     * @param _dTheta the difference in theta between the ending position and the current position.
     */
    public void eraseToRT( final double _dRho, final double _dTheta ) {
        dc.eraseToRT( _dRho, _dTheta );
    }


    /**
     * Draws a straight line from the current position to the given point.  The given point's coordinates are considered relative to the current position, and
     * in the current rotation.
     *
     * @param _point the point to draw a line to, with coordinates relative to the current position.
     */
    public void lineTo( final Point _point ) {
        dc.lineTo( _point );
    }


    /**
     * Draws a straight line from the current position to the point at the given delta x and delta y from the current position, in the current rotation.
     *  @param _dX the difference in x between the ending position and the current position.
     * @param _dY the difference in y between the ending position and the current position.
     */
    public void lineToXY( final double _dX, final double _dY ) {
        dc.lineToXY( _dX, _dY );
    }


    /**
     * Draws a straight line from the current position to the point at the given delta rho and delta theta from the current position, in the current
     * rotation.
     *  @param _dRho the difference in rho between the ending position and the current position.
     * @param _dTheta the difference in theta between the ending position and the current position.
     */
    public void lineToRT( final double _dRho, final double _dTheta ) {
        dc.lineToRT( _dRho, _dTheta );
    }


    /**
     * Draws an arithmetic spiral from the current position to the given end point.  The center of the spiral will be at the given center point.
     * The spiral will have the given number of complete turns plus (possibly) a partial turn after that to reach the specified end point.  Both the end
     * position and center position are relative to the current position, and in the current rotation.
     *  @param _end the end point of the spiral.
     * @param _center the center point of the spiral.
     * @param _centerTheta the theta for either the start or end of the spiral, if it hits the center.
     * @param _turns the number of complete turns (positive for clockwise, negative for anti-clockwise).
     */
    public void spiralTo( final Point _end, final Point _center, final double _centerTheta, final int _turns ) {
        dc.spiralTo( _end, _center, _centerTheta, _turns );
    }


    /**
     * Draws an arithmetic spiral from the current position to the given end point position.  The center of the spiral will be at the given center position.
     * The spiral will have the given number of complete turns plus (possibly) a partial turn after that to reach the specified end point.  Both the end
     * position and center position are relative to the current position, and in the current rotation.
     *  @param _dxEnd the delta x from the current position to the end position.
     * @param _dyEnd the delta y from the current position to the end position.
     * @param _dxCenter the delta x from the current position to the spiral center position.
     * @param _dyCenter the delta y from the current position to the spiral center position.
     * @param _turns the number of complete turns (positive for clockwise, negative for anti-clockwise).
     */
    public void spiralToXY( final double _dxEnd, final double _dyEnd, final double _dxCenter, final double _dyCenter, final int _turns ) {
        dc.spiralToXY( _dxEnd, _dyEnd, _dxCenter, _dyCenter, _turns );
    }


    /**
     * Draws an arithmetic spiral from the current position to the given end point position.  The center of the spiral will be at the given center position.
     * The spiral will have the given number of complete turns plus (possibly) a partial turn after that to reach the specified end point.  Both the end
     * position and center position are relative to the current position, and in the current rotation.
     *  @param _drEnd the delta rho from the current position to the end position.
     * @param _dtEnd the delta theta from the current position to the end position.
     * @param _drCenter the delta rho from the current position to the spiral center position.
     * @param _dtCenter the delta theta from the current position to the spiral center position.
     * @param _turns the number of complete turns (positive for clockwise, negative for anti-clockwise).
     */
    public void spiralToRT( final double _drEnd, final double _dtEnd, final double _drCenter, final double _dtCenter, final int _turns ) {
        dc.spiralToRT( _drEnd, _dtEnd, _drCenter, _dtCenter, _turns );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position around the given center point.  The center point's location is relative to
     * the current position, in the current rotation.  Positive arc angles are drawn clockwise, negative arc angles anti-clockwise.
     *  @param _center the center point of the arc, relative to the current position and in the current rotation.
     * @param _arcAngle the angle of the arc to be drawn.
     */
    public void arcAround( final Point _center, final double _arcAngle ) {
        dc.arcAround( _center, _arcAngle );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position around the center point at the given delta x and delta y from the current
     * position, in the current rotation.
     *  @param _dX the difference in x between the center position and the current position.
     * @param _dY the difference in y between the center position and the current position.
     * @param _arcAngle the angular fraction of the arc to draw.
     */
    public void arcAroundXY( final double _dX, final double _dY, final double _arcAngle ) {
        dc.arcAroundXY( _dX, _dY, _arcAngle );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position around the center point at the given delta rho and delta theta from the current
     * position, in the current rotation.
     *  @param _dRho the difference in rho between the center position and the current position.
     * @param _dTheta the difference in theta between the center position and the current position.
     * @param _arcAngle the angular fraction of the arc to draw.
     */
    public void arcAroundRT( final double _dRho, final double _dTheta, final double _arcAngle ) {
        dc.arcAroundRT( _dRho, _dTheta, _arcAngle );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position to the given point relative to the current position, in the current rotation.
     * Positive arc angles are drawn clockwise, negative arc angles anti-clockwise.
     *  @param _end
     * @param _arcAngle
     */
    public void arcTo( final Point _end, final double _arcAngle ) {
        dc.arcTo( _end, _arcAngle );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position to the point at the given delta x and delta y from the current position, in
     * the current rotation.
     *  @param _dX the difference in x between the ending position and the current position.
     * @param _dY the difference in y between the ending position and the current position.
     * @param _arcAngle the angular fraction of the arc to draw.
     */
    public void arcToXY( final double _dX, final double _dY, final double _arcAngle ) {
        dc.arcToXY( _dX, _dY, _arcAngle );
    }


    /**
     * Draws a circular arc with the given arc angle from the current position to the point at the given delta rho and delta theta from the current position,
     * in the current rotation.
     *  @param _dRho the difference in rho between the ending position and the current position.
     * @param _dTheta the difference in theta between the ending position and the current position.
     * @param _arcAngle the angular fraction of the arc to draw.
     */
    public void arcToRT( final double _dRho, final double _dTheta, final double _arcAngle ) {
        dc.arcToRT( _dRho, _dTheta, _arcAngle );
    }


    /**
     * Draws a cubic Bézier curve from the current position to the given end point, with the curve controlled by the two given control point.  Control point 1
     * is relative to the current position, and controls the slope of the line as it leaves the current position.  Control point 2 is relative to the end
     * point, and controls the slope of the line as it approaches the end point.  The end point is relative to the current position.
     *  @param _cp1 control point 1, relative to the current position.
     * @param _cp2 control point 2, relative to the end point.
     * @param _end the end point, relative to the current position.
     */
    public void curveTo( final Point _cp1, final Point _cp2, final Point _end ) {
        dc.curveTo( _cp1, _cp2, _end );
    }


    /**
     * Draws a cubic Bézier curve from the current position to the given end position, with the curve controlled by the two given control point positions.
     * All of the positions are relative to the current ball position, expressed as a delta x and delta y in the current rotation.  Control point 1
     * controls the slope of the line as it leaves the starting point.  Control point 2 controls the slope of the line as it approaches the end point.
     *  @param _cp1X the difference in x between control point 1 and the current position.
     * @param _cp1Y the difference in y between control point 1 and the current position.
     * @param _cp2X the difference in x between control point 2 and the ending position.
     * @param _cp2Y the difference in y between control point 2 and the ending position.
     * @param _endX the difference in x between the ending position and the current position.
     * @param _endY the difference in y between the ending position and the current position.
     */
    public void curveToXY( final double _cp1X, final double _cp1Y, final double _cp2X, final double _cp2Y, final double _endX, final double _endY ) {
        dc.curveToXY( _cp1X, _cp1Y, _cp2X, _cp2Y, _endX, _endY );
    }


    /**
     * Draws a cubic Bézier curve from the current position to the given end position, with the curve controlled by the two given control point positions.
     * All of the positions are relative to the current ball position, expressed as a delta rho and delta theta in the current rotation.  Control point 1
     * controls the slope of the line as it leaves the starting point.  Control point 2 controls the slope of the line as it approaches the end point.
     *  @param _cp1Rho   the difference in rho between control point 1 and the current position.
     * @param _cp1Theta the difference in theta between control point 1 and the current position.
     * @param _cp2Rho   the difference in rho between control point 2 and the ending position.
     * @param _cp2Theta the difference in theta between control point 2 and the ending position.
     * @param _endRho   the difference in rho between the ending position and the current position.
     * @param _endTheta the difference in theta between the ending position and the current position.
     */
    public void curveToRT( final double _cp1Rho, final double _cp1Theta, final double _cp2Rho, final double _cp2Theta, final double _endRho, final double _endTheta ) {
        dc.curveToRT( _cp1Rho, _cp1Theta, _cp2Rho, _cp2Theta, _endRho, _endTheta );
    }


    /**
     * Draws a circle at the current rho for the given number of revolutions.  If the number of orbits is positive, they will be
     * in a clockwise direction, otherwise counterclockwise.
     *
     * @param _orbits the number of orbits to make.
     */
    public void orbit( final int _orbits ) {
        dc.orbit( _orbits );
    }


    /**
     * Draws a straight line from the current position to the table center at the same theta as the current position.
     */
    public void home() {
        dc.home();
    }


    /**
     * Arc around the table center by the given angle.  The rho is not changed.
     * @param _theta
     */
    public void arcAroundTableCenter( final double _theta ) {
        dc.arcAroundTableCenter( _theta );
    }


    /**
     * Draws a straight line to the given theta (in the current rotation) on the edge of the table.
     *
     * @param _theta the position on the edge to draw a line to.
     */
    public void edge( final double _theta ) {
        dc.edge( _theta );
    }


    public void write( final String _fileName ) throws IOException {
        dc.write( _fileName );
    }


    public void renderPNG( final String _fileName ) throws IOException {
        dc.renderPNG( _fileName );
    }


    /**
     * Draw the given line starting from the current position, and in the current rotation.
     *
     * @param _line the line to draw.
     */
    public void draw( final Line _line ) {
        dc.draw( _line );
    }


    /**
     * Sets the current relative position to x,y 0,0.
     */
    public void zeroCurrentRelativePosition() {
        dc.zeroCurrentRelativePosition();
    }


    /**
     * Sets the current relative position to the given point.
     *
     * @param _position the new current relative position.
     */
    public void setCurrentRelativePosition( final Point _position ) {
        dc.setCurrentRelativePosition( _position );
    }


    /**
     * Returns the current relative position.
     *
     * @return the current relative position.
     */
    public Point getCurrentRelativePosition() {
        return dc.getCurrentRelativePosition();
    }


    /**
     * Returns a vector from the current relative position to the given relative position.
     *
     * @param _destination the relative position to get a vector to.
     * @return the vector to the destination position.
     */
    public Point vectorTo( final Point _destination ) {
        return dc.vectorTo( _destination );
    }


    /**
     * Returns a vector from the current relative position to the given relative position.
     *
     * @param _destination the relative position to get a vector to.
     * @return the vector to the destination position.
     */
    public Point to( final Point _destination ) {
        return dc.vectorTo( _destination );
    }


    /**
     * Returns a marker for the current position.
     *
     * @return a marker for the current position.
     */
    public Marker marker() {
        return dc.marker();
    }


    /**
     * Rotate the canvas about the current center to the given angle in radians.
     *
     * @param _theta the angle to rotate the canvas to, in radians.
     */
    public void rotateTo( final double _theta ) {
        dc.rotateTo( _theta );
    }


    /**
     * Rotate the canvas by the given angle in radians from its current rotation.  Positive angles rotate the canvas clockwise, negative angles anticlockwise.
     *
     * @param _deltaTheta the angle to rotate the canvas by.
     */
    public void rotateBy( final double _deltaTheta ) {
        dc.rotateBy( _deltaTheta );
    }


    public void clear() {
        dc.clear();
    }


    public double getMaxPointDistance() {
        return dc.getMaxPointDistance();
    }


    public void setMaxPointDistance( final double _maxPointDistance ) {
        dc.setMaxPointDistance( _maxPointDistance );
    }


    public double getFitToleranceRho() {
        return dc.getFitToleranceRho();
    }


    public Model getModel() {
        return dc.getModel();
    }


    public void setModel( final Model _model ) {
        dc.setModel( _model );
    }


    public double getMaxFitErrorMeters() {
        return dc.getMaxFitErrorMeters();
    }


    public void setMaxFitErrorMeters( final double _maxFitErrorMeters ) {
        dc.setMaxFitErrorMeters( _maxFitErrorMeters );
    }


    public int getPixelsPerRho() {
        return dc.getPixelsPerRho();
    }


    /**
     * Sets the number of pixels per rho unit for the generation of PNG files.  Effectively this sets the radius of the generated file in pixels.  For
     * instance, a value of 1000 pixels per rho would result in a 2000 x 2000 pixel PNG file.
     *
     * @param _pixelsPerRho the number of PNG file pixels in one rho unit.
     */
    public void setPixelsPerRho( final int _pixelsPerRho ) {
        dc.setPixelsPerRho( _pixelsPerRho );
    }


    /**
     * Returns the current absolute position of the ball on the Sisyphus table, as held by the drawing context.
     *
     * @return The current absolute position of the ball on the Sisyphus table.
     */
    public Position getCurrentPosition() {
        return dc.getCurrentPosition();
    }


    public double getEraseSpacing() {
        return dc.getEraseSpacing();
    }


    public void setEraseSpacing( final double _eraseSpacing ) {
        dc.setEraseSpacing( _eraseSpacing );
    }


    public boolean isMute() {
        return dc.isMute();
    }


    public void setMute( final boolean _mute ) {
        dc.setMute( _mute );
    }


    public double getCurrentRotation() {
        return dc.getCurrentRotation();
    }
}
