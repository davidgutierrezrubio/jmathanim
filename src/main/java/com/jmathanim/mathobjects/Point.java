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
import com.jmathanim.Utils.Vec;

/**
 * This class represents a point
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public final class Point extends MathObject {

    String[] DEFAULT_CONFIG_POINT = {
        "VISIBLE", "TRUE",
        "RADIUS", ".01"//Radius relative to width screen
    };

    public double x, y, z;

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
    public Point(double x, double y, Properties cnf) {
        this(x, y, 0, cnf);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @param configParam
     */
    public Point(double x, double y, double z, Properties configParam) {
        super(configParam);
        this.x = x;
        this.y = y;
        this.z = z;
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG_POINT, configParam);

    }

    @Override
    public Vec getCenter() {
        return new Vec(x, y, z);
    }

    @Override
    public void draw(Renderer r) {
        double rad1 = Double.parseDouble(cnf.getProperty("RADIUS"));
        r.setColor(Color.WHITE);
//        double  w = (double) (.5*rad*r.getWidth());//Radius relative to screen width
        double rad=r.getCamera().relScalarToWidth(rad1);
        r.drawCircle(x , y , rad);

    }

    @Override
    public void moveTo(Vec coords) {
        x = coords.x;
        y = coords.y;
        z = coords.z;

    }

    @Override
    public void shift(Vec shiftVector) {
        x += shiftVector.x;
        y += shiftVector.y;
        z += shiftVector.z;
    }

    @Override
    public MathObject copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
