/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathobjects;

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

    String[] DEFAULT_CONFIG = {
        "VISIBLE", "TRUE",
        "RADIUS", "10"
    };

    public float x, y, z;

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public Point(float x, float y, float z) {
        this(x, y, z, null);
    }

    /**
     *
     * @param x
     * @param y
     */
    public Point(float x, float y) {
        this(x, y, 0, null);
    }

    /**
     *
     * @param x
     * @param y
     * @param cnf
     */
    public Point(float x, float y, Properties cnf) {
        this(x, y, 0, cnf);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @param cnf
     */
    public Point(float x, float y, float z, Properties configParam) {
        super(configParam);
        this.x = x;
        this.y = y;
        this.z = z;
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG, configParam);

    }

   
    @Override
    public Vec getCenter() {
        return new Vec(x,y,z);
    }

    @Override
    public void draw(Graphics2D g2d) {
        int r = Integer.parseInt(cnf.getProperty("RADIUS"));
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)(x-.5*r), (int)(y-.5*r), r, r);
        
    }

    
    


    
}
