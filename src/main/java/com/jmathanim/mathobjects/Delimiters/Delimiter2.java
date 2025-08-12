package com.jmathanim.mathobjects.Delimiters;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.MODrawPropertiesArrayMP;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.*;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;
import com.jmathanim.mathobjects.Text.TextUpdaters.CountUpdaterFactory;
import com.jmathanim.mathobjects.Text.TextUpdaters.LengthUpdaterFactory;
import com.jmathanim.mathobjects.Text.TextUpdaters.TextUpdaterFactory;
import com.jmathanim.mathobjects.updaters.Updater;

public abstract class Delimiter2 extends Constructible {
    public final Point labelMarkPoint;
    protected final Point A;
    protected final Point B;
    protected final Shape delimiterShapeToDraw;
    protected final MODrawProperties mpDelimiterShape;
    protected final MODrawPropertiesArrayMP mpDelimiter;
    /**
     * Elements created to be sent to the renderer.
     * Usually one Shape (delimiter) and optionally a MathObject as Label
     */
    protected final MathObjectGroup groupElementsToBeDrawn;
    public MathObject delimiterLabelToDraw;
    protected double amplitudeScale;
    protected MathObject delimiterLabel;
    protected TextUpdaterFactory textUpdaterFactory;
    protected double labelMarkGap;
    protected Delimiter.Rotation rotateLabel;
    protected double delimiterScale;
    protected double minimumWidthToShrink;

    /**
     * Type of delimiter to draw (bracket, parenthesis, length_arrow, etc.)
     */
    protected Delimiter2.Type type;
    /**
     * Gap to apply between control points and delimiter
     */
    protected double gap;


    public Delimiter2(Point A, Point B, Delimiter2.Type type, double gap) {
        super();
        this.A = A;
        this.B = B;
        this.type = type;
        this.gap = gap;

        this.delimiterShapeToDraw = new Shape();
        this.mpDelimiterShape = (MODrawProperties) this.delimiterShapeToDraw.getMp();


        this.mpDelimiter = new MODrawPropertiesArrayMP();
        this.mpDelimiter.add(mpDelimiterShape);

        this.mpDelimiter.loadFromStyle("DEFAULT");


        labelMarkPoint = Point.at(0, 0);
        this.rotateLabel = Delimiter.Rotation.SMART;
        this.delimiterLabel = new NullMathObject();


        groupElementsToBeDrawn = MathObjectGroup.make();
        groupElementsToBeDrawn.addWithKey("shape", this.delimiterShapeToDraw);

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
    public static Delimiter2 make(Point A, Point B, Delimiter2.Type type, double gap) {
        Delimiter2 resul = null;
        switch (type) {
            case BRACE:
            case PARENTHESIS:
            case BRACKET:
                resul = ShapeDelimiter2.make(A, B, type, gap);
                break;
            case LENGTH_ARROW:
            case LENGTH_BRACKET:
                resul = LengthMeasure2.make(A, B, type, gap);
                break;
        }
        resul.amplitudeScale = 1;
        resul.delimiterScale = 1;
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
    public Delimiter2 copy() {

        Delimiter2 copy = make(A.copy(), B.copy(), type, gap);
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
    public <T extends Delimiter2> T setAmplitudeScale(double amplitudeScale) {
        this.amplitudeScale = amplitudeScale;
        this.buildDelimiterShape();
        return (T) this;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        super.copyStateFrom(obj);
        if (!(obj instanceof Delimiter2)) {
            return;
        }
        Delimiter2 del = (Delimiter2) obj;

        this.A.copyStateFrom(del.A);
        this.B.copyStateFrom(del.B);
        getMp().copyFrom(obj.getMp());
        this.labelMarkGap = del.labelMarkGap;
        this.mpDelimiter.copyFrom(del.mpDelimiter);
        delimiterShapeToDraw.copyStateFrom(del.delimiterShapeToDraw);
        delimiterLabelToDraw = del.delimiterLabelToDraw.copy();
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
        System.out.println(this.delimiterShapeToDraw.getMp() + " " + this.mpDelimiterShape);
    }


    public Delimiter2 setLabel(String text, double labelGap) {
        return setLabel(LaTeXMathObject.make(text), labelGap);
    }

    public <T extends Delimiter2> T setLabel(MathObject label, double labelGap) {
        this.labelMarkGap = labelGap;
        this.delimiterLabel = label;

        if (this.mpDelimiter.size() == 2) {
            this.mpDelimiter.getMpArray().remove(1);
        }
        this.mpDelimiter.add(label.getMp());


        groupElementsToBeDrawn.clear();
        groupElementsToBeDrawn.add(delimiterLabelToDraw, delimiterShapeToDraw);
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
     * @param mg  The MathObjectGroup whose size will be counted and displayed in the label.
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
}
