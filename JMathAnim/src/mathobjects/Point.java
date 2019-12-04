/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathobjects;

import Renderers.Renderer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Properties;
import Utils.ConfigUtils;
import Utils.Vec;

/**
 * This class represents a point
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public final class Point extends MathObject {

    String[] DEFAULT_CONFIG_POINT = {
        "VISIBLE", "TRUE",
        "RADIUS", "10"
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
     * @param cnf
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
        return new Vec(x,y,z);
    }

    @Override
    public void draw(Renderer r) {
        int rad = Integer.parseInt(cnf.getProperty("RADIUS"));
        r.setColor(Color.WHITE);
        r.drawCircle(x-.5*rad, y-.5*rad, rad);
        
    }

    
    


    
}
