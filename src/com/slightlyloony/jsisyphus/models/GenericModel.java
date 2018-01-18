package com.slightlyloony.jsisyphus.models;

/**
 * A generic model designed to work for any Sisyphus table.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public class GenericModel implements Model {

    @Override
    public double rhoStepsPerMeter() {
        return 100_000;
    }


    @Override
    public double thetaStepsPerRevolution() {
        return 20_800;
    }


    @Override
    public double tableRadiusMeters() {
        return 0.4;
    }


    @Override
    public String modelName() {
        return "Generic Sisyphus table";
    }
}
