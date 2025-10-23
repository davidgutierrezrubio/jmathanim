package com.jmathanim.mathobjects;

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;

public interface AffineTransformable <T extends AffineTransformable<T>>  extends Boxable,Copyable<T> {

    /**
     * Apply the given affine transform to the object. Object is altered
     * @param affineJTransform Affine transform to apply
     * @return This object
     */
    T applyAffineTransform(AffineJTransform affineJTransform);

    /**
     * Shift object with the given vector
     *
     * @param shiftVector Amount of shifting
     * @return The same object, after shifting
     */
    default T shift(Coordinates<?> shiftVector) {
        AffineJTransform tr = AffineJTransform.createTranslationTransform(shiftVector);
        applyAffineTransform(tr);
        return (T) this;
    }

    /**
     * Shift object. Overloaded method (2D version)
     *
     * @param x x-coordinate of shift vector
     * @param y y-coordinate of shift vector
     * @return The same object, after shifting
     */
    default T shift(double x, double y) {
        return shift(new Vec(x, y));
    }

    /**
     * Shift object.Overloaded method (3D version)
     *
     * @param x x-coordinate of shift vector
     * @param y y-coordinate of shift vector
     * @param z z-coordinate of shift vector
     * @return The same object, after shifting
     */
    default T shift(double x, double y, double z) {
        return shift(new Vec(x, y, z));
    }

    /**
     * Scale from center of object (2D version)
     *
     * @param sx x-scale factor
     * @param sy y-scale factor
     * @return The same object, after scaling
     */
    default T scale(double sx, double sy) {
        scale(getCenter(), sx, sy);
        return (T) this;
    }

    /**
     * Scale from center of object (2D version) in a uniform scale
     *
     * @param s scale factor
     * @return The same object, after scaling
     */
    default T scale(double s) {
        return (T) scale(getCenter(), s, s);
    }

    /**
     * Scale from a given center (uniform scale)
     *
     * @param scaleCenter Scale center
     * @param scale       scale factor
     * @return The same object, after scaling
     */
    default T scale(Coordinates<?> scaleCenter, double scale) {
        return scale(scaleCenter, scale, scale, scale);
    }

    /**
     * Scale from a given center (2D version)
     *
     * @param scaleCenter Scale center
     * @param sx          x-scale factor
     * @param sy          y-scale factor
     * @return The same object, after scaling
     */
    default T scale(Coordinates<?> scaleCenter, double sx, double sy) {
        return scale(scaleCenter, sx, sy, 1);
    }

    /**
     * Scale from the center of object (3D version)
     *
     * @param sx x-scale factor
     * @param sy y-scale factor
     * @param sz z-scale factor
     * @return The same object, after scaling
     */
    default T scale(double sx, double sy, double sz) {
        scale(getBoundingBox().getCenter(), sx, sy, sz);
        return (T) this;
    }

    /**
     * Scale from a given center (3D version)
     *
     * @param scaleCenter Scale center
     * @param sx          x-scale factor
     * @param sy          y-scale factor
     * @param sz          z-scale factor
     * @return The same object, after scaling
     */
    default T scale(Coordinates<?> scaleCenter, double sx, double sy, double sz) {
        AffineJTransform tr = AffineJTransform.createScaleTransform(scaleCenter.getVec(), sx, sy, sz);
        applyAffineTransform(tr);
        return (T) this;
    }


    /**
     * Performs a 2D-Rotation of the MathObject around the object center
     *
     * @param angle Angle, in radians
     * @return The same object, after rotating
     */
    default T rotate(double angle) {
        return rotate(getCenter(), angle);
    }

    /**
     * Performs a 2D-Rotation of the MathObject around the given rotation center
     *
     * @param center Rotation center
     * @param angle  Angle, in radians
     * @return The same object, after rotating
     */
    default T rotate(Coordinates<?> center, double angle) {
        AffineJTransform tr = AffineJTransform.create2DRotationTransform(center, angle);
        applyAffineTransform(tr);
        return (T) this;
    }

    /**
     * Performs a 3D-Rotation of the MathObject around the center of the object
     *
     * @param anglex Rotation angle in x axis, in radians
     * @param angley Rotation angle in y axis, in radians
     * @param anglez Rotation angle in z axis, in radians
     * @return The same object, after rotating
     */
    default T rotate3d(double anglex, double angley, double anglez) {
        return rotate3d(this.getCenter(), anglex, angley, anglez);
    }

    /**
     * Performs a 2D-Rotation of the MathObject around the given rotation center
     *
     * @param center Rotation center
     * @param anglex Rotation angle in x axis, in radians
     * @param angley Rotation angle in y axis, in radians
     * @param anglez Rotation angle in z axis, in radians
     * @return The same object, after rotating
     */
    default T rotate3d(Coordinates<?> center, double anglex, double angley, double anglez) {
        AffineJTransform tr = AffineJTransform.create3DRotationTransform(center, anglex, angley, anglez, 1);
        applyAffineTransform(tr);
        return (T) this;
    }


    /**
     * Shifts the object so that its center lies at the specified location
     *
     * @param p Destination point
     * @return The current object
     */
    default  T moveTo(Coordinates<?> p) {
        Vec c=getCenter();
        return shift(c.to(p));
    }

    /**
     * Overloaded method. Shifts the object so that its center lies at the specified location
     *
     * @param x x destiny coordinate
     * @param y y destiny coordinate
     * @return The current object
     */
    default T moveTo(double x, double y) {
        return moveTo(Vec.to(x, y));
    }

    /**
     * Move the object the minimum so that fits inside the given bounding box object and given gaps. If the bounding box
     * of the object is already inside the bounding box, this method has no effect. If the bounding box of the object is
     * wider or taller than the container box, nothing is done.
     *
     * @param containerBox  Boxable to smash object. May be a Rect, MathObject or Camera
     * @param horizontalGap Horizontal gap between the smashed object and the container bounding box
     * @param verticalGap   Vertical gap between the smashed object and the container bounding box
     * @return This object
     */
    default T smash(Boxable containerBox, double horizontalGap, double verticalGap) {
        Rect rObj = this.getBoundingBox();
        rObj.smash(containerBox, horizontalGap, verticalGap);
        shift(getCenter().to(rObj.getCenter()));
        return (T) this;
    }

    /**
     * Overloaded method. Move the object the minimum so that fits inside the given bounding box object and no gaps. If
     * the bounding box of the object is already inside the bounding box, this method has no effect.
     *
     * @param containerBox Boxable to smash object. May be a Rect, MathObject or Camera
     * @return This object
     */
    default T smash(Boxable containerBox) {
        return smash(containerBox, 0, 0);
    }

}
