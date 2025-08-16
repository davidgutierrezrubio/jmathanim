package com.jmathanim.mathobjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;

public class RigidBox extends MathObject {
    private MathObject mathObject;

    public RigidBox(MathObject mathObject) {
        this.mathObject = mathObject;
    }

    @Override
    public Stylable getMp() {
        return mathObject.getMp();
    }

    public MathObject getMathObject() {
        return mathObject;
    }

    public void setMathObject(MathObject mathObject) {
        this.mathObject = mathObject;
    }

    @Override
    public RigidBox copy() {
        RigidBox copy = new RigidBox(mathObject);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        super.copyStateFrom(obj);
    }

    @Override
    protected Rect computeBoundingBox() {
        return mathObject.getBoundingBox().getTransformedRect(modelMatrix);
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera camera) {
        mathObject.copy().applyAffineTransform(modelMatrix).draw(scene, r, camera);
    }

    public <T extends MathObject> T applyAffineTransform(AffineJTransform transform) {
        AffineJTransform compose = modelMatrix.compose(transform);
        modelMatrix.copyFrom(compose);
        return (T) this;// By default does nothing
    }
    public void resetMatrix() {
        modelMatrix.copyFrom(new AffineJTransform());
    }


}
