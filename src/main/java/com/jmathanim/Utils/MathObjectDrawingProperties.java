/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import com.jmathanim.Renderers.Renderer;
import java.awt.Color;

/**
 * This class stores all drawing properties of a MathObject like color,
 * thickness, alpha, etc.
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class MathObjectDrawingProperties {

    public Color color;
    public Color fillColor;
    public Double thickness;
    //If false, thickness is computed to be a percentage of the width
    //to ensure zoom or resolution doesn't affect the result
    public Boolean absoluteThickness;
    public Boolean fill;
    public Boolean visible;
    public Double alpha;
    public Integer layer;//Layer to draw. Slower means under.
    public Boolean absolutePosition;//If true, position comes in absolute screen coordinates
    public Boolean drawPathBorder;

    public MathObjectDrawingProperties() {
        //Default, boring values
        this.color = Color.WHITE;
        this.fillColor = Color.YELLOW;
        this.thickness = .005d;
        this.alpha = 1d;
        this.visible = true;
        this.fill = false;
        this.drawPathBorder = false;
        this.absoluteThickness = false;
        this.layer = 1;//Layer 0 should be reserved for background
        this.absolutePosition = false;

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
        color = (prop.color == null ? color : new Color(prop.color.getRGB()));
        thickness = (prop.thickness == null ? thickness : prop.thickness);
        alpha = (prop.alpha == null ? alpha : prop.alpha);
        visible = (prop.visible == null ? visible : prop.visible);
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
    public void interpolateFrom(MathObjectDrawingProperties a,MathObjectDrawingProperties b, double t) {
        //Interpolate color
        interpolateColor(a.color,b.color, t);
        this.thickness=(1-t)*a.thickness+t*b.thickness;

    }

    /**
     * Replaces current color with an interpolated value of given colors
     * @param colA Color A to interpolate
     * @param colB Color B to interpolate
     * @param t Interpolation value (t=0 gives colA and t=1 gives colB)
     */
    public void interpolateColor(Color colA, Color colB,double t) {
        int r = (int) ((1 - t) * colA.getRed() + t * colB.getRed());
        int g = (int) ((1 - t) * colA.getGreen() + t * colB.getGreen());
        int b = (int) ((1 - t) * colA.getBlue() + t * colB.getBlue());

        this.color=new Color(r, g,b);

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

    public MathObjectDrawingProperties copy() {
        MathObjectDrawingProperties resul=new MathObjectDrawingProperties();
        resul.digestFrom(this);
        return resul;
    }
}
