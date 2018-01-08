package com.slightlyloony.jsisyphus;

/**
 * A place to hold commonly used values.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class Common {

    // physical characteristics of Sisyphus table...
    public static final double STEPS_PER_RHO_MM = 101.31;
    public static final double STEPS_PER_2PI_THETA = 20_800;
    public static final double RHO_LENGTH_MM = 209.55;
    public static final double SU_PER_MM = 1 / RHO_LENGTH_MM;    // Sisyphus units per millimeter...

    // resolution control...
    public static final double VISUAL_RESOLUTION_MM = 0.5;
    public static final double MAX_ALLOWABLE_DRAWING_ERROR_MM = 0.5;
    public static final double VISUAL_RESOLUTION_SU = VISUAL_RESOLUTION_MM * SU_PER_MM;
    public static final double MAX_ALLOWABLE_DRAWING_ERROR_SU = MAX_ALLOWABLE_DRAWING_ERROR_MM * SU_PER_MM;
}
