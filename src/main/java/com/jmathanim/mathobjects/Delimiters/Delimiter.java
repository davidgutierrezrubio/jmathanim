/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.mathobjects.Delimiters;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;
import com.jmathanim.mathobjects.updateableObjects.AnchoredMathObject;
import com.jmathanim.mathobjects.updaters.Updater;

/**
 * A extensible delimiter like braces or parenthesis
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Delimiter extends MathObject {

    public final Point labelMarkPoint;
    protected final Point A;
    protected final Point B;
    protected final Point scaledA;
    protected final Point scaledB;
    protected double amplitudeScale;
    protected MathObject delimiterLabel;
    protected MathObject delimiterLabelToDraw;
    protected MODrawPropertiesArray mpDelimiter;
    protected double labelMarkGap;
    protected Rotation rotateLabel;

    protected MathObjectGroup delimiterToDraw;

    protected double delimiterScale;
    protected double minimumWidthToShrink;
    protected Type type;
    /**
     * Gap to apply between control points and delimiter
     */
    protected double gap;

    public Delimiter(Point A, Point B, Type type, double gap) {
        this.A = A;
        this.B = B;
        this.scaledA = A.copy();
        this.scaledB = B.copy();
        this.type = type;
        this.gap = gap;
        //An invisible path with only a point (to properly stack and align)
        this.delimiterLabel = null;//Shape.polyLine(Point.at(0, 0)).visible(false);
        this.mpDelimiter = new MODrawPropertiesArray();
        labelMarkPoint = Point.at(0, 0);
        this.rotateLabel = Rotation.SMART;
        this.delimiterToDraw = MathObjectGroup.make();

        delimiterScale = 1;
        amplitudeScale = 1;

    }

    /**
     * Constructs a new delimiter.The points mark the beginning and end of the
     * delimiter.The delimiter lies at the "left" of vector AB.
     *
     * @param A    Beginning point
     * @param B    Ending point
     * @param type Type of delimiter, one enum {@link Type}
     * @param gap  Gap between control points and delimiter
     * @return The delimiter
     */
    public static Delimiter make(Point A, Point B, Type type, double gap) {
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

    /**
     * Adds a label with the length.The points mark the beginning and end of the
     * delimiter.The delimiter lies at the "left" of vector AB.
     *
     * @param gap    Gap between control delimiter and label
     * @param format Format to print the length, for example "0.00"
     * @return The Label, a LatexMathObject
     */
    public LaTeXMathObject addLengthLabel(double gap,
                                          String format) {
        setLabel("${#0}$", .1);
        LaTeXMathObject t = (LaTeXMathObject) getLabel();
        t.setArgumentsFormat(format);

        Updater updater = new Updater() {
            @Override
            public void update(JMathAnimScene scene) {
                t.getArg(0).setScalar(A.to(B).norm());

            }
        };
        t.registerUpdater(updater);
        t.update(JMathAnimConfig.getConfig().getScene());

        return (LaTeXMathObject) getLabel();
    }

    /**
     * Adds a label that displays the count of objects in a given MathObjectGroup.
     * The label is positioned based on the delimiter's label mark point.
     *
     * <p>This method sets the label format to display the count of the MathObjectGroup
     * size. The label will automatically update whenever the size of the group changes.</p>
     *
     * @param gap The gap between the delimiter and the label.
     * @param mg  The MathObjectGroup whose size will be counted and displayed in the label.
     * @return The label as a LaTeXMathObject that shows the count of objects in the group.
     */
    public LaTeXMathObject addCountLabel(double gap, MathObjectGroup mg) {
        setLabel("${#0}$", .1);
        LaTeXMathObject t = (LaTeXMathObject) getLabel();
        t.setArgumentsFormat("#");
        t.registerUpdater(new Updater() {
//            @Override
//            public int computeUpdateLevel() {
//                return Math.max(A.getUpdateLevel(), B.getUpdateLevel()) + 1;
//            }

            @Override
            public void update(JMathAnimScene scene) {
                t.getArg(0).setScalar(mg.size());

            }
        });
        t.update(JMathAnimConfig.getConfig().getScene());
        return (LaTeXMathObject) getLabel();
    }


    /**
     * Adds a label with the vector coordinates.The points mark the beginning and end of the
     * delimiter.The delimiter lies at the "left" of vector AB.
     *
     * @param gap    Gap between control delimiter and label
     * @param format Format to print the numbers, for example "0.00"
     * @return The Label, a LatexMathObject
     */
    public LaTeXMathObject addVecLabel(double gap, String format) {
        setLabel("$({#0},{#1})$", .1);
        LaTeXMathObject t = (LaTeXMathObject) getLabel();
        t.registerUpdater(new Updater() {
            @Override
            public void update(JMathAnimScene scene) {
                Vec v = A.to(B);
                t.getArg(0).setScalar(v.x);
                t.getArg(1).setScalar(v.y);

            }
        });
        t.update(JMathAnimConfig.getConfig().getScene());
        return (LaTeXMathObject) getLabel();
    }

    public Delimiter setLabel(String text, double labelGap) {
        return setLabel(LaTeXMathObject.make(text), labelGap);
    }

    public <T extends Delimiter> T setLabel(MathObject label, double labelGap) {
        mpDelimiter.add(label);
        this.labelMarkGap = labelGap;
        this.delimiterLabel = label;
        return (T) this;
    }

    public <T extends Delimiter> T removeLabel() {
        mpDelimiter.remove(delimiterLabel);
        delimiterLabel = Shape.polyLine(Point.at(0, 0)).visible(false);
        return (T) this;
    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    abstract protected MathObjectGroup buildDelimiterShape();

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        if (delimiterScale == 0) {
            return;// Do nothing
        }
        rebuildShape(scene);

        if (isVisible()) {
            for (MathObject d : delimiterToDraw) {
                d.draw(scene, r, cam);
            }
        }
//            delimiterToDraw.draw(scene, r, cam);
    }

    private void rebuildShape(JMathAnimScene scene) {
        scaledA.v.copyFrom(A.interpolate(B, .5 * (1 - amplitudeScale)).v);
        scaledB.v.copyFrom(B.interpolate(A, .5 * (1 - amplitudeScale)).v);
        MODrawProperties moDrawPropertiesArray=delimiterToDraw.getMp().copy();
        delimiterToDraw = buildDelimiterShape();
//        delimiterToDraw.getMp().copyFrom(moDrawPropertiesArray);
        if (delimiterLabel != null)
            delimiterLabel.update(scene);
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform transform) {
        A.applyAffineTransform(transform);
        B.applyAffineTransform(transform);
        delimiterToDraw.applyAffineTransform(transform);
        if (delimiterLabel != null)
            delimiterLabel.applyAffineTransform(transform);
        return (T) this;
    }

    @Override
    public Rect computeBoundingBox() {
        if ((A.isEquivalentTo(B, 0) || (delimiterScale == 0))) {
            return new EmptyRect();
        }
        rebuildShape(JMathAnimConfig.getConfig().getScene());
        Rect bb = delimiterToDraw.getBoundingBox();
        if (delimiterLabel != null) {
            Rect boundingBox = delimiterLabel.getBoundingBox();
            return Rect.union(bb, boundingBox);
        } else return bb;
    }

    /**
     * Returns the scale of the amplitude of delimiter. A value of 1 draws the
     * delimiter from one anchor point to another. Smaller values scales the
     * delimiter in the same proportion. This value is used mainly for
     * showCreation animations-like.
     *
     * @param amplitudeScale The delimiter scale, from 0 to 1. Values are automatically cropped to this interval.
     */
    public <T extends Delimiter> T setAmplitudeScale(double amplitudeScale) {
        this.amplitudeScale = amplitudeScale;
        return (T) this;
    }
    /**
     * Returns the scale of the amplitude of delimiter. A value of 1 draws the
     * delimiter from one anchor point to another. Smaller values scales the
     * delimiter in the same proportion. This value is used mainly for
     * showCreation animations-like.
     *
     * @return The amplitude scale. A value from 0 to 1
     */
    public double getAmplitudeScale() {
        return amplitudeScale;
    }
    @Override
    public Delimiter copy() {
        Delimiter copy = make(A.copy(), B.copy(), type, gap);
        copy.getMp().copyFrom(this.getMp());
        copy.getDelimiterShape().copyStateFrom(getDelimiterShape());

        if (delimiterLabel != null) {
            copy.setLabel(getLabel().copy(), labelMarkGap);
        }
        copy.amplitudeScale = amplitudeScale;
        copy.delimiterScale = delimiterScale;
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        super.copyStateFrom(obj);
        if (!(obj instanceof Delimiter)) {
            return;
        }
        Delimiter del = (Delimiter) obj;



        this.A.copyStateFrom(del.A);
        this.B.copyStateFrom(del.B);
        getMp().copyFrom(obj.getMp());
        getDelimiterShape().copyStateFrom(del.getDelimiterShape());
        if (del.delimiterLabel != null) {
            setLabel(del.getLabel().copy(), del.labelMarkGap);
            getLabel().getMp().copyFrom(del.getLabel().getMp());
        }
        amplitudeScale = del.amplitudeScale;
        delimiterScale = del.delimiterScale;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(A, B);
        setUpdateLevel(Math.max(A.getUpdateLevel(), B.getUpdateLevel()) + 1);
    }

    @Override
    public final Stylable getMp() {
        return mpDelimiter;
    }

    /**
     * Gets the label mark point. The label mark point is used to position the
     * label. Labels are centered around this point. The gap parameter used when
     * adding labels sets the distance between this point and the delimiter.
     *
     * @return The label mark point.
     */
    public Point getLabelMarkPoint() {
        return labelMarkPoint;
    }

    /**
     * Returns the rotate label flag
     *
     * @return True if label should be rotated, false otherwise.
     */
    public Rotation getRotationType() {
        return rotateLabel;
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

    /**
     * Returns the delimiter Shape
     * @return A MathObjectGroup containing one (or more) Shapes to be drawn as delimiter
     */
    public abstract MathObjectGroup getDelimiterShape();
    /**
     * Gets the label MathObject
     *
     * @return The label
     */
    public MathObject getLabel() {
        return delimiterLabel;
    }

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
