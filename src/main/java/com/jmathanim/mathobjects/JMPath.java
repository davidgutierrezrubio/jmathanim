/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.CircularArrayList;
import com.jmathanim.Utils.Rect;
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

    static public final int MATHOBJECT = 1; //Arc, line, segment...
    static public final int SVG_PATH = 2; //SVG import, LaTeX object...
    public final CircularArrayList<JMPathPoint> points; //points from the curve
    public final CircularArrayList<Boolean> visiblePoints;//Whether this point is visible or not
    public boolean isClosed;
    public boolean isInterpolated;
    public int pathType; //Default value

    double tension = .3d;

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
        tension = 0.3d; //Default tension
        isInterpolated = false;//By default, path hasn't interpolation points
        pathType = JMPath.MATHOBJECT;//Default value
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
//        addPoint(points.get(0).copy());//TODO: Mark this new point as dependent in case point0 moves
//        points.get(0).isCurved=false;
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
     * called once all points have been added and only if there is generated
     * shape (not to be used with SVG import, for example)
     */
    public void generateControlPoints() //For now, only one method
    {
        //If this is a SVG path, don't generate control points
        if (this.pathType == JMPath.SVG_PATH) {
            return;
        }
        int numPoints = points.size();
        if (isClosed) {
            numPoints = numPoints + 1;
        }
        for (int n = 0; n < numPoints; n++) {
            int i = n - 1;
            int k = n + 1;
            int L = n + 2;
            JMPathPoint p1 = points.get(i);
            JMPathPoint p2 = points.get(n);//Compute cp1 for this
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
            resul.generateControlPoints();//WARNING: THIS DOESN'T WORK WITH SVG
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
        generateControlPoints();//Recompute control points
    }

    @Override
    public String toString() {
        String resul = "";
        for (JMPathPoint p : points) {
            resul += (p.isCurved ? "C" : "R");
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
        pathSmall.generateControlPoints();
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

    /**
     * Returns a full copy of the path. JMPathPoint objects are also copied
     * @return A copy of the path
     */
    public JMPath rawCopy() {
        JMPath resul = new JMPath();

        for (int n = 0; n < points.size(); n++) {
            resul.addPoint(points.get(n).copy());
        }

        resul.isClosed = isClosed;
        resul.isInterpolated = isInterpolated;
        resul.tension = tension;
        resul.pathType = pathType;
        return resul;
    }

    /**
     * Creates a copy of the path, with all their attributes. JMPathPoint objects
     * are referenced instead of copied
     *
     * @return A copy of the path
     */
    public JMPath copy() {
        JMPath resul = new JMPath();
        resul.points.addAll(points);

        //Copy attributes
        resul.isClosed = isClosed;
        resul.isInterpolated = isInterpolated;
        resul.tension = tension;
        resul.pathType = pathType;
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
            JMPathPoint point = tempPath.points.get(direction * n + step);
            if (direction < 0) //If reverse the path, we must swap control points
            {

                double cpTempX = point.cp1.v.x;
                double cpTempY = point.cp1.v.y;
                point.cp1.v.x = point.cp2.v.x;
                point.cp1.v.y = point.cp2.v.y;
                point.cp2.v.x = cpTempX;
                point.cp2.v.y = cpTempY;
            }
            points.add(point);
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
//        for (int step = 0; step < this.size(); step++) {
//            JMPath tempPath = this.copy();
//            tempPath.cyclePoints(step, -1);
//            double distanceVar = tempPath.squaredSumDistance(path2);
//            distances.add(distanceVar);
//            System.out.println("Step: " + step + ", distanceVar: " + distanceVar);
//            if (distanceVar < minDistanceVarChangeDir) {
//                minDistanceVarChangeDir = distanceVar;
//                minStepChangeDir = step;
//            }
//
//        }
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

    public Rect getBoundingBox() {
        //Initial values for min and max
        double xmin = points.get(0).p.v.x;
        double ymin = points.get(0).p.v.y;
        double xmax = points.get(0).p.v.x;
        double ymax = points.get(0).p.v.y;
        for (JMPathPoint p : points) {
            double x = p.p.v.x;
            double y = p.p.v.y;
            xmin = (x < xmin ? x : xmin);
            ymin = (y < ymin ? y : ymin);
            xmax = (x > xmax ? x : xmax);
            ymax = (y > ymax ? y : ymax);
            //Include also control points!
            double cx1 = p.cp1.v.x;
            double cy1 = p.cp1.v.y;
            xmin = (cx1 < xmin ? cx1 : xmin);
            ymin = (cy1 < ymin ? cy1 : ymin);
            xmax = (cx1 > xmax ? cx1 : xmax);
            ymax = (cy1 > ymax ? cy1 : ymax);
            double cx2 = p.cp2.v.x;
            double cy2 = p.cp2.v.y;
            xmin = (cx2 < xmin ? cx2 : xmin);
            ymin = (cy2 < ymin ? cy2 : ymin);
            xmax = (cx2 > xmax ? cx2 : xmax);
            ymax = (cy2 > ymax ? cy2 : ymax);
        }
        return new Rect(xmin, ymin, xmax, ymax);
    }

    void scale(Point point, double d, double e, double f) {
        for (JMPathPoint p : points) {
            p.scale(point, d, e, f);

        }
    }

    /**
     * Determine orientation of the path
     *
     * @return 1 if clockwise, -1 if counterwise
     */
    public int orientation() {
        //https://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in-clockwise-order/1180256#1180256
        return 1;
    }

}
