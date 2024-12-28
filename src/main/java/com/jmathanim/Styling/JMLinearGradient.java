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
import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Point;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;

import java.util.Objects;

/**
 * A class that representas a linear gradient
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMLinearGradient extends PaintStyle {

    protected Point start, end;
    protected GradientStop stops;
    protected boolean relativeToShape;
    protected CycleMethod cycleMethod;

    /**
     * Creates a new linear gradient. Both points should be in math coordinates
     * if relativeShape flag is false or in relative coordinates (from 0 to 1
     * usually) otherwise.
     *
     * @param start Starting point
     * @param end Ending point
     */
    public JMLinearGradient(Point start, Point end) {
        super();
        this.start = start;
        this.end = end;
        this.stops = new GradientStop();
        relativeToShape = false;
        cycleMethod = CycleMethod.NO_CYCLE;
    }

    @Override
    public JMLinearGradient copy() {
        JMLinearGradient resul = new JMLinearGradient(start.copy(), end.copy());
        resul.copyFrom(this);
        return resul;
    }

    @Override
    public void copyFrom(PaintStyle A) {
        if (A == null) {
            return;
        }
        if (A instanceof JMLinearGradient) {
            JMLinearGradient jmlg = (JMLinearGradient) A;
            this.start.copyFrom(jmlg.start);
            this.end.copyFrom(jmlg.end);
            this.relativeToShape = jmlg.relativeToShape;
            this.cycleMethod = jmlg.cycleMethod;
            this.stops = jmlg.stops.copy();
            this.setAlpha(jmlg.getAlpha());
        }
        //Convert radial gradient into a linear one, horizontally
         if (A instanceof JMRadialGradient) {
            JMRadialGradient jmlg = (JMRadialGradient) A;
            this.start.copyFrom(jmlg.center);
            this.end.copyFrom(jmlg.center.add(Vec.to(jmlg.radius,0)));
            this.relativeToShape = jmlg.relativeToShape;
            this.cycleMethod = jmlg.cycleMethod;
            this.stops = jmlg.stops.copy();
            this.setAlpha(jmlg.getAlpha());
        }
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
        return new LinearGradient(ss[0], ss[1], ee[0], ee[1], relativeToShape, this.cycleMethod, stops.toFXStop(getAlpha()));
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
            resul.setAlpha((1 - t) * resul.getAlpha() + t * pc.getAlpha());
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
                resul.setAlpha((1 - t) * resul.getAlpha() + t * lp.getAlpha());
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.start);
        hash = 13 * hash + Objects.hashCode(this.end);
        hash = 13 * hash + Objects.hashCode(this.stops);
        hash = 13 * hash + (this.relativeToShape ? 1 : 0);
        hash = 13 * hash + Objects.hashCode(this.cycleMethod);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JMLinearGradient other = (JMLinearGradient) obj;
        if (this.relativeToShape != other.relativeToShape) {
            return false;
        }
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        if (!Objects.equals(this.end, other.end)) {
            return false;
        }
        if (!Objects.equals(this.stops, other.stops)) {
            return false;
        }
        return this.cycleMethod == other.cycleMethod;
    }

}
