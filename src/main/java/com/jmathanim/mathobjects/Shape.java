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
import com.jmathanim.Renderers.FXRenderer.FXPathUtils;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimScene;
import javafx.scene.shape.Path;

import java.util.ArrayList;
import java.util.OptionalInt;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Shape extends MathObject {

    protected final JMPath jmpath;
    protected boolean showDebugPoints = false;
    protected boolean isConvex = false;

    public Shape() {
        super();
        this.jmpath = new JMPath();
    }

    public Shape(JMPath jmpath) {
        super();
        this.jmpath = jmpath;
    }

//    public Shape(JMPath jmpath, MODrawProperties mp) {
//        super(jmpath,mp);
//    }

    //    @Override
//    public <T extends MathObject> T shift(Vec shiftVector) {
//        jmpath.shift(shiftVector);
//        return (T) this;
//    }
//    public void removeInterpolationPoints() {
//        jmpath.removeInterpolationPoints();
//    }
    // Static methods to build most used shapes
    public static Shape square() {
        Shape obj = Shape.rectangle(Point.origin(), Point.at(1, 1));
        obj.objectLabel = "square";
        return obj;
    }

    public static Shape square(Point A, double side) {
        return Shape.rectangle(A, A.add(new Vec(side, side)));
    }

    /**
     * Creates a rectangle shape from a Rect object
     *
     * @param r Then Rect object
     * @return The created rectangle
     */
    public static Shape rectangle(Rect r) {
        return Shape.rectangle(r.getDL(), r.getUR());
    }

    /**
     * Creates a segment shape between 2 given points. The parameters points
     * will be referenced to create the segment, so moving them will modify the
     * segment.
     *
     * @param A First point
     * @param B Second point
     * @return The created segment.
     */
    public static Shape segment(Point A, Point B) {
        Shape obj = new Shape();
        JMathAnimConfig.getConfig().getScene();
        JMPathPoint p1 = JMPathPoint.lineTo(A);
        p1.isThisSegmentVisible = false;
        JMPathPoint p2 = JMPathPoint.lineTo(B);
        obj.jmpath.addJMPoint(p1, p2);
        return obj;
    }

    /**
     * Creates a rectangle shape from 2 opposite points
     *
     * @param A First point
     * @param B Second point
     * @return The rectangle
     */
    public static Shape rectangle(Point A, Point B) {
        Shape obj = new Shape();
        JMPathPoint p1 = JMPathPoint.lineTo(A);
        JMPathPoint p2 = JMPathPoint.lineTo(B.v.x, A.v.y);
        JMPathPoint p3 = JMPathPoint.lineTo(B);
        JMPathPoint p4 = JMPathPoint.lineTo(A.v.x, B.v.y);
        obj.jmpath.addJMPoint(p1, p2, p3, p4);
        return obj;
    }

    /**
     * Creates a rectangle shape from 3 consecutive points.
     *
     * @param A First point
     * @param B Second point. The fourth point will be the opposite of this
     *          point. This will be the point with index 0 in the path.
     * @param C Third point
     * @return The rectangle
     */
    public static Shape rectangle(Point A, Point B, Point C) {
        Shape obj = new Shape();

        JMPathPoint p1 = JMPathPoint.lineTo(B);
        JMPathPoint p2 = JMPathPoint.lineTo(C);
        JMPathPoint p3 = JMPathPoint.lineTo(C.add(B.to(A)));
        JMPathPoint p4 = JMPathPoint.lineTo(A);
        obj.jmpath.addJMPoint(p1, p2, p3, p4);
        return obj;
    }

    /**
     * Creates a polygonal shape with the given points
     *
     * @param points Points of the polygon, varargs or array Point[]
     * @return The polygon
     */
    public static Shape polygon(Point... points) {
        Shape obj = new Shape();
        for (Point newPoint : points) {
            JMPathPoint p = JMPathPoint.lineTo(newPoint);
            obj.getPath().addJMPoint(p);
        }
        return obj;
    }

    // Static methods to build most commons shapes

    /**
     * Creates a shape composed of multiple connected segments
     *
     * @param points Points to connect. Varargs or array Point[]
     * @return The polyline
     */
    public static Shape polyLine(Point... points) {
        Shape obj = polygon(points);
        obj.objectLabel = "polyLine";
        obj.get(0).isThisSegmentVisible = false;
        return obj;
    }

    /**
     * Creates a basic right-angled triangle shape (0,0)-(1,0)-(0,1)
     *
     * @return
     */
    public static Shape triangle() {
        return polygon(Point.at(0, 0), Point.at(1, 0), Point.at(0, 1));
    }

    /**
     * Generates a regular polygon shape inscribed in a unit circle. The first
     * point of the shape lies in the coordinates (1,0)
     *
     * @param numSides Number of sides
     * @return The generated Shape
     */
    public static Shape regularInscribedPolygon(int numSides) {
        Point[] points = new Point[numSides];
        for (int i = 0; i < numSides; i++) {
            points[i] = Point.at(Math.cos(2 * PI * i / numSides), Math.sin(2 * PI * i / numSides));
        }
        return polygon(points);
    }

    /**
     * Creates a regular polygon shape, with first vertex at (0,0) and side 1
     *
     * @param numsides Number of sides
     * @return A Shape object representing the polygon
     */
    public static Shape regularPolygon(int numsides) {
        Shape obj = new Shape();
        obj.objectLabel = "regPol";
        Point newPoint = Point.origin();
        for (int n = 0; n < numsides; n++) {
            double alpha = 2 * n * Math.PI / numsides;
            Vec moveVector = new Vec(Math.cos(alpha), Math.sin(alpha));
            newPoint = newPoint.add(moveVector);
            JMPathPoint p = JMPathPoint.lineTo(newPoint);
            obj.getPath().addJMPoint(p);
        }
        return obj;
    }

    /**
     * Creates an arc shape with radius 1 and center origin. First point is
     * (1,0)
     *
     * @param angle       Angle in radians of the arc
     * @param numSegments Number of segments
     * @return The created arc
     */
    public static Shape arc(double angle, int numSegments) {
        double c = 0.15915494309189533576888376d;//0.5/PI
        Shape obj = Shape.circle(32).getSubShape(0, c * angle);
        obj.objectLabel = "arc";
        return obj;
    }

    /**
     * Creates an arc shape with radius 1 and center origin. First point is
     * (1,0). Default value of 32 segments.
     *
     * @param angle Angle in radians of the arc
     * @return The created arc
     */
    public static Shape arc(double angle) {
        return arc(angle, 32);
    }

    /**
     * Creates an arch Shape from A to B with given radius. The arc can be drawn
     * counterclockwise or clockwise depending on the boolean parameter.
     * If the radius is too small to draw an arc, an straight segment is drawn instead.
     *
     * @param startPoint         Starting point
     * @param endPoint           Ending point
     * @param radius             Radius of the arc
     * @param isCounterClockwise If true, draws the counterclockwise arc, clockwise otherwise.
     * @return The created arc
     */

    public static Shape arc(Point startPoint, Point endPoint, double radius, boolean isCounterClockwise) {
        // First, compute arc center
        Vec midpointVector = startPoint.to(endPoint).mult(0.5);
        Vec radiusVector = midpointVector.rotate(PI / 2).normalize();
        double squaredDistance = midpointVector.dot(midpointVector);
        double discriminant = Math.max(0, radius * radius - squaredDistance);
        double radiusOffset = Math.sqrt(discriminant);
        Point arcCenter = startPoint.copy().shift(midpointVector).shift(radiusVector.mult(radiusOffset));

        AffineJTransform transformation = AffineJTransform.createDirect2DIsomorphic(
                startPoint, arcCenter,
                Point.at(1, 0), Point.origin(),
                1
        );

        if (!isCounterClockwise) {
            transformation = transformation.compose(AffineJTransform.createReflectionByAxis(Point.origin(), Point.at(1, 0), 1));
        }
        Point transformedEndPoint = transformation.getTransformedObject(endPoint);
        double angle = transformedEndPoint.v.getAngle();
        Shape arcShape = Shape.arc(angle);
        return arcShape.applyAffineTransform(transformation.getInverse());
    }

    /**
     * Creates a new circle shape, with 4 points
     *
     * @return The created circle
     */
    public static Shape circle() {
        //precomputed parameter for control points
        final double d = 0.5522847498307935d;//=4/3*tan(PI/8)
        Shape resul = new Shape();
        JMPathPoint p1 = JMPathPoint.make(1, 0, 1, -d, 1, d);
        JMPathPoint p2 = JMPathPoint.make(0, 1, d, 1, -d, 1);
        JMPathPoint p3 = JMPathPoint.make(-1, 0, -1, d, -1, -d);
        JMPathPoint p4 = JMPathPoint.make(0, -1, -d, -1, d, -1);
        resul.getPath().addJMPoint(p1, p2, p3, p4);
        resul.objectLabel = "circle";
        return resul;
    }

    /**
     * Creates a circle shape with the given number of jmpathpoints
     *
     * @param numSegments Number of segments
     * @return The created circle
     */
    public static Shape circle(int numSegments) {
        Shape obj = new Shape();
        obj.objectLabel = "circle";
        double x1, y1;
        double step = Math.PI * 2 / numSegments;
        double cte = 4d / 3 * Math.tan(.5 * Math.PI / numSegments);
        double alphaC = 0;
        for (int k = 0; k < numSegments; k++) {
            x1 = Math.cos(alphaC);
            y1 = Math.sin(alphaC);
            Point p = new Point(x1, y1);
            Vec v1 = new Vec(-y1, x1);//This vector is already normalized

            v1.multInSite(cte);
            Point cp1 = p.add(v1);
            Point cp2 = p.add(v1.multInSite(-1));
            JMPathPoint jmp = JMPathPoint.curveTo(p);
            jmp.cpExit.v.copyFrom(cp1.v);
            jmp.cpEnter.v.copyFrom(cp2.v);
            obj.jmpath.addJMPoint(jmp);

            alphaC += step;
        }
        return obj;
    }

    /**
     * Creates an annulus with the given min and max radius
     *
     * @param minRadius Inner radius of the annulus
     * @param maxRadius Outer radius of the annulus
     * @return The annulus created
     */
    public static Shape annulus(double minRadius, double maxRadius) {
        Shape extCircle = Shape.circle().scale(maxRadius);
        Shape intCircle = Shape.circle().scale(minRadius);
        Shape obj = extCircle.merge(intCircle.reverse(), false, false);
        obj.objectLabel = "annulus";
        return obj;
    }

    /**
     * Generates a Shape from given LOGO commands
     *
     * @param logoCommands
     * @return The generated Shape
     */
    public static Shape logo(String logoCommands) {
        LogoInterpreter interpreter = new LogoInterpreter();
        Shape resul = interpreter.toShape(logoCommands);
        resul.style("default");
        return resul;
    }

    /**
     * Returns a new Point object lying in the Shape, at the given position
     *
     * @param t Position parameter, from 0 (beginning) to 1 (end)
     * @return a new Point object at the specified position of the shape.
     */
    public Point getPointAt(double t) {
        return jmpath.getJMPointAt(t).p;
    }

    /**
     * Returns a new Point object lying in the Shape, at the given parametrized
     * position, considering the arclentgh of the curve.
     *
     * @param t Position parameter, from 0 (beginning) to 1 (end)
     * @return a new Point object at the specified position of the shape.
     */
    public Point getParametrizedPointAt(double t) {
        return jmpath.getParametrizedPointAt(t);
    }

    public Point getCentroid() {
      return getPath().getCentroid();
    }

    @Override
    public Shape copy() {
        Shape resul = new Shape(jmpath.copy());
        resul.getMp().copyFrom(getMp());
        resul.objectLabel = this.objectLabel + "_copy";
        resul.copyStateFrom(this);
        return resul;
    }

    /**
     * Align this object with another one
     *
     * @param <T>  MathObject subclass
     * @param obj  Object to align with. This object remains unaltered.
     * @param type Align type, a value from the enum Align
     * @return This object
     */
    @Override
    public <T extends MathObject> T align(Boxable obj, Align type) {
        Vec shiftVector = Vec.to(0, 0);
        Rect thisBoundingBox = this.getBoundingBox();
        Rect objectBoundingBox = obj.getBoundingBox();
        switch (type) {
            case LOWER:
                shiftVector.y = objectBoundingBox.ymin - thisBoundingBox.ymin;
                break;
            case UPPER:
                shiftVector.y = objectBoundingBox.ymax - thisBoundingBox.ymax;
                break;
            case LEFT:
                shiftVector.x = objectBoundingBox.xmin - thisBoundingBox.xmin;
                break;
            case RIGHT:
                shiftVector.x = objectBoundingBox.xmax - thisBoundingBox.xmax;
                break;
            case VCENTER:
                shiftVector.y = 0.5 * ((objectBoundingBox.ymin + objectBoundingBox.ymax) - (thisBoundingBox.ymin + thisBoundingBox.ymax));
                break;
            case HCENTER:
                shiftVector.x = 0.5 * ((objectBoundingBox.xmin + objectBoundingBox.xmax) - (thisBoundingBox.xmin + thisBoundingBox.xmax));
                break;
        }
        shift(shiftVector);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
        jmpath.applyAffineTransform(tr);
        tr.applyTransformsToDrawingProperties(this);
        return (T) this;
    }

    @Override
    public Rect computeBoundingBox() {
        return jmpath.getBoundingBox();
    }

    /**
     * Overloaded method. Check if a given point is inside the shape
     *
     * @param p Point to check
     * @return True if p lies inside of the shape (regardless of being filled or
     * not). False otherwise.
     */
    public boolean containsPoint(Point p) {
        return containsPoint(p.v);
    }

    /**
     * Check if a given vector is inside the shape
     *
     * @param v Vector to check
     * @return True if v lies inside of the shape (regardless of being filled or
     * not). False otherwise.
     */
    public boolean containsPoint(Vec v) {
        Camera dummyCamera = JMathAnimConfig.getConfig().getCamera();
        Path path = FXPathUtils.createFXPathFromJMPath(jmpath, dummyCamera);
        path.setFill(JMColor.parse("black").getFXColor()); // It's necessary that the javafx path is filled to work
        double[] xy = dummyCamera.mathToScreenFX(v);
        return path.contains(xy[0], xy[1]);
    }
    //Ray Casting algorithm. Only works for straight, simple polygons
    //
    //    public boolean pointInPolygon(Point point) {
    //        int numSides =size();
    //        int numIntersections = 0;
    //        for (int i = 0; i < numSides; i++) {
    //            Vec currentVertex = jmpath.get(i).p.v;
    //            Vec nextVertex = jmpath.get(i+1).p.v;
    //            if ((currentVertex.y > point.v.y) != (nextVertex.y > point.v.y)
    //                    && (point.v.x < (nextVertex.x - currentVertex.x) * (point.v.y - currentVertex.y) / (nextVertex.y - currentVertex.y) + currentVertex.x)) {
    //                numIntersections++;
    //            }
    //        }
    //        return numIntersections % 2 == 1;
    //    }

    protected Path convertToPath(javafx.scene.shape.Shape shape) {
        if (shape == null) {
            return null;
        }
        if (shape instanceof Path) {
            return (Path) shape;
        }
        return (Path) javafx.scene.shape.Shape.union(new Path(), shape);
    }

    /**
     * Returns the n-th JMPathPoint of path.
     *
     * @param n index. A cyclic index, so that 0 means the first point and -1
     *          the last one
     * @return The JMPathPoint
     */
    public JMPathPoint get(int n) {
        return jmpath.jmPathPoints.get(n);
    }

    /**
     * Returns a reference to the point at position n This is equivalent to
     * get(n).p
     *
     * @param n Point number. A cyclic index, so that 0 means the first point
     *          and -1 the last one
     * @return The point
     */
    public Point getPoint(int n) {
        return jmpath.get(n).p;
    }

    /**
     * Returns the path associated to this Shape
     *
     * @return A JMPath object
     */
    public JMPath getPath() {
        return jmpath;
    }

    /**
     * Gets
     *
     * @param a
     * @param b
     * @return
     */
    public com.jmathanim.mathobjects.Shape getSubShape(double a, double b) {
        Shape subShape = new Shape();
        subShape.getMp().copyFrom(this.getMp());
        if (!jmpath.isEmpty()) {
            final JMPath subPath = jmpath.getSubPath(a, b);
            subShape.getPath().jmPathPoints.addAll(subPath.jmPathPoints);
        }
        return subShape;
    }

    /**
     * Computes the JMPath of the substraction of this Shape with another one
     *
     * @param s2 Shape to substract
     * @return A JMpath object of the substraction
     */
    public JMPath getSubstractPath(Shape s2) {
        FXPathUtils fXPathUtils = new FXPathUtils();
        //        Camera dummyCamera = new DummyCamera();
        Camera camera = scene.getCamera();
        Path path = FXPathUtils.createFXPathFromJMPath(jmpath, camera); //
        Path path2 = FXPathUtils.createFXPathFromJMPath(s2.getPath(), camera);
        path.setFill(JMColor.parse("black").getFXColor()); // It's necessary that the javafx path is filled to work
        path2.setFill(JMColor.parse("black").getFXColor()); // It's necessary that the javafx path is filled to work
        javafx.scene.shape.Shape newpa = Path.subtract(path, path2);
        Path convertToPath = convertToPath(newpa);
        // Distille!
        //        fXPathUtils.distille(convertToPath);
        return fXPathUtils.createJMPathFromFXPath(convertToPath, camera);
    }

    /**
     * Computes the JMPath of the union of this Shape with another one
     *
     * @param s2 Shape to compute the union
     * @return A JMpath object of the union
     */
    public JMPath getUnionPath(Shape s2) {
        FXPathUtils fXPathUtils = new FXPathUtils();
        //        DummyCamera dummyCamera = new DummyCamera();
        Camera dummyCamera = scene.getCamera();
        Path path = FXPathUtils.createFXPathFromJMPath(jmpath, dummyCamera);
        Path path2 = FXPathUtils.createFXPathFromJMPath(s2.getPath(), dummyCamera);
        path.setFill(JMColor.parse("black").getFXColor()); // It's necessary that the javafx path is filled to work
        path2.setFill(JMColor.parse("black").getFXColor()); // It's necessary that the javafx path is filled to work
        javafx.scene.shape.Shape newpa = Path.union(path, path2);
        Path convertToPath = convertToPath(newpa);
        //        writeFXPathPoints(convertToPath);
        // Distille!
        fXPathUtils.distille(convertToPath);
        return fXPathUtils.createJMPathFromFXPath(convertToPath, dummyCamera);
    }

    /**
     * Computes the JMPath of the intersection of this Shape with another one
     *
     * @param s2 Shape to intersect with
     * @return A JMpath object of the intersection
     */
    public JMPath getIntersectionPath(Shape s2) {
//        FXPathUtils fXPathUtils = new FXPathUtils();
//        Camera dummyCamera = new DummyCamera();
        Camera dummyCamera = scene.getCamera();
        Path path = FXPathUtils.createFXPathFromJMPath(jmpath, dummyCamera);
        Path path2 = FXPathUtils.createFXPathFromJMPath(s2.getPath(), dummyCamera);
        path.setFill(JMColor.parse("black").getFXColor());// It's necessary that the javafx path is filled to work
        path2.setFill(JMColor.parse("black").getFXColor());// It's necessary that the javafx path is filled to work
        javafx.scene.shape.Shape newpa = Path.intersect(path, path2);
        Path convertToPath = convertToPath(newpa);
//        writeFXPathPoints(convertToPath);
        // Distille!
        FXPathUtils ut = new FXPathUtils();
        ut.distille(convertToPath);
//        writeFXPathPoints(convertToPath);

        return ut.createJMPathFromFXPath(convertToPath, dummyCamera);
    }

    /**
     * Creates a new Shape object with the intersection of this Shape and
     * another one. Styling properties of the new Shape are copied from calling
     * object.
     *
     * @param <T> Calling class
     * @param s2  Shape to intersect with
     * @return A Shape with the intersecion
     */
    public <T extends Shape> T intersect(Shape s2) {
        com.jmathanim.mathobjects.Shape resul = new com.jmathanim.mathobjects.Shape(getIntersectionPath(s2));
        resul.getMp().copyFrom(this.getMp());
        return (T) resul;
    }

    /**
     * Creates a new Shape object with the union of this Shape and another one.
     * Styling properties of the new Shape are copied from calling object.
     *
     * @param <T> Calling class
     * @param s2  Shape to compute the union
     * @return A Shape with the union
     */
    public <T extends Shape> T union(Shape s2) {
        com.jmathanim.mathobjects.Shape resul = new com.jmathanim.mathobjects.Shape(getUnionPath(s2));
        resul.getMp().copyFrom(this.getMp());
        return (T) resul;
    }

    /**
     * Creates a new Shape object with the substraction of this Shape with
     * another one. Styling properties of the new Shape are copied from calling
     * object.
     *
     * @param <T> Calling class
     * @param s2  Shape to substract
     * @return A Shape with the substraction
     */
    public <T extends Shape> T substract(Shape s2) {
        com.jmathanim.mathobjects.Shape resul = new com.jmathanim.mathobjects.Shape(getSubstractPath(s2));
        resul.getMp().copyFrom(this.getMp());
        return (T) resul;
    }

    /**
     * Check if the current object is empty (for example: a MultiShape with no
     * objects). A empty object case should be considered as they return null
     * bounding boxes.
     *
     * @return True if object is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return getPath().isEmpty();
    }

    /**
     * Returns the convex flag for this shape. This flag is false by default but
     * can be manually changed. Convex shapes can be drawed using simpler,
     * faster algorithms.
     *
     * @return True if the shape is convex, false if it is concave.
     */
    public boolean isIsConvex() {
        return isConvex;
    }

    /**
     * Mark this shape as convex. If convex, a simpler and faster algorithm to
     * draw it can be used.
     *
     * @param isConvex True if the shape is convex, false if it is concave.
     */
    public void setIsConvex(boolean isConvex) {
        this.isConvex = isConvex;
    }

    /**
     * Return the value of boolean flag showDebugPoints
     *
     * @return If true, the point number will be superimposed on screen when
     * drawing this shape
     */
    public boolean isShowDebugPoints() {
        return showDebugPoints;
    }

    /**
     * Sets the vaue of boolean flag showDebugPoints. If true, the point number
     * will be superimposed on screen when drawing this shape
     *
     * @param showDebugPoints
     * @return This object
     */
    public Shape setShowDebugPoints(boolean showDebugPoints) {
        this.showDebugPoints = showDebugPoints;
        return this;
    }

    /**
     * Returns the size, that is, the number of JMPathPoints of the Shape
     *
     * @return Number of JMPathPoints
     */
    public int size() {
        return jmpath.size();
    }

    /**
     * Gets the normal vector of the shape, asumming the shape is planar.
     *
     * @return The normal vector
     */
    public Vec getNormalVector() {
        if (size() < 3) {
            return Vec.to(0, 0, 0);
        }
        Vec v1 = get(size() / 3).p.to(get(0).p);
        Vec v2 = get(size() / 3).p.to(get(size() / 2).p);
        return v1.cross(v2);
    }

    /**
     * Reverse the points of the path. First point becomes last. The object is
     * altered
     *
     * @param <T> Calling class
     * @return This object
     */
    public <T extends Shape> T reverse() {
        getPath().reverse();
        return (T) this;
    }

    @Override
    public String toString() {
        return objectLabel + ":" + jmpath.toString();
    }

    @Override
    public void restoreState() {
        super.restoreState();
        jmpath.restoreState();
        this.getMp().restoreState();
    }

    @Override
    public void saveState() {
        super.saveState();
        jmpath.saveState();
        this.getMp().saveState();
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        OptionalInt m = getPath().jmPathPoints.stream().mapToInt(t -> t.getUpdateLevel()).max();
        if (m.isPresent()) {
            setUpdateLevel(m.getAsInt() + 1);
        } else {
            setUpdateLevel(0);
        }
    }

    /**
     * Merges with the given Shape, adding all their jmpathpoints.If the shapes
     * were disconnected they will remain so unless the connect parameter is set
     * to true. In such case, the shapes will be connected by a straight line
     * from the last point of the calling object to the first point of the given
     * one.
     *
     * @param <T>         Calling Shape subclass
     * @param sh          Shape to merge
     * @param connectAtoB If true, the end of path A will be connected to the
     *                    beginning of path B by a straight line
     * @param connectBtoA If true, the end of path B will be connected to the
     *                    beginning of path A by a straight line
     * @return This object
     */
    public <T extends Shape> T merge(Shape sh, boolean connectAtoB, boolean connectBtoA) {
        jmpath.merge(sh.getPath().copy(), connectAtoB, connectBtoA);
        return (T) this;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        super.copyStateFrom(obj);
        if (!(obj instanceof Shape)) {
            return;
        }
        Shape sh2 = (Shape) obj;
        this.getMp().copyFrom(sh2.getMp());

        getPath().copyStateFrom(sh2.getPath());
        absoluteSize = sh2.absoluteSize;
        isConvex = sh2.isConvex;
        showDebugPoints = sh2.showDebugPoints;
    }


    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        if (isVisible()) {
            if (absoluteSize) {
                r.drawAbsoluteCopy(this, getAbsoluteAnchor().v);
            } else {
                r.drawPath(this, cam);
                if (isShowDebugPoints()) {
                    for (int n = 0; n < size(); n++) {
                        r.debugText("" + n, getPoint(n).v);
                    }

                }
            }
        }
    }

    public ArrayList<ArrayList<float[]>> computePolygonalPieces() {
        return jmpath.computePolygonalPieces(scene.getCamera());
    }


}
