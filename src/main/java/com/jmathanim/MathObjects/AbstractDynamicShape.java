package com.jmathanim.MathObjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.jmathanim.JMathAnimScene;

public abstract class AbstractDynamicShape<T extends AbstractDynamicShape<T>> extends AbstractShape<T> {

    protected final MathObjectGroup objectsToDraw;

    public AbstractDynamicShape() {
        this.objectsToDraw = MathObjectGroup.make();
    }

    @Override
    public void performMathObjectUpdateActions(JMathAnimScene scene) {
        rebuildShape();
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        for (MathObject<?> toDraw : this.objectsToDraw) {
            toDraw.draw(scene, r, cam);
        }
    }

    protected abstract void rebuildShape();
}
