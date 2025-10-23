package com.jmathanim.mathobjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;

public class RigidBox extends MathObject<RigidBox> {
    protected final AffineJTransform baseModelMatrix;
    private MathObject<?> mathObjectReference;
    private MathObject<?> mathObjectCopyToDraw;
    private boolean isCopyToDrawTransformedByMatrix;

    public RigidBox(MathObject<?> mathObject) {
        this.mathObjectReference = mathObject;
        this.mathObjectCopyToDraw = mathObject.copy();
        isCopyToDrawTransformedByMatrix = false;
        baseModelMatrix = new AffineJTransform();
        setObjectLabel("rigidbox");
    }

    @Override
    public DrawStyleProperties getMp() {
        return mathObjectReference.getMp();
    }

    public MathObject<?> getReferenceMathObject() {
        return mathObjectReference;
    }

    public void setMathObjectReference(MathObject<?> mathObjectReference) {

        this.mathObjectReference = (mathObjectReference == null ? new NullMathObject() : mathObjectReference);
        this.mathObjectCopyToDraw = this.mathObjectReference.copy();
    }

    public MathObject<?> getMathObjectCopyToDraw() {
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
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof RigidBox)) return;
        RigidBox rigidBox = (RigidBox) obj;
        modelMatrix.copyFrom(rigidBox.modelMatrix);
        baseModelMatrix.copyFrom(rigidBox.baseModelMatrix);
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

    public RigidBox applyAffineTransform(AffineJTransform affineJTransform) {
        AffineJTransform compose = modelMatrix.compose(affineJTransform);
        modelMatrix.copyFrom(compose);
        isCopyToDrawTransformedByMatrix = false;
        return this;
    }

    public RigidBox applyAffineTransformToBaseTransform(AffineJTransform transform) {
        AffineJTransform compose = baseModelMatrix.compose(transform);
        baseModelMatrix.copyFrom(compose);
        return this;
    }


    public void resetMatrix() {
        modelMatrix.copyFrom(baseModelMatrix);
        isCopyToDrawTransformedByMatrix = false;
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        mathObjectReference.update(scene);
        setHasBeenUpdated(true);
    }

    @Override
    public String toString() {
        return "RigidBox[" + mathObjectReference + ']';
    }

    @Override
    protected boolean isHasBeenUpdated() {
        return super.isHasBeenUpdated() && mathObjectReference.isHasBeenUpdated();
    }

    @Override
    protected void setHasBeenUpdated(boolean hasBeenUpdated) {
        super.setHasBeenUpdated(hasBeenUpdated);
        if (!hasBeenUpdated) mathObjectReference.setHasBeenUpdated(hasBeenUpdated);
    }
}
