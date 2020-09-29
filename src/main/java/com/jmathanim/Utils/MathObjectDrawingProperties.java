/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import com.jmathanim.Renderers.Renderer;
import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class stores all drawing properties of a MathObject like color,
 * thickness, alpha, etc.
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class MathObjectDrawingProperties {

    public static final int SOLID = 1;
    public static final int DASHED = 2;
    public static final int DOTTED = 3;

    public final JMColor drawColor;
    public final JMColor fillColor;
    public Double thickness=1d;
    //If false, thickness is computed to be a percentage of the width
    //to ensure zoom or resolution doesn't affect the result
    public Boolean absoluteThickness=false;
    public Integer dashStyle=1;

    public MathObjectDrawingProperties() {
        drawColor = new JMColor(1, 1, 1, 1);
        fillColor = new JMColor(0, 0, 0, 0);
    }

    /**
     * Absorb all non-null properties of a given properties class
     *
     * @param prop
     */
    public void digestFrom(MathObjectDrawingProperties prop) {
        if (prop == null) {//Nothing to do here!
            return;
        }
        drawColor.set(prop.drawColor);
        fillColor.set(prop.fillColor);
        thickness = (prop.thickness == null ? thickness : prop.thickness);
        dashStyle = (prop.dashStyle == null ? dashStyle : prop.dashStyle);
        absoluteThickness = (prop.absoluteThickness == null ? absoluteThickness : prop.absoluteThickness);
    }

    /**
     * Interpolate values from another MathObjecDrawingProperties
     *
     * @param pro
     * @param t Interpolation parameter
     */
    public void interpolateFrom(MathObjectDrawingProperties a, MathObjectDrawingProperties b, double t) {
        //Interpolate colors
        drawColor.set(a.drawColor.interpolate(b.drawColor, t));
        fillColor.set(a.fillColor.interpolate(b.fillColor, t));

//        interpolateColor(a.fillColor, b.fillColor, t);
        this.thickness = (1 - t) * a.thickness + t * b.thickness;

    }

    public void setRandomDrawColor() {
        drawColor.set(JMColor.random());
    }

    public static Color randomColor() {
        int r = ThreadLocalRandom.current().nextInt(0, 255 + 1);
        int g = ThreadLocalRandom.current().nextInt(0, 255 + 1);
        int b = ThreadLocalRandom.current().nextInt(0, 255 + 1);
        return new Color(r, g, b);
    }

    public void setFillAlpha(float alpha) {
        this.fillColor.alpha = alpha;
    }

    public void setDrawAlpha(float alpha) {
        this.drawColor.alpha = alpha;
    }

    public boolean isFilled() {
        return (this.fillColor.alpha > 0);
    }

    public void setFilled(boolean fill) {
        if (fill && fillColor.alpha == 0) {
            setFillAlpha(1);
        }
        if (!fill) {
            setFillAlpha(0);
        }
    }

    public double getThickness(Renderer r) {
        double resul;
        if (absoluteThickness) {
            resul = thickness;
        } else {
            resul = r.getCamera().relScalarToWidth(thickness);
        }
        return resul;
    }

    public MathObjectDrawingProperties copy() {//TODO: FIX THIS
        MathObjectDrawingProperties resul = new MathObjectDrawingProperties();
        resul.copyFrom(this);
        return resul;
    }

    public void copyFrom(MathObjectDrawingProperties mp) {
//        drawColor;
//        fillColor;
//        thickness;
//        absoluteThickness;
//        absolutePosition;
//        dashStlye
        drawColor.set(mp.drawColor);
        fillColor.set(mp.fillColor);
        thickness = mp.thickness;
        dashStyle = mp.dashStyle;
        absoluteThickness = mp.absoluteThickness;
        dashStyle = mp.dashStyle;
    }
//
}
