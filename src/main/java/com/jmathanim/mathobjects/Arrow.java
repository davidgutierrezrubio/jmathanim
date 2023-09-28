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
    private double baseHeight1;
    private double baseHeight2;
    private double baseRealHeight1;
    private double baseRealHeight2;

    private double gapA, gapB;

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

    private final Point A, B;
    private double baseDist1, baseDist2;
    private double arrowThickness;
    private final Shape head1, head2;
    double headHeight1, headHeight2;
    private ArrowType typeA, typeB;

    public static Arrow make(Point A, Point B, ArrowType type) {
        Arrow resul = new Arrow(A, B);
        resul.typeA = ArrowType.NONE_BUTT;
        resul.typeB = type;
        resul.loadModels();
        resul.rebuildShape();
        resul.scene = JMathAnimConfig.getConfig().getScene();
        return resul;
    }

    public Point getStart() {
        return A;
    }

    public Point getEnd() {
        return B;
    }

    public static Arrow makeDouble(Point A, Point B, ArrowType typeA, ArrowType typeB) {
        Arrow resul = new Arrow(A, B);
        resul.typeA = typeA;
        resul.typeB = typeB;
        resul.angle = 0;
        resul.loadModels();
        resul.rebuildShape();
        resul.scene = JMathAnimConfig.getConfig().getScene();
        return resul;
    }

    private Arrow(Point A, Point B) {
        this.A = A;
        this.B = B;
        head1 = new Shape();
        head2 = new Shape();
        arrowThickness = 20;//TODO: Default value. This should be in config file
        distScale = 1d;
    }

    private void loadModels() {
        Shape h1 = loadHeadShape(typeA);
        Shape h2 = loadHeadShape(typeB);
        h2.scale(-1, -1);

        if (h1.getProperty("gap") != null) {
            gapA = (double) h1.getProperty("gap");
        } else {
            gapA = 0;
        }
        if (h2.getProperty("gap") != null) {
            gapB = (double) h2.getProperty("gap");
        } else {
            gapB = 0;
        }

        head1.getPath().clear();
        head2.getPath().clear();
        head1.getPath().addJMPointsFrom(h1.getPath());
        head2.getPath().addJMPointsFrom(h2.getPath());
        baseDist1 = head1.getPoint(0).to(head1.getPoint(-1)).norm();
        baseDist2 = head2.getPoint(0).to(head2.getPoint(-1)).norm();
        baseHeight1 = head1.getBoundingBox().ymax - head1.getPoint(0).v.y;
        baseHeight2 = head2.getPoint(0).v.y - head2.getBoundingBox().ymin;
        baseRealHeight1 = head1.getHeight();
        baseRealHeight2 = head2.getHeight();
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
                 resul.setProperty("gap", -resul.getHeight()*.5);
                return resul;
            default:
                throw new AssertionError();
        }
    }

    private void rebuildShape() {
        Shape h1A = head1.copy();
        Shape h1B = head2.copy();
        double dist = A.to(B).norm() * distScale;
        //Scale heads to adjust to thickness
        double rThickness = scene.getRenderer().ThicknessToMathWidth(arrowThickness);

        double hh = (baseRealHeight1 - gapA) / baseDist1 + (baseRealHeight1 - gapB) / baseDist2;
        rThickness = Math.min(rThickness, .75 * dist / hh);
        h1A.scale(rThickness / baseDist1);
        h1B.scale(rThickness / baseDist2);

        double rbaseHeight1 = baseHeight1 * rThickness / baseDist1;
        double rbaseHeight2 = baseHeight2 * rThickness / baseDist2;

        h1B.shift(h1B.getPoint(-1).to(h1A.getPoint(0)));//Align points 0 of bot shapes
        headHeight1 = h1A.getHeight();
        headHeight2 = h1B.getHeight();
        double rgapA = gapA * rThickness / baseDist1;
        double rgapB = gapB * rThickness / baseDist2;
        getPath().clear();
        double longBody = dist - rbaseHeight1 - rbaseHeight2 -rgapA - rgapB;
        h1B.shift(0, -longBody);
        Point startPoint = h1A.getBoundingBox().getUpper().shift(0, rgapA);
        Point endPoint = h1B.getBoundingBox().getLower().shift(0, -rgapB);

        if (angle == 0) {
            getPath().addJMPointsFrom(h1A.getPath());
            merge(h1B, true, true);
        } else {

            h1A.rotate(startPoint, angle);
            h1B.rotate(endPoint, -angle);

            //Rectas
//        CTLine ct1 = CTLine.make(h1A.get(0).p, h1A.get(-1).p);
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
        AffineJTransform trShift = AffineJTransform.createTranslationTransform(startPoint.to(A));
        AffineJTransform trRotate = AffineJTransform.create2DRotationTransform(A, A.to(B).getAngle() + .5 * PI);
        getPath().applyAffineTransform(trShift);
        getPath().applyAffineTransform(trRotate);
//        getPath().shift(lower.to(A));

    }

    @Override
    public Arrow copy() {
        Arrow copy = Arrow.makeDouble(A.copy(), B.copy(), typeA, typeB);
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
            if ((this.typeA != ar.typeA) || (this.typeB != ar.typeB)) {
                reloadModels = true;
            }
            this.typeA = ar.typeA;
            this.typeB = ar.typeB;
            this.A.copyFrom(ar.A);
            this.B.copyFrom(ar.B);
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

    public ArrowType getTypeA() {
        return typeA;
    }

    public void setTypeA(ArrowType typeA) {
        if (typeA != this.typeA) {
            this.typeA = typeA;
            loadModels();
            rebuildShape();
        }
    }

    public ArrowType getTypeB() {
        return typeB;
    }

    public void setTypeB(ArrowType typeB) {
        if (typeB != this.typeB) {
            this.typeB = typeB;
            loadModels();
            rebuildShape();
        }
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
//        super.applyAffineTransform(tr);
        A.applyAffineTransform(tr);
        B.applyAffineTransform(tr);
        rebuildShape();
        return (T) this;
    }

    @Override
    public void on_setLineCap(StrokeLineCap linecap) {
        super.on_setLineCap(linecap);
        loadModels();
        rebuildShape();
    }

    public Arrow setRotation(double angle) {
        this.angle = angle;
        rebuildShape();
        return this;
    }

    @Override
    public Rect computeBoundingBox() {
        rebuildShape();
        return getPath().getBoundingBox();
    }
    

}
