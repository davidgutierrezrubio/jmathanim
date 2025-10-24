package com.jmathanim.MathObjects;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * This class encpasulates several internal methods of MathObject
 */
public class DebugTools {
    public static void setHasBeenUpdated(MathObject<?> obj, boolean hasBeenUpdated) {
        obj.setHasBeenUpdated(hasBeenUpdated);
    }

    public static boolean isHasBeenUpdated(MathObject<?> obj) {
        return obj.isHasBeenUpdated();
    }

    public static String getDebugText(MathObject<?> obj) {
        if (obj instanceof Constructible) {
            return ((Constructible<?>) obj).getMathObject().getDebugText();
        }else {
            return obj.getDebugText();
        }
    }

    public static void setDebugText(MathObject<?> obj, String text) {
       if (obj instanceof Constructible) {
           ((Constructible<?>) obj).getMathObject().setDebugText(text);
       }else {
           obj.setDebugText(text);
       }
    }

    public static void addToSceneHook(MathObject<?> obj, JMathAnimScene scene) {
        obj.addToSceneHook(scene);
    }

    public static void removedFromSceneHook(MathObject<?> mathObject, JMathAnimScene scene) {
        mathObject.removedFromSceneHook(scene);
    }

    /**
     * Sets the flag for showing debug points. For objects with holds an AbstractShape or a collection of
     * AbstractShapes, a path number will be superimposed in each JMPathPoint of the path. If the element is not a valid
     * AbstractShape or collection, no action is done.
     *
     * @param mathObject      MathObject to set the flag
     * @param showDebugPoints The flag
     */
    public static void setShowDebugPoints(MathObject<?> mathObject, boolean showDebugPoints) {
        if (mathObject instanceof AbstractShape<?>) {
            AbstractShape<?> abstractShape = (AbstractShape<?>) mathObject;
            abstractShape.setShowDebugPoints(showDebugPoints);
            return;
        }
        if (mathObject instanceof AbstractMathGroup<?>) {
            AbstractMathGroup<?> abstractMathGroup = (AbstractMathGroup<?>) mathObject;
            for (MathObject<?> obj : abstractMathGroup) {
                setShowDebugPoints(obj, showDebugPoints);
            }
        }
        if (mathObject instanceof AbstractMultiShapeObject<?, ?>) {
            AbstractMultiShapeObject<?, ?> abstractMultiShapeObject = (AbstractMultiShapeObject<?, ?>) mathObject;
            for (AbstractShape<?> obj : abstractMultiShapeObject) {
                setShowDebugPoints(obj, showDebugPoints);
            }
        }
    }

    /**
     * Sets the flag for showing debug indices. For collection objects like MathObjectGroup or LatexMathObject a index
     * number will be superimposed in each element. If the element is not a valid object collection, no action is done.
     *
     * @param mathObject MathObject to set the flag
     * @param value      The flag
     */
    public static void setShowDebugIndices(MathObject<?> mathObject, boolean value) {
        if (mathObject instanceof AbstractMathGroup<?>) {
            AbstractMathGroup<?> abstractMathGroup = (AbstractMathGroup<?>) mathObject;
            if (value) {
                int k = 0;
                for (MathObject<?> o : abstractMathGroup) {
                    setDebugText(o, "" + k);
                    k++;
                }
            } else {
                for (MathObject<?> o : abstractMathGroup) {
                    setDebugText(o, "");
                }
            }
        }
        if (mathObject instanceof AbstractMultiShapeObject<?, ?>) {
            AbstractMultiShapeObject<?, ?> abstractMultiShapeObject = (AbstractMultiShapeObject<?, ?>) mathObject;
            if (value) {
                int k = 0;
                for (AbstractShape<?> o : abstractMultiShapeObject) {
                    setDebugText(o, "" + k);
                    k++;
                }
            } else {
                for (AbstractShape<?> o : abstractMultiShapeObject) {
                    setDebugText(o, "");
                }
            }
        }
    }

}
