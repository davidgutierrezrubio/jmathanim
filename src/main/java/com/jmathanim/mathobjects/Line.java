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
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * Represents an infinite line, given by 2 points.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Line extends MathObject<Line> implements HasDirection, shouldUdpateWithCamera {

    private final JMPathPoint bp1, bp2;
    private final Vec p1;
    private final Vec p2;
    private final Shape visiblePiece;
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
        this.p1 = p1.getVec();
        this.p2 = p2.getVec();
        bp1 = new JMPathPoint(Vec.to(0, 0), true);// trivial boundary points, just to
        // initialize objects
        bp2 = new JMPathPoint(Vec.to(0, 0), true);// trivial boundary points, just to
        // initialize objects
        visiblePiece = new Shape();
        visiblePiece.getPath().addJMPoint(bp1, bp2);
        visiblePiece.get(0).setThisSegmentVisible(false);
        setCamera(JMathAnimConfig.getConfig().getCamera());//First default camera
        computeBoundPoints(getCamera());
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

    public static Line make(Point a, Vec b) {
        return new Line(a, a.add(b));
    }

    @Override
    public Line applyAffineTransform(AffineJTransform transform) {
        p1.applyAffineTransform(transform);
        p2.applyAffineTransform(transform);
        transform.applyTransformsToDrawingProperties(this);
        return this;
    }

    /**
     * Compute border points in the view area of the given renderer.This is need in order to draw an "infinite" line
     * which always extend to the whole visible area. The border points are stored in bp1 and bp2
     *
     * @param cam Camera with the view to compute bound points
     */
    private void computeBoundPoints(Camera cam) {
        Rect rect = cam.getMathView();
        double[] intersectLine = rect.intersectLine(p1.x, p1.y, p2.x, p2.y);

        if (intersectLine == null) {
            // If there are no getIntersectionPath points, take p1 and p2 (workaround)
            bp1.getV().x = p1.x;
            bp1.getV().y = p1.y;
            bp2.getV().x = p2.x;
            bp2.getV().y = p2.y;
        } else {
            bp1.getV().x = intersectLine[0];
            bp1.getV().y = intersectLine[1];
            bp2.getV().x = intersectLine[2];
            bp2.getV().y = intersectLine[3];
        }
        bp1.getvExit().copyCoordinatesFrom(bp1.getV());
        bp1.getvEnter().copyCoordinatesFrom(bp1.getV());
        bp2.getvExit().copyCoordinatesFrom(bp2.getV());
        bp2.getvEnter().copyCoordinatesFrom(bp2.getV());
        bp1.setThisSegmentVisible(false);
    }

    @Override
    public Line copy() {
        Line resul = new Line(p1.copy(), p2.copy());
        resul.getMp().copyFrom(getMp());
        return resul;
    }

    @Override
    protected Rect computeBoundingBox() {
        JMathAnimScene.logger.warn("Trying to compute bounding box of an infinite line, returning EmptyRect");
        return new EmptyRect();
    }


    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        update(JMathAnimConfig.getConfig().getScene());// TODO: remove coupling
        if (isVisible()) {
            visiblePiece.draw(scene, r, cam);
        }
        scene.markAsAlreadydrawn(this);
    }

    /**
     * Returns the coordinates of the line lying in the boundaries of the math view. From the 2 points of the boundary,
     * this is next to p1.
     *
     * @return A vector with the coordinates of the boundary point
     */
    public Vec getBorderPoint1() {
        update(scene);
        return bp1.getVec();
    }

    /**
     * Returns the coordinates of the line lying in the boundaries of the math view. From the 2 points of the boundary,
     * this is next to p2.
     *
     * @return A vector with the coordinates of the boundary point
     */
    public Vec getBorderPoint2() {
        update(scene);
        return bp2.getVec();
    }

    /**
     * Returns the center of the object. As the center of an infinite line doesn't exists, return the first of the
     * generating points instead.
     *
     * @return The first point of the generator points p1 and p2
     */
    @Override
    public Vec getCenter() {
        return p1.copy();
    }

    @Override
    public Vec getDirection() {
        return p1.to(p2);
    }

    @Override
    public DrawStyleProperties getMp() {
        return visiblePiece.getMp();
    }

    public Coordinates<Point> getP1() {
        if (pointP1 == null) {
            pointP1 = Point.at(p1);
        }
        return pointP1;
    }

    public Point getP2() {
        if (pointP2 == null) {
            pointP2 = Point.at(p2);
        }
        return pointP2;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, p1, p2);//You can safely pass null to this method
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
        JMPathPoint a = bp1.copy().scale(getCenter(), scale, scale);
        JMPathPoint b = bp2.copy().scale(getCenter(), scale, scale);
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

    @Override
    public void updateWithCamera(Camera camera) {
        computeBoundPoints(camera);
    }

}
