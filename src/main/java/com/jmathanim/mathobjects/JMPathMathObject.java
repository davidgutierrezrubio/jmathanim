/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.Vec;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class JMPathMathObject extends MathObject {

    protected final JMPath jmpath;
    protected boolean needsRecalcControlPoints;
    protected int numInterpolationPoints = 20;//TODO: Adaptative interpolation
    protected boolean isClosed = false;
    protected final ArrayList<Point> vertices;

    /**
     * Type of path, JMPath.STRAIGHT or JMPath.CURVED
     */
    protected int pathType;
    protected final Point center;

    public JMPathMathObject() {
        this(null);
    }

    public JMPathMathObject(Properties configParam) {
        super(configParam);
        vertices = new ArrayList<Point>();
        jmpath = new JMPath();
        needsRecalcControlPoints = false;
        center = new Point(0, 0);
    }

    /**
     * This method computes all necessary points to the path (interpolation and
     * control)
     */
    protected final void computeJMPath() {
        //TODO: ¿Compute intermediate points?
        JMPath jmpathTemp = new JMPath();
        jmpath.clear();//clear points
        for (Point p : vertices) {
            jmpathTemp.add(p);
        }
        if (isClosed) {
            jmpathTemp.close();
        } else {
            jmpathTemp.open();
        }
        jmpathTemp.curveType = pathType;
        if (numInterpolationPoints > 1) {
            jmpath.addPointsFrom(jmpathTemp.interpolate(numInterpolationPoints));//Interpolate points
        } else {
            jmpath.addPointsFrom(jmpathTemp);
        }
        //Compute center
        Vec vecCenter = new Vec(0, 0);
        for (Point p : jmpath.getPoints()) {
            vecCenter.addInSite(p.v);
        }
        vecCenter.multInSite(1. / jmpath.size());
        center.v = vecCenter;

        jmpath.computeControlPoints(pathType);
        needsRecalcControlPoints = false;
    }

    @Override
    public Point getCenter() {
        return center;

    }

    @Override
    public void shift(Vec shiftVector) {
        for (Point p : vertices) {
            p.shift(shiftVector);
        }
        center.shift(shiftVector);
        update();
    }

}
