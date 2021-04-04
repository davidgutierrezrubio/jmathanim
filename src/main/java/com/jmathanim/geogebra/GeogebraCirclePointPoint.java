/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.geogebra;

import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * Represents a Circle imported from Geogebra with 2 points (center and another
 * one in the perimeter)
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class GeogebraCirclePointPoint extends Shape {

    //Circle(point,point)
    //Circle(point,number)
    //Circle(point,Segment)
    //Circle(point,point,point)
    Point A, B;
    protected double radius;
    public final Point circleCenter;
    private final Shape originalCircle;

    public static GeogebraCirclePointPoint make(Point A, Point B) {
        GeogebraCirclePointPoint resul = new GeogebraCirclePointPoint(A, B);
        resul.rebuildShape();
        return resul;
    }

    protected GeogebraCirclePointPoint(Point A, Point B) {
        super();
        this.A = A;
        this.B = B;
        originalCircle = Shape.circle();
        circleCenter = Point.at(0, 0);
    }

    public final void rebuildShape() {
        computeCircleCenterRadius();
        this.getPath().jmPathPoints.clear();
        this.getPath().addJMPointsFrom(originalCircle.copy().getPath());
        this.scale(this.radius);
        this.shift(this.circleCenter.v);
    }

    public void computeCircleCenterRadius() {
        this.radius = A.to(B).norm();
        this.circleCenter.v.x=A.v.x;
        this.circleCenter.v.y=A.v.y;
    }

    @Override
    public int getUpdateLevel() {
        return Math.max(A.getUpdateLevel(), B.getUpdateLevel()) + 1;
    }

    @Override
    public void update(JMathAnimScene scene) {
        rebuildShape();
    }

    @Override
    public void saveState() {
        A.saveState();
        B.saveState();
    }

    @Override
    public void restoreState() {
        A.restoreState();
        B.restoreState();
    }

}
