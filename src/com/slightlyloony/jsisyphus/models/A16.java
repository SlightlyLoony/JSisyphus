package com.slightlyloony.jsisyphus.models;

/**
 * The first version of the 16" diameter Sisyphus table.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class A16 implements Model {

    @Override
    public double rhoStepsPerMeter() {
        return 101_310;
    }


    @Override
    public double thetaStepsPerRevolution() {
        return 20_800;
    }


    @Override
    public double tableRadiusMeters() {
        return 0.209549886843061;
    }


    @Override
    public String modelName() {
        return "Original 16 inch diameter Sisyphus table";
    }
}
