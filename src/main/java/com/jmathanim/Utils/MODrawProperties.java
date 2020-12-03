/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.Utils;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point.DotSyle;
import com.jmathanim.mathobjects.Stateable;
import java.awt.Color;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.shape.StrokeLineCap;

/**
 * This class stores all drawing properties of a MathObject like color,
 * thickness, alpha, etc.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MODrawProperties implements Stateable {

    public JMColor getDrawColor() {
        return drawColor;
    }

    public JMColor getFillColor() {
        return fillColor;
    }

    public enum DashStyle {
        SOLID, DASHED, DOTTED
    }

    //When added a new property here, remember to include it in rawCopyFrom and copyFrom
    private JMColor drawColor;
    private JMColor fillColor;
    public Double thickness = 1d;
    //If false, thickness is computed to be a percentage of the width
    //to ensure zoom or resolution doesn't affect the result
    public Boolean absoluteThickness = true;
    public DashStyle dashStyle = DashStyle.SOLID;
    private Integer layer = null;
    public Boolean fillColorIsDrawColor = false; //If true, fillColor is always overriden by drawColor

    //Styles used for specified objects
    //Point
    public DotSyle dotStyle = DotSyle.CIRCLE;
    private MODrawProperties mpBackup;
    public StrokeLineCap linecap = StrokeLineCap.ROUND;

    public MODrawProperties() {
        drawColor = new JMColor(1, 1, 1, 1);
        fillColor = new JMColor(0, 0, 0, 0);
    }

    /**
     * Generates a MODrawProperties instance with all its values null. It is
     * useful if you want to interpolate only some values. By default, null
     * values are not interpolated or copied.
     *
     * @return
     */
    public static MODrawProperties makeNullValues() {
        MODrawProperties nullMP = new MODrawProperties();
        nullMP.drawColor = null;
        nullMP.fillColor = null;
        nullMP.thickness = null;
        nullMP.absoluteThickness = null;
        nullMP.dashStyle = null;
        nullMP.absoluteThickness = null;
        nullMP.layer = null;
        nullMP.absoluteThickness = null;
        nullMP.dotStyle = null;
        nullMP.linecap = null;
        nullMP.fillColorIsDrawColor = null;
        return nullMP;
    }

    /**
     * Absorb all non-null properties of a given properties class
     *
     * @param prop
     */
    public void copyFrom(MODrawProperties prop) {
        if (prop == null) {//Nothing to do here!
            return;
        }
        if (prop.drawColor != null) {
            drawColor = prop.drawColor.copy();
        }
        if (prop.fillColor != null) {
            fillColor = prop.fillColor.copy();
        }
        thickness = (prop.thickness == null ? thickness : prop.thickness);
        dashStyle = (prop.dashStyle == null ? dashStyle : prop.dashStyle);
        absoluteThickness = (prop.absoluteThickness == null ? absoluteThickness : prop.absoluteThickness);
        dotStyle = (prop.dotStyle == null ? dotStyle : prop.dotStyle);
        layer = (prop.layer == null ? layer : prop.layer);
        linecap = (prop.linecap == null ? linecap : prop.linecap);
        fillColorIsDrawColor = (prop.fillColorIsDrawColor == null ? fillColorIsDrawColor : prop.fillColorIsDrawColor);
    }

    /**
     * Copy attributes from the given {@link MODrawProperties} object Null
     * values are copied also
     *
     * @param mp The object to copy attributes from.
     */
    public void rawCopyFrom(MODrawProperties mp) {
        drawColor.copyFrom(mp.drawColor);
        fillColor.copyFrom(mp.fillColor);
        thickness = mp.thickness;
        dashStyle = mp.dashStyle;
        absoluteThickness = mp.absoluteThickness;
        dashStyle = mp.dashStyle;
        layer = mp.layer;
        dotStyle = mp.dotStyle;
        linecap = mp.linecap;
        fillColorIsDrawColor = mp.fillColorIsDrawColor;
    }

    /**
     * Interpolate values from another MathObjecDrawingProperties. Only
     * drawColor, fillColor and thickness are actually interpolated
     *
     * @param a base drawing parameters
     * @param b Destination drawing parameters
     * @param alpha Interpolation parameter
     */
    public void interpolateFrom(MODrawProperties a, MODrawProperties b, double alpha) {
        if (alpha == 1)//in this case, copy directly all non-null attributes, including non-interpolable
        {
            this.copyFrom(b);
            return;
        }
        //If not, interpolate drawColor, fillColor and thickness (if they are not null)
        //Interpolate colors
        if (b.drawColor != null) {
            drawColor.copyFrom(a.drawColor.interpolate(b.drawColor, alpha));
        }
        if (b.fillColor != null) {
            fillColor.copyFrom(a.fillColor.interpolate(b.fillColor, alpha));
        }
        if (b.thickness != null) {
            this.thickness = (1 - alpha) * a.thickness + alpha * b.thickness;
        }

    }

    public void setRandomDrawColor() {
        drawColor.copyFrom(JMColor.random());
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

    /**
     * Returns a copy of this object. All objects are raw-copied.
     *
     * @return A raw copy of the object.
     */
    public MODrawProperties copy() {
        MODrawProperties resul = new MODrawProperties();
        resul.copyFrom(this);
        return resul;
    }

    /**
     * Returns Dash Style, from its name, using reflection. Used when loading
     * config files.
     *
     * @param textContent Name of the dash patterns
     * @return The dash style
     */
    static DashStyle parseDashStyle(String str) {
        DashStyle resul = DashStyle.SOLID; //default dash
        try {
            resul = DashStyle.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            JMathAnimScene.logger.warn("Dash pattern {} not recognized, using default {}", str, resul);
        }

        return resul;
    }

    static DotSyle parseDotStyle(String str) {
        DotSyle resul = DotSyle.CIRCLE; //default dash
        try {
            resul = DotSyle.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            JMathAnimScene.logger.warn("Dot style {} not recognized, using default {}", str, resul);
        }
        return resul;
    }

    /**
     * Load attributes from given style. If such style doesn't exist, no changes
     * are done, and a warning log is showed.
     *
     * @param name The name of the style
     */
    public void loadFromStyle(String name) {
        name=name.toUpperCase();
        HashMap<String, MODrawProperties> styles = JMathAnimConfig.getConfig().getStyles();
        if (styles.containsKey(name)) {
            this.copyFrom(styles.get(name));
        } else {
            JMathAnimScene.logger.warn("No style with name {} found", name);
        }
    }

    /**
     * Returns a new {@link MODrawProperties} created from the current style. If
     * no such style exists, a default MathObjectDrawingProperties is created.
     *
     * @param name Style name
     * @return A new {@link MODrawProperties} object created from the current
     * class.
     */
    public static MODrawProperties createFromStyle(String name) {
        MODrawProperties resul = MODrawProperties.makeNullValues();
        resul.loadFromStyle(name);
        return resul;
    }

    public Integer getLayer() {
        if (layer == null) {//If null, sets default value 0
            layer = 0;
        }
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    @Override
    public void saveState() {
        this.mpBackup = this.copy();
    }

    @Override
    public void restoreState() {
        this.copyFrom(this.mpBackup);
    }

    public void setDrawColor(JMColor drawColor) {
        if (drawColor != null) {
            this.drawColor = drawColor.copy();
        }
    }

    public void setFillColor(JMColor fillColor) {
        if (fillColor != null) {
            this.fillColor = fillColor.copy();
        }
    }

    public void setFillColorIsDrawColor(Boolean fillColorIsDrawColor) {
        this.fillColorIsDrawColor = fillColorIsDrawColor;
    }

    public Boolean isFillColorIsDrawColor() {
        return fillColorIsDrawColor;
    }

}
