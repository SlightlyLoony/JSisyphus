package com.slightlyloony.jsisyphus;

/**
 * A place to hold commonly used values.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Common {

    public static final double SISYPHUS_TABLE_RADIUS = 0.2;                                    // meters...
    public static final double VISUAL_RESOLUTION = 0.00025;                                      // meters...
    public static final double MIN_POINT_SPACING = VISUAL_RESOLUTION / SISYPHUS_TABLE_RADIUS;  // Sisyphus table distance units...
    public static final double MAX_ALLOWABLE_DRAWING_ERROR = MIN_POINT_SPACING * 2;            // Sisyphus table distance units...
}
