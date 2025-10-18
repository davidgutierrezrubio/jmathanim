/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
import com.jmathanim.Utils.Vec;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * The {@code Stylable} interface provides a contract for objects that possess styling properties such as colors,
 * transparency, thickness, and other graphical attributes. It also supports state management, interpolation, and
 * hierarchical styling through child sub-properties.
 */
public interface DrawStyleProperties extends Stylable {

    /**
     * Returns a copy of this object. All objects are raw-copied.
     *
     * @return A raw copy of the object.
     */
    MODrawProperties copy();

    /**
     * Absorb all non-null properties of a given properties class
     *
     * @param prop
     */
    void copyFrom(DrawStyleProperties prop);

    /**
     * Copy the interpolated values from two MathObjecDrawingProperties. Only drawColor, fillColor and thickness are
     * actually interpolated
     *
     * @param a     base drawing parameters
     * @param b     Destination drawing parameters
     * @param alpha Interpolation parameter
     */
    void interpolateFrom(DrawStyleProperties a, DrawStyleProperties b, double alpha);

    /**
     * Interpolate values with another MathObjecDrawingProperties. Only drawColor, fillColor and thickness are actually
     * interpolated
     *
     * @param dst   Destination drawing parameters
     * @param alpha Interpolation parameter
     */
    void interpolateFrom(DrawStyleProperties dst, double alpha);

    /**
     * Load attributes from given style. If such style doesn't exist, no changes are done, and a warning log is showed.
     *
     * @param name The name of the style
     */
    void loadFromStyle(String name);

    /**
     * Copy attributes from the given {@link MODrawProperties} object Null values are copied also
     *
     * @param mp The object to copy attributes from.
     */
    void rawCopyFrom(MODrawProperties mp);

    DrawStyleProperties setDrawAlpha(double alpha);

    DrawStyleProperties setFillAlpha(double alpha);

    Integer getLayer();

    DrawStyleProperties setLayer(int layer);

    PaintStyle getDrawColor();

    DrawStyleProperties setDrawColor(PaintStyle drawColor);

    PaintStyle getFillColor();

    DrawStyleProperties setFillColor(PaintStyle fillColor);

    StrokeLineCap getLineCap();

    StrokeLineJoin getLineJoin();

    DrawStyleProperties setLineJoin(StrokeLineJoin linejoin);

    DrawStyleProperties setLinecap(StrokeLineCap linecap);

    Double getThickness();

    DrawStyleProperties setThickness(Double thickness);

    DotStyle getDotStyle();

    DrawStyleProperties setDotStyle(DotStyle dotStyle);

    DashStyle getDashStyle();

    DrawStyleProperties setDashStyle(DashStyle dashStyle);

    Boolean isAbsoluteThickness();

    DrawStyleProperties setAbsoluteThickness(Boolean absThickness);

    DrawStyleProperties setVisible(Boolean absThickness);

    Boolean isVisible();

    Boolean isFaceToCamera();

    Vec getFaceToCameraPivot();

    DrawStyleProperties setFaceToCameraPivot(Vec pivot);

    DrawStyleProperties setFaceToCamera(Boolean faceToCamera);

//    T getSubMP(int n);

    /**
     * Multiplies the current thickness by given factor
     *
     * @param multT Factor
     * @return
     */
    DrawStyleProperties multThickness(double multT);

    /**
     * Multiplies the current draw alpha by given factor
     *
     * @param mult Factor
     * @return
     */
    DrawStyleProperties multDrawAlpha(double mult);

    /**
     * Multiplies the current fill alpha by given factor
     *
     * @param mult Factor
     * @return
     */
    DrawStyleProperties multFillAlpha(double mult);

    MODrawProperties getFirstMP();

    boolean hasBeenChanged();

    void setHasBeenChanged(boolean hasBeenChanged);

}
