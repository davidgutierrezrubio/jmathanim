/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.CircularArrayList;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.util.ArrayList;

/**
 * This class stores info for drawing a curve with control points, tension...
 * It's independent of the renderer, which should translate it to proper drawing
 * commands
 *
 * @author David Gutiérrez <davidgutierrezrubio@gmail.com>
 */
public class JMPath implements Updateable, Stateable {

    static public final int MATHOBJECT = 1; //Arc, line, segment...
    static public final int SVG_PATH = 2; //SVG import, LaTeX object...
    public final CircularArrayList<JMPathPoint> jmPathPoints; //points from the curve
    public final CircularArrayList<Boolean> visiblePoints;//Whether this point is visible or not
    public boolean isClosed;
    public boolean isInterpolated;
    public int pathType; //Default value

    double tension = .3d;
    private JMPath pathBackup;

    public JMPath() {
        this(new ArrayList<Point>());
    }

    public JMPath(ArrayList<Point> points) {
        this.jmPathPoints = new CircularArrayList<>();
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
        for (JMPathPoint jmp : jmPathPoints) {
            resul.add(jmp.p);
        }
        return resul;
    }

    public void setPoints(ArrayList<Point> points) {
        this.jmPathPoints.clear();
        for (Point p : points) {
            this.jmPathPoints.add(new JMPathPoint(p, true, JMPathPoint.TYPE_VERTEX));
        }
    }

    public JMPathPoint getPoint(int n) {
        return jmPathPoints.get(n);
    }

    public Point getControlPoint2(int n) {
        return jmPathPoints.get(n).cp2;
    }

    public int size() {
        return jmPathPoints.size();
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
        jmPathPoints.get(0).isVisible=true;
    }

    public void open() {
        isClosed = false;
        jmPathPoints.get(0).isVisible=false;
    }

    public void addPoint(Point... points) {
        for (Point p : points) {
            jmPathPoints.add(new JMPathPoint(p, true, JMPathPoint.TYPE_VERTEX));
        }
    }

    public void addJMPoint(JMPathPoint... points) {
        for (JMPathPoint e : points) {
            jmPathPoints.add(e);
        }
    }

    public void addCPoint1(Point e) {
        jmPathPoints.get(jmPathPoints.size() - 1).cp1.v = e.v.copy();
    }

    public void addCPoint2(Point e) {
        jmPathPoints.get(jmPathPoints.size() - 1).cp2.v = e.v.copy();
    }

