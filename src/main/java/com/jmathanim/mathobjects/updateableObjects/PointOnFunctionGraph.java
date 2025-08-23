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
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Enum.DotStyle;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.FunctionGraph;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.updaters.Coordinates;

/**
 * Updateable point which updates the y-coordinate to be f(x). Shifting this
 * point horizontally moves the point along the funcion graph
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PointOnFunctionGraph extends MathObject<PointOnFunctionGraph> implements Coordinates {

    FunctionGraph fg;
    private final Point slopePointRight;
    private final Point slopePointLeft;
    private final Point pointOnFunction;
    private static double DELTA_DERIVATE=0.000001d;

    /**
     * Static builder. Creates and returns a new point at given coordinates.
     *
     * @param x x coordinate
     * @param fg Function graph
     * @return The created point
     */
    public static PointOnFunctionGraph make(double x, FunctionGraph fg) {
        return new PointOnFunctionGraph(x, fg);
    }

    /**
     * Creates an updateable point which automatically updates the y-component
     * to be so that lies in the function graph
     *
     * @param x The initial x component of the point
     * @param fg Function graph
     */
    public PointOnFunctionGraph(double x, FunctionGraph fg) {
        super();
        this.fg = fg;
        slopePointRight = Point.at(x, 0);
        slopePointLeft = Point.at(x, 0);
        pointOnFunction=Point.at(x,0);
        computePoints();
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        computePoints();
    }

    private void computePoints() {
        pointOnFunction.v.y = this.fg.getFunctionValue(pointOnFunction.v.x);
        slopePointRight.v.x = pointOnFunction.v.x + DELTA_DERIVATE;
        slopePointRight.v.y = pointOnFunction.v.y + this.fg.getSlope(pointOnFunction.v.x, -1);

        slopePointLeft.v.x = pointOnFunction.v.x - DELTA_DERIVATE;
        slopePointLeft.v.y = pointOnFunction.v.y - this.fg.getSlope(pointOnFunction.v.x, -1);
    }

    public FunctionGraph getFg() {
        return fg;
    }

    public Point getSlopePointRight() {
        return slopePointRight;
    }

    public Point getSlopePointLeft() {
        return slopePointLeft;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera camera) {
        pointOnFunction.draw(scene, r, camera);
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, fg);
    }

    @Override
    public PointOnFunctionGraph copy() {
        PointOnFunctionGraph copy = new PointOnFunctionGraph(pointOnFunction.v.x, fg);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    protected Rect computeBoundingBox() {
        return pointOnFunction.getBoundingBox();
    }

    @Override
    public void copyStateFrom(MathObject<?> obj) {
        super.copyStateFrom(obj);
        PointOnFunctionGraph pg = (PointOnFunctionGraph) obj;
        pointOnFunction.v.y = pg.pointOnFunction.v.y;
    }

    @Override
    public PointOnFunctionGraph applyAffineTransform(AffineJTransform tr) {
        super.applyAffineTransform(tr);
        computePoints();
        return this;
    }

    @Override
    public Vec getVec() {
        return pointOnFunction.getVec();
    }

    @Override
    public Stylable getMp() {
        return pointOnFunction.getMp();
    }

    public PointOnFunctionGraph dotStyle(DotStyle dotStyle) {
        getMp().setDotStyle(dotStyle);
        return this;
    }
}
