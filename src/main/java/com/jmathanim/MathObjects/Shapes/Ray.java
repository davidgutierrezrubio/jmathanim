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
package com.jmathanim.MathObjects.Shapes;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Constructible.Lines.HasDirection;
import com.jmathanim.MathObjects.*;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * Represents an infinite ray from A an direction AB
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Ray extends AbstractShape<Ray> implements HasDirection {

    private final JMPathPoint boundaryPoint1, boundaryPoint2;
    private final Vec p1;
    private final Vec p2;

    /**
     * Creates a ray that passes through p with direction v
     *
     * @param p Point
     * @param v Direction vector
     */
    public Ray(Point p, Vec v) {
        this(p, p.add(v));
    }

    /**
     * Creates a new line that passes through given points, with specified MathDrawingProperties
     *
     * @param p1 First point
     * @param p2 Second point
     */
    protected Ray(Coordinates<?> p1, Coordinates<?> p2) {
        super();
        this.p1 = p1.getVec();
        this.p2 = p2.getVec();
        addDependency(this.p1);
        addDependency(this.p2);
        boundaryPoint1 = new JMPathPoint(this.p1, true);// trivial boundary points, just to
        // initialize objects
        boundaryPoint2 = new JMPathPoint(Point.at(0, 0), true);// trivial boundary points, just to
        // initialize objects
        getPath().addJMPoint(boundaryPoint1, boundaryPoint2);
        get(0).setSegmentToThisPointVisible(false);
        rebuildShape();
    }


    public static Ray XAxisPositive() {
        return new Ray(Point.at(0, 0), Point.at(1, 0));
    }

    public static Ray XAxisNegative() {
        return new Ray(Point.at(0, 0), Point.at(-1, 0));
    }

    public static Ray XYBisectorPositive() {
        return new Ray(Point.at(0, 0), Point.at(1, 1));
    }

    public static Ray XYBisectorNegative() {
        return new Ray(Point.at(0, 0), Point.at(-1, -1));
    }

    public static Ray YAxisPositive() {
        return new Ray(Point.at(0, 0), Point.at(0, 1));
    }

    public static Ray YAxisNegative() {
        return new Ray(Point.at(0, 0), Point.at(0, -1));
    }

    /**
     * Creates a new Ray object. Ray is a Shape object with 2 points, as a Segment but it overrides the draw method so
     * that it extends itself to all the view, to look like an infinite ray.
     *
     * @param a First point (start of ray)
     * @param b Second point (ray will pass through here)
     * @return The Ray object
     */
    public static Ray make(Coordinates<?> a, Coordinates<?> b) {
        return new Ray(a, b);
    }

    /**
     * Returns a ray starting from point a with direction given by a vector
     *
     * @param start     Starting point
     * @param direction Direction
     * @return The Ray object
     */
    public static Ray makePointDir(Coordinates<?> start, Vec direction) {
        return new Ray(start, start.add(direction));
    }

    @Override
    public Ray applyAffineTransform(AffineJTransform affineJTransform) {
        getP1().getVec().applyAffineTransform(affineJTransform);
        getP2().getVec().applyAffineTransform(affineJTransform);
        affineJTransform.applyTransformsToDrawingProperties(this);
        return this;
    }

    /**
     * Compute border points in the view area of the given renderer. This is need in order to draw an "infinite" ray
     * which always extend to the whole visible area. The border points is stored in bp2
     *
     */
    public final void rebuildShape() {
        Rect rect = camera.getMathView();
        Vec p1v = p1.getVec();
        Vec p2v = p2.getVec();
        double[] intersectLine = rect.intersectLine(p1v.x, p1v.y, p2v.x, p2v.y);

        if (intersectLine == null) {
            // If there are no getIntersectionPath points, take p1 and p2 (workaround)
            boundaryPoint2.getV().copyCoordinatesFrom(p2v);
        } else {
            boundaryPoint2.getV().x = intersectLine[2];
            boundaryPoint2.getV().y = intersectLine[3];
        }
        boundaryPoint2.getVExit().x = boundaryPoint2.getV().x;
        boundaryPoint2.getVExit().y = boundaryPoint2.getV().y;
        boundaryPoint2.getVEnter().x = boundaryPoint2.getV().x;
        boundaryPoint2.getVEnter().y = boundaryPoint2.getV().y;

    }


    @Override
    public void copyStateFrom(Stateable obj) {
        super.copyStateFrom(obj);
        if (!(obj instanceof Ray)) return;
        Ray ray= (Ray) obj;
        p1.copyCoordinatesFrom(ray.p1);
        p2.copyCoordinatesFrom(ray.p2);
        rebuildShape();
    }

    @Override
    public Rect computeBoundingBox() {
        //Bounding box of the visible segment
        rebuildShape();
        return Rect.make(boundaryPoint1, boundaryPoint2);
    }

    @Override
    public Ray copy() {
        Ray resul = new Ray(p1.getVec().copy(), p2.getVec().copy());
        resul.copyStateFrom(this);
        return resul;
    }


    /**
     * Returns the point of the line lying in the boundaries of the math view. From the 2 points of the boundary, this
     * is next to p1.
     *
     * @param scene The scene, needed to obtain the math view
     * @return A referenced copy of the boundary point
     */
    public Vec getBorderPoint1(JMathAnimScene scene) {
        update();
        return boundaryPoint1.getV();
    }

    /**
     * Returns the point of the line lying in the boundaries of the math view. From the 2 points of the boundary, this
     * is next to p2.
     *
     * @param scene The scene, needed to obtain the math view
     * @return A referenced copy of the boundary point
     */
    public Vec getBorderPoint2(JMathAnimScene scene) {
        update();
        return boundaryPoint2.getV();
    }

    /**
     * Returns the center of the object. As the center of an infinite line doesn't exists, return the first of the
     * generating points instead.
     *
     * @return The first point of the generator points p1 and p2
     */
    @Override
    public Vec getCenter() {
        return p1.getVec().copy();
    }

    @Override
    public Vec getDirection() {
        return p1.to(p2);
    }

    public Vec getP1() {
        return p1;
    }

    public Vec getP2() {
        return p2;
    }


    /**
     * Creates a finite Segment, that runs over the screen plus a percent gap
     *
     * @param cam   Camera with math view
     * @param scale Scale to apply. 1 returns the visible part of the line.
     * @return A segment with the visibleFlag part of the line
     */
    public Shape toSegment(Camera cam, double scale) {
        rebuildShape();
        JMPathPoint a = boundaryPoint1.copy().scale(getCenter(), scale, scale);
        JMPathPoint b = boundaryPoint2.copy().scale(getCenter(), scale, scale);
        Shape segment = Shape.segment(a, b);
        segment.getMp().copyFrom(this.getMp());
        return segment;
    }

    @Override
    public Shape toShape() {
        return toSegment(camera, 1);
    }

    public Shape toSegment(Camera cam) {
        return toSegment(cam, 1);
    }


    @Override
    public void performMathObjectUpdateActions() {
        rebuildShape();
    }
}
