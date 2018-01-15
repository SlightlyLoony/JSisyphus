package com.slightlyloony.jsisyphus;

import com.slightlyloony.jsisyphus.lines.ArbitraryLine;
import com.slightlyloony.jsisyphus.lines.Line;
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

    private List<SisyphusFitter> paths;  // holds all the paths that we've drawn...
    private double maxPointDistance;
    private final Model model;
    private final double maxFitErrorMeters;
    private final double fitToleranceRho;


    public DrawingContext( final Model _model, final double _maxFitErrorMeters ) {
        paths = new ArrayList<>();
        model = _model;
        maxFitErrorMeters = _maxFitErrorMeters;
        fitToleranceRho = maxFitErrorMeters / model.tableRadiusMeters();
        maxPointDistance = 0.001;  // approximately .2mm on A16 table...
    }


    public void write( final String _fileName ) throws IOException {
        // TODO: clamp at rho == 1...
        // TODO: optimize by removing points along circle (esp. at rho == 1)...

        Position current = Position.CENTER;
        StringBuilder out = new StringBuilder();
        out.append( "0 0\n" );  // force a starting (0,0) vertice...
        for( SisyphusFitter path : paths ) {
            out.append( path.toSisyphusString( current ) );
            current = path.getEnd();
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

        // draw a spiral line for each path we have...
        for( int i = 0; i < paths.size(); i++ ) {

            // draw a straight line for each pair of points within the Sisyphus line...
            Line line = null;
            List<Position> points = line.getPoints();
            for( int j = 0; j < points.size() - 1; j++ ) {

                int x1 = 1000 + (int) Math.round( points.get( j ).getX() * 1000 );
                int y1 = 1000 + (int) Math.round( points.get( j ).getY() * -1000 );
                int x2 = 1000 + (int) Math.round( points.get( j + 1 ).getX() * 1000 );
                int y2 = 1000 + (int) Math.round( points.get( j + 1 ).getY() * -1000 );
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
                seg = new ArbitraryLine( this, points.subList( i, si + i + 1 ) );
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
//        if( (vertices.size() > 0) && (_vertice.equals( vertices.get( vertices.size() - 1 ) )))
//            return;
//        vertices.add( _vertice );
    }


    private boolean testDrawingFit( final Line _segment ) {

//        // the line we're comparing with, using the shortest direction...
//        double et = _segment.getEnd().getTheta();
//        double st = _segment.getStart().getTheta();
//        double dt = et - et ;
//        boolean direction = et >= st;
//        if( Math.signum( st ) != Math.signum( et ) ) {
//            if( dt > Math.PI ) {
//                direction = !direction;
//            }
//        }
//        SisyphusFitter sp = new SisyphusFitter( _segment.getStart(), _segment.getEnd() );
//
//        // check the error on all points on the line, starting with the first, middle, and end (as an optimization)...
//        List<Position> points = _segment.getPoints();
//        double me = Common.MAX_ALLOWABLE_DRAWING_ERROR_SU;
//        int mi = points.size() >> 1;
//        if( sp.distance( _segment.getStart() ) > me ) return false;
//        if( sp.distance( _segment.getEnd() ) > me   ) return false;
//        if( sp.distance( points.get( mi ) ) > me    ) return false;
//        for( int i = 1; i < mi; i++ )
//            if( sp.distance( points.get( i ) ) > me ) return false;
//        for( int i = mi + 1; i < points.size(); i++ )
//            if( sp.distance( points.get( i ) ) > me ) return false;
        return true;
    }


    public void clear() {
        paths.clear();
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
