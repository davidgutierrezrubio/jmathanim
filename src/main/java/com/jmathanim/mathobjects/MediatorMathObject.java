package com.jmathanim.mathobjects;

/**
 * This class encpasulates several internal methods of MathObject
 */
public class MediatorMathObject {
    public static void setHasBeenUpdated(MathObject obj,boolean hasBeenUpdated) {
        obj.hasBeenUpdated=hasBeenUpdated;
    }
    public static boolean isHasBeenUpdated(MathObject obj) {
        return obj.hasBeenUpdated;
    }
}
