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
import com.jmathanim.Renderers.FXRenderer.JavaFXRendererUtils;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.LogoInterpreter;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import javafx.scene.shape.Path;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Shape extends AbstractShape<Shape> {


    public Shape() {
        super();
    }

    public Shape(JMPath jmpath) {
        super(jmpath);
    }

    // Static methods to build most used shapes
    public static Shape square() {
        Shape obj = Shape.rectangle(Vec.to(0,0), Vec.to(1, 1));
        obj.objectLabel = "square";
        return obj;
    }

    @Override
    public Shape toShape() {
        return this;
    }

    public static Shape square(Point A, double side) {
        return Shape.rectangle(A, A.add(Vec.to(side, side)));
    }
//    public Shape(JMPath jmpath, MODrawProperties mp) {
//        super(jmpath,mp);
//    }

    /**
     * Creates a rectangle shape from a Rect object
     *
     * @param r Then Rect object
     * @return The created rectangle
     */
    public static Shape rectangle(Rect r) {
        return Shape.rectangle(r.getLowerLeft(), r.getUpperRight());
    }

    /**
     * Creates a segment shape between 2 given points. The parameters points will be referenced to create the segment,
     * so moving them will modify the segment.
     *
     * @param A First point
     * @param B Second point
     * @return The created segment.
     */
    public static Shape segment(Coordinates<?> A, Coordinates<?> B) {
        return segment(A, B, 2);
    }

    /**
     * Creates a segment shape between 2 given points. The parameters points will be referenced to create the segment,
     * so moving them will modify the segment. The Shape will contain also numPoints-2 intermediate points equally
     * distributed.
     *
     * @param A         First point
     * @param B         Second point
     * @param numPoints Number of points, including start an ending point. A number greater or equal than 2.
     * @return The created segment.
     */
    public static Shape segment(Coordinates<?> A, Coordinates<?> B, int numPoints) {
        if (numPoints < 2) {
            numPoints = 2;
        }
        Coordinates<?>[] points = new Coordinates[numPoints];
        points[0] = A;

        for (int i = 1; i < numPoints - 1; i++) {
            points[i] = A.getVec().interpolate(B, 1d * i / (numPoints - 1));
        }
        points[numPoints - 1] = B;
        return polyLine(points);
    }

    /**
     * Creates a rectangle shape from 2 opposite points
     *
     * @param A First point
     * @param B Second point
     * @return The rectangle
     */
    public static Shape rectangle(Coordinates A, Coordinates B) {
        Shape obj = new Shape();
        Vec vA = A.getVec();
        Vec vB = B.getVec();
        JMPathPoint p1 = JMPathPoint.lineTo(vA);
        JMPathPoint p2 = JMPathPoint.lineTo(vB.x, vA.y);
        JMPathPoint p3 = JMPathPoint.lineTo(B);
        JMPathPoint p4 = JMPathPoint.lineTo(vA.x, vB.y);
        obj.getPath().addJMPoint(p1, p2, p3, p4);
        return obj;
    }

    /**
     * Creates a rectangle shape from 3 consecutive points.
     *
     * @param A First point
     * @param B Second point. The fourth point will be the opposite of this point. This will be the point with index 0
     *          in the path.
     * @param C Third point
     * @return The rectangle
     */
    public static Shape rectangle(Point A, Point B, Point C) {
        Shape obj = new Shape();

        JMPathPoint p1 = JMPathPoint.lineTo(B);
        JMPathPoint p2 = JMPathPoint.lineTo(C);
        JMPathPoint p3 = JMPathPoint.lineTo(C.add(B.to(A)));
        JMPathPoint p4 = JMPathPoint.lineTo(A);
        obj.getPath().addJMPoint(p1, p2, p3, p4);
        return obj;
    }

    /**
     * Creates a polygonal shape with the given points
     *
     * @param points Points of the polygon, varargs or array Point[]
     * @return The polygon
     */
    public static Shape polygon(Coordinates... points) {
        Shape obj = new Shape();
        for (Coordinates newPoint : points) {
            JMPathPoint p = JMPathPoint.lineTo(newPoint);
            obj.getPath().addJMPoint(p);
        }
        return obj;
    }

    /**
     * Creates a shape composed of multiple connected segments
     *
     * @param points Points to connect. Varargs or array Point[]
     * @return The polyline
     */
    public static Shape polyLine(Coordinates... points) {
        Shape obj = polygon(points);
        obj.objectLabel = "polyLine";
        obj.get(0).setSegmentToThisPointVisible(false);
        return obj;
    }

    /**
     * Creates a basic right-angled triangle shape (0,0)-(1,0)-(0,1)
     *
     * @return
     */
    public static Shape triangle() {
        return polygon(Vec.to(0, 0), Vec.to(1, 0), Vec.to(0, 1));
    }

    // Static methods to build most commons shapes

    /**
     * Generates a regular polygon shape inscribed in a unit circle. The first point of the shape lies in the
     * coordinates (1,0)
     *
     * @param numSides Number of sides
     * @return The generated Shape
     */
    public static Shape regularInscribedPolygon(int numSides) {
        Vec[] points = new Vec[numSides];
        for (int i = 0; i < numSides; i++) {
            points[i] = Vec.to(Math.cos(2 * PI * i / numSides), Math.sin(2 * PI * i / numSides));
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
        Vec newPoint = Vec.to(0,0);
        for (int n = 0; n < numsides; n++) {
            double alpha = 2 * n * Math.PI / numsides;
            Vec moveVector = Vec.to(Math.cos(alpha), Math.sin(alpha));
            newPoint = newPoint.add(moveVector);
            JMPathPoint p = JMPathPoint.lineTo(newPoint);
            obj.getPath().addJMPoint(p);
        }
        return obj;
    }

    /**
     * Creates an arc shape with radius 1 and center origin. First point is (1,0)
     *
     * @param angle       Angle in radians of the arc
     * @param numSegments Number of segments
     * @return The created arc
     */
    public static Shape arc(double angle, int numSegments) {
        JMPath path = getArcPath(angle, numSegments);
        Shape obj = new Shape(path);
        obj.objectLabel = "circle";
        return obj;
    }

    private static JMPath getArcPath(double angle, int numSegments) {
        JMPath path = new JMPath();

        double x1, y1;
        double step = angle / (numSegments - 1);
        double cte = 4d / 3 * Math.tan(angle / 4 / (numSegments - 1));
        double alphaC = 0;
        JMPathPoint jmp = JMPathPoint.make(1, 0, 1, -cte, 1, cte);
        for (int k = 0; k < numSegments; k++) {
            path.addJMPoint(jmp.copy().rotate(Point.origin(), k * step));
        }
        path.get(0).setSegmentToThisPointVisible(false);
        path.get(0).getVEnter().copyCoordinatesFrom(path.get(0).getV());
        path.get(-1).getVExit().copyCoordinatesFrom(path.get(-1).getV());
        return path;
    }

    /**
     * Creates an arc shape with radius 1 and center origin. First point is (1,0). Default value of 4 segments.
     *
     * @param angle Angle in radians of the arc
     * @return The created arc
     */
    public static Shape arc(double angle) {
        return arc(angle, 4);
    }

    /**
     * Creates an arch Shape from A to B with given radius. The arc can be drawn counterclockwise or clockwise depending
     * on the boolean parameter. If the radius is too small to draw an arc, an straight segment is drawn instead.
     *
     * @param startPoint         Starting point
     * @param endPoint           Ending point
     * @param radius             Radius of the arc
     * @param isCounterClockwise If true, draws the counterclockwise arc, clockwise otherwise.
     * @return The created arc
     */

    public static Shape arc(Coordinates startPoint, Coordinates endPoint, double radius, boolean isCounterClockwise) {
        // First, compute arc center
        Vec startVec = startPoint.getVec();
        Vec endVec = endPoint.getVec();

        Vec midpointVector = startVec.to(endVec).mult(0.5);
        Vec radiusVector = midpointVector.rotate(PI / 2).normalize();

        double squaredDistance = midpointVector.dot(midpointVector);
        double discriminant = Math.max(0, radius * radius - squaredDistance);
        double radiusOffset = Math.sqrt(discriminant);
        Vec arcCenter = startVec.add(midpointVector).add(radiusVector.mult(radiusOffset));

        AffineJTransform transformation = AffineJTransform.createDirect2DIsomorphic(
                startVec, arcCenter,
                Vec.to(1, 0), Vec.to(0, 0),
                1
        );

        if (!isCounterClockwise) {
            transformation = transformation.compose(AffineJTransform.createReflectionByAxis(Point.origin(), Vec.to(1, 0), 1));
        }
        Vec transformedEndPoint = endVec.copy().applyAffineTransform(transformation);
        double angle = transformedEndPoint.getAngle();
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
        JMPath path = new JMPath();

        double x1, y1;
        double step = Math.PI * 2 / numSegments;
        double cte = 4d / 3 * Math.tan(.5 * Math.PI / numSegments);
        double alphaC = 0;
        JMPathPoint jmp = JMPathPoint.make(1, 0, 1, -cte, 1, cte);
        for (int k = 0; k < numSegments; k++) {
            path.addJMPoint(jmp.copy());
            jmp.rotate(Point.origin(), step);
        }
        Shape obj = new Shape(path);
        obj.objectLabel = "circle";
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


    public Vec getCentroid() {
        return getPath().getCentroid();
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

        protected Path convertToPath (javafx.scene.shape.Shape shape){
            if (shape == null) {
                return null;
            }
            if (shape instanceof Path) {
                return (Path) shape;
            }
            return (Path) javafx.scene.shape.Shape.union(new Path(), shape);
        }

    /**
         * Computes the JMPath of the substraction of this Shape with another one
         *
         * @param s2 Shape to substract
         * @return A JMpath object of the substraction
         */
        public JMPath getSubstractPath (Shape s2){
            JavaFXRendererUtils fXRendererUtilsJava = new JavaFXRendererUtils();
            //        Camera dummyCamera = new DummyCamera();
            Camera camera = scene.getCamera();
            Path path = JavaFXRendererUtils.createFXPathFromJMPath(getPath(), Vec.to(0, 0), camera); //
            Path path2 = JavaFXRendererUtils.createFXPathFromJMPath(s2.getPath(), Vec.to(0, 0), camera);
            path.setFill(JMColor.parse("black").getFXColor()); // It's necessary that the javafx path is filled to work
            path2.setFill(JMColor.parse("black").getFXColor()); // It's necessary that the javafx path is filled to work
            javafx.scene.shape.Shape newpa = Path.subtract(path, path2);
            Path convertToPath = convertToPath(newpa);
            // Distille!
            //        fXPathUtils.distille(convertToPath);
            return fXRendererUtilsJava.createJMPathFromFXPath(convertToPath, camera);
        }

        /**
         * Computes the JMPath of the union of this Shape with another one
         *
         * @param s2 Shape to compute the union
         * @return A JMpath object of the union
         */
        public JMPath getUnionPath (Shape s2){
            JavaFXRendererUtils fXRendererUtilsJava = new JavaFXRendererUtils();
            //        DummyCamera dummyCamera = new DummyCamera();
            Camera dummyCamera = scene.getCamera();
            Path path = JavaFXRendererUtils.createFXPathFromJMPath(getPath(), Vec.to(0, 0), dummyCamera);
            Path path2 = JavaFXRendererUtils.createFXPathFromJMPath(s2.getPath(), Vec.to(0, 0), dummyCamera);
            path.setFill(JMColor.parse("black").getFXColor()); // It's necessary that the javafx path is filled to work
            path2.setFill(JMColor.parse("black").getFXColor()); // It's necessary that the javafx path is filled to work
            javafx.scene.shape.Shape newpa = Path.union(path, path2);
            Path convertToPath = convertToPath(newpa);
            //        writeFXPathPoints(convertToPath);
            // Distille!
            fXRendererUtilsJava.distille(convertToPath);
            return fXRendererUtilsJava.createJMPathFromFXPath(convertToPath, dummyCamera);
        }

        /**
         * Computes the JMPath of the intersection of this Shape with another one
         *
         * @param s2 Shape to intersect with
         * @return A JMpath object of the intersection
         */
        public JMPath getIntersectionPath (Shape s2){
//        FXPathUtils fXPathUtils = new FXPathUtils();
//        Camera dummyCamera = new DummyCamera();
            Camera dummyCamera = scene.getCamera();
            Path path = JavaFXRendererUtils.createFXPathFromJMPath(getPath(), Vec.to(0, 0), dummyCamera);
            Path path2 = JavaFXRendererUtils.createFXPathFromJMPath(s2.getPath(), Vec.to(0, 0), dummyCamera);
            path.setFill(JMColor.parse("black").getFXColor());// It's necessary that the javafx path is filled to work
            path2.setFill(JMColor.parse("black").getFXColor());// It's necessary that the javafx path is filled to work
            javafx.scene.shape.Shape newpa = Path.intersect(path, path2);
            Path convertToPath = convertToPath(newpa);
//        writeFXPathPoints(convertToPath);
            // Distille!
            JavaFXRendererUtils ut = new JavaFXRendererUtils();
            ut.distille(convertToPath);
//        writeFXPathPoints(convertToPath);

            return ut.createJMPathFromFXPath(convertToPath, dummyCamera);
        }

        /**
         * Creates a new Shape object with the intersection of this Shape and another one. Styling properties of the new
         * Shape are copied from calling object.
         *
         * @param s2  Shape to intersect with
         * @return A Shape with the intersecion
         */
        public <T extends Shape > T intersect(Shape s2) {
            Shape resul = new Shape(getIntersectionPath(s2));
            resul.getMp().copyFrom(this.getMp());
            return (T) resul;
        }

        /**
         * Creates a new Shape object with the union of this Shape and another one. Styling properties of the new Shape are
         * copied from calling object.
         *
         * @param s2  Shape to compute the union
         * @return A Shape with the union
         */
        public <T extends Shape > T union(Shape s2) {
            Shape resul = new Shape(getUnionPath(s2));
            resul.getMp().copyFrom(this.getMp());
            return (T) resul;
        }

        /**
         * Creates a new Shape object with the substraction of this Shape with another one. Styling properties of the new
         * Shape are copied from calling object.
         *
         * @param s2  Shape to substract
         * @return A Shape with the substraction
         */
        public <T extends Shape > T substract(Shape s2) {
            Shape resul = new Shape(getSubstractPath(s2));
            resul.getMp().copyFrom(this.getMp());
            return (T) resul;
        }

        /**
         * Check if the current object is empty (for example: a MultiShape with no objects). A empty object case should be
         * considered as they return null bounding boxes.
         *
         * @return True if object is empty, false otherwise
         */
        @Override
        public boolean isEmpty () {
            return getPath().isEmpty();
        }

        /**
         * Returns the convex flag for this shape. This flag is false by default but can be manually changed. Convex shapes
         * can be drawn using simpler, faster algorithms.
         *
         * @return True if the shape is convex, false if it is concave.
         */
        public boolean isIsConvex () {
            return isConvex;
        }

        /**
         * Mark this shape as convex. If convex, a simpler and faster algorithm to draw it can be used.
         *
         * @param isConvex True if the shape is convex, false if it is concave.
         */
        public void setIsConvex ( boolean isConvex){
            this.isConvex = isConvex;
        }

    @Override
    public Shape copy() {
        Shape resul = new Shape(jmpath.copy());
        resul.objectLabel = this.objectLabel + "_copy";
        resul.copyStateFrom(this);
        return resul;
    }

}
