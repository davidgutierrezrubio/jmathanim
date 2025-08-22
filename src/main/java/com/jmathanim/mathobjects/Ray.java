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
import com.jmathanim.Constructible.Lines.HasDirection;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.MODrawPropertiesArray;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint.JMPathPointType;

/**
 * Represents an infinite ray from A an direction AB
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Ray extends Shape implements HasDirection {

    private final JMPathPoint bp1, bp2;
    private final Point p1;
    private final Point p2;
    private final Shape visiblePiece;
    MODrawPropertiesArray mpArray;

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
    public Ray(Point p1, Point p2) {
        super();
        mpArray = new MODrawPropertiesArray();
        mpArray.copyFrom(JMathAnimConfig.getConfig().getDefaultMP());// Default MP values);
        this.p1 = p1;
        this.p2 = p2;
        getPath().clear(); // Super constructor adds p1, p2. Delete them
        bp1 = new JMPathPoint(this.p1, true, JMPathPointType.VERTEX);// trivial boundary points, just to
        // initialize objects
        bp2 = new JMPathPoint(new Point(0, 0), true, JMPathPointType.VERTEX);// trivial boundary points, just to
        // initialize objects
        visiblePiece = new Shape();
        visiblePiece.getPath().addJMPoint(bp1, bp2);
        visiblePiece.getMp().copyFrom(this.getMp());
        getPath().addPoint(p1, p2);
        get(0).isThisSegmentVisible = false;
        mpArray.add(visiblePiece);
    }

    public static Ray XAxisPositive() {
        return new Ray(new Point(0, 0), new Point(1, 0));
    }

    public static Ray XAxisNegative() {
        return new Ray(new Point(0, 0), new Point(-1, 0));
    }

    public static Ray XYBisectorPositive() {
        return new Ray(new Point(0, 0), new Point(1, 1));
    }

    public static Ray XYBisectorNegative() {
        return new Ray(new Point(0, 0), new Point(-1, -1));
    }

    public static Ray YAxisPositive() {
        return new Ray(new Point(0, 0), new Point(0, 1));
    }

    public static Ray YAxisNegative() {
        return new Ray(new Point(0, 0), new Point(0, -1));
    }

    /**
     * Creates a new Ray object. Ray is a Shape object with 2 points, as a Segment but it overrides the draw method so
     * that it extends itself to all the view, to look like an infinite ray.
     *
     * @param a First point (start of ray)
     * @param b Second point (ray will pass through here)
     * @return The Ray object
     */
    public static Ray make(Point a, Point b) {
        return new Ray(a, b);
    }

    /**
     * Overloaded builder. Returns a ray starting from point a with direction given by a vector
     *
     * @param start     Starting point
     * @param direction Direction
     * @return The Ray object
     */
    public static Ray make(Point start, Vec direction) {
        return new Ray(start, start.add(direction));
    }

    @Override
    public Ray applyAffineTransform(AffineJTransform transform) {
        getP1().applyAffineTransform(transform);
        getP2().applyAffineTransform(transform);
        if (hasMPCreated())
            transform.applyTransformsToDrawingProperties(this);
        return this;
    }

    /**
     * Compute border points in the view area of the given renderer. This is need in order to draw an "infinite" ray
     * which always extend to the whole visible area. The border points is stored in bp2
     *
     * @param cam Camera with the view to compute bound points
     */
    public final void computeBoundPoints(Camera cam) {
        Rect rect = cam.getMathView();
        double[] intersectLine = rect.intersectLine(p1.v.x, p1.v.y, p2.v.x, p2.v.y);

        if (intersectLine == null) {
            // If there are no getIntersectionPath points, take p1 and p2 (workaround)
            bp2.v.x = p2.v.x;
            bp2.v.y = p2.v.y;
        } else {
            bp2.v.x = intersectLine[2];
            bp2.v.y = intersectLine[3];
        }
        bp2.vExit.x = bp2.v.x;
        bp2.vExit.y = bp2.v.y;
        bp2.vEnter.x = bp2.v.x;
        bp2.vEnter.y = bp2.v.y;

    }

    @Override
    public Ray copy() {
        Ray resul = new Ray(p1.copy(), p2.copy());
        resul.getMp().copyFrom(getMp());
        return resul;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        update(scene);
        if (isVisible()) {
            visiblePiece.draw(scene, r, cam);
        }
        scene.markAsAlreadydrawn(this);
    }

    /**
     * Returns the point of the line lying in the boundaries of the math view. From the 2 points of the boundary, this
     * is next to p1.
     *
     * @param scene The scene, needed to obtain the math view
     * @return A referencedCopy of the boundary point
     */
    public Point getBorderPoint1(JMathAnimScene scene) {
        update(scene);
        return Point.at(bp1.v.copy());
    }

    /**
     * Returns the point of the line lying in the boundaries of the math view. From the 2 points of the boundary, this
     * is next to p2.
     *
     * @param scene The scene, needed to obtain the math view
     * @return A referencedCopy of the boundary point
     */
    public Point getBorderPoint2(JMathAnimScene scene) {
        update(scene);
        return Point.at(bp2.v.copy());
    }

    /**
     * Returns the center of the object. As the center of an infinite line doesn't exists, return the first of the
     * generating points instead.
     *
     * @return The first point of the generator points p1 and p2
     */
    @Override
    public Point getCenter() {
        return p1.copy();
    }

    @Override
    public Vec getDirection() {
        return p1.to(p2);
    }

    @Override
    public final Stylable getMp() {
        return mpArray;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(p1, p2);
        setUpdateLevel(Math.max(p1.getUpdateLevel(), p2.getUpdateLevel()) + 1);
    }

    @Override
    public void restoreState() {
        super.restoreState();
        p1.restoreState();
        p2.restoreState();
    }

    @Override
    public void saveState() {
        super.saveState();
        p1.saveState();
        p2.saveState();
    }

    /**
     * Creates a finite Segment, that runs over the screen plus a percent gap
     *
     * @param cam   Camera with math view
     * @param scale Scale to apply. 1 returns the visible part of the line.
     * @return A segment with the visibleFlag part of the line
     */
    public Shape toSegment(Camera cam, double scale) {
        computeBoundPoints(cam);
        Point a = bp1.copy().scale(getCenter(), scale, scale);
        Point b = bp2.copy().scale(getCenter(), scale, scale);
        Shape segment = Shape.segment(a, b);
        segment.getMp().copyFrom(this.getMp());
        return segment;
    }

    public Shape toSegment(Camera cam) {
        return toSegment(cam, 1);
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        computeBoundPoints(getCamera());
    }

}
