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
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.text.DecimalFormat;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * This class represents a point in 2D or 3D space
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Point extends MathObject {

    public final Vec v;
    private Shape dotShape;

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
        RealMatrix pRow = new Array2DRowRealMatrix(new double[][]{{1d, v.x, v.y, v.z}});
        RealMatrix pNew = pRow.multiply(tr.getMatrix());

        v.x = pNew.getEntry(0, 1);
        v.y = pNew.getEntry(0, 2);
        v.z = pNew.getEntry(0, 3);
        tr.applyTransformsToDrawingProperties(this);
        return (T) this;
    }

    public enum DotSyle {
        CIRCLE, CROSS, PLUS,RING
    }

    public enum ShadingStyle {
        NO_SHADING, FLAT, SMOOTH, PHONG, GOURAUD
    }

    /**
     * Static method. Returns a new point at (0,0), with default style
     *
     * @return The created Point
     */
    public static final Point origin() {
        return Point.at(0, 0);
    }

    public static final Point relAt(double x, double y) {
        return JMathAnimConfig.getConfig().getCamera().getMathView().getRelPoint(x, y);
    }

    /**
     * Static method. Returns a new point at (1,0), with default style
     *
     * @return The created Point
     */
    public static final Point unitX() {
        return Point.at(1, 0);
    }

    /**
     * Static method. Returns a new point at (0,1), with default style
     *
     * @return The created Point
     */
    public static final Point unitY() {
        return Point.at(0, 1);
    }

    public static final Point unitZ() {
        return new Point(0, 0, 1);
    }

    public static final Point segmentIntersection(Point A, Point B, Point C, Point D) {
        AffineJTransform tr = AffineJTransform.createAffineTransformation(A, B, C, Point.unitX(), Point.unitY(),
                Point.origin(), 1);
        Point P = tr.getTransformedObject(D);
        double r = P.v.x + P.v.y;
        if ((r >= 1) & (P.v.x >= 0) & (P.v.y >= 0)) {
            P.v.x /= r;
            P.v.y /= r;
            return tr.getInverse().getTransformedObject(P);
        } else {
            return null;
        }
    }

    /**
     * Creates a new Point with coordinates (0,0,0), with default style.
     */
    public Point() {
        this(0, 0, 0);
    }

    /**
     * Creates a new point copying coordinates from given vector, with default
     * style.
     *
     * @param v Vector with coordinates
     */
    public Point(Vec v) {
        this(v.x, v.y, v.z);
    }

    /**
     * Overloaded method. Creates a new Point with coordinates x,y, with default
     * style. The z coordinates is set to 0.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public Point(double x, double y) {
        this(x, y, 0);
    }

    /**
     * Creates a new Point with coordinates x,y,z, with default style.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public Point(double x, double y, double z) {
        super();
        this.v = new Vec(x, y, z);
        this.getMp().loadFromStyle("dotdefault");
        this.getMp().setAbsoluteThickness(true);
    }

    /**
     * Static builder. Creates and returns a new point at given coordinates.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return The created point
     */
    public static Point at(double x, double y) {
        return new Point(x, y);
    }

    /**
     * Static builder. Creates and returns a new point at given coordinates (3d
     * space).
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return The created point
     */
    public static Point at(double x, double y, double z) {
        return new Point(x, y, z);
    }

    /**
     * Static builder.Creates and returns a new point at random coordinates,
     * inside the math view.
     *
     * @return The created point
     */
    public static Point random() {
        Rect r = JMathAnimConfig.getConfig().getCamera().getMathView();
        double x = r.xmin + (r.xmax - r.xmin) * Math.random();
        double y = r.ymin + (r.ymax - r.ymin) * Math.random();
        return new Point(x, y);
    }

    @Override
    public Point getCenter() {
        return this.copy();
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        if (v.isNaN()) {
            return;
        }
        dotShape = generateDotShape();//TODO: Do this only when needed
        dotShape.setAbsoluteSize(this.copy());
        if (isVisible()) {
            dotShape.draw(scene, r);
        }
        scene.markAsAlreadyDrawed(this);

    }

    /**
     * Stablishes dot style.
     *
     * @param dotStyle Style dot. DOT_STYLE_CIRCLE, DOT_STYLE_CROSS,
     * DOT_STYLE_PLUS
     * @return The object
     */
    public Point dotStyle(DotSyle dotStyle) {
        this.getMp().setDotStyle(dotStyle);
        return this;
    }

    private Shape generateDotShape() {
        double st = scene.getRenderer().ThicknessToMathWidth(this);
        double th = scene.getRenderer().MathWidthToThickness(st);
        switch (getMp().getDotStyle()) {
            case CROSS:
                dotShape = new Shape();
                dotShape.getPath().addPoint(Point.at(-1, 1), Point.at(1, -1), Point.at(1, 1), Point.at(-1, -1));
                dotShape.get(0).isThisSegmentVisible = false;
                dotShape.get(2).isThisSegmentVisible = false;
                dotShape.shift(v).scale(.5 * st).drawColor(getMp().getDrawColor()).thickness(.25 * th);
                break;
            case PLUS:
                dotShape = new Shape();
                dotShape.getPath().addPoint(Point.at(0, 1), Point.at(0, -1), Point.at(1, 0), Point.at(-1, 0));
                dotShape.get(0).isThisSegmentVisible = false;
                dotShape.get(2).isThisSegmentVisible = false;
                dotShape.shift(v).scale(.5 * st).drawColor(getMp().getDrawColor()).thickness(.25 * th);
                break;
            case RING:
                dotShape = Shape.circle().shift(v).scale(.5 * st).drawColor(getMp().getDrawColor())
                        .fillColor(JMColor.NONE).thickness(.25 * th);
                break;
            default:// Default case, includes CIRCLE
                dotShape = Shape.circle().shift(v).scale(.5 * st).drawColor(getMp().getDrawColor())
                        .fillColor(getMp().getDrawColor()).thickness(0);
                break;
        }
        dotShape.getMp().setFaceToCamera(true);
        dotShape.getMp().setFaceToCameraPivot(this.v);
        return dotShape;
    }

    /**
     * Returns the current dot style
     *
     * @return A value of enum DotStyle: CIRCLE, CROSS, PLUS
     */
    public DotSyle getDotStyle() {
        return getMp().getDotStyle();
    }

    @Override
    public Point copy() {
        Point resul = new Point(v);
        resul.getMp().copyFrom(getMp());
        resul.visible(this.isVisible());
        return resul;
    }

    /**
     * Return a new Point object which represents the original point plus a
     * given vector. The original point is unaltered.
     *
     * @param addVector Vector to add
     * @return Original point+addVector
     */
    public Point add(Vec addVector) {
        Point resul = (Point) this.copy();
        resul.v.addInSite(addVector);
        return resul;
    }

    @Override
    public String toString() {
        String pattern = "##0.###########";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return objectLabel + "|Point(" + decimalFormat.format(v.x) + ",  " + decimalFormat.format(v.y) + ",  " + decimalFormat.format(v.z) + ")";

    }

    /**
     * Returns Vec object point from this Point to another one
     *
     * @param B The destination point
     * @return The vector from this point to B
     */
    public Vec to(Point B) {
        return new Vec(B.v.x - v.x, B.v.y - v.y, B.v.z - v.z);
    }

    /**
     * Returns a new Point, linearly interpolated between this and p2 with alpha
     * parameter
     *
     * @param p2
     * @param alpha
     * @return The new Point
     */
    public Point interpolate(Point p2, double alpha) {
        Vec w = v.interpolate(p2.v, alpha);
        return new Point(w);

    }

    @Override
    public Rect getBoundingBox() {
        return new Rect(v.x, v.y, v.z, v.x, v.y, v.z);
    }

    @Override
    public void saveState() {
        super.saveState();
        this.v.saveState();
    }

    @Override
    public void restoreState() {
        super.restoreState();
        this.v.restoreState();
    }

    /**
     * Copy coordinates from another point p
     *
     * @param p
     */
    public void copyFrom(Point p) {
        this.v.copyFrom(p.v);
    }

    /**
     * Copy full state form another point p
     *
     * @param obj
     */
    @Override
    public void copyStateFrom(MathObject obj) {
        if (!(obj instanceof Point)) {
            return;
        }
        Point p2 = (Point) obj;
        this.copyFrom(p2);//Copy coordinates
        this.getMp().copyFrom(p2.getMp());
    }

    public boolean isEquivalentTo(Point p2, double epsilon) {
        boolean resul = false;
        if ((Math.abs(v.x - p2.v.x) <= epsilon) & (Math.abs(v.y - p2.v.y) <= epsilon) & (Math.abs(v.z - p2.v.z) <= epsilon)) {
            resul = true;
        }
        return resul;
    }

}
