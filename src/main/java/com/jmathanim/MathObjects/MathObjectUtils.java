package com.jmathanim.MathObjects;

import com.jmathanim.Constructible.CTNullMathObject;

public class MathObjectUtils {
    /**
     * Returns a null-safe copy of MathObject
     *
     * @param obj Object to get copy
     * @return Copy of object. A NullMathObject instance if obj is null
     */
    public static MathObject getSafeCopyOf(MathObject obj) {
        if (obj == null) {
            return new CTNullMathObject();
        } else {
            return obj.copy();
        }
    }
}
