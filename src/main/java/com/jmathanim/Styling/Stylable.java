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

import com.jmathanim.mathobjects.Point;
import javafx.scene.shape.StrokeLineCap;

/**
 * Anything that can be changes its style. May be a single object or a
 * collection of them.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public interface Stylable {

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
    void copyFrom(Stylable prop);

    /**
     * Copy the interpolated values from two MathObjecDrawingProperties. Only
     * drawColor, fillColor and thickness are actually interpolated
     *
     * @param a base drawing parameters
     * @param b Destination drawing parameters
     * @param alpha Interpolation parameter
     */
    void interpolateFrom(Stylable a, Stylable b, double alpha);

    /**
     * Interpolate values with another MathObjecDrawingProperties. Only
     * drawColor, fillColor and thickness are actually interpolated
     *
     * @param dst Destination drawing parameters
     * @param alpha Interpolation parameter
     */
    void interpolateFrom(Stylable dst, double alpha);

    /**
     * Load attributes from given style. If such style doesn't exist, no changes
     * are done, and a warning log is showed.
     *
     * @param name The name of the style
     */
    void loadFromStyle(String name);

    /**
     * Copy attributes from the given {@link MODrawProperties} object Null
     * values are copied also
     *
     * @param mp The object to copy attributes from.
     */
    void rawCopyFrom(MODrawProperties mp);

    void restoreState();

    void saveState();

    void setDrawAlpha(double alpha);

    void setDrawColor(JMColor drawColor);

    void setFillAlpha(double alpha);

    void setMultFillAlpha(double alpha);

    void setMultDrawAlpha(double alpha);

    void setFillColor(JMColor fillColor);

    void setFillColorIsDrawColor(Boolean fillColorIsDrawColor);

    void setFilled(boolean fill);

    void setLayer(int layer);

    public Integer getLayer();

    public JMColor getDrawColor();

    public JMColor getFillColor();

    public StrokeLineCap getLinecap();

    public void setLinecap(StrokeLineCap linecap);

    public Double getThickness();

    public void setThickness(Double thickness);

    public void setDotStyle(Point.DotSyle dotStyle);

    public Point.DotSyle getDotStyle();

    public void setDashStyle(MODrawProperties.DashStyle dashStyle);

    public MODrawProperties.DashStyle getDashStyle();

    public Boolean isAbsoluteThickness();

    public void setAbsoluteThickness(Boolean absThickness);

    public void setVisible(Boolean absThickness);

    public Boolean isVisible();

    public Boolean isFillColorIsDrawColor();

    public Stylable getSubMP(int n);

    public void multThickness(double multT);

    public MODrawProperties getFirstMP();
}
