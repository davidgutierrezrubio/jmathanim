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
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Axes extends MathObject {

    public Line xAxis, yAxis;
    private final ArrayList<Shape> xticks;
    private final ArrayList<Shape> yticks;
    private final ArrayList<MultiShapeObject> xticksLegend;
    private final ArrayList<MultiShapeObject> yticksLegend;
    public static final double INITIAL_TICK_LENGTH = .04;
    private double tickScale = 1;

    private static final double INITIAL_LEGEND_TICK_SCALE = .3;
    public double legendScale = 1;
    public static final double LEGEND_TICKS_GAP = .01;

    public Axes() {
        mp.loadFromStyle("axisdefault");
        xticks = new ArrayList<>();
        yticks = new ArrayList<>();
        xticksLegend = new ArrayList<>();
        yticksLegend = new ArrayList<>();
        xAxis = Line.make(Point.at(0, 0), Point.at(1, 0)).style("axisdefault");
        yAxis = Line.make(Point.at(0, 0), Point.at(0, 1)).style("axisdefault");
    }
   /**
     * Generates a set of pairs (ticks-legends) from start to finish (including)
     * with given step, in the x-axis
     *
     * @param start Starting number
     * @param finish Ending number
     * @param step Step
     */
    public void generateXTicks(double start, double finish, double step) {
        for (double x = start; x < finish; x += step) {
            if (x != 0) {
                addXTicksLegend(x);
            }
        }
    }

    /**
     * Generates a set of pairs (ticks-legends) from start to finish (including)
     * with given step, in the y-axis
     *
     * @param start Starting number
     * @param finish Ending number
     * @param step Step
     */
    public void generateYTicks(double start, double finish, double step) {
        for (double y = start; y < finish; y += step) {
            if (y != 0) {
                addYTicksLegend(y);
            }
        }
    }

    /**
     * Adds a pair (tick, legend text) at the given value in the y-axis. The
     * text is automatically generated from the value of y.
     *
     * @param y The y coordinate where to put the tick
     */
    public void addYTicksLegend(double y) {
        addYTicksLegend("$" + y + "$", y);
    }

    /**
     * Adds a pair (tick, legend text) at the given value in the y-axis, with
     * the specified latex string.
     *
     * @param latex The text with the legend
     * @param y The y coordinate where to put the tick
     */
    public void addYTicksLegend(String latex, double y) {
        final Shape ytick = Shape.segment(Point.at(-.5, y), Point.at(.5, y)).style("axistickdefault");;
        ytick.setAbsoluteSize(Anchor.Type.BY_CENTER);
        yticks.add(ytick);

        final LaTeXMathObject ytickLegend = LaTeXMathObject.make(latex).style("axislegenddefault");
        ytickLegend.stackTo(ytick, Anchor.Type.RIGHT, LEGEND_TICKS_GAP);
        ytickLegend.setAbsoluteSize(Anchor.Type.LEFT);
        yticksLegend.add(ytickLegend);
    }

    /**
     * Adds a pair (tick, legend text) at the given value in the x-axis. The
     * text is automatically generated from the value of x.
     *
     * @param x The x coordinate where to put the tick
     */
    public void addXTicksLegend(double x) {
        addXTicksLegend("$" + x + "$", x);
    }

    /**
     * Adds a pair (tick, legend text) at the given value in the y-axis, with
     * the specified latex string.
     *
     * @param latex The text with the legend
     * @param x The x coordinate where to put the tick
     */
    public void addXTicksLegend(String latex, double x) {
        final Shape xtick = Shape.segment(Point.at(x, -.5), Point.at(x, .5)).style("axistickdefault");
        xtick.setAbsoluteSize(Anchor.Type.BY_CENTER);
        xticks.add(xtick);
        final LaTeXMathObject xtickLegend = LaTeXMathObject.make(latex).style("axislegenddefault");
        xtickLegend.stackTo(xtick, Anchor.Type.LOWER, LEGEND_TICKS_GAP);
        xtickLegend.setAbsoluteSize(Anchor.Type.UPPER);
        xticksLegend.add(xtickLegend);
    }

    @Override
    public Point getCenter() {
        return Point.at(0, 0);
    }

    @Override
    public <T extends MathObject> T copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Rect getBoundingBox() {
        return JMathAnimConfig.getConfig().getCamera().getMathView();
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
    }

    @Override
    public void draw(Renderer r) {
        xAxis.draw(r);
        yAxis.draw(r);

        for (int n = 0; n < xticks.size(); n++) {
            Shape xt = xticks.get(n).copy().scale(INITIAL_TICK_LENGTH * tickScale);
            MultiShapeObject leg = xticksLegend.get(n).copy().scale(legendScale * INITIAL_LEGEND_TICK_SCALE);
            leg.stackTo(xt, Anchor.Type.LOWER, LEGEND_TICKS_GAP);
            xt.draw(r);
            leg.draw(r);
        }
        for (int n = 0; n < yticks.size(); n++) {
            Shape yt = yticks.get(n).copy().scale(INITIAL_TICK_LENGTH * tickScale);
            MultiShapeObject leg = yticksLegend.get(n).copy().scale(legendScale * INITIAL_LEGEND_TICK_SCALE);
            leg.stackTo(yt, Anchor.Type.LEFT, LEGEND_TICKS_GAP);
            yt.draw(r);
            leg.draw(r);
        }

    }

    @Override
    public void update(JMathAnimScene scene) {
        //TODO: Adding or removing ticks should be done here
    }

    /**
     * Returns a Line object representing the x-axis
     *
     * @return The x-axis
     */
    public Line getxAxis() {
        return xAxis;
    }

    /**
     * Returns a Line object representing the y-axis
     *
     * @return The y-axis
     */
    public Line getyAxis() {
        return yAxis;
    }

    /**
     * Returns the current legend scale
     *
     * @return The legend scale
     */
    public double getLegendScale() {
        return legendScale;
    }

    /**
     * Sets the scale of the text legends. Default value is 1.
     *
     * @param legendScale The legend scale
     */
    public void setLegendScale(double legendScale) {
        this.legendScale = legendScale;
    }

    @Override
    public <T extends MathObject> T style(String name) {
        super.style(name);
        applyMPToSubobjects();
        return (T) this;
    }

    @Override
    public <T extends MathObject> T layer(int layer) {
        super.layer(layer);
        applyMPToSubobjects();
        return (T) this;
    }

    @Override
    public <T extends MathObject> T drawAlpha(double alpha) {
        super.drawAlpha(alpha);
        applyMPToSubobjects();
        return (T) this;
    }

    @Override
    public <T extends MathObject> T fillColor(JMColor fc) {
        super.fillColor(fc);
        applyMPToSubobjects();
        return (T) this;
    }

    @Override
    public <T extends MathObject> T drawColor(JMColor dc) {
        super.drawColor(dc);
        applyMPToSubobjects();
        return (T) this;
    }

    private void applyMPToSubobjects() {
        xAxis.mp.copyFrom(mp);
        yAxis.mp.copyFrom(mp);
        for (Shape s : xticks) {
            s.mp.copyFrom(mp);
        }
        for (Shape s : yticks) {
            s.mp.copyFrom(mp);
        }
        for (MultiShapeObject s : xticksLegend) {
            for (Shape sh : s) {
                sh.mp.copyFrom(mp);
            }

        }
        for (MultiShapeObject s : yticksLegend) {
            for (Shape sh : s) {
                sh.mp.copyFrom(mp);
            }
        }
    }

    @Override
    public <T extends MathObject> T thickness(double newThickness
    ) {
        xAxis.thickness(newThickness);
        yAxis.thickness(newThickness);
        for (Shape s : xticks) {
            s.thickness(newThickness);
        }
        for (Shape s : yticks) {
            s.thickness(newThickness);
        }
        return (T) this;
    }

    /**
     * Returns the current tick scale
     *
     * @return The tick scale
     */
    public double getTickScale() {
        return tickScale;
    }

    /**
     * Sets the scaling of the ticks, both vertical and horizontal.Default value
     * is 1.
     *
     * @param tickScale Scale to apply to ticks
     */
    public void setTickScale(double tickScale) {
        this.tickScale = tickScale;
    }
}
