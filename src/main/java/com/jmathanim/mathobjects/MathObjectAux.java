package com.jmathanim.mathobjects;

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;

abstract class MathObjectAux<T extends MathObjectAux<T>>{

    /**
     * Shift object with the given vector
     *
     * @param shiftVector Amount of shifting
     * @return The same object, after shifting
     */
    public T shift(Vec shiftVector) {
        AffineJTransform tr = AffineJTransform.createTranslationTransform(shiftVector);
        tr.applyTransform(this);
        return (T) this;
    }

    /**
     * Shift object. Overloaded method (2D version)
     *
     * @param x   x-coordinate of shift vector
     * @param y   y-coordinate of shift vector
     * @return The same object, after shifting
     */
    public final T shift(double x, double y) {
        return shift(new Vec(x, y));
    }

}
