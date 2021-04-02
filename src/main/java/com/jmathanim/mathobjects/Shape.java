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
import com.jmathanim.Cameras.DummyCamera;
import com.jmathanim.Renderers.FXPathUtils;
import com.jmathanim.Renderers.JavaFXRenderer;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import java.util.ArrayList;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Shape extends MathObject {

    private final JMPath jmpath;
    protected final ArrayList<JMPathPoint> vertices;
    private boolean showDebugPoints = false;

    public Shape() {
        this(new JMPath(), null);
    }

    public Shape(JMPath jmpath) {
        this(jmpath, null);
    }

    public Shape(JMPath jmpath, MODrawProperties mp) {
        super(mp);
        vertices = new ArrayList<>();
        this.jmpath = jmpath;
    }

    public Shape(MODrawProperties mp) {
        super(mp);
        vertices = new ArrayList<>();
        jmpath = new JMPath();
    }

    public JMPathPoint get(int n) {
        return jmpath.getJMPoint(n);
    }

    public Point getPoint(int n) {
        return get(n).p;
    }

    public JMPath getPath() {
        return jmpath;
    }

    protected final void computeVerticesFromPath() {
        vertices.clear();
        for (JMPathPoint p : jmpath.jmPathPoints) {
            if (p.type == JMPathPoint.JMPathPointType.VERTEX) {
                vertices.add(p);
            }
        }
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
        Shape resul = new Shape(jmpath.rawCopy(), copy);
        resul.absoluteSize = this.absoluteSize;
        resul.label = this.label + "_copy";
        return resul;
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
        return label + ":" + jmpath.toString();
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        for (JMPathPoint p : jmpath.jmPathPoints) {
            scene.registerUpdateable(p.p);
            scene.registerUpdateable(p.cpExit);
            scene.registerUpdateable(p.cpEnter);
        }
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        for (JMPathPoint p : jmpath.jmPathPoints) {
            scene.unregisterUpdateable(p.p);
            scene.unregisterUpdateable(p.cpExit);
            scene.unregisterUpdateable(p.cpEnter);
        }
    }

    @Override
    public void restoreState() {
        super.restoreState();
        jmpath.restoreState();
    }

    @Override
    public void saveState() {
        super.saveState();
        jmpath.saveState();
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
     * @param connect If true, the 2 paths will be connected by a straight line
     * @param reverse If true, reverse the points of the second shape. This is
     * useful for filling purposes if you are trying to add a "hole" to a shape
     * @return This object
     */
    public <T extends Shape> T merge(Shape sh, boolean connect, boolean reverse) {
        JMPath pa = sh.getPath().copy();
        if (reverse) {
            pa.reverse();
        }
        //If the first path is already a closed one, open it
        //with 2 identical points (old-fashioned style of closing shapes)
        final JMPathPoint jmPoint = jmpath.getJMPoint(0);
        if (jmPoint.isThisSegmentVisible) {
            jmpath.jmPathPoints.add(jmPoint.copy());
            jmPoint.isThisSegmentVisible = false;
        }

        //Do the same with the second path
        final JMPathPoint jmPoint2 = pa.getJMPoint(0);
        if (jmPoint2.isThisSegmentVisible) {
            pa.jmPathPoints.add(jmPoint2.copy());
        }
        jmPoint2.isThisSegmentVisible = connect;
        //Now you can add the points
        jmpath.jmPathPoints.addAll(pa.jmPathPoints);
        return (T) this;
    }

    public int size() {
        return jmpath.size();
    }

    //Static methods to build most used shapes
    public static Shape square() {
        Shape obj = Shape.square(new Point(0, 0), 1);
        obj.label = "square";
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

    //Static methods to build most commons shapes
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
        obj.label = "polyLine";
        obj.get(0).isThisSegmentVisible = false;
        return obj;
    }

    public static Shape regularPolygon(int numsides) {
        return regularPolygon(numsides, new Point(0, 0), 1);
    }

    public static Shape regularPolygon(int numsides, Point A, double side) {
        Shape obj = new Shape();
        obj.label = "regPol";
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
        obj.label = "arc";
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
        obj.getPath().getJMPoint(0).isThisSegmentVisible = false;//Open path
//        obj.get(0).cp1.v.copyFrom(obj.get(0).p.v);
//        obj.get(-1).cp2.v.copyFrom(obj.get(-1).p.v);
        return obj;
    }

    public static Shape circle() {
        return circle(4);
    }

    public static Shape circle(int numSegments) {
        Shape obj = new Shape();
        obj.label = "circle";
        double x1, y1;
        double step = Math.PI * 2 / numSegments;
        double cte = 4d / 3 * Math.tan(.5 * Math.PI / numSegments);
        for (double alphaC = 0; alphaC < 2 * Math.PI; alphaC += step) {
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
        Shape intCircle = Shape.circle().scale(.75);
        Shape obj = extCircle.merge(intCircle, false, true);
        obj.label = "annulus";
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
        DummyCamera dummyCamera = new DummyCamera();
        Path path = fXPathUtils.createFXPathFromJMPath(this, jmpath, dummyCamera);
        path.setFill(JMColor.parse("black").getFXColor());//It's necessary that the javafx path is filled to work
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
        Path path = fXPathUtils.createFXPathFromJMPath(this, jmpath, dummyCamera);
        Path path2 = fXPathUtils.createFXPathFromJMPath(s2, s2.getPath(), dummyCamera);
        path.setFill(JMColor.parse("black").getFXColor());//It's necessary that the javafx path is filled to work
        path2.setFill(JMColor.parse("black").getFXColor());//It's necessary that the javafx path is filled to work
        javafx.scene.shape.Shape newpa = Path.intersect(path, path2);
        Path convertToPath = convertToPath(newpa);
//        writeFXPathPoints(convertToPath);
        //Distille!
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
        DummyCamera dummyCamera = new DummyCamera();
        Path path = fXPathUtils.createFXPathFromJMPath(this, jmpath, dummyCamera);
        Path path2 = fXPathUtils.createFXPathFromJMPath(s2, s2.getPath(), dummyCamera);
        path.setFill(JMColor.parse("black").getFXColor());//It's necessary that the javafx path is filled to work
        path2.setFill(JMColor.parse("black").getFXColor());//It's necessary that the javafx path is filled to work
        javafx.scene.shape.Shape newpa = Path.union(path, path2);
        Path convertToPath = convertToPath(newpa);
        writeFXPathPoints(convertToPath);
        //Distille!
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
        Path path = fXPathUtils.createFXPathFromJMPath(this, jmpath, dummyCamera);//TODO: Make a null camera
        Path path2 = fXPathUtils.createFXPathFromJMPath(s2, s2.getPath(), dummyCamera);
        path.setFill(JMColor.parse("black").getFXColor());//It's necessary that the javafx path is filled to work
        path2.setFill(JMColor.parse("black").getFXColor());//It's necessary that the javafx path is filled to work
        javafx.scene.shape.Shape newpa = Path.subtract(path, path2);
        Path convertToPath = convertToPath(newpa);
        //Distille!
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

}
