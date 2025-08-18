package com.jmathanim.mathobjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;

public class RigidBox extends MathObject {
    private MathObject mathObjectReference;
    private MathObject mathObjectCopyToDraw;
    private boolean isCopyToDrawTransformedByMatrix;

    public RigidBox(MathObject mathObject) {
        this.mathObjectReference = mathObject;
        this.mathObjectCopyToDraw = mathObject.copy();
        isCopyToDrawTransformedByMatrix = false;
        setObjectLabel("rigidbox");
    }

    @Override
    public Stylable getMp() {
        return mathObjectReference.getMp();
    }

    public MathObject getReferenceMathObject() {
        return mathObjectReference;
    }

    public void setMathObjectReference(MathObject mathObjectReference) {

        this.mathObjectReference = (mathObjectReference == null ? new NullMathObject() : mathObjectReference);
        this.mathObjectCopyToDraw = this.mathObjectReference.copy();
    }

    public MathObject getMathObjectCopyToDraw() {
        if (!isCopyToDrawTransformedByMatrix) {
            mathObjectCopyToDraw.applyAffineTransform(modelMatrix);
            mathObjectCopyToDraw.getMp().copyFrom(mathObjectReference.getMp());
        }
        return mathObjectCopyToDraw;
    }

    @Override
    public RigidBox copy() {
        RigidBox copy = new RigidBox(mathObjectReference);
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
        return mathObjectReference.getBoundingBox().getTransformedRect(modelMatrix);
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera camera) {
        mathObjectCopyToDraw.copyStateFrom(mathObjectReference);
        mathObjectCopyToDraw.applyAffineTransform(modelMatrix).draw(scene, r, camera);
        isCopyToDrawTransformedByMatrix = true;
    }

    public <T extends MathObject> T applyAffineTransform(AffineJTransform transform) {
        AffineJTransform compose = modelMatrix.compose(transform);
        modelMatrix.copyFrom(compose);
        isCopyToDrawTransformedByMatrix = false;
        return (T) this;// By default does nothing
    }

    public void resetMatrix() {
        modelMatrix.copyFrom(new AffineJTransform());
        isCopyToDrawTransformedByMatrix = false;
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        mathObjectReference.update(scene);
    }

    @Override
    public String toString() {
        return "RigidBox[" + mathObjectReference + ']';
    }
}
