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
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.EmptyRect;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.Utils.UsefulLambdas;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updateableObjects.AnchoredMathObject;

/**
 * A extensible delimiter like braces or parenthesis
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Delimiter extends MathObject {

    private Point A, B;
    private SVGMathObject body;
    private double amplitudeScale;
    private MathObject delimiterLabel;
    protected MODrawPropertiesArray mpDelimiter;
    private final Point labelMarkPoint;
    private double labelMarkGap;
    private boolean rotateLabel;
    private MathObjectGroup delimiterToDraw;
    private double minimumWidthToShrink;
    private double delimiterScale;

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
        BRACKET
    }

    private final Type type;
    /**
     * Gap to apply between control points and delimiter
     */
    private double gap;

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
        Delimiter resul = new Delimiter(A, B, type, gap);
        ResourceLoader rl = new ResourceLoader();
        String name = "";
        switch (type) {
            case BRACE:
                name = "#braces.svg";
                break;
            case PARENTHESIS:
                name = "#parenthesis.svg";
                break;
            case BRACKET:
                name = "#bracket.svg";
                break;
        }
        resul.setBody(new SVGMathObject(rl.getResource(name, "delimiters")));
        resul.style("latexdefault");
        resul.drawAlpha(0);// This is necessary so that "stitches" are not seen when fadeIn or fadeOut
        resul.amplitudeScale = 1;
        resul.delimiterScale=1;
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
                anchorA = Anchor.Type.UL;
                anchorB = Anchor.Type.UR;
                break;
            case LOWER:
                anchorA = Anchor.Type.DR;
                anchorB = Anchor.Type.DL;
                break;
            case RIGHT:
                anchorA = Anchor.Type.UR;
                anchorB = Anchor.Type.DR;
                break;
            case LEFT:
                anchorA = Anchor.Type.DL;
                anchorB = Anchor.Type.UL;
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

    public Delimiter setLabel(MathObject label, double labelGap) {
        //Anchors the JMNumber
        mpDelimiter.add(label);
        this.labelMarkGap = labelGap;
//        updateableLabel = new AnchoredMathObject(setLabel, Anchor.reverseAnchorPoint(anchorType), labelMarkPoint, anchorType, 0);
//        scene.registerUpdateable(updateableLabel);
        this.delimiterLabel = label;
        return this;
    }

    public void removeLabel() {
        mpDelimiter.remove(delimiterLabel);
//        scene.unregisterUpdateable(updateableLabel);

    }

    private Delimiter(Point A, Point B, Type type, double gap) {
        this.A = A;
        this.B = B;
        this.type = type;
        this.gap = gap;
        this.delimiterLabel = null;
        this.mpDelimiter = new MODrawPropertiesArray();
        labelMarkPoint = Point.at(0, 0);
        this.rotateLabel = false;
        minimumWidthToShrink = .5;
        delimiterScale=1;
        amplitudeScale=1;
    }

    public void setBody(SVGMathObject body) {
        this.body = body;
        delimiterShapeWidth = body.getBoundingBox().getWidth();
    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    private MathObjectGroup buildDelimiterShape() {
        if (amplitudeScale == 0) {
            return MathObjectGroup.make();
        }
        Point AA = A.interpolate(B, .5 * (1 - amplitudeScale));
        Point BB = B.interpolate(A, .5 * (1 - amplitudeScale));
        double width = AA.to(BB).norm();//The final width of the delimiter

        MultiShapeObject delimiterShape = body.copy();
        body.getMp().copyFrom(this.getMp());
//        for (Shape sh : delimiterShape) {
//            sh.getMp().copyFrom(this.getMp());
//        }

        if (type == Type.BRACE) {
            minimumWidthToShrink = .5;
//            double wr = (width < minimumWidthToShrink ? a - (width - minimumWidthToShrink)
//                    * (width - minimumWidthToShrink) / minimumWidthToShrink / minimumWidthToShrink : a);
            double wr=.25*delimiterScale*UsefulLambdas.allocateTo(0, minimumWidthToShrink).applyAsDouble(width);
            delimiterShape.setWidth(wr);
            double hasToGrow = Math.max(0, width - wr);
            // 0,1,2,3,4,5 shapes Shapes 1 and 4 are extensible
            double w0 = delimiterShape.get(0).getBoundingBox().getWidth();
            double w1 = delimiterShape.get(1).getBoundingBox().getWidth();
            double wSpace = .5 * (hasToGrow);
            Shape brace = delimiterShape.get(0).copy();
            brace.merge(delimiterShape.get(2).shift(wSpace, 0), true, false)
                    .merge(delimiterShape.get(1).reverse().shift(2 * wSpace, 0), true, false)
                    .merge(delimiterShape.get(3).shift(wSpace, 0), true, true);
            delimiterShape = SVGMathObject.make(brace);
        }

        if ((type == Type.PARENTHESIS) || (type == Type.BRACKET)) {
            double minimumWidthToShrink = 1.5;
            double wr = (width < minimumWidthToShrink ? 1 - (width - minimumWidthToShrink)
                    * (width - minimumWidthToShrink) / minimumWidthToShrink / minimumWidthToShrink : 1);
            delimiterShape.setWidth(wr);
            double hasToGrow = width - wr;
            // 0,1,2,3 shapes where shapes 1 and 2 are extensible
            double w = delimiterShape.get(1).getBoundingBox().getWidth();
            double scale = 1 + .5 * hasToGrow / w;
            delimiterShape.get(1).scale(delimiterShape.get(1).getBoundingBox().getLeft(), scale, 1);
            delimiterShape.get(2).scale(delimiterShape.get(2).getBoundingBox().getRight(), scale, 1);
            delimiterShape.get(0).shift(.5 * hasToGrow, 0);
            delimiterShape.get(3).shift(-.5 * hasToGrow, 0);
        }

        Rect bb = delimiterShape.getBoundingBox();
        delimiterShape.shift(0, gap);
        labelMarkPoint.stackTo(delimiterShape, Anchor.Type.UPPER, labelMarkGap);
        AffineJTransform tr = AffineJTransform.createDirect2DHomothecy(bb.getDL(), bb.getDR(), AA, BB, 1);
        MathObjectGroup resul = MathObjectGroup.make(delimiterShape);
        MathObject lab;
        tr.applyTransform(resul);

        if (delimiterLabel != null) {
            lab = delimiterLabel.copy().scale(amplitudeScale);
            resul.add(lab);
            if (rotateLabel) {
                lab.stackTo(labelMarkPoint, Anchor.Type.CENTER);
                tr.applyTransform(lab);
                tr.applyTransform(labelMarkPoint);
            } else {
                tr.applyTransform(labelMarkPoint);
                lab.stackTo(labelMarkPoint, Anchor.Type.CENTER);
            }
        }

        return resul;
    }
    private double delimiterShapeWidth;

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        if (isVisible()) {
            if (A.isEquivalentTo(B, 0)) {
                return;// Do nothing
            }
            delimiterToDraw = buildDelimiterShape();

//            delimiterToDraw.draw(scene, r);
            for (MathObject obj : delimiterToDraw) {
                obj.draw(scene, r);
            }
        }
    }

    @Override
    public Rect getBoundingBox() {
        if (A.isEquivalentTo(B, 0)) {
            return new EmptyRect();
        }
        return buildDelimiterShape().getBoundingBox();
    }

    /**
     * Returns the scale of the amplitude of delimiter. A value of 1 draws the delimiter from
     * one anchor point to another. Smaller values scales the delimiter in the
     * same proportion. This value is used mainly for showCreation
     * animations-like.
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
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        //This object should not be able to copy its state since
        //it is a purely dependent object
        //Only their drawing attributes!
        getMp().copyFrom(obj.getMp());
    }

    @Override
    public int getUpdateLevel() {
        return Math.max(A.getUpdateLevel(), B.getUpdateLevel()) + 1;
    }

    @Override
    public void addToSceneHook(JMathAnimScene scene) {
//        super.addToSceneHook(scene);
//        if (setLabel != null) {
//            scene.add(setLabel);
//        }
    }

    @Override
    public final Stylable getMp() {
        return mpDelimiter;
    }

    public Point getLabelMarkPoint() {
        return labelMarkPoint;
    }

    public boolean isRotateLabel() {
        return rotateLabel;
    }

    public Delimiter rotateLabel(boolean rotateLabel) {
        this.rotateLabel = rotateLabel;
        return this;
    }

    public double getDelimiterScale() {
        return delimiterScale;
    }

    public <T extends Delimiter> T setDelimiterScale(double delimiterScale) {
        this.delimiterScale = delimiterScale;
        return (T) this;
    }

    
    
}
