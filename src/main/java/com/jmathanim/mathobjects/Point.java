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
import com.jmathanim.Utils.Vec;

/**
 * This class represents a point
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public final class Point extends MathObject {

   
    public static final int TYPE_NONE = 0;
    public static final int TYPE_VERTEX = 1;
    public static final int TYPE_INTERPOLATION_POINT = 2;
    public static final int TYPE_CONTROL_POINT = 3;

    public int type;
    public Vec v;

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
        type = TYPE_NONE; //Default type of point
    }

    @Override
    public Point getCenter() {
        return this;
    }

    @Override
    public void draw(Renderer r) {
        r.setColor(mp.color);
        double rad = mp.getThickness(r);
        r.drawCircle(v.x, v.y, rad);

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
    public MathObject copy() {
        return new Point(v);//TODO: Improve this
    }

    @Override
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

}
