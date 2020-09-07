/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import java.awt.Color;
import java.util.Properties;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.ConfigUtils;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;

/**
 * This class represents a point
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Point extends MathObject {

    public Vec v;

    public Point(Point p) {
        this(p.v);
    }

    public Point(Vec v) {
        this(v.x, v.y, v.z);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
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
     * @param cnf
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
    public void moveTo(Vec coords) {
        v.x = coords.x;
        v.y = coords.y;
        v.z = coords.z;
        update();

    }

    @Override
    public void shift(Vec shiftVector) {
        v.x += shiftVector.x;
        v.y += shiftVector.y;
        v.z += shiftVector.z;
        update();
    }

    @Override
    public Point copy() {
        return new Point(v);//TODO: Improve this
    }

    @Override
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        v.x = (1 - sx) * scaleCenter.v.x + sx * v.x;
        v.y = (1 - sy) * scaleCenter.v.y + sy * v.y;
        v.z = (1 - sz) * scaleCenter.v.z + sz * v.z;
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
        return "Point(" + v.x + "," + v.y + ")";

    }

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
    public void update() {
        //Nothing else to do
        updateDependents();
    }

    @Override
    public void prepareForNonLinearAnimation() {
    }

    @Override
    public void processAfterNonLinearAnimation() {
    }

    @Override
    public void setDrawParam(double t, int sliceType) {
        //Nothing to do here, it's just a point!!
    }

    @Override
    public Rect getBoundingBox() {
        return new Rect(v.x, v.y, v.x, v.y);
    }

    @Override
    public void setDrawAlpha(double t) {
        this.mp.setDrawAlpha((float) t);
    }

    @Override
    public void setFillAlpha(double t) {
        //Nothing to do  here
    }

}
