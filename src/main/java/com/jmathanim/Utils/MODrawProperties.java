/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Stateable;
import java.awt.Color;
import java.lang.reflect.Field;
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
    public static final int SOLID = 1;
    public static final int DASHED = 2;
    public static final int DOTTED = 3;

    //When added a new property here, remember to include it in digestFrom and copyFrom
    public final JMColor drawColor;
    public final JMColor fillColor;
    public Double thickness = 1d;
    //If false, thickness is computed to be a percentage of the width
    //to ensure zoom or resolution doesn't affect the result
    public Boolean absoluteThickness = true;
    public Integer dashStyle = 1;
    private int layer = 0;
    public boolean castShadows = true;//If shadows, this object should cast them

    //Styles used for specified objects
    //Point
    public Integer dotStyle = Point.DOT_STYLE_CIRCLE;
    private MODrawProperties mpBackup;
    public StrokeLineCap linecap = StrokeLineCap.ROUND;

    public MODrawProperties() {
        drawColor = new JMColor(1, 1, 1, 1);
        fillColor = new JMColor(0, 0, 0, 0);
    }

    /**
     * Absorb all non-null properties of a given properties class
     *
     * @param prop
     */
    public void digestFrom(MODrawProperties prop) {
        if (prop == null) {//Nothing to do here!
            return;
        }
        drawColor.copyFrom(prop.drawColor);
        fillColor.copyFrom(prop.fillColor);
        thickness = (prop.thickness == null ? thickness : prop.thickness);
        dashStyle = (prop.dashStyle == null ? dashStyle : prop.dashStyle);
        absoluteThickness = (prop.absoluteThickness == null ? absoluteThickness : prop.absoluteThickness);
        dotStyle = (prop.dotStyle == null ? dotStyle : prop.dotStyle);
        castShadows = prop.castShadows;
        layer = prop.layer;
        linecap = prop.linecap;
    }

    /**
     * Copy attributes from the given {@link MODrawProperties} object
     *
     * @param mp The object to copy attributes from.
     */
    public void copyFrom(MODrawProperties mp) {
        drawColor.copyFrom(mp.drawColor);
        fillColor.copyFrom(mp.fillColor);
        thickness = mp.thickness;
        dashStyle = mp.dashStyle;
        absoluteThickness = mp.absoluteThickness;
        dashStyle = mp.dashStyle;
        layer = mp.layer;
        dotStyle = mp.dotStyle;
        castShadows = mp.castShadows;
        linecap=mp.linecap;
    }

    /**
     * Interpolate values from another MathObjecDrawingProperties
     *
     * @param a
     * @param b
     * @param t Interpolation parameter
     */
    public void interpolateFrom(MODrawProperties a, MODrawProperties b, double t) {
        //Interpolate colors
        drawColor.copyFrom(a.drawColor.interpolate(b.drawColor, t));
        fillColor.copyFrom(a.fillColor.interpolate(b.fillColor, t));

//        interpolateColor(a.fillColor, b.fillColor, t);
        this.thickness = (1 - t) * a.thickness + t * b.thickness;

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
    static Integer parseDashStyle(String str) {
        int resul = SOLID; //default dash
        try {
            Field field = MODrawProperties.class.getField(str.toUpperCase());
            resul = field.getInt(field);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            JMathAnimScene.logger.warn("Dash pattern {} not recognized ", str);
        }
        return resul;
    }

    static int parseDotStyle(String str) {
        int resul = Point.DOT_STYLE_CIRCLE; //default dash
        try {
            String styleName = str.toUpperCase();

            //Adds the suffix, if it doesn't include it already
            if (!styleName.contains("_")) {
                styleName = "DOT_STYLE_" + styleName;
            }

            Field field = Point.class.getField(styleName);
            resul = field.getInt(field);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            JMathAnimScene.logger.warn("Dash pattern {} not recognized ", str);
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
        HashMap<String, MODrawProperties> styles = JMathAnimConfig.getConfig().getStyles();
        if (styles.containsKey(name)) {
            this.digestFrom(styles.get(name));
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
        MODrawProperties resul = new MODrawProperties();
        resul.loadFromStyle(name);
        return resul;
    }

    public int getLayer() {
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

}
