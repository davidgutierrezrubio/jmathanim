/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Vec;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class JMPathMathObject extends MathObject {

    public final JMPath jmpath;
    protected boolean needsRecalcControlPoints;
    protected int numInterpolationPoints = 1;//TODO: Adaptative interpolation
    protected boolean isClosed = false;
    protected final ArrayList<JMPathPoint> vertices;

    /**
     * Type of path, JMPath.STRAIGHT or JMPath.CURVED
     */
    protected final Point center;

    public JMPathMathObject() {
        this(null);
    }

    public JMPathMathObject(JMPath jmpath, MathObjectDrawingProperties mp) {
        super(mp);
        vertices = new ArrayList<JMPathPoint>();
        jmpath = new JMPath();
        needsRecalcControlPoints = false;
        center = new Point(0, 0);
        this.jmpath = jmpath;
    }

    public JMPathMathObject(MathObjectDrawingProperties mp) {//TODO: Fix this
        super(mp);
        vertices = new ArrayList<JMPathPoint>();
        jmpath = new JMPath();
        computeVerticesFromPath();
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
        jmpath.curveType = jmpath.curveType;
        //This should'nt be done unless necessary (an animation for example)
        if (numInterpolationPoints > 1) {
            jmpath.interpolate(numInterpolationPoints);//Interpolate points
        } 
        updateCenter();

        jmpath.computeControlPoints();
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
        for (JMPathPoint p : vertices) {
            p.p.shift(shiftVector);
        }
        center.shift(shiftVector);
        update();
    }

    public void setCurveType(int type) {
        jmpath.curveType = type;
    }

    public int getCurveType() {
        return jmpath.curveType;
    }

    @Override
    public void setDrawParam(double drawParam) {
         double sliceSize = jmpath.points.size() * drawParam;
            for (int n = 0; n < jmpath.points.size(); n++) {
                jmpath.getPoint(n).isVisible=(n<=sliceSize);
            }
        
    }
    
}
