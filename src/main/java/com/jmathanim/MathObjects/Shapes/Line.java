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
 * Represents an infinite line, given by 2 points.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Line extends AbstractShape<Line> implements HasDirection, shouldUdpateWithCamera, hasTrivialBoundingBox {

    private final JMPathPoint borderPoint1, borderPoint2;
    protected final Coordinates<?> p1;
    protected final Coordinates<?> p2;
    private Point pointP1;//Visible points to be created on the fly if the user asks for them
    private Point pointP2;

    /**
     * Creates a line that passes through p1 and p2
     *
     * @param p1 First point
     * @param p2 Second point
     */
    public Line(Coordinates<?> p1, Coordinates<?> p2) {
        super();
        this.p1 = p1;
        this.p2 = p2;
        addDependency(this.p1);
        addDependency(this.p2);
        borderPoint1 = new JMPathPoint(Vec.to(0, 0), true);// trivial boundary points, just to
        // initialize objects
        borderPoint2 = new JMPathPoint(Vec.to(0, 0), true);// trivial boundary points, just to
        // initialize objects
        getPath().addJMPoint(borderPoint1, borderPoint2);
        get(0).setSegmentToThisPointVisible(false);
        rebuildShape();
    }

    public static Line XAxis() {
        return new Line(Vec.to(0, 0), Vec.to(1, 0));
    }

    public static Line XYBisector() {
        return new Line(Vec.to(0, 0), Vec.to(1, 1));
    }

    public static Line YAxis() {
        return new Line(Vec.to(0, 0), Vec.to(0, 1));
    }

    /**
     * Creates a new Line object. Line is a Shape object with 2 points, as a Segment but it overrides the draw method so
     * that it extends itself to all the view, to look like an infinite line.
     *
     * @param a First point
     * @param b Second point
     * @return The line object
     */
    public static Line make(Coordinates<?> a, Coordinates<?> b) {
        return new Line(a, b);
    }

    public static Line makePointDir(Coordinates<?> a, Coordinates<?> b) {
        return new Line(a, a.add(b));
    }

    @Override
    public Line applyAffineTransform(AffineJTransform affineJTransform) {
        p1.getVec().applyAffineTransform(affineJTransform);
        p2.getVec().applyAffineTransform(affineJTransform);
        affineJTransform.applyTransformsToDrawingProperties(this);
        rebuildShape();
        return this;
    }

    /**
     * Compute border points in the view area of the given renderer.This is need in order to draw an "infinite" line
     * which always extend to the whole visible area. The border points are stored in bp1 and bp2
     *
     */
    public void rebuildShape() {
        Rect rect = camera.getMathView();
        double[] intersectLine = rect.intersectLine(p1.getVec().x, p1.getVec().y, p2.getVec().x, p2.getVec().y);

        if (intersectLine == null) {
            // If there are no getIntersectionPath points, take p1 and p2 (workaround)
            borderPoint1.getV().copyCoordinatesFrom(p1);
            borderPoint2.getV().copyCoordinatesFrom(p2);
        } else {
            borderPoint1.getV().x = intersectLine[0];
            borderPoint1.getV().y = intersectLine[1];
            borderPoint2.getV().x = intersectLine[2];
            borderPoint2.getV().y = intersectLine[3];
        }
        borderPoint1.getVExit().copyCoordinatesFrom(borderPoint1.getV());
        borderPoint1.getVEnter().copyCoordinatesFrom(borderPoint1.getV());
        borderPoint2.getVExit().copyCoordinatesFrom(borderPoint2.getV());
        borderPoint2.getVEnter().copyCoordinatesFrom(borderPoint2.getV());
        borderPoint1.setSegmentToThisPointVisible(false);
    }

    @Override
    public Line copy() {
        Line resul = new Line(p1.copy(), p2.copy());
        resul.copyStateFrom(this);
        return resul;
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        super.copyStateFrom(obj);
        if (!(obj instanceof Line)) return;
            Line line= (Line) obj;
        p1.copyCoordinatesFrom(line.p1);
        p2.copyCoordinatesFrom(line.p2);
        rebuildShape();
    }

    @Override
    protected Rect computeBoundingBox() {
        //Bounding box of the visible segment
        rebuildShape();
        return Rect.make(borderPoint1, borderPoint2);
    }

    /**
     * Returns the coordinates of the line lying in the boundaries of the math view. From the 2 points of the boundary,
     * this is next to p1.
     *
     * @return A vector with the coordinates of the boundary point
     */
    public Vec getBorderPoint1() {
        update(scene);
        return borderPoint1.getVec();
    }

    /**
     * Returns the coordinates of the line lying in the boundaries of the math view. From the 2 points of the boundary,
     * this is next to p2.
     *
     * @return A vector with the coordinates of the boundary point
     */
    public Vec getBorderPoint2() {
        update(scene);
        return borderPoint2.getVec();
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
        return p1.getVec();
    }

    public Vec getP2() {
        return p2.getVec();
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
        JMPathPoint a = borderPoint1.copy().scale(getCenter(), scale, scale);
        JMPathPoint b = borderPoint2.copy().scale(getCenter(), scale, scale);
        Shape segment = Shape.segment(a, b);
        segment.getMp().copyFrom(this.getMp());
        return segment;
    }

    @Override
    public Shape toShape() {
        return toSegment(camera,1);
    }

    public Shape toSegment(Camera cam) {
        return toSegment(cam, 1);
    }


    @Override
    protected void performUpdateActions(JMathAnimScene scene) {
        rebuildShape();
    }

    @Override
    public void updateWithCamera(Camera camera) {
        rebuildShape();
    }

}