    public void clear() {
        jmPathPoints.clear();
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
        int numPoints = jmPathPoints.size();
        if (isClosed) {
            numPoints = numPoints + 1;
        }
        for (int n = 0; n < numPoints; n++) {
            int i = n - 1;
            int k = n + 1;
            int L = n + 2;
            JMPathPoint p1 = jmPathPoints.get(i);
            JMPathPoint p2 = jmPathPoints.get(n);//Compute cp1 for this
            JMPathPoint p3 = jmPathPoints.get(k);//Compute cp2 for this
            JMPathPoint p4 = jmPathPoints.get(L);

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
            int numPoints = jmPathPoints.size();
            if (!isClosed) {//If curve is open, stop at n-1 point
                numPoints--;
            }

            for (int n = 0; n < numPoints; n++) {
//                int k = (n + 1) % points.size(); //Next point, first if curve is closed
                JMPathPoint v1 = getPoint(n);
                JMPathPoint v2 = getPoint(n + 1);

                v1.type = JMPathPoint.TYPE_VERTEX;
                resul.addJMPoint(v1); //Add the point of original curve
                for (int j = 1; j < numDivs; j++) //Now compute the new ones
                {
                    double alpha = ((double) j) / numDivs;
                    JMPathPoint interpolate = interpolateBetweenTwoPoints(v1, v2, alpha);
                    resul.addJMPoint(interpolate);
                }
            }
            //Copy basic attributes of the original curve
            resul.isClosed = this.isClosed;
            resul.generateControlPoints();//WARNING: THIS DOESN'T WORK WITH SVG NOR CURVED PATHS
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
        for (JMPathPoint p : jmPathPoints) {
            if (p.type == JMPathPoint.TYPE_INTERPOLATION_POINT) {
                toRemove.add(p);
            }
        }
        jmPathPoints.removeAll(toRemove);//Remove all interpolation points
        isInterpolated = false;//Mark this path as no interpolated
        //Now, restore old control points
        //for curved paths control points are modified so that a backup is necessary
        for (JMPathPoint p : jmPathPoints) {
            if (p.cp1vBackup != null) {
                p.cp1.v = p.cp1vBackup;
                p.cp1vBackup = null;
            }
            if (p.cp2vBackup != null) {
                p.cp2.v = p.cp2vBackup;
                p.cp2vBackup = null;
            }
        }

//        generateControlPoints();//Recompute control points
    }

    @Override
    public String toString() {
        String resul = "";
        for (JMPathPoint p : jmPathPoints) {
            resul += p.toString();

        }
        return resul;
    }

    /**
     * Add all points from a given path
     *
     * @param jmpathTemp
     */
    public void addPointsFrom(JMPath jmpathTemp) {
        jmPathPoints.addAll(jmpathTemp.jmPathPoints);
    }

    public static JMPathPoint interpolateBetweenTwoPoints(JMPathPoint v1, JMPathPoint v2, double alpha) {

        JMPathPoint interpolate;
        if (v2.isCurved) {
            //De Casteljau's Algorithm: https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
            Point E = v1.p.interpolate(v1.cp1, alpha); //New cp1 of v1
            Point G = v2.cp2.interpolate(v2.p, alpha); //New cp2 of v2
            Point F = v1.cp1.interpolate(v2.cp2, alpha);
            Point H = E.interpolate(F, alpha);//cp2 of interpolation point
            Point J = F.interpolate(G, alpha);//cp1 of interpolation point
            Point K = H.interpolate(J, alpha); //Interpolation point
            interpolate = new JMPathPoint(K, v2.isVisible, JMPathPoint.TYPE_INTERPOLATION_POINT);
            interpolate.cp1.v = J.v;
            interpolate.cp2.v = H.v;
            //Change control points from v1 and v2,save
            //backup values to restore after removing interpolation points
            if (v1.cp1vBackup == null) {
                v1.cp1vBackup = v1.cp1.v;
            }
            if (v2.cp2vBackup == null) {
                v2.cp2vBackup = v2.cp2.v;
            }

            v1.cp1.v = E.v;
            v2.cp2.v = G.v;

        } else {
            //Straight interpolation
            Point interP = new Point(v1.p.v.interpolate(v2.p.v, alpha));
            //Interpolation point is visible iff v2 is visible
            //Control points are by default the same as v1 and v2 (straight line)
            interpolate = new JMPathPoint(interP, v2.isVisible, JMPathPoint.TYPE_INTERPOLATION_POINT);
        }
        interpolate.isCurved = v2.isCurved;
        return interpolate;
    }

    /**
     * Returns a full copy of the path. JMPathPoint objects are also copied
     *
     * @return A copy of the path
     */
    public JMPath rawCopy() {
        JMPath resul = new JMPath();

        for (int n = 0; n < jmPathPoints.size(); n++) {
            resul.addJMPoint(jmPathPoints.get(n).copy());
        }

        resul.isClosed = isClosed;
        resul.isInterpolated = isInterpolated;
        resul.tension = tension;
        resul.pathType = pathType;
        return resul;
    }

    /**
     * Creates a copy of the path, with all their attributes. JMPathPoint
     * objects are referenced instead of copied
     *
     * @return A copy of the path
     */
    public JMPath copy() {
        JMPath resul = new JMPath();
        resul.jmPathPoints.addAll(jmPathPoints);

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

        if (!isClosed) {
            if (direction == -1) {
                step = this.size() - 1;
            }
        }
        JMPath tempPath = this.copy();
        jmPathPoints.clear();

        for (int n = 0; n < tempPath.size(); n++) {
            JMPathPoint point = tempPath.jmPathPoints.get(direction * n + step);
            if (direction < 0) //If reverse the path, we must swap control points
            {

                double cpTempX = point.cp1.v.x;
                double cpTempY = point.cp1.v.y;
                point.cp1.v.x = point.cp2.v.x;
                point.cp1.v.y = point.cp2.v.y;
                point.cp2.v.x = cpTempX;
                point.cp2.v.y = cpTempY;
            }
            jmPathPoints.add(point);
        }

    }

    /**
     * Compute the sum of distance of points from aligned paths This distance
     * should be minimized in order to Transform more smoothly. The paths should
     * have the same number of points.
     *
     * @param path2 The other path
     * @return Distance. Null if paths have different number of points
     */
    public Double sumDistance(JMPath path2) {
        if (this.size() != path2.size()) {
            return null;
        }
        double resul = 0;
        double sumSq = 0;
        double sum = 0;
        for (int n = 0; n < this.size(); n++) {
            Vec v1 = jmPathPoints.get(n).p.v;
            Vec v2 = path2.jmPathPoints.get(n).p.v;
            double dist = v1.distanceTo(v2);
            sumSq += dist;
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
    public void minimizeSumDistance_old(JMPath path2) {
        ArrayList<Double> distances = new ArrayList<Double>();
        double minDistanceVarNoChangeDir = 999999999;
        int minStepNoChangeDir = 0;

        //If the path is open, we can't cycle the path, so 
        //we set numberOfCycles to 1
        int numberOfCycles = (this.isClosed ? this.size() : 1);
        //First, without changing direction
        for (int step = 0; step < numberOfCycles; step++) {
            JMPath tempPath = this.copy();
            tempPath.cyclePoints(step, 1);
            double distanceVar = tempPath.sumDistance(path2);
            distances.add(distanceVar);
            if (distanceVar < minDistanceVarNoChangeDir) {
                minDistanceVarNoChangeDir = distanceVar;
                minStepNoChangeDir = step;
            }

        }
        double minDistanceVarChangeDir = 999999999;
        int minStepChangeDir = 0;
        for (int step = 0; step < numberOfCycles; step++) {
            JMPath tempPath = this.copy();
            tempPath.cyclePoints(step, -1);
            double distanceVar = tempPath.sumDistance(path2);
            distances.add(distanceVar);
            if (distanceVar < minDistanceVarChangeDir) {
                minDistanceVarChangeDir = distanceVar;
                minStepChangeDir = step;
            }
            if (minDistanceVarNoChangeDir < minDistanceVarChangeDir) {
                this.cyclePoints(minStepNoChangeDir, 1);
            } else {
                this.cyclePoints(minStepChangeDir, -1);
            }

        }
    }

    void shift(Vec shiftVector) {
        for (JMPathPoint p : jmPathPoints) {
            p.shift(shiftVector);
        }
    }

    public Rect getBoundingBox() {
        //Initial values for min and max
        double xmin = jmPathPoints.get(0).p.v.x;
        double ymin = jmPathPoints.get(0).p.v.y;
        double xmax = jmPathPoints.get(0).p.v.x;
        double ymax = jmPathPoints.get(0).p.v.y;
        for (JMPathPoint p : jmPathPoints) {
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
        for (JMPathPoint p : jmPathPoints) {
            p.scale(point, d, e, f);

        }
    }

    /**
     * Determine orientation of the path
     *
     * @return 1 if clockwise, -1 if counterwise
     */
    public int getOrientation() {
        //https://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in-clockwise-order/1180256#1180256

        //get the point with lowest y and, in case of tie, max x
        int nmax = 0;
        double ymin = jmPathPoints.get(0).p.v.y;
        double xmax = jmPathPoints.get(0).p.v.x;
        for (int n = 0; n < jmPathPoints.size(); n++) {
            double y0 = jmPathPoints.get(n).p.v.y;
            double x0 = jmPathPoints.get(n).p.v.x;

            if ((y0 < ymin) || ((ymin == y0) && (x0 > xmax))) {
                ymin = y0;
                xmax = x0;
                nmax = n;
            }
        }
        Vec A = jmPathPoints.get(nmax).p.v;
        Vec B = jmPathPoints.get(nmax - 1).p.v;
        Vec C = jmPathPoints.get(nmax + 1).p.v;

        Vec AB = B.minus(A);
        Vec AC = C.minus(A);
        double cross = AB.cross(AC).z;
        int resul = (Math.signum(cross) < 0 ? -1 : 1);

        return resul;
    }

    @Override
    public int getUpdateLevel() {
        int resul = -1;
        for (JMPathPoint p : jmPathPoints) {
            resul = Math.max(resul, p.getUpdateLevel());
        }
        return resul;
    }

    @Override
    public void update() {
        //This should do nothing, let their points to update by themselves
    }

    public void copyFrom(JMPath path) {
        this.clear();
        this.addPointsFrom(path.rawCopy());
        this.isClosed = path.isClosed;
        this.isInterpolated = path.isInterpolated;
        this.pathType = path.pathType;
        this.tension = path.tension;
        this.visiblePoints.clear();
        this.visiblePoints.addAll(path.visiblePoints);

    }

    @Override
    public void restoreState() {
        for (JMPathPoint p : jmPathPoints) {
            p.restoreState();
        }
        this.isClosed = pathBackup.isClosed;
        this.isInterpolated = pathBackup.isInterpolated;
        this.pathType = pathBackup.pathType;
        this.tension = pathBackup.tension;
        this.visiblePoints.clear();
        this.visiblePoints.addAll(pathBackup.visiblePoints);

    }

    @Override
    public void saveState() {
        pathBackup = new JMPath();
        for (JMPathPoint p : jmPathPoints) {
            p.saveState();
        }
        pathBackup.isClosed = this.isClosed;
        pathBackup.isInterpolated = this.isInterpolated;
        pathBackup.pathType = this.pathType;
        pathBackup.tension = this.tension;
        pathBackup.visiblePoints.clear();
        pathBackup.visiblePoints.addAll(pathBackup.visiblePoints);
    }
}
