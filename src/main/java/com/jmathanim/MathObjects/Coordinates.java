package com.jmathanim.MathObjects;

import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.Vec;

public interface Coordinates<T extends Coordinates<T>> extends Boxable,Dirtyable  {

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
    }

    default void copyCoordinatesFrom(double x, double y) {
        Vec v1 = getVec();
        v1.x = x;
        v1.y = y;
    }


    /**
     * Adds the given vector to this and stores the resul. The original vector is altered and the method returns this
     * object.
     *
     * @param coords The vector to add
     * @return The Coordinates object
     */
    default T addInSite(Coordinates<?> coords) {
        Vec v1 = getVec();
        Vec v2 = coords.getVec();
        v1.x += v2.x;
        v1.y += v2.y;
        v1.z += v2.z;
        return (T) this;
    }


    /**
     * Substracts the given vector to this and stores the resul. The original vector is altered and the method returns
     * this object.
     *
     * @param coords The vector to substract
     * @return This vector
     */
    default T minusInSite(Coordinates<?> coords) {
        Vec v1 = getVec();
        Vec v2 = coords.getVec();
        v1.x -= v2.x;
        v1.y -= v2.y;
        v1.z -= v2.z;
        return (T) this;
    }

    /**
     * Multiplies the vector by a scalar and stores the resul. The original vector is altered and the method returns
     * this object.
     *
     * @param lambda The scalar to multiply
     * @return This vector
     */
    default T multInSite(double lambda) {
        Vec v1 = getVec();
        v1.x *= lambda;
        v1.y *= lambda;
        v1.z *= lambda;
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
     * Substracts the given vector to this and return the result. The original coordinates object is unaltered.
     *
     * @param v2 The coordinates to substract
     * @return The substraction result
     */
    public T minus(Coordinates<?> v2);

    /**
     * Returns a new vector representing this vector scaled by a factor. The current vector is unaltered.
     *
     * @param lambda The factor
     * @return The new vector
     */
    public T mult(double lambda);


    default Vec to(Coordinates<?> b2) {
        return Vec.to(b2.getVec().x - this.getVec().x, b2.getVec().y - this.getVec().y, b2.getVec().z - this.getVec().z);
    }

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
        Vec v1=getVec();
        return (Double.isNaN(v1.x)) || (Double.isNaN(v1.y)) || Double.isNaN(v1.z);
    }
}
