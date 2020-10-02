/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.text.DecimalFormat;
import java.util.HashSet;

/**
 * This class represents a point in 2D or 3D space
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Point extends MathObject {

    public Vec v;
    private Vec vBackup;

    public final HashSet<JMPathPoint> jmPoints;

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
    public Point(double x, double y, MathObjectDrawingProperties mp) {
        this(x, y, 0, mp);

    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @param mp
     */
    public Point(double x, double y, double z, MathObjectDrawingProperties mp) {
        super(mp);
        this.v = new Vec(x, y, z);
        this.mp.absoluteThickness = true;
        this.mp.thickness = 8d;//default value
        jmPoints = new HashSet<>();
    }

    /**
     * Static builder
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return
     */
    public static Point make(double x, double y) {
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
        r.drawDot(this);

    }

    @Override
    public <T extends MathObject> T moveTo(Vec coords) {
        return shift(coords.minus(v));

    }

    @Override
    public <T extends MathObject> T shift(Vec shiftVector) {
        v.x += shiftVector.x;
        v.y += shiftVector.y;
        v.z += shiftVector.z;
        return (T) this;
    }

    @Override
    public Point copy() {
        Point resul = new Point(v);
        resul.mp.copyFrom(mp);
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
    public void update() {
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

}
