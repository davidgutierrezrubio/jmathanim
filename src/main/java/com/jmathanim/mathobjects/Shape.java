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
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import java.util.OptionalInt;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Shape extends MathObject {

    private final JMPath jmpath;
    private boolean showDebugPoints = false;
    private boolean isConvex = false;

    public Shape() {
        this(new JMPath(), null);
    }

    public Shape(JMPath jmpath) {
        this(jmpath, null);
    }

    public Shape(JMPath jmpath, MODrawProperties mp) {
        super(mp);
        this.jmpath = jmpath;
    }

    public Shape(MODrawProperties mp) {
        super(mp);
        jmpath = new JMPath();
    }

    public JMPathPoint get(int n) {
        return jmpath.jmPathPoints.get(n);
    }

    /**
     * Returns a reference to the point at position n This is equivalent to
     * get(n).p
     *
     * @param n Point number
     * @return The point
     */
    public Point getPoint(int n) {
        return get(n).p;
    }

    public JMPath getPath() {
        return jmpath;
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

    public Point getCentroid() {
        Point resul = new Point(0, 0, 0);
        for (JMPathPoint p : jmpath.jmPathPoints) {
            resul.v.x += p.p.v.x;
            resul.v.y += p.p.v.y;
            resul.v.z += p.p.v.z;
        }
        resul.v.x /= jmpath.size();
        resul.v.y /= jmpath.size();
        resul.v.z /= jmpath.size();
        return resul;
    }

//    @Override
//    public <T extends MathObject> T shift(Vec shiftVector) {
//        jmpath.shift(shiftVector);
//        return (T) this;
//    }
    public void removeInterpolationPoints() {
        jmpath.removeInterpolationPoints();
    }

    @Override
    public Shape copy() {
        final MODrawProperties copy = getMp().copy();
        Shape resul = new Shape(jmpath.copy(), copy);
        resul.absoluteSize = this.absoluteSize;
        resul.objectLabel = this.objectLabel + "_copy";
        resul.isConvex = this.isConvex;
        resul.showDebugPoints = this.showDebugPoints;
        return resul;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        if (!(obj instanceof Shape)) {
            return;
        }
        Shape sh2 = (Shape) obj;
        this.getMp().copyFrom(sh2.getMp());
        for (int i = 0; i < size(); i++) {
            get(i).copyStateFrom(sh2.get(i));
        }

    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        if (isVisible()) {
            if (absoluteSize) {
                r.drawAbsoluteCopy(this, getAbsoluteAnchor().v);
            } else {
                r.drawPath(this);
                if (isShowDebugPoints()) {
                    for (int n = 0; n < size(); n++) {
                        r.debugText("" + n, getPoint(n).v);
                    }

                }
            }
        }
    }

    @Override
    public Rect getBoundingBox() {
        return jmpath.getBoundingBox();
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

    /**
     * Merges with the given Shape, adding all their jmpathpoints.If the shapes
     * were disconnected they will remain so unless the connect parameter is set
     * to true. In such case, the shapes will be connected by a straight line
     * from the last point of the calling object to the first point of the given
     * one.
     *
     * @param <T> Calling Shape subclass
     * @param sh Shape to merge
     * @param connectAtoB If true, the end of path A will be connected to the
     * beginning of path B by a straight line
     * @param connectBtoA If true, the end of path B will be connected to the
     * beginning of path A by a straight line
     * @return This object
     */
    public <T extends Shape> T merge(Shape sh, boolean connectAtoB, boolean connectBtoA) {
        getPath().merge(sh.getPath().copy(), connectAtoB, connectBtoA);
        return (T) this;
    }

    public int size() {
        return jmpath.size();
    }

    // Static methods to build most used shapes
    public static Shape square() {
        Shape obj = Shape.square(new Point(0, 0), 1);
        obj.objectLabel = "square";
        return obj;
    }

    public static Shape square(Point A, double side) {

        return Shape.rectangle(A, A.add(new Vec(side, side)));
    }

    public static Shape rectangle(Rect r) {
        return Shape.rectangle(r.getDL(), r.getUR());
    }

    public static Shape segment(Point A, Vec v) {
        return segment(A, A.add(v));
    }

    // Static methods to build most commons shapes
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
     * Creates a rectangle from 2 opposite points
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
     * Creates a rectangle from 3 consecutive points.
     *
     * @param A First point
     * @param B Second point. The fourth point will be the opposite of this
     * point. This will be the point with index 0 in the path.
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
     * Creates a polygon with the given points
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
     * Creates a basic right-angled triangle (0,0)-(1,0)-(0,1)
     *
     * @return
     */
    public static Shape triangle() {
        return polygon(Point.at(0, 0), Point.at(1, 0), Point.at(0, 1));
    }

    /**
     * Generates a regular polygon inscribed in a unit circle. The first point
     * of the shape lies in the coordinates (1,0)
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
     * Creates a regular polygon, with first vertex at (0,0) and side 1
     *
     * @param numsides Number of sides
     * @return A Shape object representing the polygon
     */
    public static Shape regularPolygon(int numsides) {
        return regularPolygon(numsides, new Point(0, 0), 1);
    }

    private static Shape regularPolygon(int numsides, Point A, double side) {
        Shape obj = new Shape();
        obj.objectLabel = "regPol";
        Point newPoint = (Point) A.copy();
        for (int n = 0; n < numsides; n++) {
            double alpha = 2 * n * Math.PI / numsides;
            Vec moveVector = new Vec(side * Math.cos(alpha), side * Math.sin(alpha));
            newPoint = newPoint.add(moveVector);
            JMPathPoint p = JMPathPoint.lineTo(newPoint);
            obj.getPath().addJMPoint(p);
        }
        return obj;
    }

    public static Shape arc(double angle) {
        Shape obj = new Shape();
        obj.objectLabel = "arc";
        double x1, y1;
        int nSegs = 4;
        int segsForFullCircle = (int) (2 * PI * nSegs / angle);
        double cte = 4d / 3 * Math.tan(.5 * Math.PI / segsForFullCircle);
        for (int n = 0; n < nSegs + 1; n++) {
            double alphaC = angle * n / nSegs;
            x1 = Math.cos(alphaC);
            y1 = Math.sin(alphaC);
            Point p = new Point(x1, y1);
            Vec v1 = new Vec(-y1, x1);

            v1.multInSite(cte);
            Point cp1 = p.add(v1);
            Point cp2 = p.add(v1.multInSite(-1));
            JMPathPoint jmp = JMPathPoint.curveTo(p);
            jmp.cpExit.copyFrom(cp1);
            jmp.cpEnter.copyFrom(cp2);
            obj.jmpath.addJMPoint(jmp);
        }
//        obj.getPath().generateControlPoints();
//        obj.getPath().jmPathPoints.remove(0);
//        obj.getPath().jmPathPoints.remove(-1);
        obj.getPath().jmPathPoints.get(0).isThisSegmentVisible = false;// Open path
//        obj.get(0).cp1.v.copyFrom(obj.get(0).p.v);
//        obj.get(-1).cp2.v.copyFrom(obj.get(-1).p.v);
        return obj;
    }

    public static Shape circle() {
        return circle(4);
    }

    /**
     * Creates a circle with the given number of segments
     *
     * @param numSegments Number of segments
     * @return The circle
     */
    public static Shape circle(int numSegments) {
        Shape obj = new Shape();
        obj.objectLabel = "circle";
        double x1, y1;
        double step = Math.PI * 2 / numSegments;
        double cte = 4d / 3 * Math.tan(.5 * Math.PI / numSegments);
        for (double alphaC = 0; alphaC < 2 * Math.PI; alphaC += step) {
            x1 = Math.cos(alphaC);
            y1 = Math.sin(alphaC);
            Point p = new Point(x1, y1);
            Vec v1 = new Vec(-y1, x1);//This vector is already normalized

            v1.multInSite(cte);
            Point cp1 = p.add(v1);
            Point cp2 = p.add(v1.multInSite(-1));
            JMPathPoint jmp = JMPathPoint.curveTo(p);
            jmp.cpExit.copyFrom(cp1);
            jmp.cpEnter.copyFrom(cp2);
            obj.jmpath.addJMPoint(jmp);
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

    public boolean isShowDebugPoints() {
        return showDebugPoints;
    }

    public void setShowDebugPoints(boolean showDebugPoints) {
        this.showDebugPoints = showDebugPoints;
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
        int size = getPath().size();
        for (int n = 0; n < size; n++) {
            get(n).applyAffineTransform(tr);
        }
        tr.applyTransformsToDrawingProperties(this);
        return (T) this;
    }

    /**
     * Check if a given point is inside the shape
     *
     * @param p Point to check
     * @return True if p lies inside of the shape (regardless of being filled or
     * not). False otherwise.
     */
    public boolean containsPoint(Point p) {
        return containsPoint(p.v);
    }

    /**
     * Overloaded method. Check if a given vector is inside the shape
     *
     * @param v Vector to check
     * @return True if v lies inside of the shape (regardless of being filled or
     * not). False otherwise.
     */
    public boolean containsPoint(Vec v) {
        FXPathUtils fXPathUtils = new FXPathUtils();
//        DummyCamera dummyCamera = new DummyCamera();
        Camera dummyCamera = JMathAnimConfig.getConfig().getCamera();
        Path path = FXPathUtils.createFXPathFromJMPath(jmpath, dummyCamera);
        path.setFill(JMColor.parse("black").getFXColor());// It's necessary that the javafx path is filled to work
        double xy[] = dummyCamera.mathToScreenFX(v);
        return path.contains(xy[0], xy[1]);
    }

    /**
     * Computes the JMPath of the intersection of this Shape with another one
     *
     * @param s2 Shape to intersect with
     * @return A JMpath object of the intersection
     */
    public JMPath getIntersectionPath(Shape s2) {
        FXPathUtils fXPathUtils = new FXPathUtils();
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
     * @param s2 Shape to intersect with
     * @return A Shape with the intersecion
     */
    public Shape intersect(Shape s2) {
        Shape resul = new Shape(getIntersectionPath(s2));
        resul.getMp().copyFrom(this.getMp());
        return resul;
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
        path.setFill(JMColor.parse("black").getFXColor());// It's necessary that the javafx path is filled to work
        path2.setFill(JMColor.parse("black").getFXColor());// It's necessary that the javafx path is filled to work
        javafx.scene.shape.Shape newpa = Path.union(path, path2);
        Path convertToPath = convertToPath(newpa);
//        writeFXPathPoints(convertToPath);
        // Distille!
        fXPathUtils.distille(convertToPath);

        return fXPathUtils.createJMPathFromFXPath(convertToPath, dummyCamera);
    }

    /**
     * Creates a new Shape object with the union of this Shape and another one.
     * Styling properties of the new Shape are copied from calling object.
     *
     * @param s2 Shape to compute the union
     * @return A Shape with the union
     */
    public Shape union(Shape s2) {
        Shape resul = new Shape(getUnionPath(s2));
        resul.getMp().copyFrom(this.getMp());
        return resul;
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
        Camera dummyCamera = scene.getCamera();
        Path path = FXPathUtils.createFXPathFromJMPath(jmpath, dummyCamera);// TODO: Make a null camera
        Path path2 = FXPathUtils.createFXPathFromJMPath(s2.getPath(), dummyCamera);
        path.setFill(JMColor.parse("black").getFXColor());// It's necessary that the javafx path is filled to work
        path2.setFill(JMColor.parse("black").getFXColor());// It's necessary that the javafx path is filled to work
        javafx.scene.shape.Shape newpa = Path.subtract(path, path2);
        Path convertToPath = convertToPath(newpa);
        // Distille!
//        fXPathUtils.distille(convertToPath);

        return fXPathUtils.createJMPathFromFXPath(convertToPath, dummyCamera);
    }

    /**
     * Creates a new Shape object with the substraction of this Shape with
     * another one. Styling properties of the new Shape are copied from calling
     * object.
     *
     * @param s2 Shape to substract
     * @return A Shape with the substraction
     */
    public Shape substract(Shape s2) {
        Shape resul = new Shape(getSubstractPath(s2));
        resul.getMp().copyFrom(this.getMp());
        return resul;
    }

    private Path convertToPath(javafx.scene.shape.Shape shape) {
        if (shape == null) {
            return null;
        }
        if (shape instanceof Path) {
            return (Path) shape;
        }
        return (Path) javafx.scene.shape.Shape.union(new Path(), shape);
    }

    private void writeFXPathPoints(Path pa) {
        System.out.println("FXPATH");
        System.out.println("-----------------------");
        int counter = 0;
        for (PathElement el : pa.getElements()) {
            System.out.println(counter + ":  " + el);
            counter++;
        }
        System.out.println("-----------------------");
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
     * Reverse the points of the path. First point becomes last.
     *
     * @return This object
     */
    public Shape reverse() {
        getPath().reverse();
        return this;
    }

    /**
     * Gets
     *
     * @param a
     * @param b
     * @return
     */
    public Shape getSubShape(double a, double b) {
        Shape subShape = new Shape();
        subShape.getMp().copyFrom(this.getMp());
        if (!getPath().isEmpty()) {
            final JMPath subPath = getPath().getSubPath(a, b);
            subShape.getPath().jmPathPoints.addAll(subPath.jmPathPoints);
        }
        return subShape;
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
}
