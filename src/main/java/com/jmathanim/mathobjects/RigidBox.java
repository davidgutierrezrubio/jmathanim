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
        setObjectLabel("rigidbox");
    }

    @Override
    public Stylable getMp() {
        return mathObject.getMp();
    }

    public MathObject getMathObject() {
        return mathObject;
    }

    public void setMathObject(MathObject mathObject) {

        this.mathObject = (mathObject == null ? new NullMathObject() : mathObject);
    }

    @Override
    public RigidBox copy() {
        RigidBox copy = new RigidBox(mathObject);
        copy.setObjectLabel(this.objectLabel + "_copy");
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {

//        super.copyStateFrom(obj);
        if (obj instanceof RigidBox) {
            RigidBox rigidBox = (RigidBox) obj;
            modelMatrix.copyFrom(rigidBox.modelMatrix);
        }

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

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        mathObject.update(scene);
    }
    public MathObject getTransformedCopyObject() {
        return   mathObject.copy().applyAffineTransform(modelMatrix);
    }

    @Override
    public String toString() {
        return "RigidBox[" + mathObject + ']';
    }
}
