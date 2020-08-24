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

//    static public final int CURVED = 1; //Curved line
//    static public final int STRAIGHT = 2; //Straight line
    public final CircularArrayList<JMPathPoint> points; //points from the curve
    public final CircularArrayList<Boolean> visiblePoints;//Whether this point is visible or not
    private boolean isClosed;
    public boolean isInterpolated;
    public boolean isFilled;
    public boolean isBorderDrawed;

    double tension;

    public JMPath() {
        this(new ArrayList<Point>());
    }

    public JMPath(ArrayList<Point> points) {
        this.points = new CircularArrayList<>();
        this.setPoints(points);
//        this.controlPoints1 = new CircularArrayList<>();
//        this.controlPoints2 = new CircularArrayList<>();
        this.visiblePoints = new CircularArrayList<>();
        isClosed = false;
        isFilled = false;
        isBorderDrawed = true;
        tension = 0.3d; //Default tension
        isInterpolated = false;//By default, path hasn't interpolation points
    }

    public ArrayList<Point> getPoints() {
        ArrayList<Point> resul = new ArrayList<>();
        for (JMPathPoint jmp : points) {
            resul.add(jmp.p);
        }
        return resul;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points.clear();
        for (Point p : points) {
            this.points.add(new JMPathPoint(p, true, JMPathPoint.TYPE_VERTEX));
        }
    }

    public JMPathPoint getPoint(int n) {
        return points.get(n);
    }

    public Point getControlPoint2(int n) {
        return points.get(n).cp2;
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
        //Add first point and mark as closed
        addPoint(points.get(0).copy());//TODO: Mark this new point as dependent in case point0 moves
        isClosed = true;
    }

    public void open() {
        isClosed = false;
    }

    public boolean addPoint(Point p) {
        return points.add(new JMPathPoint(p, true, JMPathPoint.TYPE_VERTEX));
    }

    public boolean addPoint(JMPathPoint e) {
        return points.add(e);
    }

    public void addCPoint1(Point e) {
        points.get(points.size() - 1).cp1.v = e.v.copy();
    }

    public void addCPoint2(Point e) {
        points.get(points.size() - 1).cp2.v = e.v.copy();
    }

    public void clear() {
        points.clear();
    }

    /**
     * Compute control points, using various methods This method should be
     * called once all points have been added
     *
     */
    public void computeControlPoints() //For now, only one method
    {
        int numPoints = points.size();
        if (!isClosed) {
            numPoints = numPoints - 1;
        }
        for (int n = 0; n < numPoints; n++) {
            int i = n - 1;
            int k = n + 1;
            int L = n + 2;
            JMPathPoint p1 = points.get(i);
            JMPathPoint p2 = points.get(n);//Compute cp2 for this
            JMPathPoint p3 = points.get(k);//Compute cp2 for this
            JMPathPoint p4 = points.get(L);
            double x1 = p1.p.v.x;
            double y1 = p1.p.v.y;
            double x2 = p2.p.v.x;
            double y2 = p2.p.v.y;
            double x3 = p3.p.v.x;
            double y3 = p3.p.v.y;
            double x4 = p4.p.v.x;
            double y4 = p4.p.v.y;
            if (p3.isCurved) {
                double tension = 0.3d;
                double mod31 = Math.sqrt((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1));
                double mod42 = Math.sqrt((x4 - x2) * (x4 - x2) + (y4 - y2) * (y4 - y2));
                double mod23 = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));
                double cx1 = x2 + mod23 / mod31 * tension * (x3 - x1);
                double cy1 = y2 + mod23 / mod31 * tension * (y3 - y1);
                double cx2 = x3 - mod23 / mod42 * tension * (x4 - x2);
                double cy2 = y3 - mod23 / mod42 * tension * (y4 - y2);
                p2.cp1.v.x = cx1;
                p2.cp1.v.y = cy1;
                p3.cp2.v.x = cx2;
                p3.cp2.v.y = cy2;
            } else {
                //If this path is straight, control points becomes vertices. Although this is not used
                //when drawing straight paths, it becomes handy when doing transforms from STRAIGHT to CURVED paths
                p2.cp1.v.x = p2.p.v.x;
                p2.cp1.v.y = p2.p.v.y;
                p3.cp2.v.x = p3.p.v.x;
                p3.cp2.v.y = p3.p.v.y;
            }

        }
    }

    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Interpolates a curve calculating intermediate points. If the original
     * curve has n points, the new should have (n-1)*numDivs+1 for open curves
     * and n*numDivs for closed ones.
     *
     * @param numDivs Between 2 given points, the number of new points to
     * create. 0 leaves the curve unaltered. 1 computes the middle point
     */
    public void interpolate(int numDivs) {
        if (numDivs > 1) {

            JMPath resul = new JMPath(); //New, interpolated path
            int numPoints = points.size();
            if (!isClosed) {//If curve is open, stop at n-1 point
                numPoints--;
            }

            for (int n = 0; n < numPoints; n++) {
//                int k = (n + 1) % points.size(); //Next point, first if curve is closed
                JMPathPoint v1 = getPoint(n);
                JMPathPoint v2 = getPoint(n + 1);

                v1.type = JMPathPoint.TYPE_VERTEX;
                resul.addPoint(v1); //Add the point of original curve
                for (int j = 1; j < numDivs; j++) //Now compute the new ones
                {
                    double alpha = ((double) j) / numDivs;
                    JMPathPoint interpolate = interpolateBetweenTwoPoints(v1, v2, alpha);
                    resul.addPoint(interpolate);
                }
            }
            //Copy basic attributes of the original curve
            resul.isClosed = this.isClosed;
            resul.computeControlPoints();
            resul.isInterpolated = true;//Mark this path as interpolated
            this.clear();
            this.addPointsFrom(resul);
        }
    }

    /**
     * Remove interpolation points from path and mark it as no interpolated
     */
    public void removeInterpolationPoints() {
        ArrayList<JMPathPoint> toRemove = new ArrayList<>();
        for (JMPathPoint p : points) {
            if (p.type == JMPathPoint.TYPE_INTERPOLATION_POINT) {
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
        for (JMPathPoint p : points) {
            resul += ", " + p;
        }
        return resul;
    }

    /**
     * Add all points from a given path
     *
     * @param jmpathTemp
     */
    void addPointsFrom(JMPath jmpathTemp) {
        points.addAll(jmpathTemp.points);
    }

    /**
     * Align the number of points of this path with the given one. Align the
     * paths so that they have the same number of points, interpolating the
     * smaller one if necessary.
     *
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
            JMPathPoint v1 = pathSmall.getPoint(n);
            JMPathPoint v2 = pathSmall.getPoint(n + 1);
            v1.type = JMPathPoint.TYPE_VERTEX;
            resul.addPoint(v1); //Add the point of original curve
            numDivForThisVertex = numDivs;
            if (n < rest) { //The <rest> first vertex have an extra interpolation point
                numDivForThisVertex += 1;
            }
            for (int j = 1; j < numDivForThisVertex; j++) //Now compute the new ones
            {
                double alpha = j * 1.d / numDivForThisVertex;
                JMPathPoint interpolate = interpolateBetweenTwoPoints(v1, v2, alpha);
                resul.addPoint(interpolate);
            }
        }
        pathSmall.clear();
        pathSmall.addPointsFrom(resul);
        pathSmall.computeControlPoints();
    }

    public JMPathPoint interpolateBetweenTwoPoints(JMPathPoint v1, JMPathPoint v2, double alpha) {

        JMPathPoint interpolate;
        if (v2.isCurved) {
            double x0 = v1.p.v.x;
            double y0 = v1.p.v.y;
            double x1 = v1.cp1.v.x;
            double y1 = v1.cp1.v.y;
            double x2 = v2.cp2.v.x;
            double y2 = v2.cp2.v.y;
            double x3 = v2.p.v.x;
            double y3 = v2.p.v.y;
            double ix = x0 * (1 - alpha) * (1 - alpha) * (1 - alpha) + 3 * x1 * alpha * (1 - alpha) * (1 - alpha);
            ix += 3 * x2 * alpha * alpha * (1 - alpha) + x3 * alpha * alpha * alpha;
            double iy = y0 * (1 - alpha) * (1 - alpha) * (1 - alpha) + 3 * y1 * alpha * (1 - alpha) * (1 - alpha);
            iy += 3 * y2 * alpha * alpha * (1 - alpha) + y3 * alpha * alpha * alpha;
            interpolate = new JMPathPoint(new Point(ix, iy), v2.isVisible, JMPathPoint.TYPE_INTERPOLATION_POINT);
        } else {
            Point interP = new Point(v1.p.v.interpolate(v2.p.v, alpha));
            //Interpolation point is visible iff v2 is visible
            interpolate = new JMPathPoint(interP, v2.isVisible, JMPathPoint.TYPE_INTERPOLATION_POINT);
        }
        interpolate.isCurved = v2.isCurved;
        return interpolate;
    }

    public JMPath rawCopy() {
        JMPath resul = new JMPath();

        for (int n = 0; n < points.size(); n++) {
            resul.addPoint(points.get(n).copy());
        }

        resul.isClosed = isClosed;
        resul.isFilled = isFilled;
        resul.isBorderDrawed = isBorderDrawed;
        resul.isInterpolated = isInterpolated;
        resul.tension = tension;
        return resul;
    }

    /**
     * Creates a copy of the path, with all their attributes JMPathPoint objects
     * are referenced
     *
     * @return A copy of the path
     */
    public JMPath copy() {
        JMPath resul = new JMPath();
        resul.points.addAll(points);

        //Copy attributes
        resul.isClosed = isClosed;
        resul.isFilled = isFilled;
        resul.isBorderDrawed = isBorderDrawed;
        resul.isInterpolated = isInterpolated;
        resul.tension = tension;
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

        for (int n = 0; n < tempPath.size(); n++) {
            points.add(tempPath.points.get(direction * n + step));
        }

    }

    /**
     * Compute the sum of distance of points from aligned paths This distance
     * should be minimized in order to Transform more smoothly
     *
     * @param path2 The other path
     * @return Distance
     */
    public Double squaredSumDistance(JMPath path2) {
        double resul = 0;
        double sumSq = 0;
        double sum = 0;
        for (int n = 0; n < this.size(); n++) {
            Vec v1 = points.get(n).p.v;
            Vec v2 = path2.points.get(n).p.v;
            double dist = v1.distanceTo(v2);
            sumSq += dist * dist;
            sum += dist;
        }
        sum /= this.size();
//        resul = sumSq / this.size() - (sum * sum);
        resul = sum;
        return resul;
    }

    /**
     * Cycles the point of closed path (and inverts its orientation if
     * necessary) in order to minimize the sum of squared distances from the
     * points of two paths with the same number of nodes
     *
     * @param path2
     */
    public void minimizeSquaredDistance(JMPath path2) {
        ArrayList<Double> distances = new ArrayList<Double>();
        double minDistanceVarNoChangeDir = 999999999;
        int minStepNoChangeDir = 0;

        //First, without changing direction
        for (int step = 0; step < this.size(); step++) {
            JMPath tempPath = this.copy();
            tempPath.cyclePoints(step, 1);
            double distanceVar = tempPath.squaredSumDistance(path2);
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
            double distanceVar = tempPath.squaredSumDistance(path2);
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

    void shift(Vec shiftVector) {
        for (JMPathPoint p : points) {
            p.shift(shiftVector);
        }
    }

}
