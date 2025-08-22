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

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TickAxes extends MathObject {

    private static final double INITIAL_LEGEND_SCALE = .3;
    private static final double INITIAL_MARK_SCALE = .025;

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

    public static TickAxes makeXTick(double x, String latex, TickType tt, double maxShowScale) {
        double markScale = getMarkScaleFortype(tt);
        final Shape xtick = Shape
                .segment(Point.at(x, -INITIAL_MARK_SCALE * markScale), Point.at(x, INITIAL_MARK_SCALE * markScale))
                .style(getStyleNameFortype(tt));
        final LaTeXMathObject xtickLegend = LaTeXMathObject.make(latex).style("axislegenddefault");
        xtickLegend.scale(INITIAL_LEGEND_SCALE);
        return new TickAxes(x, xtickLegend, xtick, TickOrientation.XAXIS, maxShowScale);
    }

    public static TickAxes makeYTick(double y, String latex, TickType tt, double maxShowScale) {
        double markScale = getMarkScaleFortype(tt);
        final Shape ytick = Shape
                .segment(Point.at(-INITIAL_MARK_SCALE * markScale, y), Point.at(INITIAL_MARK_SCALE * markScale, y))
                .style(getStyleNameFortype(tt));

        final LaTeXMathObject ytickLegend = LaTeXMathObject.make(latex).style("axislegenddefault");
        ytickLegend.scale(INITIAL_LEGEND_SCALE);
        return new TickAxes(y, ytickLegend, ytick, TickOrientation.YAXIS, maxShowScale);
    }

    LaTeXMathObject legend;
    double location;
    double maximumScaleToShow;
    MODrawPropertiesArray mpArray;
    TickOrientation orientation;

    Shape tick;
    TickType tickType;

    public TickAxes(double location, LaTeXMathObject legend, Shape mark, TickOrientation type,
            double maximumScaleToShow) {
        super();
        mpArray = new MODrawPropertiesArray();
        this.location = location;
        this.legend = legend;
        this.tick = mark;
        mpArray.add(legend, tick);
        this.maximumScaleToShow = maximumScaleToShow;
        this.orientation = type;
    }

    @Override
    public TickAxes copy() {
        TickAxes copy = new TickAxes(location, legend.copy(), tick.copy(), orientation, maximumScaleToShow);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
         super.copyStateFrom(obj);
        if (!(obj instanceof TickAxes)) {
            return;
        }
        TickAxes t = (TickAxes) obj;
        getLegend().copyStateFrom(t.getLegend());
        getTick().copyStateFrom(t.getTick());
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        if (isVisible()) {
            if (shouldDraw(r.getCamera())) {
                legend.draw(scene, r, cam);
                tick.draw(scene, r, cam);
            }
        }
        scene.markAsAlreadydrawn(this);

    }

    @Override
    protected Rect computeBoundingBox() {
        return Rect.union(tick.getBoundingBox(), legend.getBoundingBox());
    }

    public LaTeXMathObject getLegend() {
        return legend;
    }

    public double getLocation() {
        return location;
    }

    public double getMaximumScaleToShow() {
        return maximumScaleToShow;
    }

    @Override
    public Stylable getMp() {
        return mpArray;
    }

    public TickOrientation getOrientation() {
        return orientation;
    }

    public Shape getTick() {
        return tick;
    }

    public TickType getTickType() {
        return tickType;
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

    public enum TickOrientation {
        XAXIS, YAXIS
    }

    public enum TickType {
        PRIMARY, SECONDARY
    }

}
