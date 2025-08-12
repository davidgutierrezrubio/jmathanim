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
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.mathobjects.*;

/**
 * A Cartesian Grid
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CartesianGrid extends MultiShapeObject implements shouldUdpateWithCamera {

    private final double xStep;
    private final double yStep;
    private final double centerY;
    private final double centerX;
    private final Rect currentView;

    /**
     * Creates a new cartesian grid located at a given reference point with
     * given horizontal and vertical spaces between lines. Grid with be drawn
     * using the style gridPrimaryDefault, if available.
     *
     * @param centerX x coordinate of reference point
     * @param centerY y coordinate of reference point
     * @param xStep x step between vertical lines
     * @param yStep y step between horizontal lines
     * @return The created grid
     */
    public static CartesianGrid make(double centerX, double centerY, double xStep, double yStep) {
        CartesianGrid resul = new CartesianGrid(centerX, centerY, xStep, yStep);
        //Default camera
           resul.setCamera(JMathAnimConfig.getConfig().getCamera());
        resul.style("gridPrimaryDefault");
        resul.layer(-Integer.MAX_VALUE);
        resul.recomputeGrid();
        resul.alignGridToScreen();
        return resul;
    }

    /**
     * Creates a new cartesian grid with primary and secondary lines located at
     * a given reference point with given horizontal and vertical spaces between
     * lines. Grid with be drawn using the style gridPrimaryDefault and
     * gridSecondaryDefault, if availables.
     *
     * @param centerX x coordinate of reference point
     * @param centerY y coordinate of reference point
     * @param xStep x step between vertical lines
     * @param yStep y step between horizontal lines
     * @param xStepSec x step between vertical lines for secondary lines
     * @param yStepSec y step between vertical lines for secondary lines
     * @return The created grid
     */
    public static MathObjectGroup make(double centerX, double centerY, double xStep, double yStep, double xStepSec, double yStepSec) {
        CartesianGrid big = new CartesianGrid(centerX, centerY, xStep, yStep);
        CartesianGrid small = new CartesianGrid(centerX, centerY, xStepSec, yStepSec);

        //First default camera
        big.setCamera(JMathAnimConfig.getConfig().getCamera());
        small.setCamera(JMathAnimConfig.getConfig().getCamera());
        big.style("gridPrimaryDefault");
        big.layer(-Integer.MAX_VALUE + 1);
        big.recomputeGrid();
        big.alignGridToScreen();
        small.style("gridSecondaryDefault");
        small.layer(-Integer.MAX_VALUE);
        small.recomputeGrid();
        small.alignGridToScreen();
        return MathObjectGroup.make(big, small);
        //TODO: Fix this, it shouldnt be a MathObjectGroup, but a CartesianGrid object
    }

    protected CartesianGrid(double x, double y, double w, double h) {
        super();
        this.centerX = x;
        this.centerY = y;
        this.xStep = w;
        this.yStep = h;
        this.currentView = Rect.centeredUnitSquare();
    }

    private void recomputeGrid() {
        this.clearShapes();

        Rect bb = this.getCamera().getMathView();
        double wv = 1 * bb.getWidth();
        double hv = 1 * bb.getHeight();

        int nw = ((int) (wv / xStep)) + 2;
        int nh = ((int) (hv / yStep)) + 2;

        this.add(Line.XAxis().shift(this.centerX, this.centerY).setObjectLabel("grid_xaxis"));
        this.add(Line.YAxis().shift(this.centerX, this.centerY).setObjectLabel("grid_yaxis"));
        for (int n = 1; n < nw; n++) {
            this.add(Line.YAxis().shift(this.centerX + n * xStep, this.centerY).setObjectLabel("grid_y1_" + n));
            this.add(Line.YAxis().shift(this.centerX - n * xStep, this.centerY).setObjectLabel("grid_x1_" + n));
        }
        for (int n = 1; n < nh; n++) {
            this.add(Line.XAxis().shift(this.centerX, this.centerY + n * yStep).setObjectLabel("grid_x2_" + n));
            this.add(Line.XAxis().shift(this.centerX, this.centerY - n * yStep).setObjectLabel("grid_y2_" + n));
        }

        this.getMp().copyFrom(this.getMp());//Re-apply style to created lines
    }

    public void alignGridToScreen() {
        Rect bb = this.getBoundingBox();
        Rect mv = this.getCamera().getMathView();
        while (bb.xmin > mv.xmin) {
            this.shift(-this.xStep, 0);
            bb.xmin -= this.xStep;
        }
//        while (bb.xmin < mv.xmin) {
//            this.shift(this.xStep, 0);
//            bb.xmin += this.xStep;
//        }

        while (bb.xmax < mv.xmax) {
            this.shift(this.xStep, 0);
            bb.xmax += this.xStep;
        }
//         while (bb.xmax > mv.xmax) {
//            this.shift(-this.xStep, 0);
//            bb.xmax -= this.xStep;
//        }
//        
        
        while (bb.ymin > mv.ymin) {
            this.shift(0, -this.yStep);
            bb.ymin -= this.yStep;
        }
//         while (bb.ymin < mv.ymin) {
//            this.shift(0, this.yStep);
//            bb.ymin += this.yStep;
//        }
         
        while (bb.ymax < mv.ymax) {
            this.shift(0, this.yStep);
            bb.ymax += this.yStep;
        }
//         while (bb.ymax >mv.ymax) {
//            this.shift(0, -this.yStep);
//            bb.ymax -= this.yStep;
//        }

    }

    @Override
    public CartesianGrid copy() {
        CartesianGrid copy = CartesianGrid.make(this.centerX, this.centerY, this.xStep, this.yStep);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
         super.copyStateFrom(obj);
        if (obj instanceof CartesianGrid) {
            CartesianGrid grid = (CartesianGrid) obj;
            this.getMp().copyFrom(grid.getMp());
        }
    }

    public void rebuildShape() {
        final Rect mathView = getCamera().getMathView();
        if (!this.currentView.equals(mathView)) {
            if ((this.currentView.getWidth() != mathView.getWidth()) || (this.currentView.getHeight() != mathView.getHeight())) {
                recomputeGrid();
            }
            alignGridToScreen();
            this.currentView.copyFrom(mathView);
        }
    }

    @Override
    public void updateWithCamera(Camera camera) {
        recomputeGrid();
        alignGridToScreen();
    }
}
