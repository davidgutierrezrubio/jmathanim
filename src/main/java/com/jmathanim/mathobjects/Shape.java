/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Shape extends MathObject {

    public final JMPath jmpath;
    protected boolean needsRecalcControlPoints;
    protected int numInterpolationPoints = 1;//TODO: Adaptative interpolation
    protected final ArrayList<JMPathPoint> vertices;

    /**
     * Type of path, JMPath.STRAIGHT or JMPath.CURVED
     */
    private ArrayList<Boolean> visibilityTemp;
    private double fillAlphaTemp;

    public Shape() {
        this(null);
    }

    public Shape(JMPath jmpath, MathObjectDrawingProperties mp) {
        super(mp);
        vertices = new ArrayList<JMPathPoint>();
        this.jmpath = jmpath;
        needsRecalcControlPoints = false;
    }

    public Shape(MathObjectDrawingProperties mp) {//TODO: Fix this
        super(mp);
        vertices = new ArrayList<JMPathPoint>();
        jmpath = new JMPath();
        needsRecalcControlPoints = false;
    }

    public JMPathPoint getJMPoint(int n) {
        return jmpath.getJMPoint(n);
    }

    public Point getPoint(int n) {
        return jmpath.getJMPoint(n).p;
    }

    /**
     * This method computes all necessary points to the path (interpolation and
     * control)
     */
    protected final void computeJMPathFromVertices(boolean close) {
        jmpath.clear();//clear points
        for (JMPathPoint p : vertices) {
            jmpath.addJMPoint(p);
        }
//        if (close) {
//            jmpath.close();
//        } else {
//            jmpath.open();
//        }

        jmpath.generateControlPoints();
        needsRecalcControlPoints = false;
    }

    public JMPath getPath() {
        return jmpath;
    }

    protected final void computeVerticesFromPath() {
        vertices.clear();
        for (JMPathPoint p : jmpath.jmPathPoints) {
            if (p.type == JMPathPoint.TYPE_VERTEX) {
                vertices.add(p);
            }
        }
    }

    @Override
    public Point getCenter() {
//        return getBoundingBox().getCenter();
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

    @Override
    public <T extends MathObject> T shift(double x, double y) {
        return shift(new Vec(x, y));
    }

    @Override
    public <T extends MathObject> T shift(Vec shiftVector) {
        jmpath.shift(shiftVector);
        return (T) this;
    }

    @Override
    public void setDrawParam(double drawParam, int numSlices) {

        //If this is the first call, be sure to store visibility status
        if (drawParam == 0) {
            fillAlphaTemp = mp.fillColor.alpha;
            visibilityTemp = new ArrayList<Boolean>();
            for (int n = 0; n < jmpath.jmPathPoints.size(); n++) {
                visibilityTemp.add(jmpath.jmPathPoints.get(n).isThisSegmentVisible);
            }
        }

        mp.setFillAlpha((float) (fillAlphaTemp * drawParam));

//        jmpath.isFilled = (drawParam >= 1);//Fill path if is completely drawn
        double sliceSize = jmpath.jmPathPoints.size() * drawParam / numSlices;

        for (int n = 0; n < jmpath.jmPathPoints.size() / numSlices; n++) {
            for (int k = 0; k < numSlices; k++) {//TODO: Store initial visible in array
                int h = k * jmpath.jmPathPoints.size() / numSlices + n;
                if (n < sliceSize) {
                    jmpath.getJMPoint(h).isThisSegmentVisible = visibilityTemp.get(h);
                } else {
                    jmpath.getJMPoint(h).isThisSegmentVisible = false;
                }
            }
        }

    }

    public void removeInterpolationPoints() {
        jmpath.removeInterpolationPoints();
    }

    @Override
    public Shape copy() {
        final MathObjectDrawingProperties copy = mp.copy();
        Shape resul = new Shape(jmpath.rawCopy(), copy);
        resul.setObjectType(this.getObjectType());//Copy object type
        return resul;
    }

    @Override
    public void prepareForNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processAfterNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void draw(Renderer r) {

        if (absoluteSize) {
            r.drawAbsoluteCopy(this, getAbsoluteAnchorPoint().v);
        } else {
            r.drawPath(this);
        }
    }

    @Override
    public Rect getBoundingBox() {
        return jmpath.getBoundingBox();
    }

    @Override
    public String toString() {
        return jmpath.toString();
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        for (JMPathPoint p : jmpath.jmPathPoints) {
            scene.registerObjectToBeUpdated(p.p);
            scene.registerObjectToBeUpdated(p.cp1);
            scene.registerObjectToBeUpdated(p.cp2);
        }
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
        for (JMPathPoint p : jmpath.jmPathPoints) {
            scene.unregisterObjectToBeUpdated(p.p);
            scene.unregisterObjectToBeUpdated(p.cp1);
            scene.unregisterObjectToBeUpdated(p.cp2);
        }
    }

    @Override
    public void update() {
        for (JMPathPoint p : jmpath.jmPathPoints) {
            p.update();
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

    @Override
    public <T extends MathObject> T moveTo(Vec coords
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //Static methods to build most used shapes
    public static Shape square() {
        return Shape.square(new Point(0, 0), 1);
    }

    public static Shape square(Point A, double side) {

        return Shape.rectangle(A, A.add(new Vec(side, side)));
    }

    public static Shape rectangle(Rect r) {
        return Shape.rectangle(r.getDL(), r.getUR());
    }

    //Static methods to build most commons shapes
    public static Shape segment(Point A, Point B) {
        Shape obj = new Shape();
        JMathAnimConfig.getConfig().getScene();
        JMPathPoint p1 = JMPathPoint.lineTo(A);
        p1.isThisSegmentVisible = false;
        JMPathPoint p2 = JMPathPoint.lineTo(B);
        obj.jmpath.addJMPoint(p1, p2);
        obj.setObjectType(SEGMENT);
        return obj;
    }
    public static Shape line(Point A, Point B) {
        return new Line(A,B);
    }
    public static Shape rectangle(Point A, Point B) {
        Shape obj = new Shape();
        JMathAnimConfig.getConfig().getScene();
        JMPathPoint p1 = JMPathPoint.lineTo(A);
        JMPathPoint p2 = JMPathPoint.lineTo(B.v.x, A.v.y);//TODO: Make it updateable
        JMPathPoint p3 = JMPathPoint.lineTo(B);
        JMPathPoint p4 = JMPathPoint.lineTo(A.v.x, B.v.y);
        obj.jmpath.addJMPoint(p1, p2, p3, p4);
        obj.setObjectType(RECTANGLE);
        return obj;
    }

    public static Shape polygon(Point... points) {
        Shape obj = new Shape();
        for (Point newPoint : points) {
            JMPathPoint p = JMPathPoint.lineTo(newPoint);
            obj.getPath().addJMPoint(p);
        }
        return obj;
    }

    public static Shape regularPolygon(int numsides) {
        return regularPolygon(numsides, new Point(0, 0), 1);
    }

    public static Shape regularPolygon(int numsides, Point A, double side) {
        Shape obj = new Shape();
        Point newPoint = (Point) A.copy();
        for (int n = 0; n < numsides; n++) {
            double alpha = 2 * n * Math.PI / numsides;
            Vec moveVector = new Vec(side * Math.cos(alpha), side * Math.sin(alpha));
            newPoint = newPoint.add(moveVector);
            JMPathPoint p = JMPathPoint.lineTo(newPoint);
            obj.getPath().addJMPoint(p);
        }
        obj.setObjectType(REGULAR_POLYGON);
        return obj;
    }

    public static Shape arc(double angle) {
        Shape obj = new Shape();
        double x1, y1;
        double step = Math.PI * 2 / 4;
        for (double alphaC = -step; alphaC < angle + step; alphaC += step) {//generate one extra point at each end
            x1 = Math.cos(alphaC);
            y1 = Math.sin(alphaC);
            Point newPoint = new Point(x1, y1);
            JMPathPoint p = JMPathPoint.curveTo(newPoint);
            p.isCurved = true;
            obj.jmpath.addJMPoint(p);
        }
        obj.jmpath.generateControlPoints();
        obj.setObjectType(ARC);
        obj.jmpath.jmPathPoints.remove(0);
        obj.jmpath.jmPathPoints.remove(-1);
        obj.jmpath.getJMPoint(0).isThisSegmentVisible = false;//Open path
        return obj;
    }

    public static Shape circle() {
        Shape obj = new Shape();
        double x1, y1;
        double step = Math.PI * 2 / 40;
        for (double alphaC = 0; alphaC < 2 * Math.PI; alphaC += step) {
            x1 = Math.cos(alphaC);
            y1 = Math.sin(alphaC);
            Point newPoint = new Point(x1, y1);
            JMPathPoint p = JMPathPoint.curveTo(newPoint);
            p.isCurved = true;
            obj.jmpath.addJMPoint(p);
        }
        obj.jmpath.generateControlPoints();
//        obj.jmpath.close();
        obj.setObjectType(CIRCLE);
        return obj;
    }
    public static Shape circle(Point center,double radius)
    {
        return circle().scale(radius).shift(center.v);
    }

}