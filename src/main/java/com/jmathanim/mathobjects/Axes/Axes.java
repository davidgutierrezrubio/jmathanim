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
package com.jmathanim.mathobjects.Axes;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.mathobjects.Axes.Axes.LEGEND_TICKS_GAP;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Axes extends MathObject {

    public static final double LEGEND_TICKS_GAP = .5;
    DecimalFormat format;

    MODrawPropertiesArray mpArray;
    private final Line xAxis;
    private final ArrayList<TickAxes> xticks;
    private final ArrayList<TickAxes> xticksBase;
    private final Line yAxis;
    private final ArrayList<TickAxes> yticks, yticksBase;

    public static Axes makeBasicAxes() {
        return makeBasicAxes(0, -1);
    }

    public static Axes makeBasicAxes(int a, int b) {
        Axes resul = new Axes();
        if (a <= b) {
            resul.generatePrimaryXTicks(a, b, 1);
            resul.generatePrimaryYTicks(a, b, 1);
        }
        return resul;
    }

    public Axes() {
        mpArray = new MODrawPropertiesArray();
        getMp().loadFromStyle("axisdefault");
        xticksBase = new ArrayList<>();
        xticks = new ArrayList<>();
        yticksBase = new ArrayList<>();
        yticks = new ArrayList<>();
        xAxis = Line.make(Point.at(0, 0), Point.at(1, 0)).style("axisdefault");
        yAxis = Line.make(Point.at(0, 0), Point.at(0, 1)).style("axisdefault");
        mpArray.add(xAxis, yAxis);
        Locale locale = new Locale("en", "UK");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        String pattern = "#.####";
        format = new DecimalFormat(pattern, symbols);
    }

    /**
     * Adds a pair (tick, legend text) at the given value in the x-axis.The text
     * is automatically generated from the value of x.The maxWidthToShow
     * parameter is the maximum width of the math view to show this tick. A
     * value of 0 means always show this tick
     *
     * @param x The x coordinate where to put the tick
     * @param tickType Tick orientation (primary or secondary)
     * @param maxWidthToShow max scale to show the tick
     */
    public void addXTicksLegend(double x, TickAxes.TickType tickType, double maxWidthToShow) {
        addXTicksLegend("$" + format.format(x) + "$", x, tickType, maxWidthToShow);
    }

    /**
     * Adds a pair (tick, legend text) at the given value in the y-axis, with
     * the specified latex string.The maxWidthToShow parameter is the maximum
     * width of the math view to show this tick. A value of 0 means always show
     * this tick
     *
     * @param latex The text with the legend
     * @param x The x coordinate where to put the tick
     * @param tickType Tick orientation (primary or secondary)
     * @param maxWidthToShow max scale to show the tick
     */
    public void addXTicksLegend(String latex, double x, TickAxes.TickType tickType, double maxWidthToShow) {
        if (!xticksBase.stream().anyMatch(t -> (t.location == x))) {
            TickAxes tick = TickAxes.makeXTick(x, latex, tickType, maxWidthToShow);
            xticksBase.add(tick);
            mpArray.add(tick);
        }
    }

    // /**
//     * Generates a set of pairs (ticks-legends) from start to finish (including)
//     * with given step, in the x-axis.The maxScale parameter is the maximum
//     * width of the math view to show this tick. A value of 0 means always show
//     * this tick
//     *
//     * @param start Starting number
//     * @param finish Ending number
//     * @param step Step
//     * @param tickType TickScale (primary or secondary, sets the size of the
//     * markers)
//     * @param maxScale max scale to show these ticks
//     */
//    public void generateXTicks(double start, double finish, double step, TickScale tickType, double maxScale) {
//        for (double x = start; x < finish; x += step) {
//            if (x != 0) {
//                addXTicksLegend(x, tickType, maxScale);
//            }
//        }
//    }
    /**
     * Adds a pair (tick, legend text) at the given value in the y-axis.The text
     * is automatically generated from the value of y. The maxWidthToShow
     * parameter is the maximum width of the math view to show this tick. A
     * value of 0 means always show this tick
     *
     * @param y The y coordinate where to put the tick
     * @param maxWidthToShow max scale to show the tick
     */
    public void addYTicksLegend(double y, TickAxes.TickType tickType, double maxWidthToShow) {
        addYTicksLegend("$" + format.format(y) + "$", y, tickType, maxWidthToShow);
    }

    /**
     * Adds a pair (tick, legend text) at the given value in the y-axis, with
     * the specified latex string.The maxWidthToShow parameter is the maximum
     * width of the math view to show this tick. A value of 0 means always show
     * this tick
     *
     * @param latex The text with the legend
     * @param y The y coordinate where to put the tick
     * @param tickType Tick orientation (primary or secondary)
     * @param maxWidthToShow max scale to show the tick
     */
    public void addYTicksLegend(String latex, double y, TickAxes.TickType tickType, double maxWidthToShow) {
        if (!yticksBase.stream().anyMatch(t -> (t.location == y))) {
            TickAxes tick = TickAxes.makeYTick(y, latex, tickType, maxWidthToShow);
            yticksBase.add(tick);
            mpArray.add(tick);
        }
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
        // Do nothing
        return (T) this;
    }

    @Override
    public <T extends MathObject> T copy() {
        return null;// For now, it doesn't make senses to make a copy of the axes
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        if (isVisible()) {
            xAxis.draw(scene, r);
            yAxis.draw(scene, r);

            for (TickAxes xtick : xticks) {
                xtick.draw(scene, r);
            }
            for (TickAxes ytick : yticks) {
                ytick.draw(scene, r);
            }
        }
        scene.markAsAlreadyDrawed(this);
    }

    /**
     * Generates a set of pairs (ticks-legends) from start to finish (including)
     * with given step, in the x-axis. The primary ticks are slightly bigger
     * than the secondary, and they are always shown on the screen
     *
     * @param start Starting number
     * @param finish Ending number
     * @param step Step
     * @return This object
     */
    public Axes generatePrimaryXTicks(double start, double finish, double step) {
        for (double x = start; x <= finish; x += step) {
            if (x != 0) {
                addXTicksLegend(x, TickAxes.TickType.PRIMARY, 0);
            }
        }
        return this;
    }

    /**
     * Generates a set of pairs (ticks-legends) from start to finish (including)
     * with given step, in the y-axis. The primary ticks are slightly bigger
     * than the secondary, and they are always shown on the screen
     *
     * @param start Starting number
     * @param finish Ending number
     * @param step Step
     * @return This object
     */
    public Axes generatePrimaryYTicks(double start, double finish, double step) {
        for (double y = start; y <= finish; y += step) {
            if (y != 0) {
                addYTicksLegend(y, TickAxes.TickType.PRIMARY, 0);
            }
        }
        return this;
    }

    /**
     * Generates a set of pairs (ticks-legends) from start to finish (including)
     * with given step, in the x-axis.The maxWidthToShow parameter is the
     * maximum width of the math view to show this tick.A value of 0 means
     * always show this tick.
     *
     * @param start Starting number
     * @param finish Ending number
     * @param step Step
     * @param maxWidthToShow max width of the current camera to show these ticks
     * @return This object
     */
    public Axes generateSecondaryXTicks(double start, double finish, double step, double maxWidthToShow) {
        for (double x = start; x < finish; x += step) {
            if (x != 0) {
                addXTicksLegend(x, TickAxes.TickType.SECONDARY, maxWidthToShow);
            }
        }
        return this;
    }

    /**
     * Generates a set of pairs (ticks-legends) from start to finish (including)
     * with given step, in the y-axis.The maxWidthToShow parameter is the
     * maximum width of the math view to show this tick.A value of 0 means
     * always show this tick.
     *
     * @param start Starting number
     * @param finish Ending number
     * @param step Step
     * @param maxWidthToShow max scale to show these ticks
     * @return This object
     */
    public Axes generateSecondaryYTicks(double start, double finish, double step, double maxWidthToShow) {
        for (double y = start; y < finish; y += step) {
            if (y != 0) {
                addYTicksLegend(y, TickAxes.TickType.SECONDARY, maxWidthToShow);
            }
        }
        return this;
    }

    @Override
    public Rect computeBoundingBox() {
        return JMathAnimConfig.getConfig().getCamera().getMathView();
    }

    @Override
    public Point getCenter() {
        return Point.at(0, 0);
    }

    public DecimalFormat getFormat() {
        return format;
    }

    public void setFormat(DecimalFormat format) {
        this.format = format;
    }

    @Override
    public final Stylable getMp() {
        return mpArray;
    }

    public ArrayList<TickAxes> getXticks() {
        return xticks;
    }

    public ArrayList<TickAxes> getYticks() {
        return yticks;
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

    @Override
    public <T extends MathObject> T thickness(double newThickness) {
        xAxis.thickness(newThickness);
        yAxis.thickness(newThickness);
        for (TickAxes s : xticksBase) {
            s.tick.thickness(newThickness);
        }
        for (TickAxes s : yticksBase) {
            s.tick.thickness(newThickness);
        }
        return (T) this;
    }

    @Override
    public void update(JMathAnimScene scene) {
        double xmax = scene.getCamera().getMathView().xmax;
        double xmin = scene.getCamera().getMathView().xmin;
        double scale = (xmax - xmin) / 4;
        xticks.clear();
        for (int n = 0; n < xticksBase.size(); n++) {
            if (xticksBase.get(n).shouldDraw(scene.getCamera())) {
                TickAxes copy = xticksBase.get(n).copy();
                copy.tick.scale(scale);
                copy.legend.scale(scale);
                copy.legend.stackTo(copy.tick, Anchor.Type.LOWER, LEGEND_TICKS_GAP * copy.legend.getHeight());
                xticks.add(copy);
            }
        }

        yticks.clear();
        for (int n = 0; n < yticksBase.size(); n++) {
            if ((yticksBase.get(n).shouldDraw(scene.getCamera()))) {
                TickAxes copy = yticksBase.get(n).copy();
                copy.tick.scale(scale);
                copy.legend.scale(scale);
                copy.legend.stackTo(copy.tick, Anchor.Type.RIGHT, LEGEND_TICKS_GAP * copy.legend.getHeight());
                yticks.add(copy);
            }
        }
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        //Nothing to do by now
    }

    public enum TickScale {
        PRIMARY, SECONDARY
    }
}
