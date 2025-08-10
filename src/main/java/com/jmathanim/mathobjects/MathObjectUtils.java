package com.jmathanim.mathobjects;

public class MathObjectUtils {
    /**
     * Returns a null-safe copy of MathObject
     *
     * @param obj Object to get copy
     * @return Copy of object. A NullMathObject instance if obj is null
     */
    public static MathObject getSafeCopyOf(MathObject obj) {
        if (obj == null) {
            return new NullMathObject();
        } else {
            return obj.copy();
        }
    }
}
