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
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.EmptyRect;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;
import com.jmathanim.mathobjects.updateableObjects.AnchoredMathObject;

/**
 * A extensible delimiter like braces or parenthesis
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Delimiter extends MathObject {

    protected final Point A;
    protected final Point B;
    protected final Point scaledA;
    protected final Point scaledB;

    protected double amplitudeScale;
    protected MathObject delimiterLabel;
    protected MathObject delimiterLabelToDraw;
    protected MODrawPropertiesArray mpDelimiter;
    public final Point labelMarkPoint;
    protected double labelMarkGap;
    protected Rotation rotateLabel;
    protected MathObjectGroup delimiterToDraw;

    protected double delimiterScale;
    protected double minimumWidthToShrink;

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

    protected Type type;
    /**
     * Gap to apply between control points and delimiter
     */
    protected double gap;

    /**
     * Constructs a new delimiter.The points mark the beginning and end of the
     * delimiter.The delimiter lies at the "left" of vector AB.
     *
     * @param A Beginning point
     * @param B Ending point
     * @param type Type of delimiter, one enum {@link Type}
     * @param gap Gap between control points and delimiter
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
     * @param anchorType Anchor to use. Currently UPPER, LOWER, RIGHT and LEFT
     * are allowed. Other anchors return a null object and an error message.
     * @param delimiterType Delimiter type
     * @param gap Gap to put between anchor points and delimiter
     * @return The delimiter
     */
    public static Delimiter stackTo(MathObject obj, Anchor.Type anchorType, Type delimiterType, double gap) {
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

    public Delimiter(Point A, Point B, Type type, double gap) {
        this.A = A;
        this.B = B;
        this.scaledA = A.copy();
        this.scaledB = B.copy();
        this.type = type;
        this.gap = gap;
        //An invisible path with only a point (to properly stack and align)
        this.delimiterLabel = Shape.polyLine(Point.at(0, 0)).visible(false);
        this.mpDelimiter = new MODrawPropertiesArray();
        labelMarkPoint = Point.at(0, 0);
        this.rotateLabel = Rotation.SMART;

        delimiterScale = 1;
        amplitudeScale = 1;

    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    abstract protected MathObjectGroup buildDelimiterShape();

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        if (isVisible()) {
            if (delimiterScale == 0) {
                return;// Do nothing
            }
            scaledA.v.copyFrom(A.interpolate(B, .5 * (1 - amplitudeScale)).v);
            scaledB.v.copyFrom(B.interpolate(A, .5 * (1 - amplitudeScale)).v);
            delimiterLabel.update(scene);
            delimiterToDraw = buildDelimiterShape();
            for (MathObject d : delimiterToDraw) {
                d.draw(scene, r, cam);
            }
            delimiterToDraw.draw(scene, r, cam);
        }
    }

    @Override
    public Rect computeBoundingBox() {
        if ((A.isEquivalentTo(B, 0) || (delimiterScale == 0))) {
            return new EmptyRect();
        }
        return buildDelimiterShape().getBoundingBox();
    }

    /**
     * Returns the scale of the amplitude of delimiter. A value of 1 draws the
     * delimiter from one anchor point to another. Smaller values scales the
     * delimiter in the same proportion. This value is used mainly for
     * showCreation animations-like.
     *
     * @param delimiterScale The delimiter scale, from 0 to 1
     */
    public void setAmplitudeScale(double delimiterScale) {
        this.amplitudeScale = delimiterScale;
    }

    @Override
    public Delimiter copy() {
        Delimiter copy = make(A.copy(), B.copy(), type, gap);
        copy.getMp().copyFrom(this.getMp());
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
        getMp().copyFrom(obj.getMp());
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
     * @param <T> Subclass
     * @param delimiterScale Scale. Default value is 1.
     * @return This object
     */
    public <T extends Delimiter> T setDelimiterScale(double delimiterScale) {
        this.delimiterScale = delimiterScale;
        return (T) this;
    }

    /**
     * Gets the label MathObject
     *
     * @return The label
     */
    public MathObject getLabel() {
        return delimiterLabel;
    }

//    /**
//     * Adds a label that automatically updates to show the length of the
//     * delimiter.
//     *
//     * @param <T> Calling subclass
//     * @param format A string format, in the DecimalFormat class syntax
//     * @param gap Gap to leave between this label and the delimiter
//     * @return This object
//     */
//    public <T extends Delimiter> T addLengthLabel(String format, double gap) {
//        JMNumber jm = JMNumber.length(scaledA, scaledB);
//        jm.setArgumentsFormat(format);
//        return setLabel(jm, gap);
//    }
//    /**
//     * Overloaded function. Adds a label that automatically updates to show the
//     * length of the delimite, using 2 (at most) decimal places.
//     *
//     * @param <T> Calling subclass
//     * @param gap Gap to leave between this label and the delimiter
//     * @return This object
//     */
//    public <T extends Delimiter> T addLengthLabel(double gap) {
//        return addLengthLabel("#.##", gap);
//    }
}
