package com.jmathanim.mathobjects;

import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * This class encpasulates several internal methods of MathObject
 */
public class MediatorMathObject {
    public static void setHasBeenUpdated(MathObject obj, boolean hasBeenUpdated) {
        obj.hasBeenUpdated = hasBeenUpdated;
    }

    public static boolean isHasBeenUpdated(MathObject obj) {
        return obj.hasBeenUpdated;
    }

    public static String getDebugText(MathObject obj) {
        return obj.getDebugText();
    }

    public static void setDebugText(MathObject obj, String text) {
        obj.setDebugText(text);
    }

    public static void addToSceneHook(MathObject obj, JMathAnimScene scene) {
        obj.addToSceneHook(scene);
    }

    public static void removedFromSceneHook(MathObject mathObject, JMathAnimScene scene) {
        mathObject.removedFromSceneHook(scene);
    }
}
