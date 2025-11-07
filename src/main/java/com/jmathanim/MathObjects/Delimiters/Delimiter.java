package com.jmathanim.MathObjects.Delimiters;

import com.jmathanim.Constructible.CTNullMathObject;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.DelimiterType;
import com.jmathanim.Enum.RotationType;
import com.jmathanim.MathObjects.*;
import com.jmathanim.MathObjects.Text.LatexMathObject;
import com.jmathanim.MathObjects.Text.TextUpdaters.CountUpdaterFactory;
import com.jmathanim.MathObjects.Text.TextUpdaters.LengthUpdaterFactory;
import com.jmathanim.MathObjects.Text.TextUpdaters.TextUpdaterFactory;
import com.jmathanim.MathObjects.Updaters.Updater;
import com.jmathanim.Styling.DrawStylePropertiesObjectsArray;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;

public abstract class Delimiter extends Constructible<Delimiter> {
    public final Vec labelMarkPoint;
    protected final Coordinates<?> A;
    protected final Coordinates<?> B;
    protected final Shape delimiterShapeToDraw;
//    protected final MODrawProperties mpDelimiterShape;
    protected final DrawStylePropertiesObjectsArray mpDelimiter;
    /**
     * Elements created to be sent to the renderer.
     * Usually one Shape (delimiter) and optionally a MathObject as Label
     */
    protected final MathObjectGroup groupElementsToBeDrawn;
    private MathObject<?> delimiterLabel;
    public final RigidBox delimiterLabelRigidBox;
    protected double amplitudeScale;
    protected TextUpdaterFactory textUpdaterFactory;
    protected double labelMarkGap;
    protected RotationType rotationType;
    protected double delimiterScale;
    protected double minimumWidthToShrink;

    /**
     * Type of delimiter to draw (bracket, parenthesis, length_arrow, etc.)
     */
    protected DelimiterType type;
    /**
     * Gap to apply between control points and delimiter
     */
    protected double gap;


    public Delimiter(Coordinates<?> A, Coordinates<?> B, DelimiterType type, double gap) {
        super();
        this.A = A;
        this.B = B;
        this.type = type;
        this.gap = gap;

        this.delimiterShapeToDraw = new Shape();
//        this.mpDelimiterShape = (MODrawProperties) this.delimiterShapeToDraw.getMp();


        this.mpDelimiter = new DrawStylePropertiesObjectsArray();
        this.mpDelimiter.add(delimiterShapeToDraw);
        this.mpDelimiter.loadFromStyle("DEFAULT");


        labelMarkPoint = Vec.to(0, 0);
        this.rotationType = RotationType.SMART;
        this.delimiterLabel = new CTNullMathObject();
        delimiterLabelRigidBox=new RigidBox(this.delimiterLabel);
        this.mpDelimiter.add(delimiterLabelRigidBox);


        groupElementsToBeDrawn = MathObjectGroup.make();
        groupElementsToBeDrawn.addWithKey("shape", this.delimiterShapeToDraw);
        groupElementsToBeDrawn.addWithKey("label", this.delimiterLabelRigidBox);


        addDependency(this.A);
        addDependency(this.B);
        addDependency(this.groupElementsToBeDrawn);

        delimiterScale = 1;
        amplitudeScale = 1;

    }

    /**
     * Constructs a new delimiter.The points mark the beginning and end of the delimiter.The delimiter lies at the
     * "left" of vector AB.
     *
     * @param A    Beginning point
     * @param B    Ending point
     * @param type Type of delimiter, one enum {@link DelimiterType}
     * @param gap  Gap between control points and delimiter
     * @return The delimiter
     */
    public static Delimiter make(Coordinates<?> A, Coordinates<?> B, DelimiterType type, double gap) {
        Delimiter resul = null;
        switch (type) {
            case BRACE:
            case PARENTHESIS:
            case BRACKET:
            case INVISIBLE:
                resul = ShapeDelimiter.makeShapeDelimiter(A, B, type, gap);
                break;
            case LENGTH_ARROW:
            case LENGTH_BRACKET:
                resul = LengthMeasure.makeLengthMeasure(A, B, type, gap);
                break;
        }
        resul.amplitudeScale = 1;
        resul.delimiterScale = 1;
        return resul;
    }

