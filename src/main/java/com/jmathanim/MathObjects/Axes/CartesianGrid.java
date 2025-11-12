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
package com.jmathanim.MathObjects.Axes;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.MathObjects.*;
import com.jmathanim.MathObjects.Shapes.Line;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Styling.DrawStylePropertiesArray;
import com.jmathanim.Styling.DrawStylePropertiesObjectsArray;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.ArrayList;

/**
 * A Cartesian Grid
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CartesianGrid extends MathObject<CartesianGrid> implements shouldUdpateWithCamera,hasTrivialBoundingBox {

    private final Rect currentView;
    protected final ArrayList<Line> horizontalPrimaryLines;
    protected final ArrayList<Line> verticalPrimaryLines;
    protected final ArrayList<Line> horizontalSecondaryLines;
    protected final ArrayList<Line> verticalSecondaryLines;
    private final DrawStylePropertiesArray mp;
    private final DrawStylePropertiesObjectsArray primaryGridStyle;
    private final DrawStylePropertiesObjectsArray secondaryGridStyle;
    protected final Vec center;
    protected final Vec steps;
    protected int secondaryXDivision;
    protected int secondaryYDivision;
    private final MathObjectGroup allLines;

    protected CartesianGrid(double x, double y, double xStepPrimary, double yStepPrimary, int secondaryXDivision, int secondaryYDivision) {
        super();
        this.center=Vec.to(x,y);
        this.steps = Vec.to(xStepPrimary, yStepPrimary);
        this.secondaryXDivision = secondaryXDivision;
        this.secondaryYDivision = secondaryYDivision;
        this.currentView = Rect.centeredUnitSquare();
        this.horizontalPrimaryLines = new ArrayList<>();
        this.horizontalSecondaryLines = new ArrayList<>();
        this.verticalPrimaryLines = new ArrayList<>();
        this.verticalSecondaryLines = new ArrayList<>();
        this.mp = new DrawStylePropertiesArray();
        this.primaryGridStyle = new DrawStylePropertiesObjectsArray();
        this.secondaryGridStyle = new DrawStylePropertiesObjectsArray();
        this.mp.add(primaryGridStyle, secondaryGridStyle);
        allLines = MathObjectGroup.make();

    }

    /**
     * Creates a new cartesian grid located at a given reference point with given horizontal and vertical spaces between
     * lines. Grid will be drawn using the style gridPrimaryDefault style, if available.
     *
     * @param centerX            x coordinate of reference point
     * @param centerY            y coordinate of reference point
     * @param secondaryXDivision number of horizontal divisions to makeLengthMeasure for secondary lines
     * @param secondaryYDivision number of vertical divisions to makeLengthMeasure for secondary lines
     * @return The created grid
     */
    public static CartesianGrid make(double centerX, double centerY, int secondaryXDivision, int secondaryYDivision) {
        CartesianGrid resul = new CartesianGrid(centerX, centerY,1,1, secondaryXDivision, secondaryYDivision);
        //Default camera
        resul.setCamera(JMathAnimConfig.getConfig().getCamera());
        resul.getPrimaryGridStyle().loadFromStyle("gridPrimaryDefault");
        resul.getSecondaryGridStyle().loadFromStyle("gridSecondaryDefault");
        resul.layer(-Integer.MAX_VALUE);
        resul.recomputeGrid();
        resul.alignGridToScreen();
        return resul;
    }

    /**
     * Creates a new cartesian grid with primary and secondary lines located at a given reference point with given
     * horizontal and vertical spaces between lines. Grid with be drawn using the style gridPrimaryDefault and
     * gridSecondaryDefault, if availables.
     *
     * @param centerX            x coordinate of reference point
     * @param centerY            y coordinate of reference point
     * @param xStep              x step between vertical lines
     * @param yStep              y step between horizontal lines
     * @param secondaryXDivision number of horizontal divisions to makeLengthMeasure for secondary lines
     * @param secondaryYDivision number of vertical divisions to makeLengthMeasure for secondary lines
     * @return The created grid
     */
    public static CartesianGrid make(double centerX, double centerY, double xStep, double yStep, int secondaryXDivision, int secondaryYDivision) {
        CartesianGrid resul = new CartesianGrid(centerX, centerY, xStep, yStep, secondaryXDivision, secondaryYDivision);
        resul.getPrimaryGridStyle().loadFromStyle("gridPrimaryDefault");
        resul.getSecondaryGridStyle().loadFromStyle("gridSecondaryDefault");
        //First default camera
//        big.setCamera(JMathAnimConfig.getConfig().getCamera());
//        small.setCamera(JMathAnimConfig.getConfig().getCamera());
//        big.style("gridPrimaryDefault");
        resul.layer(-Integer.MAX_VALUE + 1);
        resul.recomputeGrid();
        resul.alignGridToScreen();
        return resul;
    }

    public DrawStylePropertiesObjectsArray getSecondaryGridStyle() {
        return secondaryGridStyle;
    }

    public DrawStylePropertiesObjectsArray getPrimaryGridStyle() {
        return primaryGridStyle;
    }

    public void recomputeGrid() {
        horizontalPrimaryLines.clear();
        horizontalSecondaryLines.clear();
        verticalPrimaryLines.clear();
        verticalSecondaryLines.clear();
        allLines.clear();

        Rect bb = this.getCamera().getMathView();
        double wv = 1 * bb.getWidth();
        double hv = 1 * bb.getHeight();


        currentView.copyFrom(new Rect(center.x-wv/2, center.y-hv/2,center.x+wv/2, center.y+hv/2));

        int nw = ((int) (wv / steps.x)) + 2;
        int nh = ((int) (hv / steps.y)) + 2;

        getPrimaryGridStyle().clear();
        getSecondaryGridStyle().clear();


        for (int n = -nw+1; n < nw; n++) {
            for (int i = 1; i < secondaryYDivision; i++) {
                double xCoordinate = (1d * i / secondaryYDivision + n) * steps.x;
                addVerticalLine(xCoordinate, false);
            }
            addVerticalLine(n*steps.x, true);
        }
        for (int n = -nh+1; n < nh; n++) {
            for (int i = 1; i < secondaryXDivision; i++) {
                addHorizontalLine((1d*i/secondaryXDivision+n)*steps.y, false);
            }
            addHorizontalLine(n*steps.y, true);
        }

        this.getPrimaryGridStyle().copyFrom(this.getPrimaryGridStyle().getFirstMP());//Re-apply style to created lines
        this.getSecondaryGridStyle().copyFrom(this.getSecondaryGridStyle().getFirstMP());//Re-apply style to created lines
    }


    private void addHorizontalLine(double yCoordinate, boolean primary) {
        if (primary) {
//            Line line = Line.YAxis().shift(this.centerX + lineNumber * xStepPrimary, this.centerY).setObjectLabel("grid_y1_p_" + lineNumber);
            Line line = Line.XAxis().shift(this.center.x , this.center.y+yCoordinate).setObjectLabel("grid_h_p");
            horizontalPrimaryLines.add(line);
            getPrimaryGridStyle().add(line);
            allLines.add(line);
        } else {
            Line line = Line.XAxis().shift(this.center.x  , this.center.y+yCoordinate).setObjectLabel("grid_h_s");
            horizontalSecondaryLines.add(line);
            getSecondaryGridStyle().add(line);
            allLines.add(line);
        }
    }

    private void addVerticalLine(double xCoordinate, boolean primary) {
        if (primary) {
//            Line line = Line.YAxis().shift(this.centerX + lineNumber * xStepPrimary, this.centerY).setObjectLabel("grid_y1_p_" + lineNumber);
            Line line = Line.YAxis().shift(this.center.x+ xCoordinate, this.center.y).setObjectLabel("grid_v_p");
            verticalPrimaryLines.add(line);
            getPrimaryGridStyle().add(line);
            allLines.add(line);
        } else {
            Line line = Line.YAxis();
            line.shift(this.center.x+ xCoordinate, this.center.y).setObjectLabel("grid_v_s");
            line.shift(1,0);
            verticalSecondaryLines.add(line);
            getSecondaryGridStyle().add(line);
            allLines.add(line);
        }
    }


    public void alignGridToScreen() {
        Rect bb = currentView;
        Rect mv = this.getCamera().getMathView();
        while (bb.xmin > mv.xmin) {
            allLines.shift(-this.steps.x,0);
            bb.xmin -= this.steps.x;
        }
//        while (bb.xmin < mv.xmin) {
//            this.shift(this.xStep, 0);
//            bb.xmin += this.xStep;
//        }

        while (bb.xmax < mv.xmax) {
            allLines.shift(this.steps.x,0);
            bb.xmax += this.steps.x;
        }
//         while (bb.xmax > mv.xmax) {
//            this.shift(-this.xStep, 0);
//            bb.xmax -= this.xStep;
//        }
//

        while (bb.ymin > mv.ymin) {
            allLines.shift(0,-this.steps.y);
            bb.ymin -= this.steps.y;
        }
//         while (bb.ymin < mv.ymin) {
//            this.shift(0, this.yStep);
//            bb.ymin += this.yStep;
//        }

        while (bb.ymax < mv.ymax) {
            allLines.shift(0,this.steps.y);
            bb.ymax += this.steps.y;
        }
//         while (bb.ymax >mv.ymax) {
//            this.shift(0, -this.yStep);
//            bb.ymax -= this.yStep;
//        }

    }

    @Override
    public CartesianGrid copy() {
        CartesianGrid copy = CartesianGrid.make(this.center.x, this.center.y, this.steps.x, this.steps.y, this.secondaryXDivision, this.secondaryYDivision);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(Stateable obj) {

        if (obj instanceof CartesianGrid) {
            CartesianGrid grid = (CartesianGrid) obj;
            this.center.copyCoordinatesFrom(grid.center);
            this.steps.copyCoordinatesFrom(grid.steps);
            this.secondaryXDivision = grid.secondaryXDivision;
            this.secondaryYDivision = grid.secondaryYDivision;
            this.currentView.copyFrom(grid.currentView);
            recomputeGrid();
            this.getMp().copyFrom(grid.getMp());
            this.getPrimaryGridStyle().copyFrom(grid.getPrimaryGridStyle().getFirstMP());
            this.getSecondaryGridStyle().copyFrom(grid.getSecondaryGridStyle().getFirstMP());

        }
    }


    @Override
    public Rect computeBoundingBox() {
        return getCamera().getMathView();
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
    public void performMathObjectUpdateActions(JMathAnimScene scene) {

    }

    @Override
    public void updateWithCamera(Camera camera) {
        recomputeGrid();
        alignGridToScreen();
    }

    @Override
    public DrawStyleProperties getMp() {
        return mp;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera camera) {
        horizontalSecondaryLines.forEach(t -> t.draw(scene, r, camera));
        verticalSecondaryLines.forEach(t -> t.draw(scene, r, camera));
        horizontalPrimaryLines.forEach(t -> t.draw(scene, r, camera));
        verticalPrimaryLines.forEach(t -> t.draw(scene, r, camera));
    }

    public ArrayList<Line> getHorizontalPrimaryLines() {
        return horizontalPrimaryLines;
    }

    public ArrayList<Line> getVerticalPrimaryLines() {
        return verticalPrimaryLines;
    }

    public ArrayList<Line> getHorizontalSecondaryLines() {
        return horizontalSecondaryLines;
    }

    public ArrayList<Line> getVerticalSecondaryLines() {
        return verticalSecondaryLines;
    }
}
