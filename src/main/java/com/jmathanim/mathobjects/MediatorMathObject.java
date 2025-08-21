package com.jmathanim.mathobjects;

import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * This class encpasulates several internal methods of MathObject
 */
public class MediatorMathObject {
    public static void setHasBeenUpdated(MathObject obj, boolean hasBeenUpdated) {
        obj.setHasBeenUpdated(hasBeenUpdated);
    }

    public static boolean isHasBeenUpdated(MathObject obj) {
        return obj.isHasBeenUpdated();
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

    public static void setShowDebugPoints(MathObject mathObject, boolean showDebugPoints) {
        if (mathObject instanceof Shape) {
            Shape sh = (Shape) mathObject;
            sh.setShowDebugPoints(showDebugPoints);
            return;
        }
        if (mathObject instanceof MathObjectGroup) {
            MathObjectGroup mg = (MathObjectGroup) mathObject;
            for (MathObject obj : mg) {
                setShowDebugPoints(obj, showDebugPoints);
            }
        }
        if (mathObject instanceof MultiShapeObject) {
            MultiShapeObject msh = (MultiShapeObject) mathObject;
            for (Shape obj : msh) {
                setShowDebugPoints(obj, showDebugPoints);
            }
        }

    }
    public static void setShowDebugIndices(MathObject obj,boolean value) {
        if (obj instanceof MathObjectGroup) {
            MathObjectGroup objs= (MathObjectGroup) obj;
            if (value) {
                int k = 0;
                for (MathObject o : objs) {
                    setDebugText(o,"" + k);
                    k++;
                }
            } else {
                for (MathObject o : objs) {
                    setDebugText(o,"");
                }
            }
        }
        if (obj instanceof MultiShapeObject) {
            MultiShapeObject objs= (MultiShapeObject) obj;
            if (value) {
                int k = 0;
                for (MathObject o : objs) {
                    setDebugText(o,"" + k);
                    k++;
                }
            } else {
                for (MathObject o : objs) {
                    setDebugText(o,"");
                }
            }
        }
    }

}