    /**
     * Creates a delimiter that is permanently stacked to a given MathObject.
     * Note that this delimiters always stack to the appropiate rect boundaryBox
     * points. Thus for example, delimiters will not rotate if the mathobject is
     * being rotated.
     *
     * @param obj
     * @param anchorType    Anchor to use. Currently UPPER, LOWER, RIGHT and LEFT
     *                      are allowed. Other anchors return a null object and an error message.
     * @param delimiterType Delimiter type
     * @param gap           Gap to put between anchor points and delimiter
     * @return The delimiter
     */
    public static Delimiter makeStacked(MathObject<?> obj, AnchorType anchorType, DelimiterType delimiterType, double gap) {
        JMathAnimScene sce = JMathAnimConfig.getConfig().getScene();//This should be better implemented, avoid static singletons
        AnchorType anchorA, anchorB;
        switch (anchorType) {
            case UPPER:
                anchorA = AnchorType.LEFT_AND_ALIGNED_UPPER;
                anchorB = AnchorType.RIGHT_AND_ALIGNED_UPPER;
                break;
            case LOWER:
                anchorA = AnchorType.RIGHT_AND_ALIGNED_LOWER;
                anchorB = AnchorType.LEFT_AND_ALIGNED_LOWER;
                break;
            case RIGHT:
                anchorA = AnchorType.RIGHT_AND_ALIGNED_UPPER;
                anchorB = AnchorType.RIGHT_AND_ALIGNED_LOWER;
                break;
            case LEFT:
                anchorA = AnchorType.LEFT_AND_ALIGNED_LOWER;
                anchorB = AnchorType.LEFT_AND_ALIGNED_UPPER;
                break;
            default:
                JMathAnimScene.logger.error("Invalid anchor for delimiter object " + anchorType.name());
                return null;
        }
        Vec A = Anchor.getAnchorPoint(obj, anchorA);
        Vec B = Anchor.getAnchorPoint(obj, anchorB);
        //Register points A and B as updateable
//        sce.registerUpdateable(new AnchoredMathObject(Point.at(A), AnchorType.CENTER, obj, anchorA));
//        sce.registerUpdateable(new AnchoredMathObject(Point.at(B), AnchorType.CENTER, obj, anchorB));

        Delimiter resul = Delimiter.make(A, B, delimiterType, gap);
        resul.registerUpdater(new Updater() {
            @Override
            public void update(JMathAnimScene scene) {
                resul.getA().copyCoordinatesFrom(Anchor.getAnchorPoint(obj, anchorA));
                resul.getB().copyCoordinatesFrom(Anchor.getAnchorPoint(obj, anchorB));
                resul.rebuildShape();
            }
        });
        return resul;
    }


    public Vec getA() {
        return A.getVec();
    }

    public Vec getB() {
        return B.getVec();
    }

    @Override
    public MathObject getMathObject() {
        return groupElementsToBeDrawn;
    }

    @Override
    public DrawStylePropertiesObjectsArray getMp() {
        return mpDelimiter;
    }


    /*
     * @return The label
     */
    public MathObject getLabel() {
        return delimiterLabel;
    }

    @Override
    public Delimiter copy() {
        Delimiter copy = make(A.copy(), B.copy(), type, gap);
        copy.copyStateFrom(this);
        return copy;
    }

    /**
     * Returns the scale of the amplitude of delimiter. A value of 1 draws the delimiter from one anchor point to
     * another. Smaller values scales the delimiter in the same proportion. This value is used mainly for showCreation
     * animations-like.
     *
     * @return The amplitude scale. A value from 0 to 1
     */
    public double getAmplitudeScale() {
        return amplitudeScale;
    }

    /**
     * Returns the scale of the amplitude of delimiter. A value of 1 draws the delimiter from one anchor point to
     * another. Smaller values scales the delimiter in the same proportion. This value is used mainly for showCreation
     * animations-like.
     *
     * @param amplitudeScale The delimiter scale, from 0 to 1. Values are automatically cropped to this interval.
     */
    public <T extends Delimiter> T setAmplitudeScale(double amplitudeScale) {
        this.amplitudeScale = amplitudeScale;
        this.buildDelimiterShape();
        return (T) this;
    }

