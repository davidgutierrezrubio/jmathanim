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

import com.jmathanim.Enum.DashStyle;
import com.jmathanim.Enum.DotStyle;
import com.jmathanim.Enum.StrokeLineCap;
import com.jmathanim.Enum.StrokeLineJoin;
import com.jmathanim.MathObjects.AbstractVersioned;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;

import java.util.HashMap;
import java.util.Objects;

/**
 * This class stores all drawing properties of a MathObject like color, thickness, alpha, etc.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MODrawProperties extends AbstractVersioned implements DrawStyleProperties {

    // When added a new property here, remember to include it in rawCopyFrom and
    // copyFrom
    private Boolean faceToCamera = false;
    private Vec faceToCameraPivot = Vec.to(0, 0);
    // If false, thickness is computed to be a percentage of the width
    // to ensure zoom or resolution doesn't affect the result
    private Boolean absoluteThickness = false;
    private StrokeLineJoin linejoin=StrokeLineJoin.ROUND;
    private MathObject<?> parent;
    private Boolean visible = true;
    private DashStyle dashStyle = DashStyle.SOLID;
    // Styles used for specified objects
    // Point
    private DotStyle dotStyle = DotStyle.CIRCLE;
    private PaintStyle<?> drawColor;
    private PaintStyle<?> fillColor;
    private Integer layer = null;
    private StrokeLineCap linecap = StrokeLineCap.ROUND;
    private Double thickness = 1d;
    private boolean hasBeenChanged;

    public MODrawProperties() {
        drawColor = new JMColor(1, 1, 1, 1);
        fillColor = new JMColor(0, 0, 0, 0);
        addDependency(drawColor);
        addDependency(fillColor);
        faceToCamera = false;
        setVisible(true);
    }

    /**
     * Returns a new {@link MODrawProperties} created from the current style. If no such style exists, a default
     * MathObjectDrawingProperties is created.
     *
     * @param name Style name
     * @return A new {@link MODrawProperties} object created from the current class.
     */
    public static MODrawProperties createFromStyle(String name) {
        MODrawProperties resul = MODrawProperties.makeNullValues();
        resul.loadFromStyle(name);
        return resul;
    }

    /**
     * Generates a MODrawProperties instance with all its values null. It is useful if you want to interpolate only some
     * values. By default, null values are not interpolated or copied.
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
        nullMP.linejoin = null;
        nullMP.faceToCamera = null;
        nullMP.visible = null;
        return nullMP;
    }

    /**
     * Returns Dash Style, from its name, using reflection. Used when loading config files.
     *
     * @param dashPatternString Name of the dash patterns
     * @return The dash style
     */
    static DashStyle parseDashStyle(String dashPatternString) {
        DashStyle resul = DashStyle.SOLID; // default dash
        try {
            resul = DashStyle.valueOf(dashPatternString.toUpperCase());
        } catch (IllegalArgumentException e) {
            JMathAnimScene.logger.error("Dash pattern "+ LogUtils.method(dashPatternString)+" not recognized, using default "+LogUtils.method(resul.name()));
        }

        return resul;
    }

    static DotStyle parseDotStyle(String str) {
        DotStyle resul = DotStyle.CIRCLE; // default dash
        try {
            resul = DotStyle.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            JMathAnimScene.logger.error("Dot style "+ LogUtils.method(str)+" not recognized, using default "+LogUtils.method(resul.name()));
        }
        return resul;
    }

    /**
     * Returns a new MODrawProperties object with all values null except for those that have in common A and B
     *
     * @param A First MODrawProperties object to intersect
     * @param B Second MODrawProperties object to intersect
     * @return The intersection of both objects
     */
    public static MODrawProperties intersect(MODrawProperties A, MODrawProperties B) {
        MODrawProperties intersect = MODrawProperties.makeNullValues();
        if (A.getDrawColor().equals(B.getDrawColor())) {
            intersect.getDrawColor().copyFrom(A.getDrawColor());
        }
        if (A.getFillColor().equals(B.getFillColor())) {
            intersect.getFillColor().copyFrom(A.getFillColor());
        }
        intersect.thickness = (Objects.equals(A.thickness, B.thickness) ? A.thickness : intersect.thickness);
        intersect.dashStyle = (Objects.equals(A.dashStyle, B.dashStyle) ? A.dashStyle : intersect.dashStyle);
        intersect.absoluteThickness = (Objects.equals(A.absoluteThickness, B.absoluteThickness) ? A.absoluteThickness : intersect.absoluteThickness);
        intersect.dotStyle = (Objects.equals(A.dotStyle, B.dotStyle) ? A.dotStyle : intersect.dotStyle);
        intersect.layer = (Objects.equals(A.layer, B.layer) ? A.layer : intersect.layer);
        intersect.linecap = (Objects.equals(A.linecap, B.linecap) ? A.linecap : intersect.linecap);
        intersect.visible = (Objects.equals(A.visible, B.visible) ? A.visible : intersect.visible);
        intersect.faceToCamera = (Objects.equals(A.faceToCamera, B.faceToCamera) ? A.faceToCamera : intersect.faceToCamera);
        intersect.faceToCameraPivot = (Objects.equals(A.faceToCameraPivot, B.faceToCameraPivot) ? A.faceToCameraPivot : intersect.faceToCameraPivot);
        return intersect;
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
    public void copyFrom(DrawStyleProperties prop) {
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
        linecap = (prop.getLineCap() == null ? linecap : prop.getLineCap());
        linejoin = (prop.getLineJoin() == null ? linejoin : prop.getLineJoin());
        visible = (prop.isVisible() == null ? visible : prop.isVisible());
        faceToCamera = (prop.isFaceToCamera() == null ? faceToCamera : prop.isFaceToCamera());
        faceToCameraPivot = (prop.getFaceToCameraPivot() == null ? faceToCameraPivot : prop.getFaceToCameraPivot());
        changeVersion();
    }

    public void setParent(MathObject<?> parent) {
        this.parent = parent;
    }

    /**
     * Copy attributes from the given {@link MODrawProperties} object Null values are copied also
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
        linejoin = mp.linejoin;
        visible = mp.visible;
        faceToCamera = mp.faceToCamera;
        faceToCameraPivot = mp.faceToCameraPivot;
       changeVersion();
    }

    @Override
    public DrawStyleProperties setAbsoluteThickness(Boolean absThickness) {
        this.absoluteThickness = absThickness;
       changeVersion();
        return this;
    }

    @Override
    public DashStyle getDashStyle() {
        return dashStyle;
    }

    @Override
    public DrawStyleProperties setDashStyle(DashStyle dashStyle) {
        this.dashStyle = dashStyle;
       changeVersion();
        return this;
    }

    @Override
    public DotStyle getDotStyle() {
        return dotStyle;
    }

    @Override
    public DrawStyleProperties setDotStyle(DotStyle dotStyle) {
        this.dotStyle = dotStyle;
       changeVersion();
        return this;
    }

    @Override
    public DrawStyleProperties setDrawAlpha(double alpha) {
        this.drawColor.setAlpha(alpha);
       changeVersion();
        return this;
    }

    @Override
    public PaintStyle getDrawColor() {
        return drawColor;
    }

    @Override
    public DrawStyleProperties setDrawColor(PaintStyle drawColor) {
        if (drawColor == null) {
            return this;
        }
        if (this.drawColor != drawColor) {
            this.drawColor = drawColor;
//            if (parent != null) {
//                parent.on_setDrawColor(this.drawColor);
//            }
           changeVersion();
        }

        return this;
    }

    @Override
    public DrawStyleProperties multDrawAlpha(double mult) {
        if (getDrawColor() != null)
            setDrawAlpha(getDrawColor().getAlpha() * mult);
        return this;
    }

    @Override
    public DrawStyleProperties multFillAlpha(double mult) {
        if (getFillColor() != null)
            setFillAlpha(getFillColor().getAlpha() * mult);
        return this;
    }

    @Override
    public DrawStyleProperties setFillAlpha(double alpha) {
        if (this.fillColor.getAlpha() != alpha) {
            this.fillColor.setAlpha(alpha);
           changeVersion();
        }

        return this;
    }

    @Override
    public PaintStyle getFillColor() {
        return fillColor;
    }

    @Override
    public DrawStyleProperties setFillColor(PaintStyle fillColor) {
        if (fillColor == null) {
            return this;
        }
        if (this.fillColor != fillColor) {
            this.fillColor = fillColor;
//            if (parent != null) {
//                parent.on_setFillColor(this.fillColor);
//            }
          changeVersion();
        }

        return this;
    }

    @Override
    public MODrawProperties getFirstMP() {
        return this;
    }
//
//    @Override
//    public boolean hasBeenChanged() {
//        return hasBeenChanged;
//    }
//
//    @Override
//    public void setHasBeenChanged(boolean hasBeenChanged) {
//        this.hasBeenChanged = hasBeenChanged;
//    }


    @Override
    public Integer getLayer() {
        if (layer == null) {// If null, sets default value 0
            layer = 0;
        }
        return layer;
    }

    @Override
    public DrawStyleProperties setLayer(int layer) {
        this.layer = layer;
       changeVersion();
        return this;

    }

    @Override
    public StrokeLineCap getLineCap() {
        return this.linecap;
    }

    @Override
    public StrokeLineJoin getLineJoin() {
        return this.linejoin;
    }

    public DrawStyleProperties setLineJoin(StrokeLineJoin linejoin) {
        if (linejoin == null) {
           changeVersion();;
        }
        if (this.linejoin != linejoin) {
            this.linejoin = linejoin;
//            if (parent != null) {
//                parent.on_setLineJoin(this.linejoin);
//            }
        }
       changeVersion();
        return this;
    }

    @Override
    public DrawStyleProperties setLinecap(StrokeLineCap linecap) {
        if (linecap == null) {
           changeVersion();;
        }
        if (this.linecap != linecap) {
            this.linecap = linecap;
//            if (parent != null) {
//                parent.on_setLineCap(this.linecap);
//            }
        }
       changeVersion();
        return this;
    }

    @Override
    public Double getThickness() {
        return thickness;
    }

    @Override
    public DrawStyleProperties setThickness(Double thickness) {
        if (!Objects.equals(this.thickness, thickness)) {
            this.thickness = thickness;
//            if (parent != null) {
//                parent.on_setThickness(thickness);
//            }
        }
       changeVersion();
        return this;
    }

    @Override
    public DrawStyleProperties setVisible(Boolean visible) {
        if (!Objects.equals(this.visible, visible)) {
            this.visible = visible;
//            if (parent != null) {
//                parent.on_setVisible(visible);
//            }
        }
       changeVersion();
        return this;
    }

    @Override
    public void interpolateFrom(DrawStyleProperties a, DrawStyleProperties b, double alpha) {
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
//            drawColor = a.getDrawColor().interpolate(b.getDrawColor(), alpha);
            drawColor = PaintStyle.interpolatePaintStyle(a.getDrawColor(), b.getDrawColor(), alpha);
        }
        if (b.getFillColor() != null) {
//            fillColor = a.getFillColor().interpolate(b.getFillColor(), alpha);
            fillColor = PaintStyle.interpolatePaintStyle(a.getFillColor(), b.getFillColor(), alpha);
        }
        if (b.getThickness() != null) {
            this.thickness = (1 - alpha) * a.getThickness() + alpha * b.getThickness();
        }
       changeVersion();
    }

    @Override
    public void interpolateFrom(DrawStyleProperties dst, double alpha) {
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
    public Boolean isVisible() {
        return visible;
    }

    /**
     * Load attributes from given style. If such style doesn't exist, no changes are done, and a warning log is showed.
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
            JMathAnimScene.logger.warn("No style with name "+LogUtils.method(name)+" found");
        }
       changeVersion();
    }

    @Override
    public DrawStyleProperties multThickness(double multT) {
        if (getThickness() != null)
            setThickness(getThickness() * multT);
       changeVersion();
        return this;
    }


    @Override
    public Boolean isFaceToCamera() {
        return faceToCamera;
    }

    @Override
    public DrawStyleProperties setFaceToCamera(Boolean faceToCamera) {
        this.faceToCamera = faceToCamera;
       changeVersion();
        return this;
    }

    @Override
    public Vec getFaceToCameraPivot() {
        return faceToCameraPivot;
    }

    @Override
    public DrawStyleProperties setFaceToCameraPivot(Vec pivot) {
        this.faceToCameraPivot = pivot;
       changeVersion();
        return this;
    }

    @Override
    public DrawStyleProperties getMp() {
        return this;
    }


    @Override
    public void performMathObjectUpdateActions(JMathAnimScene scene) {

    }

    @Override
    public void performUpdateBoundingBox(JMathAnimScene scene) {

    }

    @Override
    protected boolean applyUpdaters(JMathAnimScene scene) {
        return false;//TODO: May implement some updaters here
    }
}
