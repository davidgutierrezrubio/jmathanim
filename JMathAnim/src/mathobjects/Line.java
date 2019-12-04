/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathobjects;

import Renderers.Renderer;
import Utils.ConfigUtils;
import Utils.Vec;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Properties;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Line extends MathObject {

    String[] DEFAULT_CONFIG = {
        "THICKNESS", "1",
        "RADIUS", "10",
        "STROKEJOIN", "ROUND"
    };
    Point p1, p2;

    public Line(Point p1, Point p2) {
        this(p1, p2, null);
    }

    public Line(Point p1, Point p2, Properties configParam) {
        super(configParam);
        this.p1 = p1;
        this.p2 = p2;
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG, configParam);
    }

  

    @Override
    public Vec getCenter() {
        Vec v1 = p1.getCenter();
        Vec v2 = p2.getCenter();
        return v1.add(v2).mult(.5);
    }

    @Override
    public void draw(Renderer r) {
        Vec v1 = p1.getCenter();
        Vec v2 = p2.getCenter();
        r.setColor(Color.WHITE);
        r.drawLine(v1.xi(), v1.yi(), v2.xi(), v2.yi());

    }

}
