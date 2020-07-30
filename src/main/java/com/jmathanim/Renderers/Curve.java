/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Renderers;

import com.jmathanim.Utils.JMC;
import com.jmathanim.Utils.Vec;
import com.sun.corba.se.impl.orbutil.closure.Constant;
import java.util.ArrayList;

/**
 * This class stores info for drawing a curve with control points, tension...
 * This info should be passed to the renderer
 *
 * @author David Gutiérrez <davidgutierrezrubio@gmail.com>
 */
public class Curve {

    private ArrayList<Vec> points; //points from the curve
    private ArrayList<Vec> controlPoints1; //Control points (first)
    private ArrayList<Vec> controlPoints2; //Control points (second)
    private boolean isClosed;
    double tension;

    public Curve() {
        this(new ArrayList<Vec>());
    }

    public Curve(ArrayList<Vec> points) {
        this.points = points;
        this.controlPoints1 = new ArrayList<>();
        this.controlPoints2 = new ArrayList<>();
        isClosed = false;
        tension = 0.3d; //Default tension
    }

    public Vec getPoint(int n) {
        return points.get(n);
    }

    public Vec getControlPoint1(int n) {
        return controlPoints1.get(n);
    }

    public Vec getControlPoint2(int n) {
        return controlPoints2.get(n);
    }

    public int size() {
        return points.size();
    }

    public double getTension() {
        return tension;
    }

    public void setTension(double tension) {
        this.tension = tension;
    }

    public void close() {
        isClosed = true;
    }

    public void open() {
        isClosed = false;
    }

    public boolean add(Vec e) {
        return points.add(e);
    }

    public boolean remove(Object o) {
        return points.remove(o);
    }

    public void clear() {
        points.clear();
    }

    /**
     * Compute control points, using various methods This method should be
     * called once all points have been added
     */
    public void computeControlPoints(int curveType) //For now, only one method
    {
        controlPoints1.clear();
        controlPoints2.clear();

        if (curveType == JMC.CURVED) {
            int numPoints = points.size();
            if (numPoints > 4) //I need minimum 2 points      
            {
                if (!isClosed) {
                    numPoints = numPoints - 3;
                }
                for (int n = 0; n < numPoints; n++) {
                    int i = (n - 1) % points.size();
                    int j = (n) % points.size();
                    int k = (n + 1) % points.size();
                    int L = (n + 2) % points.size();
                    double x1 = points.get(i).x;
                    double y1 = points.get(i).y;
                    double x2 = points.get(j).x;
                    double y2 = points.get(j).y;
                    double x3 = points.get(k).x;
                    double y3 = points.get(k).y;
                    double x4 = points.get(L).x;
                    double y4 = points.get(L).y;
                    double tension = 0.3d;
                    double mod31 = Math.sqrt((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1));
                    double mod42 = Math.sqrt((x4 - x2) * (x4 - x2) + (y4 - y2) * (y4 - y2));
                    double mod23 = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));
                    double cx1 = x2 + mod23 / mod31 * tension * (x3 - x1);
                    double cy1 = y2 + mod23 / mod31 * tension * (y3 - y1);
                    double cx2 = x3 - mod23 / mod42 * tension * (x4 - x2);
                    double cy2 = y3 - mod23 / mod42 * tension * (y4 - y2);
                    controlPoints1.add(new Vec(cx1, cy1));
                    controlPoints2.add(new Vec(cx2, cy2));
                }
            }
        } //End of if type==CURVED

        if (curveType == JMC.STRAIGHT) {
            int numPoints = points.size();
            for (int n = 0; n < numPoints; n++) {
                Vec p1 = points.get(n);
                Vec p2 = points.get((n + 1) % numPoints);
                controlPoints1.add(p1);
                controlPoints2.add(p2);
            }

        }//End of if type==STRAIGHT

    }
}
