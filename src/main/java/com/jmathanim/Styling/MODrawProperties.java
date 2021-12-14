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
import com.jmathanim.Utils.Vec;
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
public class MODrawProperties implements Stylable, Stateable {

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
        nullMP.faceToCamera = null;
        return nullMP;
    }

    /**
     * Returns Dash Style, from its name, using reflection. Used when loading
     * config files.
     *
     * @param textContent Name of the dash patterns
     * @return The dash style
     */
    static DashStyle parseDashStyle(String str) {
        DashStyle resul = DashStyle.SOLID; // default dash
        try {
            resul = DashStyle.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            JMathAnimScene.logger.error("Dash pattern {} not recognized, using default {}", str, resul);
        }

        return resul;
    }

    static DotSyle parseDotStyle(String str) {
        DotSyle resul = DotSyle.CIRCLE; // default dash
        try {
            resul = DotSyle.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            JMathAnimScene.logger.error("Dot style {} not recognized, using default {}", str, resul);
        }
        return resul;
    }

    public static Color randomColor() {
        int r = ThreadLocalRandom.current().nextInt(0, 255 + 1);
        int g = ThreadLocalRandom.current().nextInt(0, 255 + 1);
        int b = ThreadLocalRandom.current().nextInt(0, 255 + 1);
        return new Color(r, g, b);
    }

    // When added a new property here, remember to include it in rawCopyFrom and
    // copyFrom
    public Boolean faceToCamera = false;
    public Vec faceToCameraPivot = Vec.to(0,0);

    // If false, thickness is computed to be a percentage of the width
    // to ensure zoom or resolution doesn't affect the result
    public Boolean absoluteThickness = true;

    public Boolean visible = true;
    public DashStyle dashStyle = DashStyle.SOLID;
    // Styles used for specified objects
    // Point
    public DotSyle dotStyle = DotSyle.CIRCLE;

    private PaintStyle drawColor;
    private PaintStyle fillColor;
    private Integer layer = null;
    public StrokeLineCap linecap = StrokeLineCap.ROUND;
    private MODrawProperties mpBackup;
    public Double thickness = 1d;

    public MODrawProperties() {
        drawColor = new JMColor(1, 1, 1, 1);
        fillColor = new JMColor(0, 0, 0, 0);
        faceToCamera = false;
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
     * Absorbs all non-null properties of a given properties class
     *
     * @param prop
     */
    @Override
    public void copyFrom(Stylable prop) {
        if (prop == null) {// Nothing to do here!
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
        visible = (prop.isVisible() == null ? visible : prop.isVisible());
        faceToCamera = (prop.isFaceToCamera() == null ? faceToCamera : prop.isFaceToCamera());
        faceToCameraPivot = (prop.getFaceToCameraPivot() == null ? faceToCameraPivot : prop.getFaceToCameraPivot());
    }

    @Override
    public void setAbsoluteThickness(Boolean absThickness) {
        this.absoluteThickness = absThickness;
    }

    @Override
    public DashStyle getDashStyle() {
        return dashStyle;
    }

    @Override
    public void setDashStyle(DashStyle dashStyle) {
        this.dashStyle = dashStyle;
    }

    @Override
    public DotSyle getDotStyle() {
        return dotStyle;
    }

    @Override
    public void setDotStyle(DotSyle dotStyle) {
        this.dotStyle = dotStyle;
    }

    @Override
    public void setDrawAlpha(double alpha) {
        this.drawColor.setAlpha(alpha);
    }

    @Override
    public PaintStyle getDrawColor() {
        return drawColor;
    }

    @Override
    public void setDrawColor(PaintStyle drawColor) {
        if (drawColor != null) {
            this.drawColor = drawColor.copy();
        }
    }

    @Override
    public void setFillAlpha(double alpha) {
        this.fillColor.setAlpha(alpha);
    }

    @Override
    public PaintStyle getFillColor() {
        return fillColor;
    }

    @Override
    public void setFillColor(PaintStyle fillColor) {
        if (fillColor != null) {
            this.fillColor = fillColor.copy();
        }
    }

    @Override
    public MODrawProperties getFirstMP() {
        return this;
    }

    @Override
    public Integer getLayer() {
        if (layer == null) {// If null, sets default value 0
            layer = 0;
        }
        return layer;
    }

    @Override
    public void setLayer(int layer) {
        this.layer = layer;
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
    public void setMultDrawAlpha(double alphaScale) {
        double newAlpha = getDrawColor().getAlpha() * alphaScale;
        newAlpha = (newAlpha > 1 ? 1 : newAlpha);
        newAlpha = (newAlpha < 0 ? 0 : newAlpha);
        this.setDrawAlpha(newAlpha);
    }

    @Override
    public void setMultFillAlpha(double alphaScale) {
        double newAlpha = getFillColor().getAlpha() * alphaScale;
        newAlpha = (newAlpha > 1 ? 1 : newAlpha);
        newAlpha = (newAlpha < 0 ? 0 : newAlpha);
        this.setFillAlpha(newAlpha);
    }

    @Override
    public Stylable getSubMP(int n) {
        return this;// Nothing sub here...
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
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @Override
    public void interpolateFrom(Stylable a, Stylable b, double alpha) {
        if (alpha == 1)// in this case, copy directly all non-null attributes, including
        // non-interpolable
        {
            this.copyFrom(b);
            return;
        }
        // If not, interpolate drawColor, fillColor and thickness (if they are
        // not null)
        // Interpolate colors
        if (b.getDrawColor() != null) {
            drawColor = a.getDrawColor().interpolate(b.getDrawColor(), alpha);
        }
        if (b.getFillColor() != null) {
            fillColor = a.getFillColor().interpolate(b.getFillColor(), alpha);
        }
        if (b.getThickness() != null) {
            this.thickness = (1 - alpha) * a.getThickness() + alpha * b.getThickness();
        }
    }

    @Override
    public void interpolateFrom(Stylable dst, double alpha) {
        interpolateFrom(this, dst, alpha);
    }

    @Override
    public Boolean isAbsoluteThickness() {
        return absoluteThickness;
    }

    public boolean isFilled() {
        return (this.fillColor.getAlpha() > 0);
    }

    @Override
    public void setFilled(boolean fill) {
        if (fill && fillColor.getAlpha() == 0) {
            setFillAlpha(1);
        }
        if (!fill) {
            setFillAlpha(0);
        }
    }

    @Override
    public Boolean isVisible() {
        return visible;
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

    @Override
    public void multThickness(double multT) {
        setThickness(getThickness() * multT);
    }

    /**
     * Copy attributes from the given {@link MODrawProperties} object Null
     * values are copied also
     *
     * @param mp The object to copy attributes from.
     */
    @Override
    public void rawCopyFrom(MODrawProperties mp) {
        drawColor = mp.drawColor.copy();
        fillColor = mp.fillColor.copy();
        thickness = mp.thickness;
        dashStyle = mp.dashStyle;
        absoluteThickness = mp.absoluteThickness;
        dashStyle = mp.dashStyle;
        layer = mp.layer;
        dotStyle = mp.dotStyle;
        linecap = mp.linecap;
        visible = mp.visible;
        faceToCamera = mp.faceToCamera;
        faceToCameraPivot = mp.faceToCameraPivot;
    }

    @Override
    public void restoreState() {
        this.copyFrom(this.mpBackup);
    }

    @Override
    public void saveState() {
        this.mpBackup = this.copy();
    }

    @Override
    public Boolean isFaceToCamera() {
        return faceToCamera;
    }

    @Override
    public void setFaceToCamera(Boolean faceToCamera) {
        this.faceToCamera = faceToCamera;
    }

    @Override
    public Vec getFaceToCameraPivot() {
        return faceToCameraPivot;
    }

    @Override
    public void setFaceToCameraPivot(Vec pivot) {
        this.faceToCameraPivot = pivot;
    }

    public enum DashStyle {
        SOLID, DASHED, DOTTED, DASHDOTTED
    }
}
