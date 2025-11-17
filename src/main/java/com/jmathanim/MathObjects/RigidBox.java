package com.jmathanim.MathObjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Constructible.NullMathObject;
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
        setMathObjectReference(mathObject);
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
        removeDependency(this.mathObjectReference);
        this.mathObjectReference = (mathObjectReference == null ? NullMathObject.make() : mathObjectReference);
        addDependency(this.mathObjectReference);
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
    public Rect computeBoundingBox() {
        Rect boundingBox1 = mathObjectReference.computeBoundingBox();
        return boundingBox1.getTransformedRect(modelMatrix);
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
    public void performMathObjectUpdateActions(JMathAnimScene scene) {
        mathObjectCopyToDraw.update(scene);
    }

    @Override
    public String toString() {
        return "RigidBox[" + mathObjectReference + ']';
    }

    @Override
    public boolean update(JMathAnimScene scene) {
        mathObjectCopyToDraw.update(scene);
        return super.update(scene);
    }
}
