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
package com.jmathanim.Styling;

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
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
public class MODrawProperties implements Stylable, Stateable {

    @Override
    public JMColor getDrawColor() {
        return drawColor;
    }

    @Override
    public JMColor getFillColor() {
        return fillColor;
    }

    @Override
    public void setMultFillAlpha(double alphaScale) {
        double newAlpha = getFillColor().alpha * alphaScale;
        newAlpha = (newAlpha > 1 ? 1 : newAlpha);
        newAlpha = (newAlpha < 0 ? 0 : newAlpha);
        this.setFillAlpha(newAlpha);
    }

    @Override
    public void setMultDrawAlpha(double alphaScale) {
        double newAlpha = getDrawColor().alpha * alphaScale;
        newAlpha = (newAlpha > 1 ? 1 : newAlpha);
        newAlpha = (newAlpha < 0 ? 0 : newAlpha);
        this.setDrawAlpha(newAlpha);
    }

    @Override
    public StrokeLineCap getLinecap() {
        return this.linecap;
    }

    @Override
    public void setLinecap(StrokeLineCap linecap) {
        this.linecap = linecap;
    }

    @Override
    public Double getThickness() {
        return thickness;
    }

    @Override
    public void setThickness(Double thickness) {
        this.thickness = thickness;
    }

    @Override
    public void setDotStyle(DotSyle dotStyle) {
        this.dotStyle = dotStyle;
    }

    @Override
    public DotSyle getDotStyle() {
        return dotStyle;
    }

    @Override
    public void setDashStyle(DashStyle dashStyle) {
        this.dashStyle = dashStyle;
    }

    @Override
    public DashStyle getDashStyle() {
        return dashStyle;
    }

    @Override
    public Boolean isAbsoluteThickness() {
        return absoluteThickness;
    }

    @Override
    public void setAbsoluteThickness(Boolean absThickness) {
        this.absoluteThickness = absThickness;
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
    @Override
    public void copyFrom(Stylable prop) {
        if (prop == null) {//Nothing to do here!
            return;
        }
        if (prop.getDrawColor() != null) {
            drawColor = prop.getDrawColor().copy();
        }
        if (prop.getFillColor() != null) {
            fillColor = prop.getFillColor().copy();
        }
        thickness = (prop.getThickness() == null ? thickness : prop.getThickness());
        dashStyle = (prop.getDashStyle() == null ? dashStyle : prop.getDashStyle());
        absoluteThickness = (prop.isAbsoluteThickness() == null ? absoluteThickness : prop.isAbsoluteThickness());
        dotStyle = (prop.getDotStyle() == null ? dotStyle : prop.getDotStyle());
        layer = (prop.getLayer() == null ? layer : prop.getLayer());
        linecap = (prop.getLinecap() == null ? linecap : prop.getLinecap());
        fillColorIsDrawColor = (prop.isFillColorIsDrawColor() == null ? fillColorIsDrawColor : prop.isFillColorIsDrawColor());
    }

    /**
     * Copy attributes from the given {@link MODrawProperties} object Null
     * values are copied also
     *
     * @param mp The object to copy attributes from.
     */
    @Override
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
    @Override
    public void interpolateFrom(Stylable a, Stylable b, double alpha) {
        if (alpha == 1)//in this case, copy directly all non-null attributes, including non-interpolable
        {
            this.copyFrom(b);
            return;
        }
        //If not, getInterpolatedColor drawColor, fillColor and thickness (if they are not null)
        //Interpolate colors
        if (b.getDrawColor() != null) {
            drawColor.copyFrom(a.getDrawColor().getInterpolatedColor(b.getDrawColor(), alpha));
        }
        if (b.getFillColor() != null) {
            fillColor.copyFrom(a.getFillColor().getInterpolatedColor(b.getFillColor(), alpha));
        }
        if (b.getThickness() != null) {
            this.thickness = (1 - alpha) * a.getThickness() + alpha * b.getThickness();
        }

    }

    public static Color randomColor() {
        int r = ThreadLocalRandom.current().nextInt(0, 255 + 1);
        int g = ThreadLocalRandom.current().nextInt(0, 255 + 1);
        int b = ThreadLocalRandom.current().nextInt(0, 255 + 1);
        return new Color(r, g, b);
    }

    @Override
    public void setFillAlpha(double alpha) {
        this.fillColor.alpha = alpha;
    }

    @Override
    public void setDrawAlpha(double alpha) {
        this.drawColor.alpha = alpha;
    }

    public boolean isFilled() {
        return (this.fillColor.alpha > 0);
    }

    @Override
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
    @Override
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
    @Override
    public void loadFromStyle(String name) {
        name = name.toUpperCase();
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

    @Override
    public Integer getLayer() {
        if (layer == null) {//If null, sets default value 0
            layer = 0;
        }
        return layer;
    }

    @Override
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

    @Override
    public void setDrawColor(JMColor drawColor) {
        if (drawColor != null) {
            this.drawColor = drawColor.copy();
        }
    }

    @Override
    public void setFillColor(JMColor fillColor) {
        if (fillColor != null) {
            this.fillColor = fillColor.copy();
        }
    }

    @Override
    public void setFillColorIsDrawColor(Boolean fillColorIsDrawColor) {
        this.fillColorIsDrawColor = fillColorIsDrawColor;
    }

    public Boolean isFillColorIsDrawColor() {
        return fillColorIsDrawColor;
    }

    public Stylable getSubMP(int n) {
        return this;//Nothing sub here...
    }

    @Override
    public void multThickness(double multT) {
        setThickness(getThickness() * multT);
    }

    public MODrawProperties getFirstMP() {
        return this;
    }
}
