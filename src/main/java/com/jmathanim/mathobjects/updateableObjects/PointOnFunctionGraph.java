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

import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.AbstractPoint;
import com.jmathanim.mathobjects.FunctionGraph;
import com.jmathanim.mathobjects.Stateable;

/**
 * Updateable point which updates the y-coordinate to be f(x). Shifting this point horizontally moves the point along
 * the funcion graph
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PointOnFunctionGraph extends AbstractPoint<PointOnFunctionGraph> {

    private static double DELTA_DERIVATE = 0.001d;
    private final Vec slopePointRight;
    private final Vec slopePointLeft;
    FunctionGraph fg;

    /**
     * Creates an updateable point which automatically updates the y-component to be so that lies in the function graph
     *
     * @param x  The initial x component of the point
     * @param fg Function graph
     */
    protected PointOnFunctionGraph (double x, FunctionGraph fg) {
        super(Vec.to(x, 0));
        this.fg = fg;
        slopePointRight = Vec.to(x, 0);
        slopePointLeft = Vec.to(x, 0);
        computePoints();
    }

    /**
     * Static builder. Creates and returns a new point at given coordinates.
     *
     * @param x  x coordinate
     * @param fg Function graph
     * @return The created point
     */
    public static PointOnFunctionGraph make(double x, FunctionGraph fg) {
        return new PointOnFunctionGraph(x, fg);
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        computePoints();
    }

    private void computePoints() {
        getVec().y = this.fg.getFunctionValue(getVec().x);
        slopePointRight.x = getVec().x + DELTA_DERIVATE;
        slopePointRight.y = getVec().y + this.fg.getSlope(getVec().x, -1)*DELTA_DERIVATE;

        slopePointLeft.x = getVec().x - DELTA_DERIVATE;
        slopePointLeft.y = getVec().y - this.fg.getSlope(getVec().x, -1)*DELTA_DERIVATE;
    }

    public FunctionGraph getFunctionGraph() {
        return fg;
    }

    public Vec getSlopePointRight() {
        return slopePointRight;
    }

    public Vec getSlopePointLeft() {
        return slopePointLeft;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, fg);
    }

    @Override
    public PointOnFunctionGraph copy() {
        PointOnFunctionGraph copy = new PointOnFunctionGraph(getVec().x, fg);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof PointOnFunctionGraph)) return;
        super.copyStateFrom(obj);
        PointOnFunctionGraph pg = (PointOnFunctionGraph) obj;
    }
}
