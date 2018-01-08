package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.lines.ArbitraryLine;
import com.slightlyloony.jsisyphus.lines.Line;
import com.slightlyloony.jsisyphus.lines.SisyphusLine;
import com.slightlyloony.jsisyphus.positions.CartesianPosition;
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
 * Provides a drawing context for the Sisyphus table, supporting drawing with translation and rotation
 * @author Tom Dilatush  tom@dilatush.com
 */
public class DrawingContext {

    private Position translation;     // represents an offset from the center of the physical Sisyphus table...
    private double rotation;          // represents a rotation from the zero heading on the physical Sisyphus table...
    private List<Position> vertices;  // holds all the vertices that we've drawn...


    public DrawingContext() {
        translation = new CartesianPosition( 0,0,0 );
        rotation = 0;
        vertices = new ArrayList<>();
    }


    public void write( final String _fileName ) throws IOException {
        // TODO: clamp at rho == 1...
        // TODO: optimize by removing points along circle (esp. at rho == 1)...

        StringBuilder out = new StringBuilder();
        for( Position vertice : vertices ) {
            out.append( vertice.toVertice() );
            out.append( "\n" );
        }
        Path path = new File( _fileName ).toPath();
        byte[] bytes = out.toString().getBytes();
        Files.write( path, bytes );
    }


    public void renderPNG( final String _fileName ) throws IOException {

        int width  = 2001;
        int height = 2001;

        BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );

        Graphics2D g = bi.createGraphics();
        g.setColor( Color.lightGray );
        g.fillRect( 0, 0, width + 1, height + 1 );
        g.setColor( Color.WHITE );
        g.fillOval( 0, 0, 2001, 2001 );
        g.setColor( Color.BLACK );

        // draw a Sisyphus line for each adjacent vertice pair we have...
        for( int i = 0; i < vertices.size() - 1; i++ ) {

            Position start = vertices.get( i );
            Position end = vertices.get( i + 1 );

            Line line = new SisyphusLine( start, end );

            // draw a straight line for each pair of points within the Sisyphus line...
            List<Position> points = line.getPoints();
            for( int j = 0; j < points.size() - 1; j++ ) {

                int x1 = 1000 + (int) Math.round( points.get( j ).getX() * 1000 );
                int y1 = 1000 + (int) Math.round( points.get( j ).getY() * -1000 );
                int x2 = 1000 + (int) Math.round( points.get( j + 1 ).getX() * 1000 );
                int y2 = 1000 + (int) Math.round( points.get( j + 1 ).getY() * -1000 );
                g.drawLine( x1, y1, x2, y2 );
            }
        }

        ImageIO.write(bi, "PNG", new File(_fileName + ".png"));
    }


    public void draw( final Line _line ) {
        draw( _line, Transformer.NOOP );
    }


    public void draw( final Line _line, final Transformer _transformer ) {

        Line line = _transformer.transform( _line );

        List<Position> points = line.getPoints();
        emit( line.getStart() );
        int last = points.size() - 1;
        int i = 0;  // start at the beginning!
        while( i < last ) {

            // get our start point...
            Position start = points.get( i );

            // do a binary search to find the longest segment we can draw as a Sisyphus line...
            boolean done = false;
            int si = last - i;
            int highestCan = 1;
            int lowestCant = last + 1 - i;
            Line seg = null;
            while( !done ) {
                seg = new ArbitraryLine( points.subList( i, si + i + 1 ) );
                boolean canDraw = testDrawingFit( seg );
                if( canDraw ) {
                    highestCan = si;
                    si = si + ((lowestCant - si ) >> 1);
                }
                else {
                    lowestCant = si;
                    si = highestCan + ((si - (highestCan + 1)) >> 1);
                }
                done = (lowestCant - highestCan == 1);
            }

            // emit the vertice...
            emit( seg.getPoints().get( highestCan ) );

            // move to the next segment...
            i += highestCan;
        }
    }


    private void emit( final Position _vertice ) {
        if( (vertices.size() > 0) && (_vertice.equals( vertices.get( vertices.size() - 1 ) )))
            return;
        vertices.add( _vertice );
    }


    private boolean testDrawingFit( final Line _segment ) {

        // the line we're comparing with...
        Line sl = new SisyphusLine( _segment.getStart(), _segment.getEnd() );

        // check the error on all points on the line, starting with the first, middle, and end (as an optimization)...
        List<Position> points = _segment.getPoints();
        double me = Common.MAX_ALLOWABLE_DRAWING_ERROR_SU;
        int mi = points.size() >> 1;
        if( sl.getDistance( _segment.getStart() ) > me ) return false;
        if( sl.getDistance( _segment.getEnd() ) > me   ) return false;
        if( sl.getDistance( points.get( mi ) ) > me    ) return false;
        for( int i = 1; i < mi; i++ )
            if( sl.getDistance( points.get( i ) ) > me ) return false;
        for( int i = mi + 1; i < points.size(); i++ )
            if( sl.getDistance( points.get( i ) ) > me ) return false;
        return true;
    }


    public void stepTranslation( final Position _step ) {
        translation = translation.add( _step );
    }


    public void stepRotation( final double _step ) {
        rotation += _step;
    }


    public Position getTranslation() {
        return translation;
    }


    public void setTranslation( final Position _translation ) {
        translation = _translation;
    }


    public double getRotation() {
        return rotation;
    }


    public void setRotation( final double _rotation ) {
        rotation = _rotation;
    }


    public void clear() {
        translation = new CartesianPosition( 0,0,0 );
        rotation = 0;
        vertices.clear();
    }


    public Position getEnd() {
        return vertices.get( vertices.size() - 1 );
    }


    public Position getStart() {
        return vertices.get( 0 );
    }
}
