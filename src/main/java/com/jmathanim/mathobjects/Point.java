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
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.text.DecimalFormat;

/**
 * This class represents a point in 2D or 3D space
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Point extends MathObject {

    public final Vec v;
    private final Vec vBackup;
    private Shape dotShape;

    public enum DotSyle {
        CIRCLE, CROSS, PLUS
    }

    public static final Point origin() {
        return Point.at(0, 0);
    }

    public static final Point unitX() {
        return Point.at(1, 0);
    }

    public static final Point unitY() {
        return Point.at(0, 1);
    }
     public static final Point unitZ() {
        return new Point(0, 0,1);
    }

    public Point() {
        this(0, 0, 0);
    }

    public Point(Point p) {
        this(p.v);
    }

    public Point(Vec v) {
        this(v.x, v.y, v.z);
    }

    /**
     * Creates a new Point with coordinates x,y,z
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public Point(double x, double y, double z) {
        this(x, y, z, null);
    }

    /**
     *
     * @param x
     * @param y
     */
    public Point(double x, double y) {
        this(x, y, 0, null);
    }

    /**
     *
     * @param x
     * @param y
     * @param mp
     */
    public Point(double x, double y, MODrawProperties mp) {
        this(x, y, 0, mp);

    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @param mp
     */
    public Point(double x, double y, double z, MODrawProperties mp) {
        super(mp);
        this.v = new Vec(x, y, z);
        this.vBackup = new Vec(x, y, z);
        this.mp.absoluteThickness = true;
//        this.mp.thickness = 8d;//default value
    }

    /**
     * Static builder
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return
     */
    public static Point at(double x, double y) {
        return new Point(x, y);
    }

    public static Point random() {
        Rect r = JMathAnimConfig.getConfig().getCamera().getMathView();
        double x = r.xmin + (r.xmax - r.xmin) * Math.random();
        double y = r.ymin + (r.ymax - r.ymin) * Math.random();
        return new Point(x, y);
    }

    @Override
    public Point getCenter() {
        return this;
    }

    @Override
    public void draw(Renderer r) {
//        r.setBorderColor(mp.drawColor);
//        double rad = mp.getThickness(r);
//        r.drawCircle(v.x, v.y, rad);
//        
        double st;
        dotShape = generateDotShape();
        dotShape.setAbsoluteSize();
        dotShape.setAbsoluteAnchorPoint(this.copy());
        dotShape.draw(r);

    }

    /**
     * Stablishes dot style.
     *
     * @param dotStyle Style dot. DOT_STYLE_CIRCLE, DOT_STYLE_CROSS,
     * DOT_STYLE_PLUS
     * @return The object
     */
    public Point dotStyle(DotSyle dotStyle) {
        this.mp.dotStyle = dotStyle;
        return this;
    }

    private Shape generateDotShape() {
        double st;
        Shape dotShape;
        switch (mp.dotStyle) {
            case CROSS:
//                st = mp.computeScreenThickness(r)/20;
                st = mp.thickness / 70;
                dotShape = new Shape();
                dotShape.getPath().addPoint(Point.at(-1, 1), Point.at(1, -1), Point.at(1, 1), Point.at(-1, -1));
                dotShape.getJMPoint(0).isThisSegmentVisible = false;
                dotShape.getJMPoint(2).isThisSegmentVisible = false;
                dotShape.shift(v).scale(st).drawColor(mp.getDrawColor()).thickness(mp.thickness);
                break;
            case PLUS:
//                st = mp.computeScreenThickness(r)/20;
                st = mp.thickness / 70;
                dotShape = new Shape();
                dotShape.getPath().addPoint(Point.at(0, 1), Point.at(0, -1), Point.at(1, 0), Point.at(-1, 0));
                dotShape.getJMPoint(0).isThisSegmentVisible = false;
                dotShape.getJMPoint(2).isThisSegmentVisible = false;
                dotShape.shift(v).scale(st).drawColor(mp.getDrawColor()).thickness(mp.thickness);
                break;
            default://Default case, includes CIRCLE
//                st = mp.computeScreenThickness(r)/200;
                st = mp.thickness / 70;
                dotShape = Shape.circle().shift(v).scale(st).drawColor(mp.getDrawColor()).fillColor(mp.getDrawColor()).thickness(0);
                break;
        }
        return dotShape;
    }

    @Override
    public <T extends MathObject> T moveTo(Point p) {
        return shift(p.v.minus(v));

    }

    public DotSyle getDotStyle() {
        return mp.dotStyle;
    }

//    @Override
//    public <T extends MathObject> T shift(Vec shiftVector) {
//        v.x += shiftVector.x;
//        v.y += shiftVector.y;
//        v.z += shiftVector.z;
//        return (T) this;
//    }
    @Override
    public Point copy() {
        Point resul = new Point(v);
        resul.mp.copyFrom(mp);
        resul.visible = this.visible;
        return resul;
    }

    /**
     * Return a new Point object which represents the original point plus a
     * given vector
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
        String pattern = "##0.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return label + "|Point(" + decimalFormat.format(v.x) + "," + decimalFormat.format(v.y) + ")";

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
    public void prepareForNonLinearAnimation() {
    }

    @Override
    public void processAfterNonLinearAnimation() {
    }

    @Override
    public Rect getBoundingBox() {
        return new Rect(v.x, v.y, v.x, v.y);
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        //Nothing to do  here
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        //Nothing to do  here
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

    public void copyFrom(Point p) {
        this.v.copyFrom(p.v);
    }

    @Override
    public void update(JMathAnimScene scene) {
    }

}
