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
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMRadialGradient implements PaintStyle {

    private double alpha;

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
        this.center = center;
        this.focusAngle = focusAngle;
        this.focusDistance = focusDistance;
        this.radius = radius;

        this.stops = new GradientStop();
        relativeToShape = false;
        cycleMethod = CycleMethod.NO_CYCLE;
        alpha = 1;//Default alpha
    }

    @Override
    public JMRadialGradient copy() {
        JMRadialGradient resul = new JMRadialGradient(center.copy(), focusAngle, focusDistance, radius);
        resul.relativeToShape = this.relativeToShape;
        resul.stops = this.stops.copy();
        resul.cycleMethod = this.cycleMethod;
        return resul;
    }

    @Override
    public double getAlpha() {
        return alpha;//Change this!
    }

    @Override
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public Paint getFXPaint(JavaFXRenderer r, Camera cam) {
        double[] cc;
        double realRadius;
        if (!relativeToShape) {
            cc = cam.mathToScreenFX(center.v);
            realRadius=cam.mathToScreen(this.radius);
        } else {
            cc = new double[]{center.v.x, 1 - center.v.y};
            realRadius=this.radius;
        }
        
        return new RadialGradient(focusAngle, focusDistance, cc[0], cc[1], realRadius, relativeToShape, cycleMethod, stops.toFXStop(alpha));
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
             resul.alpha = (1 - t) * resul.alpha + t * pc.getAlpha();
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
                resul.alpha = (1 - t) * resul.alpha + t * rp.alpha;
                return resul;
            }

        }

        return this.copy();//Do nothing, return a copy of same object
    }

    public boolean isRelativeToShape() {
        return relativeToShape;
    }

    public <T extends JMRadialGradient> T setRelativeToShape(boolean relativeToShape) {
        this.relativeToShape = relativeToShape;
        return (T) this;
    }

    public GradientStop getStops() {
        return stops;
    }

    public <T extends JMRadialGradient> T add(double t, String strCol) {
        stops.add(t, JMColor.parse(strCol));
        return (T) this;
    }

    public <T extends JMRadialGradient> T add(double t, JMColor col) {
        stops.add(t, col);
        return (T) this;
    }

    public <T extends JMRadialGradient> T remove(double t) {
        stops.remove(t);
        return (T) this;
    }

    public CycleMethod getCycleMethod() {
        return cycleMethod;
    }

    public <T extends JMRadialGradient> T setCycleMethod(CycleMethod cycleMethod) {
        this.cycleMethod = cycleMethod;
        return (T) this;
    }

}
