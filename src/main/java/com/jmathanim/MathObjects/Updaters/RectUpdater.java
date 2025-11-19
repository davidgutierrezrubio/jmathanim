package com.jmathanim.MathObjects.Updaters;

import com.jmathanim.MathObjects.Shape;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * Show the associated Rect of the object
 */
public class RectUpdater extends Updater {
    private final Shape rect;
    private final JMathAnimScene scene;


    public RectUpdater() {
        scene= JMathAnimConfig.getConfig().getScene();
        rect = Shape.square().drawColor("gold");//default style
    }

//    @Override
//    public int computeUpdateLevel() {
//        return getMathObject().getUpdateLevel() + 1;
//    }

    @Override
    public void update() {
        scene.add(rect);
        if (getMathObject() == null) {
            rect.visible(false);
        }
        AffineJTransform tr = AffineJTransform.createAffineTransformation(
                rect.getBoundingBox(),
                getMathObject().getBoundingBox(),
                1);

        rect.applyAffineTransform(tr);
    }

    public Shape getRect() {
        return rect;
    }

}
