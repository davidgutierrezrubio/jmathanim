package com.jmathanim;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Shape;
import com.jmathanim.MathObjects.Shapes.JMPath;
import com.jmathanim.MathObjects.Stateable;
import com.jmathanim.MathObjects.Text.AbstractLatexMathObject;
import com.jmathanim.MathObjects.Text.TextUpdaters.LengthUpdaterFactory;
import com.jmathanim.MathObjects.Tippable.LabelTip;
import com.jmathanim.MathObjects.Updaters.Updater;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;

public abstract class AbstractDelimiter<T extends AbstractDelimiter<T>> extends Constructible<AbstractDelimiter<T>> {
    protected final Shape delimiterShape;
    protected final JMPath pathForLabelTip;
    protected Vec A;
    protected Vec B;
    public LabelTip labelTip;
    double amplitudeScale;
    private LengthUpdaterFactory textUpdaterFactory;

    protected AbstractDelimiter(Coordinates<?> a, Coordinates<?> b) {
        this.A = a.getVec();
        this.B = b.getVec();
        addDependency(a);
        addDependency(b);
        delimiterShape = new Shape();
        pathForLabelTip = new JMPath();
        amplitudeScale=1;
    }

    @Override
    public DrawStyleProperties getMp() {
        return delimiterShape.getMp();
    }

    public LabelTip makeLengthLabelTip(String format) {
        LabelTip labelTip = LabelTip.makeLabelTip(pathForLabelTip, .5, "{#0}", true);
        labelTip.setDistanceToShapeRelative(true);
        labelTip.setDistanceToShape(.1);
        labelTip.getLaTeXObject().setArgumentsFormat(format);
        labelTip.addDependency(this.A);
        labelTip.addDependency(this.B);

        AbstractLatexMathObject<?> t = labelTip.getLaTeXObject();
        t.setArgumentsFormat(format);

        labelTip.registerUpdater(new Updater() {


            @Override
            public void applyAfter() {

            }
            @Override
            public void applyBefore() {
//                if (labelTip.isDirty()) {
//                    labelTip.tipObjectRigidBox.scale(A.to(B).norm());
//                }
                labelTip.getLaTeXObject().getArg(0).setValue(A.to(B).norm());
            }
        });
        labelTip.registerUpdater(new Updater() {
            long version = -1;

            @Override
            public void applyBefore() {

            }

            @Override
            public void applyAfter() {
                if (labelTip.isDirty()) {
                    labelTip.tipObjectRigidBox.scale(labelTip.getMarkLabelLocation(),A.to(B).norm());
                    System.out.println("change scale");
                }
            }
        });


        t.update();
        return labelTip;
    }


    public JMPath getPathForLabelTip() {
        return pathForLabelTip;
    }

    public LabelTip addLabelTip(String text) {
        labelTip = LabelTip.makeLabelTip(pathForLabelTip, .5, text, true);
        labelTip.setDistanceToShapeRelative(true);
        labelTip.setDistanceToShape(.1);
        rebuildShape();
        return labelTip;
    }

    public AbstractDelimiter<T> addLengthLabelTip(
            String format) {
        labelTip = LabelTip.makeLabelTip(pathForLabelTip, .5, "{#0}", true);
        labelTip.setDistanceToShapeRelative(true);
        labelTip.setDistanceToShape(.1);
        AbstractLatexMathObject<?> t = labelTip.getLaTeXObject();
        textUpdaterFactory = new LengthUpdaterFactory( t, A, B, format);
        textUpdaterFactory.registerUpdaters();
        labelTip.update();
        rebuildShape();

        return this;
    }




    @Override
    public AbstractDelimiter<T> setFreeMathObject(boolean isMathObjectFree) {
        if (labelTip!=null) labelTip.setFreeMathObject(isMathObjectFree);
        return super.setFreeMathObject(isMathObjectFree);
    }

    @Override
    public AbstractDelimiter<T> applyAffineTransform(AffineJTransform affineJTransform) {
        if (labelTip!=null) labelTip.applyAffineTransform(affineJTransform);
        return super.applyAffineTransform(affineJTransform);
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        if (obj instanceof AbstractDelimiter) {
            AbstractDelimiter abDel = (AbstractDelimiter) obj;
            super.copyStateFrom(obj);
            if (labelTip!=null) {
                labelTip.copyStateFrom(abDel.labelTip);
            }
            else
                labelTip=abDel.labelTip!=null ? abDel.labelTip.copy() : null;
        }

    }

    @Override
    public void performMathObjectUpdateActions() {
        if (!isFreeMathObject()) {
            rebuildShape();
        }
        if (labelTip != null) {
            pathForLabelTip.update();
            labelTip.update();
        }
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera camera) {
        getMathObject().draw(scene, r, camera);
        if (labelTip != null) labelTip.draw(scene, r, camera);
    }

    @Override
    public Shape getMathObject() {
        update();
        return delimiterShape;
    }

    @Override
    public Rect computeBoundingBox() {
        return delimiterShape.getBoundingBox();
    }


    public double getAmplitudeScale() {
        return amplitudeScale;
    }

    public void setAmplitudeScale(double amplitudeScale) {
        this.amplitudeScale = amplitudeScale;
    }
}
