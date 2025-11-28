package com.jmathanim;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Shape;
import com.jmathanim.MathObjects.Shapes.JMPath;
import com.jmathanim.MathObjects.Stateable;
import com.jmathanim.MathObjects.Text.AbstractLatexMathObject;
import com.jmathanim.MathObjects.Text.TextUpdaters.TextUpdaterFactory;
import com.jmathanim.MathObjects.Tippable.LabelTip;
import com.jmathanim.MathObjects.Updaters.Updater;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.CircularArrayList;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;

public abstract class AbstractDelimiter<T extends AbstractDelimiter<T>> extends Constructible<AbstractDelimiter<T>> {
    private LabelTypeEnum labelType;

    protected enum LabelTypeEnum {NORMAL, DISTANCE, COORDS}
    protected final Shape delimiterShape;
    protected Vec A;
    protected Vec B;
    public LabelTip labelTip;
    private double amplitudeScale;
    private TextUpdaterFactory textUpdaterFactory;
    protected final CircularArrayList<JMPath> pathsForLabelsTips;
    protected int defaultPathForLabelTip=0;

    protected AbstractDelimiter(Coordinates<?> a, Coordinates<?> b) {
        this.A = a.getVec();
        this.B = b.getVec();
        addDependency(a);
        addDependency(b);
        delimiterShape = new Shape();
        pathsForLabelsTips = new CircularArrayList<>();
        amplitudeScale=1;
    }

    @Override
    public DrawStyleProperties getMp() {
        return delimiterShape.getMp();
    }



    public JMPath getPathForLabelTip(int index) {
        return pathsForLabelsTips.get(index);
    }

    public LabelTip addLabelTip(String text) {
        labelTip = LabelTip.makeLabelTip(getPathForLabelTip(defaultPathForLabelTip), .5, text, true);
        labelTip.setDistanceToShapeRelative(true);
        labelTip.setDistanceToShape(.1);
        rebuildShape();
        return labelTip;
    }
    public LabelTip addLengthLabelTip(String format) {

        labelTip = LabelTip.makeLabelTip(getPathForLabelTip(defaultPathForLabelTip), .5, "{#0}",true);
        labelTip.setDistanceToShape(.1);
        labelTip.setDistanceToShapeRelative(true);
        labelTip.setAnchor(AnchorType.LOWER);
        labelType = LabelTypeEnum.DISTANCE;
        labelTip.addDependency(this.A);
        labelTip.addDependency(this.B);

        AbstractLatexMathObject<?> t = labelTip.getLaTeXObject();
        t.setArgumentsFormat(format);

//        LengthUpdaterFactory updaterFactory = new LengthUpdaterFactory(t, this.A, this.B, format);
//        updaterFactory.registerUpdaters();
        labelTip.registerUpdater(new Updater() {

            @Override
            public void applyBefore() {
                labelTip.getLaTeXObject().getArg(0).setValue(A.to(B).norm());
                labelTip.scale(getAmplitudeScale());
                labelTip.getLaTeXObject().update();
            }

            @Override
            public void applyAfter() {

            }
        });
        t.update();
        return labelTip;
    }



//
//    public LabelTip getLabelTip() {
//        return labelTip;
//    }
//
//    public void setLabelTip(LabelTip labelTip) {
//        this.labelTip = labelTip;
//    }

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
