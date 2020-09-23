/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import java.awt.Color;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class JMColor {

    public static JMColor WHITE = new JMColor(1, 1, 1, 1);
    public static JMColor BLACK = new JMColor(0, 0, 0, 1);
    public static JMColor RED = new JMColor(1, 0, 0, 1);
    public static JMColor GREEN = new JMColor(0, 1, 0, 1);
    public static JMColor BLUE = new JMColor(0, 0, 1, 1);
    public static JMColor GRAY = new JMColor(.5, .5, .5, 1);
    
    public double r, g, b, alpha;

    public JMColor(double r, double g, double b, double alpha) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.alpha = alpha;
    }

    public Color getColor() {
        return new Color((float) r, (float) g, (float) b, (float) alpha);
    }

    public JMColor copy() {
        return new JMColor(r, g, b, alpha);
    }

    public void set(JMColor B) {
        if (B != null) {
            r = B.r;
            g = B.g;
            b = B.b;
            alpha = B.alpha;
        }
    }

    public JMColor interpolate(JMColor B, double t) {
        double rr = (1 - t) * r + t * B.r;
        double gg = (1 - t) * g + t * B.g;
        double bb = (1 - t) * b + t * B.b;
        double aa = (1 - t) * alpha + t * B.alpha;
        return new JMColor(rr, gg, bb, aa);
    }

    //Static methods
    public static JMColor random() {
        return new JMColor(Math.random(), Math.random(), Math.random(), 1);
    }

}
