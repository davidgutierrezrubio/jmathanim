/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class JMPathMathObject extends MathObject {

    public final JMPath jmpath;
    protected boolean needsRecalcControlPoints;
    protected int numInterpolationPoints = 1;//TODO: Adaptative interpolation
    protected boolean isClosed = false;
    protected final ArrayList<JMPathPoint> vertices;

    /**
     * Type of path, JMPath.STRAIGHT or JMPath.CURVED
     */
    protected final Point center;
    private ArrayList<Boolean> visibilityTemp;
    private double fillAlphaTemp;

    public JMPathMathObject() {
        this(null);
    }

    public JMPathMathObject(JMPath jmpath, MathObjectDrawingProperties mp) {
        super(mp);
        vertices = new ArrayList<JMPathPoint>();
        this.jmpath = jmpath.copy();
        needsRecalcControlPoints = false;
        center = new Point(0, 0);
    }

    public JMPathMathObject(MathObjectDrawingProperties mp) {//TODO: Fix this
        super(mp);
        vertices = new ArrayList<JMPathPoint>();
        jmpath = new JMPath();
        needsRecalcControlPoints = false;
        center = new Point(0, 0);
    }

    /**
     * This method computes all necessary points to the path (interpolation and
     * control)
     */
    protected final void computeJMPathFromVertices() {
        //TODO: ¿Compute intermediate points?
        jmpath.clear();//clear points
        for (JMPathPoint p : vertices) {
            jmpath.addPoint(p);
        }
        if (isClosed) {
            jmpath.close();
        } else {
            jmpath.open();
        }
        //This should'nt be done unless necessary (an animation for example)
        if (numInterpolationPoints > 1) {
            jmpath.interpolate(numInterpolationPoints);//Interpolate points
        }
        updateCenter();

        jmpath.generateControlPoints();
        needsRecalcControlPoints = false;
    }

    public void updateCenter() {
        //Compute center
        Vec vecCenter = new Vec(0, 0);
        for (JMPathPoint p : jmpath.points) {
            vecCenter.addInSite(p.p.v);
        }
        vecCenter.multInSite(1. / jmpath.size());
        center.v = vecCenter;
    }

    protected final void computeVerticesFromPath() {
        vertices.clear();
        for (JMPathPoint p : jmpath.points) {
            if (p.type == JMPathPoint.TYPE_VERTEX) {
                vertices.add(p);
            }
        }
    }

    @Override
    public Point getCenter() {
        return center;

    }

    @Override
    public void shift(Vec shiftVector) {
        jmpath.shift(shiftVector);
    }

    @Override
    public void setDrawParam(double drawParam, int numSlices) {

        //If this is the first call, be sure to store visibility status
        if (drawParam == 0) {
            fillAlphaTemp = mp.fillColor.getAlpha()/255.;
            visibilityTemp = new ArrayList<Boolean>();
            for (int n = 0; n < jmpath.points.size(); n++) {
                visibilityTemp.add(jmpath.points.get(n).isVisible);
            }
        }
        
        mp.setFillAlpha((float) (fillAlphaTemp*drawParam));

//        jmpath.isFilled = (drawParam >= 1);//Fill path if is completely drawn
        double sliceSize = jmpath.points.size() * drawParam / numSlices;

        for (int n = 0; n < jmpath.points.size() / numSlices; n++) {
            for (int k = 0; k < numSlices; k++) {//TODO: Store initial visible in array
                int h = k * jmpath.points.size() / numSlices + n;
                if (n < sliceSize) {
                    jmpath.getPoint(h).isVisible = visibilityTemp.get(h);
                } else {
                    jmpath.getPoint(h).isVisible = false;
                }
            }
        }

    }

    public void removeInterpolationPoints() {
        ArrayList<JMPathPoint> toRemove = new ArrayList<>();
        for (JMPathPoint p : jmpath.points) {
            if (p.type == JMPathPoint.TYPE_INTERPOLATION_POINT) {
                toRemove.add(p);
            }
        }
        jmpath.points.removeAll(toRemove);
        jmpath.generateControlPoints();
    }

    @Override
    public void moveTo(Vec coords) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scale(Point scaleCenter, double sx, double sy, double sz) {
        jmpath.scale(scaleCenter, sx, sy, sz);
    }

    @Override
    public MathObject copy() {
        return new JMPathMathObject(jmpath.rawCopy(), mp.copy());
    }

    @Override
    public void update() {
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
        r.setBorderColor(mp.drawColor);
        r.setFillColor(mp.fillColor);
        r.setStroke(mp.getThickness(r));
        r.drawPath(this, jmpath);
    }

    @Override
    public Rect getBoundingBox() {
        return jmpath.getBoundingBox();
    }

    void setColor(Color color) {
        this.mp.drawColor = color;
    }

    void setFillColor(Color color) {
        this.mp.fillColor = color;
    }

    @Override
    public void setDrawAlpha(double t) {
        this.mp.setDrawAlpha((float) t);
    }

    @Override
    public void setFillAlpha(double t) {
        this.mp.setFillAlpha((float) t);
    }

    @Override
    public String toString() {
        return jmpath.toString();
    }

}
