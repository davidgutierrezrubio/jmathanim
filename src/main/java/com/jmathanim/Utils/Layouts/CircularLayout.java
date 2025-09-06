package com.jmathanim.Utils.Layouts;

import com.jmathanim.Enum.RotationType;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.AbstractMathGroup;
import com.jmathanim.mathobjects.Coordinates;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import static com.jmathanim.jmathanim.JMathAnimScene.PI2;

public class CircularLayout extends GroupLayout {

    Vec center;
    double radius;
    RotationType rotationType;
    private double initialAngle;
    private boolean counterClockWise;


    protected CircularLayout(Coordinates<?> center, double radius, boolean counterClockWise, RotationType rotationType, double initialAngle) {
        this.center = center.getVec();
        this.radius = radius;
        this.rotationType = rotationType;
        this.initialAngle = initialAngle;
        this.counterClockWise = counterClockWise;
    }

    public static CircularLayout make(Coordinates<?> center, double radius) {
        return new CircularLayout(center, radius, true, RotationType.FIXED, 0);
    }

    public RotationType getRotationType() {
        return rotationType;
    }

    public CircularLayout setRotationType(RotationType rotationType) {
        this.rotationType = rotationType;
        return this;
    }

    public double getInitialAngle() {
        return initialAngle;
    }

    public CircularLayout setInitialAngle(double initialAngle) {
        this.initialAngle = initialAngle;
        return this;
    }

    public boolean isCounterClockWise() {
        return counterClockWise;
    }

    public CircularLayout setCounterClockWise(boolean counterClockWise) {
        this.counterClockWise = counterClockWise;
        return this;
    }

    @Override
    protected void executeLayout(AbstractMathGroup<?> group) {
        Vec locationToAnchor = Vec.to(radius, 0).rotate(initialAngle);
        int size = group.size();
        double rotationAngle = PI2 / size * (counterClockWise ? 1 : -1);
        for (int i = 0; i < size; i++) {
            group.get(i).moveTo(locationToAnchor.add(center));
            switch (rotationType) {
                case ROTATE:
                    group.get(i).rotate(rotationAngle*i);
                    break;
                case SMART:
                    double angle = rotationAngle * i;
                    while (angle>PI/2) angle-=PI;
                    while (angle<-PI/2) angle+=PI;
                    group.get(i).rotate(angle);
                    break;
            }
            locationToAnchor.rotateInSite(rotationAngle);
            System.out.println(locationToAnchor);
        }
    }

    @Override
    public CircularLayout copy() {
        return new CircularLayout(center.copy(), radius, counterClockWise, rotationType, initialAngle);
    }
}
