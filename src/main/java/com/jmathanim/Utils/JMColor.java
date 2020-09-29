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

    public static JMColor rgbInt(int r, int g, int b, int alpha) {
        return new JMColor(r * 1.f / 255, g * 1.f / 255, b * 1.f / 255, alpha * 1.f / 255);
    }

    public static JMColor rgbInt(int r, int g, int b) {
        return JMColor.rgbInt(r, g, b, 255);
    }

    //https://stackoverflow.com/questions/4129666/how-to-convert-hex-to-rgb-using-java
    public static JMColor hex(String hex) {
        int rr, gg, bb, aa;
        hex = hex.replace("#", "");
        switch (hex.length()) {
            case 6:
                rr = Integer.valueOf(hex.substring(0, 2), 16);
                gg = Integer.valueOf(hex.substring(2, 4), 16);
                bb = Integer.valueOf(hex.substring(4, 6), 16);
                return JMColor.rgbInt(rr, gg, bb);
            case 8:
                rr = Integer.valueOf(hex.substring(0, 2), 16);
                gg = Integer.valueOf(hex.substring(2, 4), 16);
                bb = Integer.valueOf(hex.substring(4, 6), 16);
                aa = Integer.valueOf(hex.substring(6, 8), 16);
                return JMColor.rgbInt(rr, gg, bb,aa);
            default:
                return null;
        }
    }

    public Color getColor() {
        return new Color((float) r, (float) g, (float) b, (float) alpha);
    }

    public JMColor getInverse(){
        return new JMColor(1-r, 1-g, 1-b, alpha);
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
