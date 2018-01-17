package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.lines.ArithmeticSpiral;
import com.slightlyloony.jsisyphus.lines.Line;
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

    private List<Position> vertices;  // holds all the vertices we've drawn...
    private double maxPointDistance;
    private final Model model;
    private final double maxFitErrorMeters;
    private final double fitToleranceRho;
    private final int pixelsPerRho;


    public DrawingContext( final Model _model, final double _maxFitErrorMeters, final int _pixelsPerRho ) {
        vertices = new ArrayList<>();
        vertices.add( Position.CENTER );  // we always start in the middle!
        vertices.add( Position.CENTER );  // for reasons unknown to us, it takes twice to reliably work...
        model = _model;
        maxFitErrorMeters = _maxFitErrorMeters;
        fitToleranceRho = maxFitErrorMeters / model.tableRadiusMeters();
        maxPointDistance = 0.01;  // approximately 2mm on A16 table...
        pixelsPerRho = _pixelsPerRho;
    }


    /**
     * Erases from the center to the given rho ending at the given position.  The theta of the given position is normalized to [-pi..pi], then adjusted for
     * the needed number of turns.
     *
     * @param _end the final position...
     */
    public void eraseOut( final Position _end ) {

        // figure out how many turns to make...
        double drho = 1 / (model.tableRadiusMeters() * 1000);
        double turns = Math.ceil( _end.getRho() / drho );
        double endTheta = Math.PI * 2 * turns + Utils.normalizeTheta( _end.getTheta() );
        Position newEnd = new PolarPosition( _end.getRho(), endTheta );

        Line line = new ArithmeticSpiral( this, new PolarPosition( 0, 0 ), newEnd );
        draw( line );
    }


    public void write( final String _fileName ) throws IOException {
        // TODO: clamp at rho == 1...
        // TODO: optimize by removing points along circle (esp. at rho == 1)...

        Position current = Position.CENTER;
        StringBuilder out = new StringBuilder();
        for( Position position : vertices ) {
            out.append( position.toVertice() );
        }
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

            // draw a straight line for each pair of points within the Sisyphus line...
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
        draw( _line, Transformer.NOOP );
    }


    public void draw( final Line _line, final Transformer _transformer ) {

        Line line = _transformer.transform( this, _line );
        SisyphusFitter fitter = new SisyphusFitter( line.getPoints(), this );
        fitter.generate();
        for( Position vertice : fitter.getVertices() ) {
            vertices.add( vertice );
        }
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
}