    /**
     * Sets the rotate flag. If true, label will be rotated according to
     * delimiter.
     *
     * @param rotationType True if label should be rotated, false otherwise.
     * @return This object
     */
    public <T extends Delimiter> T setRotationType(RotationType rotationType) {
        this.rotationType = rotationType;
        rebuildShape();
        return (T) this;
    }


    /**
     * Returns the delimiter scale. Higher values will result in thicker shapes.
     *
     * @return The actual scale.
     */
    public double getDelimiterScale() {
        return delimiterScale;
    }

    /**
     * Sets the scale of the delimiter. Higher values will result in thicker
     * shapes
     *
     * @param <T>            Subclass
     * @param delimiterScale Scale. Default value is 1.
     * @return This object
     */
    public <T extends Delimiter> T setDelimiterScale(double delimiterScale) {
        this.delimiterScale = delimiterScale;
        return (T) this;
    }



    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof Delimiter)) {
            return;
        }
        super.copyStateFrom(obj);
        Delimiter del = (Delimiter) obj;
        delimiterShapeToDraw.copyStateFrom(del.delimiterShapeToDraw);
        delimiterLabelRigidBox.copyStateFrom(del.delimiterLabelRigidBox);

        this.A.copyCoordinatesFrom(del.A);
        this.B.copyCoordinatesFrom(del.B);
        getMp().copyFrom(del.getMp());
        this.labelMarkGap = del.labelMarkGap;
        this.mpDelimiter.copyFrom(del.mpDelimiter);


        if (isFreeMathObject()) {
//            setLabel(del.delimiterLabelRigidBox.copy(), del.labelMarkGap);
            delimiterLabelRigidBox.copyStateFrom(del.delimiterLabelRigidBox);
        }
        else
        if (del.delimiterLabel != null) {
            if (del.textUpdaterFactory instanceof LengthUpdaterFactory) {
                addLengthLabelTip(del.labelMarkGap, del.textUpdaterFactory.getFormat());
            } else if (del.textUpdaterFactory instanceof CountUpdaterFactory) {
//                addCountLabelTip(del.labelMarkGap,((CountUpdaterFactory)del.textUpdaterFactory).getObjectToCount());
            }
            else
            {
                addlabelTip(del.getLabel().copy(), del.labelMarkGap);
            }
            getLabel().getMp().copyFrom(del.getLabel().getMp());
        }
        labelMarkGap=del.labelMarkGap;
        rotationType = del.rotationType;
        minimumWidthToShrink = del.minimumWidthToShrink;
        amplitudeScale = del.amplitudeScale;
        delimiterScale = del.delimiterScale;
    }


    @Override
    public void rebuildShape() {
        if (!isFreeMathObject()) {
            buildDelimiterShape();
        }
    }

    protected abstract void buildDelimiterShape();


    public Delimiter addlabelTip(String text, double labelGap) {
        return addlabelTip(LatexMathObject.make(text), labelGap);
    }

    public <T extends Delimiter> T addlabelTip(MathObject label, double labelGap) {
        this.labelMarkGap = labelGap;
        this.delimiterLabel = label;

        this.delimiterLabelRigidBox.setMathObjectReference(label);

        rebuildShape();
        return (T) this;
    }

    /**
     * Adds a label with the length.The points mark the beginning and end of the delimiter.The delimiter lies at the
     * "left" of vector AB.
     *
     * @param gap    Gap between control delimiter and label
     * @param format Format to print the length, for example "0.00"
     * @return The Label, a LatexMathObject
     */
    public LatexMathObject addLengthLabelTip(double gap,
                                             String format) {
        addlabelTip("${#0}$", gap);
        LatexMathObject t = (LatexMathObject) getLabel();
        t.setArgumentsFormat(format);

        textUpdaterFactory = new LengthUpdaterFactory(scene, t, A, B, format);
        t.registerUpdater(textUpdaterFactory.getUpdater());
        JMathAnimScene scene = JMathAnimConfig.getConfig().getScene();
        t.update(scene);
        rebuildShape();

        return (LatexMathObject) getLabel();
    }

    @Override
    protected void performUpdateActions(JMathAnimScene scene) {
    }

    @Override
    protected Rect computeBoundingBox() {
        Rect bb = super.computeBoundingBox();
//        if (getL != null) {
//            return Rect.union(bb, labelTip.getBoundingBox());
//        } else
            return bb;
    }
}
