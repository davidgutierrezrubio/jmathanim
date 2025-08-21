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
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point.DotSyle;
import com.jmathanim.mathobjects.Stateable;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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
        nullMP.linejoin = null;
        nullMP.faceToCamera = null;
        nullMP.visible = null;
        return nullMP;
    }

    /**
     * Returns Dash Style, from its name, using reflection. Used when loading
     * config files.
     *
     * @param dashPatternString Name of the dash patterns
     * @return The dash style
     */
    static DashStyle parseDashStyle(String dashPatternString) {
        DashStyle resul = DashStyle.SOLID; // default dash
        try {
            resul = DashStyle.valueOf(dashPatternString.toUpperCase());
        } catch (IllegalArgumentException e) {
            JMathAnimScene.logger.error("Dash pattern {} not recognized, using default {}", dashPatternString, resul);
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
    private Boolean faceToCamera = false;
    private Vec faceToCameraPivot = Vec.to(0, 0);

    // If false, thickness is computed to be a percentage of the width
    // to ensure zoom or resolution doesn't affect the result
    private Boolean absoluteThickness = true;
    private StrokeLineJoin linejoin;
    private MathObject parent;

    private Boolean visible = true;
    private DashStyle dashStyle = DashStyle.SOLID;
    // Styles used for specified objects
    // Point
    private DotSyle dotStyle = DotSyle.CIRCLE;

    private PaintStyle drawColor;
    private PaintStyle fillColor;
    private Integer layer = null;
    private StrokeLineCap linecap = StrokeLineCap.ROUND;
    private MODrawProperties mpBackup;
    private Double thickness = 1d;
    private Double scaleArrowHead1 = 1d;
    private Double scaleArrowHead2 = 1d;

    public MODrawProperties() {
        drawColor = new JMColor(1, 1, 1, 1);
        fillColor = new JMColor(0, 0, 0, 0);
        faceToCamera = false;
        setVisible(true);
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
        linecap = (prop.getLineCap() == null ? linecap : prop.getLineCap());
        linejoin = (prop.getLineJoin() == null ? linejoin : prop.getLineJoin());
        visible = (prop.isVisible() == null ? visible : prop.isVisible());
        faceToCamera = (prop.isFaceToCamera() == null ? faceToCamera : prop.isFaceToCamera());
        faceToCameraPivot = (prop.getFaceToCameraPivot() == null ? faceToCameraPivot : prop.getFaceToCameraPivot());
        scaleArrowHead1 = (prop.getScaleArrowHead1() == null ? scaleArrowHead1 : prop.getScaleArrowHead1());
        scaleArrowHead2 = (prop.getScaleArrowHead1() == null ? scaleArrowHead2 : prop.getScaleArrowHead2());

    }

    public void setParent(MathObject parent) {
        this.parent = parent;
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
        linejoin = mp.linejoin;
        visible = mp.visible;
        faceToCamera = mp.faceToCamera;
        faceToCameraPivot = mp.faceToCameraPivot;
        scaleArrowHead1 = mp.scaleArrowHead1;
        scaleArrowHead2 = mp.scaleArrowHead2;
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
        if (drawColor == null) {
            return;
        }
        if (this.drawColor != drawColor) {
            this.drawColor = drawColor.copy();
//            if (parent != null) {
//                parent.on_setDrawColor(this.drawColor);
//            }
        }
    }

    @Override
    public void multDrawAlpha(double mult) {
        setDrawAlpha(getDrawColor().getAlpha() * mult);
    }

    @Override
    public void multFillAlpha(double mult) {
        setFillAlpha(getFillColor().getAlpha() * mult);
    }

    @Override
    public void setFillAlpha(double alpha) {
        if (this.fillColor.getAlpha() != alpha) {
            this.fillColor.setAlpha(alpha);
//            if (parent != null) {
//                parent.on_setFillAlpha(alpha);
//            }
        }
    }

    @Override
    public PaintStyle getFillColor() {
        return fillColor;
    }

    @Override
    public void setFillColor(PaintStyle fillColor) {
        if (fillColor == null) {
            return;
        }
        if (this.fillColor != fillColor) {
            this.fillColor = fillColor.copy();
//            if (parent != null) {
//                parent.on_setFillColor(this.fillColor);
//            }
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
    public StrokeLineCap getLineCap() {
        return this.linecap;
    }

    @Override
    public StrokeLineJoin getLineJoin() {
        return this.linejoin;
    }

    @Override
    public void setLinecap(StrokeLineCap linecap) {
        if (linecap == null) {
            return;
        }
        if (this.linecap != linecap) {
            this.linecap = linecap;
//            if (parent != null) {
//                parent.on_setLineCap(this.linecap);
//            }
        }
    }

    public void setLineJoin(StrokeLineJoin linejoin) {
        if (linejoin == null) {
            return;
        }
        if (this.linejoin != linejoin) {
            this.linejoin = linejoin;
//            if (parent != null) {
//                parent.on_setLineJoin(this.linejoin);
//            }
        }
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
        if (!Objects.equals(this.thickness, thickness)) {
            this.thickness = thickness;
//            if (parent != null) {
//                parent.on_setThickness(thickness);
//            }
        }
    }

    @Override
    public void setVisible(Boolean visible) {
        if (!Objects.equals(this.visible, visible)) {
            this.visible = visible;
//            if (parent != null) {
//                parent.on_setVisible(visible);
//            }
        }
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

    @Override
    public void setScaleArrowHead1(Double scale) {
        this.scaleArrowHead1 = scale;
    }

    @Override
    public void setScaleArrowHead2(Double scale) {
        this.scaleArrowHead2 = scale;
    }

    @Override
    public Double getScaleArrowHead1() {
        return scaleArrowHead1;
    }

    @Override
    public Double getScaleArrowHead2() {
        return scaleArrowHead2;
    }



    /**
     * Returns a new MODrawProperties object with all values null except for
     * those that have in common A and B
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
        intersect.scaleArrowHead1 = (Objects.equals(A.scaleArrowHead1, B.scaleArrowHead1) ? A.scaleArrowHead1 : intersect.scaleArrowHead1);
        intersect.scaleArrowHead2 = (Objects.equals(A.scaleArrowHead2, B.scaleArrowHead2) ? A.scaleArrowHead2 : intersect.scaleArrowHead2);
        return intersect;
    }

}
