package com.slightlyloony.jsisyphus.lines;

import com.slightlyloony.jsisyphus.positions.Position;

import java.util.List;

/**
 * Represents a line of arbitrary shape on the Sisyphus table.  Note that instances of this class are created during transformation and rotation
 * operations during drawing.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class ArbitraryLine extends ALine implements Line {


    public ArbitraryLine( final List<Position> _points ) {
        super( _points );
    }
}
