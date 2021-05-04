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
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMLinearGradient implements PaintStyle {

    private double alpha;

    protected Point start, end;
    protected GradientStop stops;
    protected boolean relativeToShape;
    protected CycleMethod cycleMethod;

    public JMLinearGradient(Point start, Point end) {
        this.start = start;
        this.end = end;
        this.stops = new GradientStop();
        relativeToShape = false;
        cycleMethod = CycleMethod.NO_CYCLE;
        alpha = 1;//Default alpha
    }

    @Override
    public JMLinearGradient copy() {
        JMLinearGradient resul = new JMLinearGradient(start.copy(), end.copy());
        resul.relativeToShape = this.relativeToShape;
//        resul.getStops().getColorHashMap().putAll(stops.getColorHashMap());
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
        double[] ss, ee;
        if (!relativeToShape) {
            ss = cam.mathToScreenFX(start.v);
            ee = cam.mathToScreenFX(end.v);
        } else {
            ss = new double[]{start.v.x, 1 - start.v.y};
            ee = new double[]{end.v.x, 1 - end.v.y};
        }
        return new LinearGradient(ss[0], ss[1], ee[0], ee[1], relativeToShape, this.cycleMethod, stops.toFXStop(alpha));
    }

    @Override
    public PaintStyle interpolate(PaintStyle p, double t) {
        if (p instanceof JMColor) {
            JMColor pc = (JMColor) p;
            JMLinearGradient resul = this.copy();
            GradientStop interStops = resul.getStops();
            for (double tt : interStops.getColorHashMap().keySet()) {
                JMColor col = interStops.getColorHashMap().get(tt);
                interStops.add(tt, (JMColor) col.interpolate(pc, t));
            }
            return resul;
        }
        if (p instanceof JMLinearGradient) {
            JMLinearGradient lp = (JMLinearGradient) p;
            //I need the 2 linear gradients to have same cycle method and relative flat to interpolate. If not, do nothing.
            if ((lp.cycleMethod == this.cycleMethod) && (lp.relativeToShape == this.relativeToShape)) {
                JMLinearGradient resul = this.copy();
                for (double tt : lp.stops.getColorHashMap().keySet()) {
                    resul.stops.addInterpolatedColor(tt);
                }
                for (double tt : resul.stops.getColorHashMap().keySet()) {
                    lp.stops.addInterpolatedColor(tt);
                }

                for (double tt : resul.stops.getColorHashMap().keySet()) {
                    JMColor colA = resul.stops.getColorHashMap().get(tt);
                    JMColor colB = lp.stops.getColorHashMap().get(tt);
                    resul.stops.add(tt, (JMColor) colA.interpolate(colB, t));

                }
                resul.start = resul.start.interpolate(lp.start, t);
                resul.end = resul.end.interpolate(lp.end, t);
                resul.alpha = t*resul.alpha+(1-t)*lp.alpha;
                return resul;
            }

        }

        return this.copy();//Do nothing, return a copy of same object
    }

    public boolean isRelativeToShape() {
        return relativeToShape;
    }

    public <T extends JMLinearGradient> T setRelativeToShape(boolean relativeToShape) {
        this.relativeToShape = relativeToShape;
        return (T) this;
    }

    public GradientStop getStops() {
        return stops;
    }

    public <T extends JMLinearGradient> T add(double t, String strCol) {
        stops.add(t, JMColor.parse(strCol));
        return (T) this;
    }

    public <T extends JMLinearGradient> T add(double t, JMColor col) {
        stops.add(t, col);
        return (T) this;
    }

    public <T extends JMLinearGradient> T remove(double t) {
        stops.remove(t);
        return (T) this;
    }

    public CycleMethod getCycleMethod() {
        return cycleMethod;
    }

    public <T extends JMLinearGradient> T setCycleMethod(CycleMethod cycleMethod) {
        this.cycleMethod = cycleMethod;
        return (T) this;
    }

}
