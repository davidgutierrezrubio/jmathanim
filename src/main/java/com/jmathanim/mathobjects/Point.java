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

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Enum.DotStyle;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updaters.Coordinates;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.text.DecimalFormat;

/**
 * This class represents a point in 2D or 3D space
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Point extends MathObject<Point> implements
        Coordinates, AffineTransformable<Point>, Interpolable<Point> {

    //Position of the point to be drawn in screen
    public final Vec v;


    //Current position of the Shape representing the point
    private final Vec previousVecPosition;
    private final Shape dotShape;

    /**
     * Creates a new Point with coordinates (0,0,0), with default style.
     */
    public Point() {
        this(0, 0, 0);
    }


    /**
     * Creates a new point linking coordinates from given vector, with default style.
     *
     * @param v Vector with coordinates
     */
    public Point(Vec v) {
        super();
        this.v = v;
        previousVecPosition = this.v.copy();
        this.dotShape = new Shape();
    }


    /**
     * Overloaded method. Creates a new Point with coordinates x,y, with default style. The z coordinates is set to 0.
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
        previousVecPosition = this.v.copy();
        this.dotShape = new Shape();
    }

    /**
     * Static method. Returns a new point at (0,0), with default style
     *
     * @return The created Point
     */
    public static Point origin() {
        return Point.at(0, 0);
    }

    public static Point relAt(double x, double y) {
        return JMathAnimConfig.getConfig().getCamera().getMathView().getRelPoint(x, y);
    }

    /**
     * Static method. Returns a new point at (1,0), with default style
     *
     * @return The created Point
     */
    public static Point unitX() {
        return Point.at(1, 0);
    }

    /**
     * Static method. Returns a new point at (0,1), with default style
     *
     * @return The created Point
     */
    public static Point unitY() {
        return Point.at(0, 1);
    }

    public static Point unitZ() {
        return new Point(0, 0, 1);
    }

    public static Vec segmentIntersection(Coordinates A, Coordinates B, Coordinates C, Coordinates D) {
        AffineJTransform tr = AffineJTransform.createAffineTransformation(A, B, C, Point.unitX(), Point.unitY(),
                Point.origin(), 1);
        Vec P = D.getVec().copy().applyAffineTransform(tr);
        double r = P.x + P.y;
        if ((r >= 1) & (P.x >= 0) & (P.y >= 0)) {
            P.x /= r;
            P.y /= r;
            return P.applyAffineTransform(tr.getInverse());
        } else {
            return null;
        }
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
     * Returns a new Point with coordinates those of given vector. Vector coordinates are copied.
     *
     * @param coords Vector with coordinates
     * @return The new point
     */
    public static Point at(Coordinates coords) {
        Vec v=coords.getVec();
        return new Point(v.x, v.y);
    }

    /**
     * Static builder. Creates and returns a new point at given coordinates (3d space).
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
     * Static builder.Creates and returns a new point at random coordinates, inside the math view.
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
    public Point applyAffineTransform(AffineJTransform tr) {
        RealMatrix pRow = new Array2DRowRealMatrix(new double[][]{{1d, v.x, v.y, v.z}});
        RealMatrix pNew = pRow.multiply(tr.getMatrix());

        v.x = pNew.getEntry(0, 1);
        v.y = pNew.getEntry(0, 2);
        v.z = pNew.getEntry(0, 3);
        if (hasMPCreated())
            tr.applyTransformsToDrawingProperties(this);
        return this;
    }

    @Override
    public Point getCenter() {
        return new Point(v.x,v.y,v.z);
    }

    @Override
    protected Stylable createDefaultMPForThisObject() {
        MODrawProperties mpPoint = JMathAnimConfig.getConfig().getDefaultMP();
        mpPoint.copyFrom(JMathAnimConfig.getConfig().getStyles().get("dotdefault"));
        mpPoint.setAbsoluteThickness(true);
        return mpPoint;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        if (v.isNaN()) {
//        if ((v.isNaN()) || (this.scene == null))  {
            return;
        }
        if (isVisible()) {
            if (dotShape.isEmpty()) {
                generateDotShape();
                dotShape.setAbsoluteSize(v);
            }
            if (!previousVecPosition.equals(v)) {
                dotShape.shift(v.minus(previousVecPosition));
                previousVecPosition.copyFrom(v);
            }

            dotShape.draw(scene, r, cam);
        }
        scene.markAsAlreadydrawn(this);

    }

    /**
     * Stablishes dot style.
     *
     * @param dotStyle Style dot. DOT_STYLE_CIRCLE, DOT_STYLE_CROSS, DOT_STYLE_PLUS
     * @return The object
     */
    public Point dotStyle(DotStyle dotStyle) {
        this.getMp().setDotStyle(dotStyle);
        return this;
    }

    private Shape generateDotShape() {
        System.out.println("generate dot shape");
        double st = scene.getRenderer().ThicknessToMathWidth(this);
        double th = scene.getRenderer().MathWidthToThickness(st);
        double sc = .5 * st;
        dotShape.getPath().clear();
        switch (getMp().getDotStyle()) {
            case CROSS:
                dotShape.getPath().addPoint(Point.at(-sc, sc), Point.at(sc, -sc), Point.at(sc, sc), Point.at(-sc, -sc));
                dotShape.get(0).isThisSegmentVisible = false;
                dotShape.get(2).isThisSegmentVisible = false;
                dotShape.shift(previousVecPosition).drawColor(getMp().getDrawColor()).thickness(.25 * th);
                break;
            case PLUS:
                dotShape.getPath().addPoint(Point.at(0, 1), Point.at(0, -1), Point.at(1, 0), Point.at(-1, 0));
                dotShape.get(0).isThisSegmentVisible = false;
                dotShape.get(2).isThisSegmentVisible = false;
                dotShape.shift(previousVecPosition).scale(.5 * st).drawColor(getMp().getDrawColor()).thickness(.25 * th);
                break;
            case TRIANGLE_DOWN_HOLLOW:
                dotShape.getPath().addPoint(Point.at(-sc, 0.5773502691893 * sc), Point.at(sc, 0.5773502691893 * sc), Point.at(0, -1.15470053838 * sc));
                dotShape
                        .shift(previousVecPosition)
                        .drawColor(getMp().getDrawColor()).thickness(.25 * th);
                break;
            case TRIANGLE_UP_HOLLOW:
                dotShape.getPath().addPoint(Point.at(-sc, -0.5773502691893 * sc), Point.at(sc, -0.5773502691893 * sc), Point.at(0, 1.15470053838 * sc));
                dotShape.shift(v).drawColor(getMp().getDrawColor()).thickness(.25 * th);
                break;
            case TRIANGLE_DOWN_FILLED:
                dotShape.getPath().addPoint(Point.at(-sc, 0.5773502691893 * sc), Point.at(sc, 0.5773502691893 * sc), Point.at(0, -1.15470053838 * sc));
                dotShape
                        .shift(previousVecPosition)
                        .drawColor(getMp().getDrawColor()).fillColor(getMp().getDrawColor()).thickness(.25 * th);
                break;
            case TRIANGLE_UP_FILLED:
                dotShape.getPath().addPoint(Point.at(-sc, -0.5773502691893 * sc), Point.at(sc, -0.5773502691893 * sc), Point.at(0, 1.15470053838 * sc));
                dotShape.shift(previousVecPosition).drawColor(getMp().getDrawColor()).fillColor(getMp().getDrawColor()).thickness(.25 * th);
                break;

            case RING:
                dotShape.getPath().copyStateFrom(Shape.circle().getPath());
                dotShape.shift(previousVecPosition).scale(.5 * st).drawColor(getMp().getDrawColor())
                        .fillColor(JMColor.NONE).thickness(.25 * th);
                break;
            default:// Default case, includes CIRCLE
                dotShape.getPath().copyStateFrom(Shape.circle().getPath());
                dotShape.shift(previousVecPosition).scale(.5 * st).drawColor(getMp().getDrawColor())
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
    public DotStyle getDotStyle() {
        return getMp().getDotStyle();
    }

    @Override
    public Point copy() {
        Point resul = new Point(v.x,v.y);
        resul.copyStateFrom(this);
        return resul;
    }

    /**
     * Return a new Point object which represents the original point plus a given vector. The original point is
     * unaltered.
     *
     * @param addVector Vector to add
     * @return Original point+addVector
     */
    public Point add(Vec addVector) {
        Point resul = this.copy();
        resul.v.addInSite(addVector);
        return resul;
    }

    @Override
    public String toString() {
        String pattern = "##0.####";
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
     * Returns a new Point, linearly interpolated between this and p2 with alpha parameter
     *
     * @param coords2 Second Point to interpolate. Any object that implements the Coordinates interface can be used.
     * @param alpha Interpolation parameter where 0 returns a copy of this object and 1 a copy of another object
     * @return The new Point
     */
    public Point interpolate(Coordinates coords2, double alpha) {
        Vec w = v.interpolate(coords2, alpha);
        return  new Point(w.x,w.y,w.z);

    }

    @Override
    protected Rect computeBoundingBox() {
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
     * Copy full state form another point p
     *
     * @param obj
     */
    @Override
    public void copyStateFrom(MathObject obj) {
        DotStyle dotStyleBackup = (hasMPCreated() ? getDotStyle() : null);
        super.copyStateFrom(obj);
        if (!(obj instanceof Point)) {
            return;
        }

        Point p2 = (Point) obj;
        DotStyle dotStyleObj = (p2.hasMPCreated() ? getDotStyle() : null);
        this.v.copyFrom(p2.v);//Copy coordinates
        Vec vv = p2.previousVecPosition.minus(this.previousVecPosition);
        this.previousVecPosition.copyFrom(p2.previousVecPosition);//Copy coordinates
//        if (p2.getDotStyle() != dotStyleBackup) {
        if (dotStyleBackup!=dotStyleObj) {
        if (!p2.dotShape.isEmpty())
            generateDotShape();
        } else {
            dotShape.shift(vv);
        }

        this.scene = obj.scene;

    }

    public boolean isEquivalentTo(Point p2, double epsilon) {
        return v.isEquivalentTo(p2.v, epsilon);
    }

    @Override
    public Vec getVec() {
        return v;
    }

}
