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

    public Color drawColor;
    public Color fillColor;
    public Double thickness;
    //If false, thickness is computed to be a percentage of the width
    //to ensure zoom or resolution doesn't affect the result
    public Boolean absoluteThickness;
    public Boolean fill;
    public Boolean visible;
    public Integer layer;//Layer to draw. Slower means under.
    public Boolean absolutePosition;//If true, position comes in absolute screen coordinates
    public Boolean drawPathBorder;
    public Integer dashStyle;

    public MathObjectDrawingProperties() {
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
        drawColor = (prop.drawColor == null ? drawColor : new Color(prop.drawColor.getRGB()));
        thickness = (prop.thickness == null ? thickness : prop.thickness);
        visible = (prop.visible == null ? visible : prop.visible);
        dashStyle = (prop.dashStyle == null ? dashStyle : prop.dashStyle);
        fill = (prop.fill == null ? fill : prop.fill);
        drawPathBorder = (prop.drawPathBorder == null ? drawPathBorder : prop.drawPathBorder);
        absoluteThickness = (prop.absoluteThickness == null ? absoluteThickness : prop.absoluteThickness);
        layer = (prop.layer == null ? layer : prop.layer);
        absolutePosition = (prop.absolutePosition == null ? absolutePosition : prop.absolutePosition);
    }

    /**
     * Interpolate values from another MathObjecDrawingProperties
     *
     * @param pro
     * @param t Interpolation parameter
     */
    public void interpolateFrom(MathObjectDrawingProperties a, MathObjectDrawingProperties b, double t) {
        //Interpolate colors
        drawColor=interpolateColor(a.drawColor, b.drawColor, t);
        fillColor=interpolateColor(a.fillColor, b.fillColor, t);
//        interpolateColor(a.fillColor, b.fillColor, t);
        this.thickness = (1 - t) * a.thickness + t * b.thickness;

    }

    /**
     * Compute an interpolated value of given colors.
     * Interpolates R,G,B and alpha
     *
     * @param colA Color A to interpolate
     * @param colB Color B to interpolate
     * @param t Interpolation value (t=0 gives colA and t=1 gives colB)
     * @return The interpolated color
     */
    public Color interpolateColor(Color colA, Color colB, double t) {
        int r = (int) ((1 - t) * colA.getRed() + t * colB.getRed());
        int g = (int) ((1 - t) * colA.getGreen() + t * colB.getGreen());
        int b = (int) ((1 - t) * colA.getBlue() + t * colB.getBlue());
        int tr = (int) ((1 - t) * colA.getAlpha() + t * colB.getAlpha());

        return new Color(r, g, b, tr);
    }

    public void setRandomDrawColor()
    {
        drawColor=randomColor();
    }
    public Color randomColor()
    {
        int r= ThreadLocalRandom.current().nextInt(0, 255 + 1);
        int g= ThreadLocalRandom.current().nextInt(0, 255 + 1);
        int b= ThreadLocalRandom.current().nextInt(0, 255 + 1);
        return new Color(r,g,b);
    }
    
    public void setFillAlpha(float alpha) {
        this.fillColor = new Color(this.fillColor.getRed(), this.fillColor.getGreen(), this.fillColor.getBlue(), (int) (255 * alpha));
    }

    public void setDrawAlpha(float alpha) {
        this.drawColor = new Color(this.drawColor.getRed(), this.drawColor.getGreen(), this.drawColor.getBlue(), (int) (255 * alpha));
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
//        fill;
//        visible;
//        absolutePosition;
//        drawPathBorder;
//        dashStlye
        drawColor = interpolateColor(mp.drawColor, mp.drawColor, 1);//Trick to make a copy
        fillColor = interpolateColor(mp.fillColor, mp.fillColor, 1);//Trick to make a copy
        thickness = mp.thickness;
        dashStyle = mp.dashStyle;
        absoluteThickness = mp.absoluteThickness;
        fill = mp.fill;
        visible = mp.visible;
        absolutePosition = mp.absolutePosition;
        drawPathBorder = mp.drawPathBorder;
        dashStyle=mp.dashStyle;
    }
//
}
