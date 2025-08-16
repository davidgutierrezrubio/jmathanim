package com.jmathanim.mathobjects.Delimiters;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.*;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;
import com.jmathanim.mathobjects.Text.TextUpdaters.CountUpdaterFactory;
import com.jmathanim.mathobjects.Text.TextUpdaters.LengthUpdaterFactory;
import com.jmathanim.mathobjects.Text.TextUpdaters.TextUpdaterFactory;
import com.jmathanim.mathobjects.updateableObjects.AnchoredMathObject;

public abstract class Delimiter extends Constructible {
    public final Point labelMarkPoint;
    protected final Point A;
    protected final Point B;
    protected final Shape delimiterShapeToDraw;
//    protected final MODrawProperties mpDelimiterShape;
    protected final MODrawPropertiesArray mpDelimiter;
    /**
     * Elements created to be sent to the renderer.
     * Usually one Shape (delimiter) and optionally a MathObject as Label
     */
    protected final MathObjectGroup groupElementsToBeDrawn;
    private MathObject delimiterLabel;
    public final RigidBox delimiterLabelRigidBox;
    protected double amplitudeScale;
    protected TextUpdaterFactory textUpdaterFactory;
    protected double labelMarkGap;
    protected Delimiter.Rotation rotateLabel;
    protected double delimiterScale;
    protected double minimumWidthToShrink;

    /**
     * Type of delimiter to draw (bracket, parenthesis, length_arrow, etc.)
     */
    protected Delimiter.Type type;
    /**
     * Gap to apply between control points and delimiter
     */
    protected double gap;


    public Delimiter(Point A, Point B, Delimiter.Type type, double gap) {
        super();
        this.A = A;
        this.B = B;
        this.type = type;
        this.gap = gap;

        this.delimiterShapeToDraw = new Shape();
//        this.mpDelimiterShape = (MODrawProperties) this.delimiterShapeToDraw.getMp();


        this.mpDelimiter = new MODrawPropertiesArray();
        this.mpDelimiter.add(delimiterShapeToDraw);
        this.mpDelimiter.loadFromStyle("DEFAULT");


        labelMarkPoint = Point.at(0, 0);
        this.rotateLabel = Delimiter.Rotation.SMART;
        this.delimiterLabel = new NullMathObject();
        delimiterLabelRigidBox=new RigidBox(this.delimiterLabel);
        this.mpDelimiter.add(delimiterLabelRigidBox);


        groupElementsToBeDrawn = MathObjectGroup.make();
        groupElementsToBeDrawn.addWithKey("shape", this.delimiterShapeToDraw);
        groupElementsToBeDrawn.addWithKey("label", this.delimiterLabelRigidBox);

        delimiterScale = 1;
        amplitudeScale = 1;

    }

