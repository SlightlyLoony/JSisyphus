package com.slightlyloony.jsisyphus.models;

/**
 * Classes implementing this interface represent models of Sisyphus tables.
 *
 * @author Tom Dilatush  tom@dilatush.com
 */
public interface Model {

    public static final Model A16 = new A16();

    double rhoStepsPerMeter();
    double thetaStepsPerRevolution();
    double tableRadiusMeters();
    String modelName();
}
