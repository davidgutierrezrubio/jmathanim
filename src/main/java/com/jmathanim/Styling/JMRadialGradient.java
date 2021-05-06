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

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.JavaFXRenderer;
import com.jmathanim.mathobjects.Point;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMRadialGradient extends PaintStyle {

    protected Point center;
    protected double focusAngle;
    protected double focusDistance;
    protected double radius;

    protected GradientStop stops;
    protected boolean relativeToShape;
    protected CycleMethod cycleMethod;

    public JMRadialGradient(Point center, double radius) {
        this(center, 0, 0, radius);
    }

    public JMRadialGradient(Point center, double focusAngle, double focusDistance, double radius) {
        super();
        this.center = center;
        this.focusAngle = focusAngle;
        this.focusDistance = focusDistance;
        this.radius = radius;

        this.stops = new GradientStop();
        relativeToShape = false;
        cycleMethod = CycleMethod.NO_CYCLE;
    }

    @Override
    public JMRadialGradient copy() {
        JMRadialGradient resul = new JMRadialGradient(center.copy(), focusAngle, focusDistance, radius);
        resul.relativeToShape = this.relativeToShape;
        resul.stops = this.stops.copy();
        resul.cycleMethod = this.cycleMethod;
        resul.setAlpha(this.getAlpha());
        return resul;
    }

    @Override
    public Paint getFXPaint(JavaFXRenderer r, Camera cam) {
        double[] cc;
        double realRadius;
        if (!relativeToShape) {
            cc = cam.mathToScreenFX(center.v);
            realRadius = cam.mathToScreen(this.radius);
        } else {
            cc = new double[]{center.v.x, 1 - center.v.y};
            realRadius = this.radius;
        }

        return new RadialGradient(focusAngle, focusDistance, cc[0], cc[1], realRadius, relativeToShape, cycleMethod, stops.toFXStop(getAlpha()));
//                cc[0], c[1], ee[0], ee[1], relativeToShape, this.cycleMethod, stops.toFXStop(alpha));
    }

    @Override
    public PaintStyle interpolate(PaintStyle p, double t) {
        if (p instanceof JMColor) {
            JMColor pc = (JMColor) p;
            JMRadialGradient resul = this.copy();
            GradientStop interStops = resul.getStops();
            for (double tt : interStops.getColorHashMap().keySet()) {
                JMColor col = interStops.getColorHashMap().get(tt);
                interStops.add(tt, (JMColor) col.interpolate(pc, t));
            }
            resul.setAlpha((1 - t) * resul.getAlpha() + t * pc.getAlpha());
            return resul;
        }
        if (p instanceof JMRadialGradient) {
            JMRadialGradient rp = (JMRadialGradient) p;
            //I need the 2 linear gradients to have same cycle method and relative flat to interpolate. If not, do nothing.
            if ((rp.cycleMethod == this.cycleMethod) && (rp.relativeToShape == this.relativeToShape)) {
                JMRadialGradient resul = this.copy();
                for (double tt : rp.stops.getColorHashMap().keySet()) {
                    resul.stops.addInterpolatedColor(tt);
                }
                for (double tt : resul.stops.getColorHashMap().keySet()) {
                    rp.stops.addInterpolatedColor(tt);
                }

                for (double tt : resul.stops.getColorHashMap().keySet()) {
                    JMColor colA = resul.stops.getColorHashMap().get(tt);
                    JMColor colB = rp.stops.getColorHashMap().get(tt);
                    resul.stops.add(tt, (JMColor) colA.interpolate(colB, t));

                }
                resul.center = resul.center.interpolate(rp.center, t);
                resul.focusAngle = (1 - t) * resul.focusAngle + t * rp.focusAngle;
                resul.focusDistance = (1 - t) * resul.focusDistance + t * rp.focusDistance;
                resul.radius = (1 - t) * resul.radius + t * rp.radius;
                resul.setAlpha((1 - t) * resul.getAlpha() + t * rp.getAlpha());
                return resul;
            }

        }

        return this.copy();//Do nothing, return a copy of same object
    }

    /**
     * Retusn the relative to shape flag
     *
     * @return If true, center, radius and focusDistance are computed relative
     * to the bounding box of the shape to paint. If false, are computed in math
     * coordinates.
     */
    public boolean isRelativeToShape() {
        return relativeToShape;
    }

    /**
     * Sets the relative to shape flag. If true, center, radius and
     * focusDistance are computed relative to the bounding box of the shape to
     * paint.If false, are computed in math coordinates.
     *
     * @param relativeToShape Relative flag, a boolean value
     * @return This object
     */
    public JMRadialGradient setRelativeToShape(boolean relativeToShape) {
        this.relativeToShape = relativeToShape;
        return this;
    }

    /**
     * Gets the color stops for this gradient
     *
     * @return A GradientStop instance with the colors
     */
    public GradientStop getStops() {
        return stops;
    }

    /**
     * Overloaded method. Adds a color mark for this gradient at specified
     * parameter.
     *
     * @param t Position to add the gradient, from 0 to 1
     * @param strCol A string with a valid color declaration to parse.
     * @return This object
     */
    public JMRadialGradient add(double t, String strCol) {
        stops.add(t, JMColor.parse(strCol));
        return this;
    }

    /**
     * Adds a color mark for this gradient at specified parameter.
     *
     * @param t Position to add the gradient, from 0 to 1
     * @param col JMColor to add
     * @return This object
     */
    public JMRadialGradient add(double t, JMColor col) {
        stops.add(t, col);
        return this;
    }

    /**
     * Removes the color from the specified position
     *
     * @param t Position to remove the color, from 0 to 1.
     * @return This object
     */
    public JMRadialGradient remove(double t) {
        stops.remove(t);
        return this;
    }

    /**
     * Returns the currently cycle method, a JavaFX cycle for gradients
     *
     * @return A cycle method, a value from the enum
     * javafx.scene.paint.CycleMethod
     */
    public CycleMethod getCycleMethod() {
        return cycleMethod;
    }

    /**
     * Sets the cycle method for this gradient. The default cycle method is
     * NO_CYCLE.
     *
     * @param cycleMethod
     * @return A cycle method, a value from the enum
     * javafx.scene.paint.CycleMethod
     */
    public JMRadialGradient setCycleMethod(CycleMethod cycleMethod) {
        this.cycleMethod = cycleMethod;
        return this;
    }

}
