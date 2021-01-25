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
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint.JMPathPointType;

/**
 * Represents an infinite line, given by 2 points.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Line extends Shape {

    private final JMPathPoint bp1, bp2;
    private final Shape visiblePiece;
    private Point p1, p2;

    /**
     * Creates a line that passes through p with direction v
     *
     * @param p Point
     * @param v Direction vector
     */
    public Line(Point p, Vec v) {
        this(p, p.add(v));
    }

    /**
     * Creates a line that passes through p1 and p2
     *
     * @param p1 First point
     * @param p2 Second point
     */
    public Line(Point p1, Point p2) {
        this(p1, p2, null);
    }

    /**
     * Creates a new line that passes through given points, with specified
     * MathDrawingProperties
     *
     * @param p1 First point
     * @param p2 Second point
     * @param mp MathDrawingProperties
     */
    public Line(Point p1, Point p2, MODrawProperties mp) {
        super(mp);
        this.p1 = p1;
        this.p2 = p2;
        getPath().clear(); //Super constructor adds p1, p2. Delete them
        bp1 = new JMPathPoint(new Point(0, 0), true, JMPathPointType.VERTEX);//trivial boundary points, just to initialize objects
        bp2 = new JMPathPoint(new Point(0, 0), true, JMPathPointType.VERTEX);//trivial boundary points, just to initialize objects
        visiblePiece = new Shape();
        visiblePiece.getPath().addJMPoint(bp1, bp2);
        visiblePiece.getMp().copyFrom(this.getMp());
        getPath().addPoint(p1, p2);
        getPath().getJMPoint(0).isThisSegmentVisible = false;
    }

    @Override
    public Line copy() {
        Line resul = new Line(p1.copy(), p2.copy());
        resul.getMp().copyFrom(getMp());
        return resul;
    }

    /**
     * Returns the point of the line lying in the boundaries of the math view.
     * From the 2 points of the boundary, this is next to p1.
     *
     * @param scene The scene, needed to obtain the math view
     * @return A copy of the boundary point
     */
    public Point getBorderPoint1(JMathAnimScene scene) {
        update(scene);
        return bp1.p.copy();
    }

    /**
     * Returns the point of the line lying in the boundaries of the math view.
     * From the 2 points of the boundary, this is next to p2.
     *
     * @param scene The scene, needed to obtain the math view
     * @return A copy of the boundary point
     */
    public Point getBorderPoint2(JMathAnimScene scene) {
        update(scene);
        return bp2.p.copy();
    }

    @Override
    public void draw(Renderer r) {
        update(JMathAnimConfig.getConfig().getScene());//TODO: remove coupling
        visiblePiece.draw(r);
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        computeBoundPoints(scene.getCamera());
    }

    @Override
    public int getUpdateLevel() {
        return Math.max(p1.getUpdateLevel(), p2.getUpdateLevel()) + 1;
    }

    /**
     * Compute border points in the view area of the given renderer.This is need
     * in order to draw an "infinite" line which always extend to the whole
     * visible area. The border points are stored in bp1 and bp2
     *
     * @param cam Camera with the view to compute bound points
     */
    public final void computeBoundPoints(Camera cam) {
        Rect rect = cam.getMathView();
        double[] intersectLine = rect.intersectLine(p1.v.x, p1.v.y, p2.v.x, p2.v.y);

        if (intersectLine == null) {
            //If there are no intersect points, take p1 and p2 (workaround)
            bp1.p.v.x = p1.v.x;
            bp1.p.v.y = p1.v.y;
            bp2.p.v.x = p2.v.x;
            bp2.p.v.y = p2.v.y;
        } else {
            bp1.p.v.x = intersectLine[0];
            bp1.p.v.y = intersectLine[1];
            bp2.p.v.x = intersectLine[2];
            bp2.p.v.y = intersectLine[3];
        }
        bp1.cpExit.v.x = bp1.p.v.x;
        bp1.cpExit.v.y = bp1.p.v.y;
        bp1.cpEnter.v.x = bp1.p.v.x;
        bp1.cpEnter.v.y = bp1.p.v.y;
        bp2.cpExit.v.x = bp2.p.v.x;
        bp2.cpExit.v.y = bp2.p.v.y;
        bp2.cpEnter.v.x = bp2.p.v.x;
        bp2.cpEnter.v.y = bp2.p.v.y;

    }

    @Override
    public void saveState() {
        super.saveState();
        p1.saveState();
        p2.saveState();
    }

    @Override
    public void restoreState() {
        super.restoreState();
        p1.restoreState();
        p2.restoreState();
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    /**
     * Returns the center of the object. As the center of an infinite line
     * doesn't exists. Take middle point of p1 and p2 instead.
     *
     * @return The middle point of the generator points p1 and p2
     */
    @Override
    public Point getCenter() {

        return p1.interpolate(p2, .5);
    }

    public static Line XAxis() {
        return new Line(new Point(0, 0), new Point(1, 0));
    }

    public static Line YAxis() {
        return new Line(new Point(0, 0), new Point(0, 1));
    }

    public static Line XYBisector() {
        return new Line(new Point(0, 0), new Point(1, 1));
    }

    /**
     * Creates a new Line object. Line is a Shape object with 2 points, as a
     * Segment but it overrides the draw method so that it extends itself to all
     * the view, to look like an infinite line.
     *
     * @param a First point
     * @param b Second point
     * @return The line object
     */
    public static Line make(Point a, Point b) {
        return new Line(a, b);
    }

    public static Line make(Point a, Vec b) {
        return new Line(a, a.add(b));
    }

    /**
     * Creates a finite Segment, that runs over the screen plus a percent gap
     *
     * @param cam Camera with math view
     * @param scale Scale to apply. 1 returns the visible part of the line.
     * @return A segment with the visibleFlag part of the line
     */
    public Shape toSegment(Camera cam, double scale) {
        computeBoundPoints(cam);
        Point a = bp1.p.copy().scale(getCenter(), scale, scale);
        Point b = bp2.p.copy().scale(getCenter(), scale, scale);
        Shape segment = Shape.segment(a, b);
        segment.getMp().copyFrom(this.getMp());
        return segment;
    }

    public Shape toSegment(Camera cam) {
        return toSegment(cam, 1);
    }

    @Override
    public <T extends MathObject> T applyLinearTransform(AffineJTransform tr) {
            getP1().applyLinearTransform(tr);
            getP2().applyLinearTransform(tr);
            tr.applyTransformsToDrawingProperties(this);
            return (T) this;
    }
    
}
