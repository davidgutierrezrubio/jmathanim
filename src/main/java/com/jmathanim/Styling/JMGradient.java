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
public class JMGradient implements PaintStyle {

    Point start, end;
    GradientStop stops;
    private boolean relativeToShape;
    CycleMethod cycleMethod;

    public JMGradient(Point start, Point end) {
        this.start = start;
        this.end = end;
        this.stops = new GradientStop();
        relativeToShape = false;
        cycleMethod = CycleMethod.NO_CYCLE;
    }

    @Override
    public PaintStyle copy() {
        JMGradient resul = new JMGradient(start.copy(), end.copy());
        resul.setRelativeToShape(relativeToShape);
        resul.getStops().getColorHashMap().putAll(stops.getColorHashMap());
        resul.setCycleMethod(cycleMethod);
        return resul;
    }

    @Override
    public double getAlpha() {
        return 1;//Change this!
    }

    @Override
    public void setAlpha(double alpha) {
        //Nothind to do here....yet!
    }

    @Override
    public Paint getFXPaint(JavaFXRenderer r, Camera cam) {
        double[] ss, ee;
        if (!relativeToShape) {
            ss = cam.mathToScreenFX(start.v);
            ee = cam.mathToScreenFX(end.v);
        } else {
            ss = new double[]{start.v.x, 1 - start.v.y};
            ee = new double[]{end.v.x, 1 - end.v.y};
        }
            return new LinearGradient(ss[0], ss[1], ee[0], ee[1], relativeToShape, CycleMethod.REFLECT, stops.toFXStop());
    }

    @Override
    public PaintStyle interpolate(PaintStyle p, double t) {
        return this.copy();//Don't implemented the interpolation yet
    }

    public boolean isRelativeToShape() {
        return relativeToShape;
    }

    public <T extends JMGradient> T setRelativeToShape(boolean relativeToShape) {
        this.relativeToShape = relativeToShape;
        return (T) this;
    }

    public GradientStop getStops() {
        return stops;
    }

    public <T extends JMGradient> T add(double t, String strCol) {
        stops.add(t, JMColor.parse(strCol));
        return (T) this;
    }

    public <T extends JMGradient> T add(double t, JMColor col) {
        stops.add(t, col);
        return (T) this;
    }

    public <T extends JMGradient> T remove(double t) {
        stops.remove(t);
        return (T) this;
    }

    public CycleMethod getCycleMethod() {
        return cycleMethod;
    }

    public <T extends JMGradient> T setCycleMethod(CycleMethod cycleMethod) {
        this.cycleMethod = cycleMethod;
        return (T) this;
    }

}
