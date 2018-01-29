package com.slightlyloony.jsisyphus;

/**
 * Instances of this class represent a change of position in the virtual Cartesian space used by {@link com.slightlyloony.jsisyphus.lines.Line} instances to
 * describe a line.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Delta {

    public double x;
    public double y;


    public Delta( final double _x, final double _y ) {
        x = _x;
        y = _y;
    }


    @Override
    public String toString() {
        return "Delta x,y: " + x + ", " + y;
    }
}
