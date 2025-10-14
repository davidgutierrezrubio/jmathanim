package com.jmathanim.Utils.Layouts;

import com.jmathanim.Enum.RotationType;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.AbstractMathGroup;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.hasPath;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

public class PathLayout extends GroupLayout {

    private final JMPath path;
    RotationType rotationType;
    private boolean parametric;


    protected PathLayout(hasPath path, RotationType rotationType, boolean parametric) {
        this.path = path.getPath();
        this.rotationType = rotationType;
        this.parametric = parametric;
    }

    /**
     * Create a new PathLayout with the given path
     * @param path An object that contains a path (Shape, JMpath, etc.)
     * @return The created layout
     */
    public static PathLayout make(hasPath path) {
        return new PathLayout(path.getPath(), RotationType.FIXED, true);
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
        boolean isOpen = path.get(0).isSegmentToThisPointVisible();

        for (int i = 0; i < size; i++) {
            double t = i * 1d / (size - (isOpen ? 0 : 1));
            Vec locationToAnchor;
            double rotationAngle;
            if (parametric) {
                locationToAnchor = path.getParametrizedVecAt(t);
                rotationAngle = path.getParametrizedSlopeAt(t, true).getAngle() - PI / 2;
            } else {
                locationToAnchor = path.getJMPointAt(t).getV();
                rotationAngle = path.getSlopeAt(t, true).getAngle() - PI / 2;
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
        }
    }

    @Override
    public PathLayout copy() {
        return new PathLayout(path.copy(), rotationType, parametric);
    }
}
