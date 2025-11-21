package com.jmathanim.MathObjects;

import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.Dependable;

public interface Coordinates<T extends Coordinates<T>> extends Boxable, Dependable, AffineTransformable<T> {

    Vec getVec();

    /**
     * Copy coordinates from given vector
     *
     * @param coords Coordinates to copy from
     */
    default void copyCoordinatesFrom(Coordinates<?> coords) {
        if (coords != null) {
            Vec v1 = getVec();
            Vec v2 = coords.getVec();
            v1.x = v2.x;
            v1.y = v2.y;
            v1.z = v2.z;
        }
        changeVersionAndMarkDirty();
    }

    default void copyCoordinatesFrom(double x, double y) {
        Vec v1 = getVec();
        v1.x = x;
        v1.y = y;
        changeVersionAndMarkDirty();
    }

    @Override
    default T shift(Coordinates<?> shiftVector) {
        Vec v1 = getVec();
        Vec v2 = shiftVector.getVec();
        v1.x += v2.x;
        v1.y += v2.y;
        v1.z += v2.z;
        changeVersionAndMarkDirty();
        return (T) this;
    }


    /**
     * Rotates the vector the specified angle around the specified center.The vector is altered (2d version).
     *
     * @param center Rotation center
     * @param angle  Rotation angle
     * @return This vector
     */
    default T rotate(Coordinates<?> center, double angle) {
//        Vec vCenter = center.getVec();
//        Vec rotatedVector = Vec.to(x - vCenter.x, y - vCenter.y);
//        rotatedVector.rotateInSite(angle);
//        return rotatedVector.shift(vCenter);
        Vec v = getVec();
        Vec vc = center.getVec();

        v.x -= vc.x;
        v.y -= vc.y;
        v.z -= vc.z;
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double a = v.x;
        double b = v.y;
        v.x = c * a - s * b;
        v.y = s * a + c * b;


        v.x += vc.x;
        v.y += vc.y;
        v.z += vc.z;
        changeVersionAndMarkDirty();
        return (T) this;
    }


    /**
     * Rotates the vector the specified angle.The vector is altered (2d version).
     *
     * @param angle Rotation angle
     * @return This vector
     */
    default T rotate(double angle) {
        Vec v = getVec();
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double a = v.x;
        double b = v.y;
        v.x = c * a - s * b;
        v.y = s * a + c * b;
        changeVersionAndMarkDirty();
        return (T) this;
    }


    @Override
    default T scale(double s) {
        Vec v1 = getVec();
        v1.x *= s;
        v1.y *= s;
        v1.z *= s;
        return (T) this;
    }

    @Override
    default T scale(Coordinates<?> scaleCenter, double sx, double sy, double sz) {
        Vec v1 = getVec();
        Vec vc = scaleCenter.getVec();
        v1.x -= vc.x;
        v1.y -= vc.y;
        v1.z -= vc.z;

        v1.x *= sx;
        v1.y *= sy;
        v1.z *= sz;

        v1.x += vc.x;
        v1.y += vc.y;
        v1.z += vc.z;
        changeVersionAndMarkDirty();
        return (T) this;
    }

    /**
     * Add the given coordinates to this and return the result. The original coordinates object is unaltered.
     *
     * @param v2 The coordinates to add
     * @return The result. A new object
     */
    public T add(Coordinates<?> v2);


    /**
     * Add the given coordinates to this and return the result. The original coordinates object is unaltered.
     *
     * @param x The x coordinate to add
     * @param y The y coordinate to add
     * @return The result. A new object
     */
    default T add(double x, double y) {
        return add(Vec.to(x, y));
    }


    /**
     * Computes the vector from this coordinates to the given ones. The original object is unaltered.
     *
     * @param destiny The destiny coordinates
     * @return A Vec from this object to the given coordinates
     */
    default Vec to(Coordinates<?> destiny) {
        return Vec.to(destiny.getVec().x - this.getVec().x, destiny.getVec().y - this.getVec().y, destiny.getVec().z - this.getVec().z);
    }

    /**
     * Computes the euclidean norm from the origin to this coordinates.
     *
     * @return The norm, a double value.
     */
    default double norm() {
        Vec v = getVec();
        return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }

    T copy();

    /**
     * Checkif any of its components is Nan
     *
     * @return True if x, y or z is NaN. False otherwise
     */
    default boolean isNaN() {
        Vec v1 = getVec();
        return (Double.isNaN(v1.x)) || (Double.isNaN(v1.y)) || Double.isNaN(v1.z);
    }
}
