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

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TickAxes implements Drawable {

    LaTeXMathObject legend;
    Shape tick;
    double maximumScaleToShow;
    double location;
    private static final double INITIAL_MARK_SCALE = .025;
    private static final double INITIAL_LEGEND_SCALE = .3;

    enum TickOrientation {
        XAXIS, YAXIS
    }
    TickOrientation orientation;

    public enum TickType {
        PRIMARY, SECONDARY
    }
    TickType tickType;

    public static TickAxes makeXTick(double x, String latex, TickType tt, double maxShowScale) {
        double markScale = getMarkScaleFortype(tt);
        final Shape xtick = Shape.segment(Point.at(x, -INITIAL_MARK_SCALE * markScale), Point.at(x, INITIAL_MARK_SCALE * markScale)).style(getStyleNameFortype(tt));
        final LaTeXMathObject xtickLegend = LaTeXMathObject.make(latex).style("axislegenddefault");
        xtickLegend.scale(INITIAL_LEGEND_SCALE);
        return new TickAxes(x, xtickLegend, xtick, TickOrientation.XAXIS, maxShowScale);
    }

    public static TickAxes makeYTick(double y, String latex, TickType tt, double maxShowScale) {
        double markScale = getMarkScaleFortype(tt);
        final Shape ytick = Shape.segment(Point.at(-INITIAL_MARK_SCALE * markScale, y), Point.at(INITIAL_MARK_SCALE * markScale, y)).style(getStyleNameFortype(tt));

        final LaTeXMathObject ytickLegend = LaTeXMathObject.make(latex).style("axislegenddefault");
        ytickLegend.scale(INITIAL_LEGEND_SCALE);
        return new TickAxes(y, ytickLegend, ytick, TickOrientation.YAXIS, maxShowScale);
    }

    public static double getMarkScaleFortype(TickType type) {
        switch (type) {
            case PRIMARY:
                return 1;
            case SECONDARY:
                return .75;
            default:
                return 1;
        }
    }

    public static String getStyleNameFortype(TickType type) {
        switch (type) {
            case PRIMARY:
                return "axisPrimaryTickDefault";
            case SECONDARY:
                return "axisSecondaryTickDefault";
            default:
                return "axisPrimaryTickDefault";
        }
    }

    public TickAxes(double location, LaTeXMathObject legend, Shape mark, TickOrientation type, double maximumScaleToShow) {
        this.location = location;
        this.legend = legend;
        this.tick = mark;
        this.maximumScaleToShow = maximumScaleToShow;
        this.orientation = type;
    }

    public void draw(Renderer r) {
        if (shouldDraw(r.getCamera())) {
            legend.draw(r);
            tick.draw(r);
        }
    }

    public boolean shouldDraw(Camera cam) {
        boolean scaleCondition = (maximumScaleToShow == 0) | (cam.getMathView().getWidth() < maximumScaleToShow);

        boolean locationCondition;
        if (orientation == TickOrientation.XAXIS) {
            double xmin = cam.getMathView().xmin;
            double xmax = cam.getMathView().xmax;
            locationCondition = (location > xmin) && (location < xmax);
        } else {
            double ymin = cam.getMathView().ymin;
            double ymax = cam.getMathView().ymax;
            locationCondition = (location > ymin) && (location < ymax);
        }
        return (scaleCondition && locationCondition);
    }

    public TickAxes copy() {
        return new TickAxes(location, legend.copy(), tick.copy(), orientation, maximumScaleToShow);
    }

    public LaTeXMathObject getLegend() {
        return legend;
    }

    public Shape getTick() {
        return tick;
    }

    public double getMaximumScaleToShow() {
        return maximumScaleToShow;
    }

    public double getLocation() {
        return location;
    }

    public TickOrientation getOrientation() {
        return orientation;
    }

    public TickType getTickType() {
        return tickType;
    }

}
