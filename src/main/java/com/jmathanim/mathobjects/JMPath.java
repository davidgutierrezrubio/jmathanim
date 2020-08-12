/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.Vec;
import java.util.ArrayList;

/**
 * This class stores info for drawing a curve with control points, tension...
 * It's independent of the renderer, which should translate it to proper drawing
 * commands
 *
 * @author David Gutiérrez <davidgutierrezrubio@gmail.com>
 */
public class JMPath {

    static public final int CURVED = 1; //Curved line
    static public final int STRAIGHT = 2; //Straight line

    public ArrayList<Point> points; //points from the curve
    public ArrayList<Point> controlPoints1; //Control points (first)
    public ArrayList<Point> controlPoints2; //Control points (second)
    private boolean isClosed;
    double tension;
    public int curveType;

    public JMPath() {
        this(new ArrayList<Point>());
    }

    public JMPath(ArrayList<Point> points) {
        this.points = points;
        this.controlPoints1 = new ArrayList<>();
        this.controlPoints2 = new ArrayList<>();
        isClosed = false;
        tension = 0.3d; //Default tension
        curveType = JMPath.CURVED;//Default
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public Point getPoint(int n) {
        return points.get(n);
    }

    public Point getControlPoint1(int n) {
        return controlPoints1.get(n);
    }

    public Point getControlPoint2(int n) {
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

    public boolean add(Point e) {
        return points.add(e);
    }

//    public boolean add(MathObject p) {
//        return points.add(p.getCenter());
//    }

    public boolean remove(Object o) {
        return points.remove(o);
    }

    public void addCPoint1(Point p) {
        p.type=Point.TYPE_CONTROL_POINT;
        controlPoints1.add(p);
    }

    public void addCPoint2(Point p) {
        p.type=Point.TYPE_CONTROL_POINT;
        controlPoints2.add(p);
    }

    public void clear() {
        points.clear();
        controlPoints1.clear();
        controlPoints2.clear();
    }

    public void computeControlPoints() {
        computeControlPoints(this.curveType);
    }

    /**
     * Compute control points, using various methods This method should be
     * called once all points have been added
     *
     * @param curveType Curve type. STRAIGHT as a polygonal line with no control
     * points. CURVED as a cubic Bezier curve.
     */
    public void computeControlPoints(int curveType) //For now, only one method
    {
        controlPoints1.clear();
        controlPoints2.clear();
        this.curveType = curveType;

        if (curveType == JMPath.CURVED) {
            int numPoints = points.size();
            if (numPoints > 4) //I need minimum 2 points      
            {
                if (!isClosed) {
                    numPoints = numPoints - 1;
                }
                for (int n = 0; n < numPoints; n++) {
                    int i = (n - 1 + points.size()) % points.size();
                    int j = (n) % points.size();
                    int k = (n + 1) % points.size();
                    int L = (n + 2) % points.size();
//                    System.out.println("Size:" + points.size() + "-->" + i + " " + " " + j + " " + k + " " + L);
                    double x1 = points.get(i).v.x;
                    double y1 = points.get(i).v.y;
                    double x2 = points.get(j).v.x;
                    double y2 = points.get(j).v.y;
                    double x3 = points.get(k).v.x;
                    double y3 = points.get(k).v.y;
                    double x4 = points.get(L).v.x;
                    double y4 = points.get(L).v.y;
                    double tension = 0.3d;
                    double mod31 = Math.sqrt((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1));
                    double mod42 = Math.sqrt((x4 - x2) * (x4 - x2) + (y4 - y2) * (y4 - y2));
                    double mod23 = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));
                    double cx1 = x2 + mod23 / mod31 * tension * (x3 - x1);
                    double cy1 = y2 + mod23 / mod31 * tension * (y3 - y1);
                    double cx2 = x3 - mod23 / mod42 * tension * (x4 - x2);
                    double cy2 = y3 - mod23 / mod42 * tension * (y4 - y2);
                    Point cp1 = new Point(cx1, cy1);
                    Point cp2 = new Point(cx2, cy2);
                    cp1.type = Point.TYPE_CONTROL_POINT;
                    cp2.type = Point.TYPE_CONTROL_POINT;
                    controlPoints1.add(cp1);
                    controlPoints2.add(cp2);
                }
            }
        } //End of if type==CURVED

        if (curveType == JMPath.STRAIGHT) {
            int numPoints = points.size();
            for (int n = 0; n < numPoints; n++) {
                Point cp1 = (Point) points.get(n).copy();
                Point cp2 = (Point) points.get((n + 1) % numPoints).copy();
                cp1.type = Point.TYPE_CONTROL_POINT;
                cp2.type = Point.TYPE_CONTROL_POINT;
                controlPoints1.add(cp1);
                controlPoints2.add(cp2);
            }

        }//End of if type==STRAIGHT

    }

    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Returns a subpath delimited by the given parameter.
     * From start to drawParam*length
     *
     * @param drawParam From 0 to 1. 1 means the whole curve.
     * @return A new JMPath representing the corresponding subpath
     */
    public JMPath getSlice(double drawParam) {
        JMPath resul = new JMPath();
        resul.curveType=this.curveType;
        if (drawParam < 1) {
            double sliceSize = points.size() * drawParam;
            for (int n = 0; n < sliceSize; n++) {
                resul.add(points.get(n));
                resul.addCPoint1(controlPoints1.get(n));
                resul.addCPoint2(controlPoints2.get(n));
            }
            resul.open();
        } else {
            resul = this;
        }
        return resul;
    }

    /**
     * Interpolates a curve calculating intermediate points. If the original
     * curve has n points, the new should have (n-1)*numDivs+1 for open curves
     * and n*numDivs for closed ones.
     *
     * @param numDivs Between 2 given points, the number of new points to
     * create. 0 leaves the curve unaltered. 1 computes the middle point
     * @return new JMPath representing the interpolated curve
     */
    public JMPath interpolate(int numDivs) {
        if (curveType == CURVED) {
            throw new UnsupportedOperationException("Not supported interpolation for CURVED paths yet."); //To change body of generated methods, choose Tools | Templates.
        }
        JMPath resul = new JMPath(); //New, interpolated path
        int numPoints = points.size();
        if (!isClosed) {//If curve is open, stop at n-1 point
            numPoints--;
        }

        for (int n = 0; n < numPoints; n++) {
            int k = (n + 1) % points.size(); //Next point, first if curve is closed
            if (curveType == CURVED) {
                //TODO: Implement curved Bezier interpolation
            }
            if (curveType == STRAIGHT) {
                Point v1 = getPoint(n);
                Point v2 = getPoint(k);
                v1.type=Point.TYPE_VERTEX;
                resul.add(v1); //Add the point of original curve
                for (int j = 0; j < numDivs; j++) //Now compute the new ones
                {
                    Point interpolate = new Point(v1.v.interpolate(v2.v, ((double) j) / numDivs));
                    interpolate.type=Point.TYPE_INTERPOLATION_POINT;
                    resul.add(interpolate);
                }
            }
        }
        //Copy basic attributes of the original curve
        resul.curveType = this.curveType;
        resul.isClosed = this.isClosed;
        resul.computeControlPoints(curveType);
        return resul;
    }

    @Override
    public String toString() {
        String resul="JMPath[";
        for (Point p:points)
            resul+=", "+p;
        return resul;
    }

    void addPointsFrom(JMPath jmpathTemp) {
        points.addAll(jmpathTemp.points);
        controlPoints1.addAll(jmpathTemp.controlPoints1);
        controlPoints2.addAll(jmpathTemp.controlPoints2);
        
        
        
    }
    

}
