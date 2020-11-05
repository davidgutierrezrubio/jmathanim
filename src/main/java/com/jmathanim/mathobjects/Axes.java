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
    ArrayList<Shape> xticks;
    ArrayList<Shape> yticks;
    ArrayList<MultiShapeObject> xticksLegend;
    ArrayList<MultiShapeObject> yticksLegend;
    public static final double TICK_LENGTH = .02;
    public static final double TICKS_SCALE = .3;
    public static final double LEGEND_TICKS_GAP = .05;

    public Axes() {
        generateAxis();
    }

    private void generateAxis() {
        xAxis = Shape.line(Point.at(0, 0), Point.at(1, 0)).style("axisdefault");
        yAxis = Shape.line(Point.at(0, 0), Point.at(0, 1)).style("axisdefault");
        xticks = new ArrayList<>();
        yticks = new ArrayList<>();
        xticksLegend = new ArrayList<>();
        yticksLegend = new ArrayList<>();
        for (int n = -5; n <= 5; n++) {
            final Shape xtick = Shape.segment(Point.at(n, -TICK_LENGTH), Point.at(n, TICK_LENGTH)).style("axistickdefault");
            xtick.setAbsoluteSize(Anchor.BY_CENTER);
            xticks.add(xtick);

            final Shape ytick = Shape.segment(Point.at(-TICK_LENGTH, n), Point.at(TICK_LENGTH, n)).style("axistickdefault");;
            ytick.setAbsoluteSize(Anchor.BY_CENTER);
            yticks.add(ytick);

            if (n != 0) {
                final LaTeXMathObject xtickLegend = LaTeXMathObject.make("$" + n + "$").style("axislegenddefault").scale(TICKS_SCALE);
                xtickLegend.stackTo(xtick, Anchor.LOWER, LEGEND_TICKS_GAP);
                xtickLegend.setAbsoluteSize(Anchor.UPPER);
                xticksLegend.add(xtickLegend);
                final LaTeXMathObject ytickLegend = LaTeXMathObject.make("$" + n + "$").style("axislegenddefault").scale(TICKS_SCALE);
                ytickLegend.stackTo(ytick, Anchor.RIGHT, LEGEND_TICKS_GAP);
                ytickLegend.setAbsoluteSize(Anchor.LEFT);
                yticksLegend.add(ytickLegend);
            }

        }
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
    public void prepareForNonLinearAnimation() {
    }

    @Override
    public void processAfterNonLinearAnimation() {
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

        for (Shape s : xticks) {
            s.draw(r);
        }
        for (Shape s : yticks) {
            s.draw(r);
        }
        for (MultiShapeObject s : xticksLegend) {
            s.draw(r);
        }
        for (MultiShapeObject s : yticksLegend) {
            s.draw(r);
        }

    }

    @Override
    public void update(JMathAnimScene scene) {
        //TODO: Adding or removing ticks should be done here
    }

}