    /**
     * Constructs a new delimiter.The points mark the beginning and end of the delimiter.The delimiter lies at the
     * "left" of vector AB.
     *
     * @param A    Beginning point
     * @param B    Ending point
     * @param type Type of delimiter, one enum {@link Delimiter.Type}
     * @param gap  Gap between control points and delimiter
     * @return The delimiter
     */
    public static Delimiter make(Point A, Point B, Delimiter.Type type, double gap) {
        Delimiter resul = null;
        switch (type) {
            case BRACE:
            case PARENTHESIS:
            case BRACKET:
                resul = ShapeDelimiter.make(A, B, type, gap);
                break;
            case LENGTH_ARROW:
            case LENGTH_BRACKET:
                resul = LengthMeasure.make(A, B, type, gap);
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
    public static Delimiter makeStacked(MathObject obj, Anchor.Type anchorType, Type delimiterType, double gap) {
        JMathAnimScene sce = JMathAnimConfig.getConfig().getScene();//This should be better implemented, avoid static singletons
        Anchor.Type anchorA, anchorB;
        switch (anchorType) {
            case UPPER:
                anchorA = Anchor.Type.ULEFT;
                anchorB = Anchor.Type.URIGHT;
                break;
            case LOWER:
                anchorA = Anchor.Type.DRIGHT;
                anchorB = Anchor.Type.DLEFT;
                break;
            case RIGHT:
                anchorA = Anchor.Type.URIGHT;
                anchorB = Anchor.Type.DRIGHT;
                break;
            case LEFT:
                anchorA = Anchor.Type.DLEFT;
                anchorB = Anchor.Type.ULEFT;
                break;
            default:
                JMathAnimScene.logger.error("Invalid anchor for delimiter object " + anchorType.name());
                return null;
        }
        Point A = Anchor.getAnchorPoint(obj, anchorA);
        Point B = Anchor.getAnchorPoint(obj, anchorB);
        //Register points A and B as updateable
        sce.registerUpdateable(new AnchoredMathObject(A, Anchor.Type.CENTER, obj, anchorA));
        sce.registerUpdateable(new AnchoredMathObject(B, Anchor.Type.CENTER, obj, anchorB));

        Delimiter resul = Delimiter.make(A, B, delimiterType, gap);
        return resul;
    }



    public Point getA() {
        return A;
    }

    public Point getB() {
        return B;
    }

    @Override
    public MathObject getMathObject() {
        return groupElementsToBeDrawn;
    }

    @Override
    public Stylable getMp() {
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
     * @param rotateLabel True if label should be rotated, false otherwise.
     * @return This object
     */
    public <T extends Delimiter> T setRotationType(Rotation rotateLabel) {
        this.rotateLabel = rotateLabel;
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
    public void copyStateFrom(MathObject obj) {
        super.copyStateFrom(obj);
        if (!(obj instanceof Delimiter)) {
            return;
        }
        Delimiter del = (Delimiter) obj;
        delimiterShapeToDraw.copyStateFrom(del.delimiterShapeToDraw);
        delimiterLabelRigidBox.copyStateFrom(del.delimiterLabelRigidBox);
        this.A.copyStateFrom(del.A);
        this.B.copyStateFrom(del.B);
        getMp().copyFrom(obj.getMp());
        this.labelMarkGap = del.labelMarkGap;
        this.mpDelimiter.copyFrom(del.mpDelimiter);


        if (isFreeMathObject()) {
//            setLabel(del.delimiterLabelRigidBox.copy(), del.labelMarkGap);
            delimiterLabelRigidBox.copyStateFrom(del.delimiterLabelRigidBox);
        }
        else
        if (del.delimiterLabel != null) {
            if (del.textUpdaterFactory instanceof LengthUpdaterFactory) {
                addLengthLabel(del.labelMarkGap, del.textUpdaterFactory.getFormat());
            } else if (del.textUpdaterFactory instanceof CountUpdaterFactory) {
                addCountLabel(del.labelMarkGap,((CountUpdaterFactory)del.textUpdaterFactory).getObjectToCount());
            }
            else
            {
                setLabel(del.getLabel().copy(), del.labelMarkGap);
            }
            getLabel().getMp().copyFrom(del.getLabel().getMp());
        }
        labelMarkGap=del.labelMarkGap;
        rotateLabel = del.rotateLabel;
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

    public void debugInfo() {
//    System.out.println(this.getObjectLabel()+":  groupElementsToBeDrawn size: "+groupElementsToBeDrawn.size());
//    System.out.println(this.getObjectLabel()+":  is Label a NullMathObject: "+(groupElementsToBeDrawn.get(1) instanceof  NullMathObject));
//    System.out.println(this.getObjectLabel()+":  is FreeMathObject: "+this.isFreeMathObject());
//    for (MathObject o: groupElementsToBeDrawn) {
//        System.out.println(this.getObjectLabel()+":  groupElementsToBeDrawn elements: "+o);
//    }
//        System.out.println(this.delimiterShapeToDraw.getMp() + " " + this.mpDelimiterShape);
    }


    public Delimiter setLabel(String text, double labelGap) {
        return setLabel(LaTeXMathObject.make(text), labelGap);
    }

    public <T extends Delimiter> T setLabel(MathObject label, double labelGap) {
        this.labelMarkGap = labelGap;
        this.delimiterLabel = label;

        this.delimiterLabelRigidBox.setMathObject(label);

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
    public LaTeXMathObject addLengthLabel(double gap,
                                          String format) {
        setLabel("${#0}$", gap);
        LaTeXMathObject t = (LaTeXMathObject) getLabel();
        t.setArgumentsFormat(format);

        textUpdaterFactory = new LengthUpdaterFactory(scene, t, A, B, format);
        t.registerUpdater(textUpdaterFactory.getUpdater());
        JMathAnimScene scene = JMathAnimConfig.getConfig().getScene();
        t.update(scene);
        rebuildShape();

        return (LaTeXMathObject) getLabel();
    }

    /**
     * Adds a label that displays the count of objects in a given MathObjectGroup. The label is positioned based on the
     * delimiter's label mark point.
     *
     * <p>This method sets the label format to display the count of the MathObjectGroup
     * size. The label will automatically update whenever the size of the group changes.</p>
     *
     * @param gap The gap between the delimiter and the label.
     * @param objectToCount  The MathObjectGroup whose size will be counted and displayed in the label.
     * @return The label as a LaTeXMathObject that shows the count of objects in the group.
     */
    public LaTeXMathObject addCountLabel(double gap, Object objectToCount) {
        setLabel("${#0}$", .1);
        LaTeXMathObject t = (LaTeXMathObject) getLabel();
        t.setArgumentsFormat("#");
        textUpdaterFactory=new CountUpdaterFactory(scene,t,objectToCount,"#");
        t.registerUpdater(textUpdaterFactory.getUpdater());
        t.update(JMathAnimConfig.getConfig().getScene());
        return (LaTeXMathObject) getLabel();
    }


    /**
     * Type of label rotation
     */
    public enum Rotation {
        FIXED, SMART, ROTATE
    }

    /**
     * Type of delimiter
     */
    public enum Type {
        /**
         * Brace {
         */
        BRACE,
        /**
         * Parenthesis (
         */
        PARENTHESIS,
        /**
         * Brackets
         */
        BRACKET,
        /**
         * Simple bar addLengthLabel length
         */
        LENGTH_BRACKET,
        /**
         * Simple arrow addLengthLabel length
         */
        LENGTH_ARROW
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        groupElementsToBeDrawn.update(scene);
    }
}
