/*
 * Copyright (C) 2022 David Gutierrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.mathobjects.Axes;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Utils.Rect;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;

/**
 * A Cartesian Grid
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CartesianGrid extends Constructible {

    private double xStep;
    private double yStep;
    Camera cam;
    private double centerY;
    private double centerX;
    private final Rect currentView;
    public final MathObjectGroup lines;

    /**
     * Creates a new cartesian grid located at a given reference point with
     * given horizontal and vertical spaces between lines
     *
     * @param centerX x coordinate of reference point
     * @param centerY y coordinate of reference point
     * @param xStep x step between vertical lines
     * @param yStep y step between horizontal lines
     * @return The created grid
     */
    public static CartesianGrid make(double centerX, double centerY, double xStep, double yStep) {
        CartesianGrid resul = new CartesianGrid(centerX, centerY, xStep, yStep);
        resul.layer(-Integer.MAX_VALUE);
        resul.recomputeGrid();
        resul.alignGridToScreen();
        return resul;
    }

    protected CartesianGrid(double x, double y, double w, double h) {
        super();
        this.centerX = x;
        this.centerY = y;
        this.xStep = w;
        this.yStep = h;
        this.cam = scene.getCamera();
        this.currentView = this.cam.getMathView().copy();
        this.lines = MathObjectGroup.make();
    }

    private void recomputeGrid() {
        this.lines.clear();

        Rect bb = this.cam.getMathView();
        double wv = 1 * bb.getWidth();
        double hv = 1 * bb.getHeight();

        int nw = ((int) (wv / xStep)) + 2;
        int nh = ((int) (hv / yStep)) + 2;

        this.lines.add(Line.XAxis().shift(this.centerX, this.centerY));
        this.lines.add(Line.YAxis().shift(this.centerX, this.centerY));
        for (int n = 1; n < nw; n++) {
            this.lines.add(Line.YAxis().shift(this.centerX + n * xStep, this.centerY));
            this.lines.add(Line.YAxis().shift(this.centerX - n * xStep, this.centerY));
        }
        for (int n = 1; n < nh; n++) {
            this.lines.add(Line.XAxis().shift(this.centerX, this.centerY + n * yStep));
            this.lines.add(Line.XAxis().shift(this.centerX, this.centerY - n * yStep));
        }

        this.getMp().copyFrom(this.getMp());//Re-apply style to created lines
    }

    private void alignGridToScreen() {
        Rect bb = this.lines.getBoundingBox();
        Rect mv = this.cam.getMathView();
        while (bb.xmin > mv.xmin) {
            this.lines.shift(-this.xStep, 0);
            bb.xmin -= this.xStep;
        }
        while (bb.xmax < mv.xmax) {
            this.lines.shift(this.xStep, 0);
            bb.xmax += this.xStep;
        }
        while (bb.ymin > mv.ymin) {
            this.lines.shift(0, -this.yStep);
            bb.ymin -= this.yStep;
        }
        while (bb.ymax < mv.ymax) {
            this.lines.shift(0, this.yStep);
            bb.ymax += this.yStep;
        }

    }

    @Override
    public CartesianGrid copy() {
        CartesianGrid copy = CartesianGrid.make(this.centerX, this.centerY, this.xStep, this.yStep);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        if (obj instanceof CartesianGrid) {
            CartesianGrid grid = (CartesianGrid) obj;
            this.getMp().copyFrom(grid.getMp());
        }
    }

    @Override
    public MathObject getMathObject() {
        return this.lines;
    }

    @Override
    public void rebuildShape() {
        final Rect mathView = cam.getMathView();
        if (!this.currentView.equals(mathView)) {
            if ((this.currentView.getWidth() != mathView.getWidth()) || (this.currentView.getHeight() != mathView.getHeight())) {
                recomputeGrid();
            }
            alignGridToScreen();
            this.currentView.copyFrom(mathView);
        }
    }
}
