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
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.EmptyRect;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updateableObjects.AnchoredMathObject;

/**
 * A extensible delimiter like braces or parenthesis
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Delimiter extends MathObject {

    private final Point A, B;
    private SVGMathObject body;
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
        Point A = Point.at(0, 0);
        Point B = Point.at(0, 0);
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
        //Register points A and B as updateable
        sce.registerUpdateable(new AnchoredMathObject(A, Anchor.Type.CENTER, obj, anchorA));
        sce.registerUpdateable(new AnchoredMathObject(B, Anchor.Type.CENTER, obj, anchorB));

        Delimiter resul = Delimiter.make(A, B, delimiterType, gap);
        return resul;
    }

    /**
     * Creates a delimiter that is permanently stacked to a given
     * MathObject.Note that this delimiters always stack to the appropiate rect
     * boundaryBox points.Thus for example, delimiters will not rotate if the
     * mathobject is being rotated. A label MathObject is stacked to the
     * delimiter (usually a text or number)
     *
     * @param obj
     * @param anchorType Anchor to use. Currently UPPER, LOWER, RIGHT and LEFT
     * are allowed. Other anchors return a null object and an error message.
     * @param delimiterType Delimiter type
     * @param gap Gap to put between anchor points and delimiter
     * @param label Object to stack to the delimiter, at the same anchor that
     * anchorType
     * @param labelGap Gap between the label and the delimiter
     * @return The delimiter
     */
    public static Delimiter stackTo(MathObject obj, Anchor.Type anchorType, Type delimiterType, double gap, MathObject label, double labelGap) {
        JMathAnimScene sce = JMathAnimConfig.getConfig().getScene();//This should be better implemented, avoid static singletons
        Delimiter resul = Delimiter.stackTo(obj, anchorType, delimiterType, gap);
        //Anchors the JMNumber
        sce.registerUpdateable(new AnchoredMathObject(label, Anchor.reverseAnchorPoint(anchorType), resul, anchorType, labelGap));

        return resul;

    }

    private Delimiter(Point A, Point B, Type type, double gap) {
        this.A = A;
        this.B = B;
        this.type = type;
        this.gap = gap;
    }

    public void setBody(SVGMathObject body) {
        this.body = body;
    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    public MultiShapeObject getDelimiterShape() {
        Point AA = A.interpolate(B, .5 * (1 - delimiterScale));
        Point BB = B.interpolate(A, .5 * (1 - delimiterScale));
        double width = AA.to(BB).norm();

        MultiShapeObject resul = body.copy();
        drawAlpha(0);// This is to ensure that the "stitches" are not seen
        for (Shape sh : resul) {
            sh.getMp().copyFrom(this.getMp());
        }

        if (type == Type.BRACE) {
            double minimumWidthToShrink = .5;
            double wr = (width < minimumWidthToShrink ? 1 - (width - minimumWidthToShrink)
                    * (width - minimumWidthToShrink) / minimumWidthToShrink / minimumWidthToShrink : 1);
            resul.setWidth(wr);
            double hasToGrow = width - resul.getBoundingBox().getWidth();
            // 0,1,2,3,4,5 shapes Shapes 1 and 4 are extensible
            double w = resul.get(1).getBoundingBox().getWidth();
            double scale = 1 + .5 * hasToGrow / w;
            resul.get(1).scale(resul.get(1).getBoundingBox().getRight(), scale, 1);
            resul.get(4).scale(resul.get(4).getBoundingBox().getLeft(), scale, 1);
            resul.get(0).shift(-.5 * hasToGrow, 0);
            resul.get(5).shift(.5 * hasToGrow, 0);
        }

        if ((type == Type.PARENTHESIS) || (type == Type.BRACKET)) {
            double minimumWidthToShrink = 1.5;
            double wr = (width < minimumWidthToShrink ? 1 - (width - minimumWidthToShrink)
                    * (width - minimumWidthToShrink) / minimumWidthToShrink / minimumWidthToShrink : 1);
            resul.setWidth(wr);
            double hasToGrow = width - resul.getBoundingBox().getWidth();
            // 0,1,2,3 shapes where shapes 1 and 2 are extensible
            double w = resul.get(1).getBoundingBox().getWidth();
            double scale = 1 + .5 * hasToGrow / w;
            resul.get(1).scale(resul.get(1).getBoundingBox().getLeft(), scale, 1);
            resul.get(2).scale(resul.get(2).getBoundingBox().getRight(), scale, 1);
            resul.get(0).shift(.5 * hasToGrow, 0);
            resul.get(3).shift(-.5 * hasToGrow, 0);
        }

        Rect bb = resul.getBoundingBox();
        resul.shift(0, gap);
        AffineJTransform tr = AffineJTransform.createDirect2DHomothecy(bb.getDL(), bb.getDR(), AA, BB, 1);
        tr.applyTransform(resul);
        return resul;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        if (isVisible()) {
            if (A.isEquivalentTo(B, 0)) {
                return;// Do nothing
            }
            MultiShapeObject delimiterToDraw = getDelimiterShape();// TODO: do this in the update method
            delimiterToDraw.draw(scene, r);
        }
    }

    @Override
    public Rect getBoundingBox() {
         if (A.isEquivalentTo(B, 0)) {
                return new EmptyRect();
            }
        return getDelimiterShape().getBoundingBox();
    }

    /**
     * Returns the scale of the delimiter. A value of 1 draws the delimiter from
     * one anchor point to another. Smaller values scales the delimiter in the
     * same proportion. This value is used mainly for showCreation
     * animations-like.
     *
     * @param delimiterScale The delimiter scale, from 0 to 1
     */
    public void setDelimiterScale(double delimiterScale) {
        this.delimiterScale = delimiterScale;
    }

    @Override
    public <T extends MathObject> T copy() {
        return (T) make(A.copy(), B.copy(), type, gap);
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        if (!(obj instanceof Delimiter)) {
            return;
        }

        Delimiter del = (Delimiter) obj;
        this.A.copyStateFrom(del.A);
        this.B.copyStateFrom(del.B);
        this.gap = del.gap;
//        this.type=type;//Final variable
    }

}
