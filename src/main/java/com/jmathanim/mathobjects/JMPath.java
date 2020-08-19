/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.CircularArrayList;
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

    public final CircularArrayList<Point> points; //points from the curve
    public final CircularArrayList<Point> controlPoints1; //Control points (first)
    public final CircularArrayList<Point> controlPoints2; //Control points (second)
    private boolean isClosed;
    public boolean isInterpolated;
    double tension;
    public int curveType;

    public JMPath() {
        this(new ArrayList<Point>());
    }

    public JMPath(ArrayList<Point> points) {
        this.points = new CircularArrayList<>();
        this.points.addAll(points);
        this.controlPoints1 = new CircularArrayList<>();
        this.controlPoints2 = new CircularArrayList<>();
        isClosed = false;
        tension = 0.3d; //Default tension
        curveType = JMPath.CURVED;//Default
        isInterpolated = false;//By default, path hasn't interpolation points
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points.clear();
        this.points.addAll(points);
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

    public boolean remove(Object o) {
        return points.remove(o);
    }

    public void addCPoint1(Point p) {
        p.type = Point.TYPE_CONTROL_POINT;
        controlPoints1.add(p);
    }

    public void addCPoint2(Point p) {
        p.type = Point.TYPE_CONTROL_POINT;
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
//                    int i = (n - 1 + points.size()) % points.size();
//                    int j = (n) % points.size();
//                    int k = (n + 1) % points.size();
//                    int L = (n + 2) % points.size();
                    int i = n - 1;
                    int j = n;
                    int k = n + 1;
                    int L = n + 2;
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
                Point cp2 = (Point) points.get((n + 1)).copy();
//                Point cp2 = (Point) points.get((n + 1) % numPoints).copy();
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
     * Returns a subpath delimited by the given parameter. From start to
     * drawParam*length
     *
     * @param drawParam From 0 to 1. 1 means the whole curve.
     * @return A new JMPath representing the corresponding subpath
     */
    public JMPath getSlice(double drawParam) {
        JMPath resul = new JMPath();
        resul.curveType = this.curveType;
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
        if (numDivs > 1) {
            if (curveType == CURVED) {
                throw new UnsupportedOperationException("Not supported interpolation for CURVED paths yet."); //To change body of generated methods, choose Tools | Templates.
            }
            JMPath resul = new JMPath(); //New, interpolated path
            int numPoints = points.size();
            if (!isClosed) {//If curve is open, stop at n-1 point
                numPoints--;
            }

            for (int n = 0; n < numPoints; n++) {
//                int k = (n + 1) % points.size(); //Next point, first if curve is closed
                int k = n + 1;
                if (curveType == CURVED) {
                    //TODO: Implement curved Bezier interpolation
                }
                if (curveType == STRAIGHT) {
                    Point v1 = getPoint(n);
                    Point v2 = getPoint(k);
                    v1.type = Point.TYPE_VERTEX;
                    resul.add(v1); //Add the point of original curve
                    for (int j = 1; j < numDivs; j++) //Now compute the new ones
                    {
                        Point interpolate = new Point(v1.v.interpolate(v2.v, ((double) j) / numDivs));
                        interpolate.type = Point.TYPE_INTERPOLATION_POINT;
                        resul.add(interpolate);
                    }
                }
            }
            //Copy basic attributes of the original curve
            resul.curveType = this.curveType;
            resul.isClosed = this.isClosed;
            resul.computeControlPoints(curveType);
            resul.isInterpolated = true;//Mark this path as interpolated
            return resul;
        } else {
            return this;
        }
    }

    /**
     * Remove interpolation points from path and mark it as no interpolated
     */
    public void removeInterpolationPoints() {
        ArrayList<Point> toRemove = new ArrayList<>();
        for (Point p : points) {
            if (p.type == Point.TYPE_INTERPOLATION_POINT) {
                toRemove.add(p);
            }
        }
        points.removeAll(toRemove);//Remove all interpolation points
        isInterpolated = false;//Mark this path as no interpolated
        computeControlPoints();//Recompute control points
    }

    @Override
    public String toString() {
        String resul = "JMPath[";
        for (Point p : points) {
            resul += ", " + p;
        }
        return resul;
    }

    void addPointsFrom(JMPath jmpathTemp) {
        points.addAll(jmpathTemp.points);
        controlPoints1.addAll(jmpathTemp.controlPoints1);
        controlPoints2.addAll(jmpathTemp.controlPoints2);
    }

    /**
     * Align the number of points of this path with the given one.
     * Align the paths so that they have the same number of points, interpolating
     * the smaller one if necessary.
     * @param path2
     */
    public void alignPaths(JMPath path2) {
        //For now, only STRAIGHT paths
        JMPath pathSmall;
        JMPath pathBig;
        if (this.size() == path2.size()) {
            return;
        }

        if (this.size() < path2.size()) {
            pathSmall = this;
            pathBig = path2;
        } else {
            pathBig = this;
            pathSmall = path2;
        }
        
        //At this point pathSmall points to the smaller path who is going to be
        //interpolated
        int nSmall = pathSmall.size();
        int nBig = pathBig.size();

        JMPath resul = new JMPath();

        int numDivs = (nBig / nSmall); //Euclidean quotient
        int rest = nBig % nSmall;//Euclidean rest
        int numDivForThisVertex;
        for (int n = 0; n < nSmall; n++) {
//                int k = (n + 1) % points.size(); //Next point, first if curve is closed
            int k = n + 1;
            if (pathSmall.curveType == CURVED) {
                throw new UnsupportedOperationException("Don't know interpolate between CURVED paths yet,sorry!");
            }
            if (pathSmall.curveType == STRAIGHT) {
                Point v1 = pathSmall.getPoint(n);
                Point v2 = pathSmall.getPoint(k);
                v1.type = Point.TYPE_VERTEX;
                resul.add(v1); //Add the point of original curve
                numDivForThisVertex = numDivs;
                if (n < rest) { //The <rest> first vertex have an extra interpolation point
                    numDivForThisVertex += 1;
                }
                for (int j = 1; j < numDivForThisVertex; j++) //Now compute the new ones
                {
                    //TODO: Improve alpha parameter
                    Point interpolate = new Point(v1.v.interpolate(v2.v, ((double) j) / numDivForThisVertex));
                    interpolate.type = Point.TYPE_INTERPOLATION_POINT;
                    resul.add(interpolate);
                }
            }
        }
        pathSmall.clear();
        pathSmall.addPointsFrom(resul);
        pathSmall.computeControlPoints();
    }

    public JMPath rawCopy() {
        JMPath resul = new JMPath();

        for (int n = 0; n < points.size(); n++) {
            resul.add((Point) points.get(n).copy());
            resul.addCPoint1((Point) controlPoints1.get(n).copy());
            resul.addCPoint2((Point) controlPoints2.get(n).copy());
        }

        resul.isClosed = isClosed;
        resul.isInterpolated = isInterpolated;
        resul.tension = tension;
        resul.curveType = curveType;
        return resul;
    }

    /**
     * Creates a copy of the path, with all their attributes Point objects are
     * not copied
     *
     * @return A copy of the path
     */
    public JMPath copy() {
        JMPath resul = new JMPath();
        resul.points.addAll(points);
        resul.controlPoints1.addAll(controlPoints1);
        resul.controlPoints2.addAll(controlPoints2);

        resul.isClosed = isClosed;
        resul.isInterpolated = isInterpolated;
        resul.tension = tension;
        resul.curveType = curveType;
        return resul;

    }

    /**
     * Cycle points in path. Point(0) becomes Point(step), Point(1) becomes
     * Point(step+1)... Useful to align paths minimizing distances
     *
     * @param step
     */
    public void cyclePoints(int step, int direction) {
        JMPath tempPath = this.copy();
        points.clear();
        controlPoints1.clear();
        controlPoints2.clear();

        for (int n = 0; n < tempPath.size(); n++) {
            points.add(tempPath.points.get(direction * n + step));
            controlPoints1.add(tempPath.controlPoints1.get(direction * n + step));
            controlPoints2.add(tempPath.controlPoints2.get(direction * n + step));
        }

    }

    /**
     * Compute the sum of distance of points from aligned paths This distance
     * should be minimized in order to Transform more smoothly
     *
     * @param path2 The other path
     * @return Distance
     */
    public Double varianceDistance(JMPath path2) {
        double resul = 0;
        double sumSq = 0;
        double sum = 0;
        for (int n = 0; n < this.size(); n++) {
            Vec v1 = points.get(n).v;
            Vec v2 = path2.points.get(n).v;
            double dist = v1.distanceTo(v2);
            sumSq += dist * dist;
            sum += dist;
        }
        sum /= this.size();
//        resul = sumSq / this.size() - (sum * sum);
        resul = sum;
        return resul;
    }

    public void minimizeDistanceVariance(JMPath path2) {
        ArrayList<Double> distances = new ArrayList<Double>();
        double minDistanceVarNoChangeDir = 999999999;
        int minStepNoChangeDir = 0;

        //First, without changing direction
        for (int step = 0; step < this.size(); step++) {
            JMPath tempPath = this.copy();
            tempPath.cyclePoints(step, 1);
            double distanceVar = tempPath.varianceDistance(path2);
            distances.add(distanceVar);
            System.out.println("Step: " + step + ", distanceVar: " + distanceVar);
            if (distanceVar < minDistanceVarNoChangeDir) {
                minDistanceVarNoChangeDir = distanceVar;
                minStepNoChangeDir = step;
            }

        }
        double minDistanceVarChangeDir = 999999999;
        int minStepChangeDir = 0;
        for (int step = 0; step < this.size(); step++) {
            JMPath tempPath = this.copy();
            tempPath.cyclePoints(step, -1);
            double distanceVar = tempPath.varianceDistance(path2);
            distances.add(distanceVar);
            System.out.println("Step: " + step + ", distanceVar: " + distanceVar);
            if (distanceVar < minDistanceVarChangeDir) {
                minDistanceVarChangeDir = distanceVar;
                minStepChangeDir = step;
            }

        }
        System.out.println("Optimum Step: " + minStepNoChangeDir + ", distance: " + minDistanceVarNoChangeDir);
        System.out.println("Optimum Step: " + minStepChangeDir + ", distance: " + minDistanceVarChangeDir);

        if (minDistanceVarNoChangeDir < minDistanceVarChangeDir) {
            this.cyclePoints(minStepNoChangeDir, 1);
        } else {
            this.cyclePoints(minStepChangeDir, -1);
        }

    }

}
