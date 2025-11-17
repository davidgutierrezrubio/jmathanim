package com.jmathanim.Constructible.Points;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Enum.DotStyle;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Interpolable;
import com.jmathanim.MathObjects.Point;
import com.jmathanim.MathObjects.Stateable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.LogUtils;

public abstract class CTAbstractPoint<T extends CTAbstractPoint<T>> extends Constructible<T> implements Coordinates<T>, Interpolable<T> {
    protected final Point pointToShow;
    protected final Vec coordinatesOfPoint;


    protected CTAbstractPoint() {
       this(Vec.to(0, 0));
    }

    protected CTAbstractPoint(Coordinates<?> A) {
        super();
        pointToShow = Point.origin();
        coordinatesOfPoint = A.getVec();//Referenced
    }

    @Override
    public Point getMathObject() {
        return pointToShow;
    }

    @Override
    public T copy() {
        return null;
    }

    @Override
    public void rebuildShape() {
        if (!isFreeMathObject()) {
            this.pointToShow.v.copyCoordinatesFrom(this.coordinatesOfPoint);
        }
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof CTAbstractPoint<?>)) return;
            CTAbstractPoint<?> cnst = (CTAbstractPoint<?>) obj;
            this.coordinatesOfPoint.copyCoordinatesFrom(cnst.coordinatesOfPoint);
            this.getMathObject().copyStateFrom(cnst.getMathObject());
            this.setFreeMathObject(cnst.isFreeMathObject());
    }

    public T dotStyle(DotStyle dotStyle) {
        pointToShow.dotStyle(dotStyle);
        return (T) this;
    }

    @Override
    public T applyAffineTransform(AffineJTransform affineJTransform) {
        pointToShow.applyAffineTransform(affineJTransform);
        if (!isFreeMathObject()) {
            this.coordinatesOfPoint.copyCoordinatesFrom(pointToShow.v);
        }
        rebuildShape();
        return (T) this;
    }

    public Vec getVec() {
        return coordinatesOfPoint;
    }

    @Override
    public T add(Coordinates<?> v2) {
        T copy = copy();
        copy.coordinatesOfPoint.shift(v2.getVec());
        return copy;
    }

    public T interpolate(Coordinates<?> coords2, double alpha) {
        T copy = copy();
        copy.coordinatesOfPoint.interpolate(coords2, alpha);
        return copy;
    }

    @Override
    public String toString() {
        if ("".equals(getObjectLabel())) {
            return String.format(
                            LogUtils.PURPLE+"%s"+LogUtils.RESET+"[" +
                            LogUtils.BLUE+"%.2f" +LogUtils.RESET+
                            ", " +
                            LogUtils.BLUE+"%.2f" +LogUtils.RESET+
                            "]",
                    getClass().getSimpleName(),
                    coordinatesOfPoint.x,
                    coordinatesOfPoint.y);
        }
        else
        return String.format(LogUtils.GREEN+"%s"+LogUtils.RESET+" = " +
                        LogUtils.PURPLE+"%s"+LogUtils.RESET+"[" +
                        LogUtils.BLUE+"%.2f" +LogUtils.RESET+
                        ", " +
                        LogUtils.BLUE+"%.2f" +LogUtils.RESET+
                        "]",
                getObjectLabel(),
                getClass().getSimpleName(),
                coordinatesOfPoint.x,
                coordinatesOfPoint.y);
    }

}
