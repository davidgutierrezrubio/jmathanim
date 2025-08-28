package com.jmathanim.Utils.Layouts;

import com.jmathanim.Enum.RotationType;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.AbstractMathGroup;
import com.jmathanim.mathobjects.AbstractShape;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

public class PathLayout extends GroupLayout {

    private final AbstractShape<?> shape;
    RotationType rotationType;
    private boolean parametric;


    protected PathLayout(AbstractShape<?> shape, RotationType rotationType, boolean parametric) {
        this.shape = shape;
        this.rotationType = rotationType;
        this.parametric = parametric;
    }

    public static PathLayout make(AbstractShape<?> shape) {
        return new PathLayout(shape, RotationType.FIXED, true);
    }

    public RotationType getRotationType() {
        return rotationType;
    }

    public PathLayout setRotationType(RotationType rotationType) {
        this.rotationType = rotationType;
        return this;
    }

    public boolean isParametric() {
        return parametric;
    }

    public PathLayout setParametric(boolean parametric) {
        this.parametric = parametric;
        return this;
    }

    @Override
    protected void executeLayout(AbstractMathGroup<?> group) {
        int size = group.size();
        boolean isOpen = shape.get(0).isSegmentToThisPointVisible();

        for (int i = 0; i < size; i++) {
            double t = i * 1d / (size - (isOpen ? 0 : 1));
            Vec locationToAnchor;
            double rotationAngle;
            if (parametric) {
                locationToAnchor = shape.getParametrizedVecAt(t);
                rotationAngle = shape.getPath().getParametrizedSlopeAt(t, true).getAngle() - PI / 2;
            } else {
                locationToAnchor = shape.getVecAt(t);
                rotationAngle = shape.getPath().getSlopeAt(t, true).getAngle() - PI / 2;
            }
            group.get(i).moveTo(locationToAnchor);
            switch (rotationType) {
                case ROTATE:
                    group.get(i).rotate(rotationAngle);
                    break;
                case SMART:
                    double angle = rotationAngle;
                    while (angle > PI / 2) angle -= PI;
                    while (angle < -PI / 2) angle += PI;
                    group.get(i).rotate(angle);
                    break;
            }
            locationToAnchor.rotateInSite(rotationAngle);
            System.out.println(locationToAnchor);
        }
    }

    @Override
    public PathLayout copy() {
        return new PathLayout(shape.copy(), rotationType, parametric);
    }
}
