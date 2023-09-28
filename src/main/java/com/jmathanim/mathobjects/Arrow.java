/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Constructible.Conics.CTCircleArc;
import com.jmathanim.Constructible.Lines.CTLine;
import com.jmathanim.Constructible.Lines.CTPerpBisector;
import com.jmathanim.Constructible.Points.CTIntersectionPoint;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import java.net.URL;
import javafx.scene.shape.StrokeLineCap;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Arrow extends Shape {

    public double distScale;
    private double angle;
    private double baseHeightStart;
    private double baseHeightEnd;
    private double baseRealHeightStart;
    private double baseRealHeightEnd;

    private double gapStart, gapEnd;

    //TODO: 
    //Hacer que sea / no sea zoom-independent
    //Archivo editable Inkscape con todas las flechas
    //SVG: Flechas,
    //Procedural: cuadrados, semicírculos, cuascírculos
    //Implementar caps style proceduralmente
    //Añadir gap interno para modificar punto al que señalan flechas
    public enum ArrowType {
        NONE_BUTT, NONE_ROUND, NONE_SQUARE, ARROW1, ARROW2, ARROW3, SQUARE
    }

    private final Point startPoint, endPoint;
    private double baseDistStart, baseDistEnd;
    private double arrowThickness;
    private final Shape startArrowHead, endArrowHead;
    private ArrowType typeStart, typeEnd;

    /**
     * Creates a new Arrow from A to B.
     *
     * @param start Starting point
     * @param end Ending point
     * @param type Type of arrow. A value of enum ArrowType
     * @return The created arrow
     */
    public static Arrow make(Point start, Point end, ArrowType type) {
        Arrow resul = new Arrow(start, end);
        resul.typeStart = ArrowType.NONE_BUTT;
        resul.typeEnd = type;
        resul.loadModels();
        resul.rebuildShape();
        resul.scene = JMathAnimConfig.getConfig().getScene();
        return resul;
    }

    /**
     * Creates a new double Arrow from A to B.
     *
     * @param A Starting point
     * @param B Ending point
     * @param typeA Type of starting arrow. A value of enum ArrowType
     * @param typeB Type of ending arrow. A value of enum ArrowType
     * @return The created arrow
     */
    public static Arrow makeDouble(Point A, Point B, ArrowType typeA, ArrowType typeB) {
        Arrow resul = new Arrow(A, B);
        resul.typeStart = typeA;
        resul.typeEnd = typeB;
        resul.angle = 0;
        resul.loadModels();
        resul.rebuildShape();
        resul.scene = JMathAnimConfig.getConfig().getScene();
        return resul;
    }

    private Arrow(Point A, Point B) {
        this.startPoint = A;
        this.endPoint = B;
        startArrowHead = new Shape();
        endArrowHead = new Shape();
        arrowThickness = 20;//TODO: Default value. This should be in config file
        distScale = 1d;
    }

    private void loadModels() {
        Shape h1 = loadHeadShape(typeStart);
        Shape h2 = loadHeadShape(typeEnd);
        h2.scale(-1, -1);

        if (h1.getProperty("gap") != null) {
            gapStart = (double) h1.getProperty("gap");
        } else {
            gapStart = 0;
        }
        if (h2.getProperty("gap") != null) {
            gapEnd = (double) h2.getProperty("gap");
        } else {
            gapEnd = 0;
        }

        startArrowHead.getPath().clear();
        endArrowHead.getPath().clear();
        startArrowHead.getPath().addJMPointsFrom(h1.getPath());
        endArrowHead.getPath().addJMPointsFrom(h2.getPath());
        baseDistStart = startArrowHead.getPoint(0).to(startArrowHead.getPoint(-1)).norm();
        baseDistEnd = endArrowHead.getPoint(0).to(endArrowHead.getPoint(-1)).norm();
        baseHeightStart = startArrowHead.getBoundingBox().ymax - startArrowHead.getPoint(0).v.y;
        baseHeightEnd = endArrowHead.getPoint(0).v.y - endArrowHead.getBoundingBox().ymin;
        baseRealHeightStart = startArrowHead.getHeight();
        baseRealHeightEnd = endArrowHead.getHeight();
    }

    private Shape loadHeadShape(ArrowType type) {
        ResourceLoader rl = new ResourceLoader();
        URL arrowUrl;
        Shape resul;
        switch (type) {
            //Always FIRST point to the RIGHT, 
            //LAST point to the LEFT
            case NONE_BUTT:
                return Shape.segment(Point.at(1, 0), Point.at(0, 0));
            case NONE_ROUND:
                resul = Shape.arc(PI);
                resul.setProperty("gap", -1d);
                return resul;
            case NONE_SQUARE:
                return Shape.segment(Point.at(1, 0), Point.at(0, 0));

            case ARROW1:
                arrowUrl = rl.getResource("#arrow1.svg", "arrows");
                return new SVGMathObject(arrowUrl).get(0);
            case ARROW2:
                arrowUrl = rl.getResource("#arrow2.svg", "arrows");
                return new SVGMathObject(arrowUrl).get(0);
            case ARROW3:
                arrowUrl = rl.getResource("#arrow3.svg", "arrows");
                return new SVGMathObject(arrowUrl).get(0);
            case SQUARE:
                arrowUrl = rl.getResource("#ArrowSquare.svg", "arrows");
                resul = new SVGMathObject(arrowUrl).get(0);
                resul.setProperty("gap", -resul.getHeight() * .5);
                return resul;
            default:
                throw new AssertionError();
        }
    }

    private void rebuildShape() {
        Shape h1A = startArrowHead.copy();
        Shape h1B = endArrowHead.copy();
        double dist = startPoint.to(endPoint).norm() * distScale;
        //Scale heads to adjust to thickness
        double rThickness = scene.getRenderer().ThicknessToMathWidth(arrowThickness);

        double hh = (baseRealHeightStart - gapStart) / baseDistStart + (baseRealHeightStart - gapEnd) / baseDistEnd;
        rThickness = Math.min(rThickness, .75 * dist / hh);
        h1A.scale(rThickness / baseDistStart);
        h1B.scale(rThickness / baseDistEnd);

        double rbaseHeightStart = baseHeightStart * rThickness / baseDistStart;
        double rbaseHeightEnd = baseHeightEnd * rThickness / baseDistEnd;

        h1B.shift(h1B.getPoint(-1).to(h1A.getPoint(0)));//Align points 0 of bot shapes
        double rgapStart = gapStart * rThickness / baseDistStart;
        double rgapEnd = gapEnd * rThickness / baseDistEnd;
        getPath().clear();
        double longBody = dist - rbaseHeightStart - rbaseHeightEnd - rgapStart - rgapEnd;
        h1B.shift(0, -longBody);
        
        //These are the start/end points of the created Shape that must match A,B
        Point refStartPoint = h1A.getBoundingBox().getUpper().shift(0, rgapStart);
        Point refEndPoint = h1B.getBoundingBox().getLower().shift(0, -rgapEnd);

        if (angle == 0) {
            getPath().addJMPointsFrom(h1A.getPath());
            merge(h1B, true, true);
        } else {
            //Create the appropiate arc shape.
            h1A.rotate(refStartPoint, angle);
            h1B.rotate(refEndPoint, -angle);

            CTPerpBisector ct1 = CTPerpBisector.make(h1A.get(0).p, h1B.get(-1).p);
            CTLine ct2 = CTLine.make(h1B.get(0).p, h1B.get(-1).p);
            CTIntersectionPoint inter = CTIntersectionPoint.make(ct1, ct2);
            CTCircleArc arc1, arc2;
            Shape shArc1, shArc2;
            AffineJTransform tr;
            if (angle > 0) {
                arc1 = CTCircleArc.make(inter.getMathObject(), h1B.get(-1).p, h1A.get(0).p);
                arc2 = CTCircleArc.make(inter.getMathObject(), h1B.get(0).p, h1A.get(-1).p);
                shArc1 = (Shape) arc1.getMathObject();
                shArc2 = (Shape) arc2.getMathObject();

                tr = AffineJTransform.createDirect2DIsomorphic(shArc1.get(0).p, shArc1.get(-1).p, h1B.get(-1).p, h1A.get(0).p, 1);
                shArc1.applyAffineTransform(tr);
                tr = AffineJTransform.createDirect2DIsomorphic(shArc2.get(0).p, shArc2.get(-1).p, h1B.get(0).p, h1A.get(-1).p, 1);
                shArc2.applyAffineTransform(tr);
            } else {
                arc1 = CTCircleArc.make(inter.getMathObject(), h1A.get(0).p, h1B.get(-1).p);
                arc2 = CTCircleArc.make(inter.getMathObject(), h1A.get(-1).p, h1B.get(0).p);
                shArc1 = (Shape) arc1.getMathObject();
                shArc2 = (Shape) arc2.getMathObject();

                tr = AffineJTransform.createDirect2DIsomorphic(shArc1.get(0).p, shArc1.get(-1).p, h1A.get(0).p, h1B.get(-1).p, 1);
                shArc1.applyAffineTransform(tr);
                tr = AffineJTransform.createDirect2DIsomorphic(shArc2.get(0).p, shArc2.get(-1).p, h1A.get(-1).p, h1B.get(0).p, 1);
                shArc2.applyAffineTransform(tr);
            }

            if (angle > 0) {
                shArc2.reverse();
            } else {
                shArc1.reverse();
            }

            //Build the shape, adding h1c and merging with h2cF
            getPath().addJMPointsFrom(h1A.getPath());
            merge(shArc2, true, false);
            merge(h1B, true, false);
            merge(shArc1, true, true);
//            getPath().distille();
        }
//        AffineJTransform trFinal = AffineJTransform.createDirect2DIsomorphic(upper, lower, A, B, 1);
        AffineJTransform trShift = AffineJTransform.createTranslationTransform(refStartPoint.to(refStartPoint));
        AffineJTransform trRotate = AffineJTransform.create2DRotationTransform(refStartPoint, refStartPoint.to(refEndPoint).getAngle() + .5 * PI);
        getPath().applyAffineTransform(trShift);
        getPath().applyAffineTransform(trRotate);
//        getPath().shift(lower.to(A));

    }

    @Override
    public Arrow copy() {
        Arrow copy = Arrow.makeDouble(startPoint.copy(), endPoint.copy(), typeStart, typeEnd);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        boolean reloadModels = false;
        if (obj instanceof Arrow) {
            Arrow ar = (Arrow) obj;
            this.arrowThickness = ar.arrowThickness;
            this.angle = ar.angle;
            this.scene = ar.scene;
            this.distScale = ar.distScale;
            if ((this.typeStart != ar.typeStart) || (this.typeEnd != ar.typeEnd)) {
                reloadModels = true;
            }
            this.typeStart = ar.typeStart;
            this.typeEnd = ar.typeEnd;
            this.startPoint.copyFrom(ar.startPoint);
            this.endPoint.copyFrom(ar.endPoint);
            if (reloadModels) {
                this.loadModels();
            }
            this.rebuildShape();
            this.getMp().copyFrom(ar.getMp());

        }
    }

    @Override
    public void update(JMathAnimScene scene) {
        rebuildShape();
    }

    public double getArrowThickness() {
        return arrowThickness;
    }

    public Arrow setArrowThickness(double arrowThickness) {
        this.arrowThickness = arrowThickness;
        return this;
    }

    /**
     * Returns the starting arrow type.
     *
     * @return A value of enum ArrowType determining the arrow that points to
     * the starting point.
     */
    public ArrowType getTypeStart() {
        return typeStart;
    }

    /**
     * Sets the starting arrow type.
     *
     * @param typeStart A value of enum ArrowType determining the arrow that
     * points to the starting point.
     */
    public void setTypeStart(ArrowType typeStart) {
        if (typeStart != this.typeStart) {
            this.typeStart = typeStart;
            loadModels();
            rebuildShape();
        }
    }

    /**
     * Returns the ending arrow type.
     *
     * @return A value of enum ArrowType determining the arrow that points to
     * the ending point.
     */
    public ArrowType getTypeEnd() {
        return typeEnd;
    }

    /**
     * Sets the ending arrow type.
     *
     * @param typeB A value of enum ArrowType determining the arrow that points
     * to the ending point.
     */
    public void setTypeEnd(ArrowType typeB) {
        if (typeB != this.typeEnd) {
            this.typeEnd = typeB;
            loadModels();
            rebuildShape();
        }
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
//        super.applyAffineTransform(tr);
        startPoint.applyAffineTransform(tr);
        endPoint.applyAffineTransform(tr);
        rebuildShape();
        return (T) this;
    }

    /**
     * Determines the curvature of the arrow. A value of 0 means a straight
     * arrow. A value of PI/2 determines a semicircle.
     *
     * @param angle An angle in radians.
     * @return This object
     */
    public Arrow setCurvature(double angle) {
        this.angle = angle;
        rebuildShape();
        return this;
    }

    @Override
    public Rect computeBoundingBox() {
        rebuildShape();
        return getPath().getBoundingBox();
    }

    /**
     * Returns the starting point
     *
     * @return A reference to the starting Point object
     */
    public Point getStart() {
        return startPoint;
    }

    /**
     * Returns the ending point
     *
     * @return A reference to the ending Point object
     */
    public Point getEnd() {
        return endPoint;
    }
}
