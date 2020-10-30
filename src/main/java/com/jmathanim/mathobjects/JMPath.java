/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.CircularArrayList;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint.JMPathPointType;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.util.ArrayList;
import java.util.Arrays;

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
    static public final int CONNECTED_COMPONENT = 3; //Connected, open component. Every path should be able to put in this way
    public final CircularArrayList<JMPathPoint> jmPathPoints; //points from the curve
    public final CircularArrayList<Boolean> visiblePoints;//Whether this point is visible or not
    public int pathType; //Default value

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
        pathType = JMPath.MATHOBJECT;//Default value
    }

    public ArrayList<Point> getPoints() {
        ArrayList<Point> resul = new ArrayList<>();
        for (JMPathPoint jmp : jmPathPoints) {
            resul.add(jmp.p);
        }
        return resul;
    }

    public final void setPoints(ArrayList<Point> points) {
        this.jmPathPoints.clear();
        for (Point p : points) {
            this.jmPathPoints.add(new JMPathPoint(p, true, JMPathPointType.VERTEX));
        }
    }

    public JMPathPoint getJMPoint(int n) {
        return jmPathPoints.get(n);
    }

    public Point getControlPoint2(int n) {
        return jmPathPoints.get(n).cp2;
    }

    public int size() {
        return jmPathPoints.size();
    }

    public void addPoint(Point... points) {
        for (Point p : points) {
            jmPathPoints.add(new JMPathPoint(p, true, JMPathPointType.VERTEX));
        }
    }

    public void addJMPoint(JMPathPoint... points) {
        jmPathPoints.addAll(Arrays.asList(points));
    }

    public void addCPoint1(Point e) {
        jmPathPoints.get(jmPathPoints.size() - 1).cp1.v.copyFrom(e.v);
    }

    public void addCPoint2(Point e) {
        jmPathPoints.get(jmPathPoints.size() - 1).cp2.v.copyFrom(e.v);
    }

    public void clear() {
        jmPathPoints.clear();
    }

    /**
     * Remove interpolation points from path and mark it as no interpolated
     */
    public void removeInterpolationPoints() {
        ArrayList<JMPathPoint> toRemove = new ArrayList<>();
        for (JMPathPoint p : jmPathPoints) {
            if (p.type == JMPathPointType.INTERPOLATION_POINT) {
                toRemove.add(p);
            }
        }
        jmPathPoints.removeAll(toRemove);//Remove all interpolation points
        //Now, restore old control points
        //for curved paths control points are modified so that a backup is necessary
        for (JMPathPoint p : jmPathPoints) {
            if (p.cp1vBackup != null) {
                p.cp1.v.copyFrom(p.cp1vBackup);
                p.cp1vBackup = null;
            }
            if (p.cp2vBackup != null) {
                p.cp2.v.copyFrom(p.cp2vBackup);
                p.cp2vBackup = null;
            }
        }

//        generateControlPoints();//Recompute control points
    }

    @Override
    public String toString() {
        String resul = "#" + jmPathPoints.size() + ":  ";
        int counter = 0;
        for (JMPathPoint p : jmPathPoints) {
            resul += "< " + counter + " " + p.toString() + "> ";
            counter++;

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

    /**
     * Proceeds to subdidivide paths to ensure the path has exactly the given
     * number of elements Invisible pieces of path are not interpolated. New
     * generated points are marked as INTERPOLATION_POINT
     *
     * @param newNumPoints New number of points. Must be greater or equal than actual number of points in the path
     */
    public void alignPathsToGivenNumberOfElements(int newNumPoints) {
        if (newNumPoints <= this.size()) {
            return; //Nothing to do here!
        }
        //First compute how many visible segments are
        ArrayList<JMPathPoint> pointsToInterpolate = new ArrayList<>();
        //Loop is from 1 because I want to add extra point to the first segment (point 1) and not the last (point 0)
        for (int n = 1; n < 1 + jmPathPoints.size(); n++) {
            JMPathPoint p = jmPathPoints.get(n);
            if (p.isThisSegmentVisible) {
                pointsToInterpolate.add(p);
            }
        }
        int numVisibleSegments = pointsToInterpolate.size();
        int numPoints = jmPathPoints.size();
        int toCreate = newNumPoints - numPoints;//Number of points to create, to put in the numVisibleSegments segments

        int numDivs = (toCreate / numVisibleSegments); //Euclidean quotient
        int rest = toCreate % numVisibleSegments;//Euclidean rest

        for (int n = 0; n < pointsToInterpolate.size(); n++) {
            JMPathPoint p = pointsToInterpolate.get(n);
            p.numDivisions = numDivs + 1;//it is number of divisions, not number of points to be created. 1 new point means divide in 2
            p.numDivisions += (n < rest ? 1 : 0);
        }
        //Once I have the number of segments to interpolate, subdivide all visible segments

        for (JMPathPoint p : pointsToInterpolate) {
            int k = jmPathPoints.indexOf(p);//Position of this point in the path
            dividePathSegment(k, p.numDivisions);
        }

    }

    /**
     * Divide path from point(k) to point(k-1) into an equal number of parts.
     *
     * @param k index of end point
     * @param numDivForThisVertex Number of subdivisions
     */
    public synchronized void dividePathSegment(int k, int numDivForThisVertex) {
        if (numDivForThisVertex < 2) {
            return;
        }
        double alpha = 1.0d / numDivForThisVertex;
        interpolateBetweenTwoPoints(k, alpha);
        dividePathSegment(k + 1, numDivForThisVertex - 1);//Keep subdividing until numDivForThisVertex=1
    }

    /**
     * Adds an interpolation point at alpha parameter between point(k-1) and
     * point(k) This method alters the control points of the points k-1 and k,
     * storing them into cp1vbackup and cp2vbackup
     *
     * @param k inded of the point to be interpolated
     * @param alpha Alpha parameter
     * @return The new JMPathPoint generated, and added to the Path
     */
    public JMPathPoint interpolateBetweenTwoPoints(int k, double alpha) {
        JMPathPoint jmp1 = getJMPoint(k - 1);
        JMPathPoint jmp2 = getJMPoint(k);
        JMPathPoint interpolate;
        if (jmp2.isCurved) {
            //De Casteljau's Algorithm: https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
            Point E = jmp1.p.interpolate(jmp1.cp1, alpha); //New cp1 of v1
            Point G = jmp2.cp2.interpolate(jmp2.p, alpha); //New cp2 of v2
            Point F = jmp1.cp1.interpolate(jmp2.cp2, alpha);
            Point H = E.interpolate(F, alpha);//cp2 of interpolation point
            Point J = F.interpolate(G, alpha);//cp1 of interpolation point
            Point K = H.interpolate(J, alpha); //Interpolation point
            interpolate = new JMPathPoint(K, jmp2.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
            interpolate.cp1.v.copyFrom(J.v);
            interpolate.cp2.v.copyFrom(H.v);
            //Change control points from v1 and v2,save
            //backup values to restore after removing interpolation points
            if (jmp1.cp1vBackup == null) {
                jmp1.cp1vBackup = jmp1.cp1.v;
            }
            if (jmp2.cp2vBackup == null) {
                jmp2.cp2vBackup = jmp2.cp2.v;
            }

            jmp1.cp1.v.copyFrom(E.v);
            jmp2.cp2.v.copyFrom(G.v);

        } else {
            //Straight interpolation
            Point interP = new Point(jmp1.p.v.interpolate(jmp2.p.v, alpha));
            //Interpolation point is visible iff v2 is visible
            //Control points are by default the same as v1 and v2 (straight line)
            interpolate = new JMPathPoint(interP, jmp2.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
        }
        interpolate.isCurved = jmp2.isCurved; //The new point is curved iff v2 is
        jmPathPoints.add(k, interpolate); //Now v2 is in position k+1!
        return interpolate;
    }

    /**
     * Get point (interpolated if necessary) that lies at position alpha where
     * alpha=0 denotes beginning of path and alpha=1 denotes the end
     *
     * @param alpha from 0 to 1, relative position inside the path
     * @return A (copy of) point that lies in the curve at relative position
     * alpha.
     */
    public Point getPointAt(double alpha) {
        Point resul;
        int k = (int) Math.floor(alpha * size());
        double t = alpha * size() - k;
        JMPathPoint v1 = getJMPoint(k);
        JMPathPoint v2 = getJMPoint(k + 1);
        if (v2.isCurved) {
            //De Casteljau's Algorithm: https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
            Point E = v1.p.interpolate(v1.cp1, t); //New cp1 of v1
            Point G = v2.cp2.interpolate(v2.p, t); //New cp2 of v2
            Point F = v1.cp1.interpolate(v2.cp2, t);
            Point H = E.interpolate(F, t);//cp2 of interpolation point
            Point J = F.interpolate(G, t);//cp1 of interpolation point
            resul = H.interpolate(J, t); //Interpolation point
        } else {
            //Straight interpolation
            resul = new Point(v1.p.v.interpolate(v2.p.v, t));
        }
        return resul;
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
        resul.pathType = pathType;
        return resul;

    }

    /**
     * Cycle points in path.Point(0) becomes Point(step), Point(1) becomes
     * Point(step+1)... Useful to align paths minimizing distances
     *
     * @param step
     * @param direction
     */
    public void cyclePoints(int step, int direction) {

//        if (!isClosed) {
//            if (direction == -1) {
//                step = this.size() - 1;
//            }
//        }
        JMPath tempPath = this.copy();
        jmPathPoints.clear();

        for (int n = 0; n < tempPath.size(); n++) {
            JMPathPoint point = tempPath.jmPathPoints.get(direction * n + step);
            if (direction < 0) //If reverse the path, we must swap control points
            {

                double cpTempX = point.cp1.v.x;
                double cpTempY = point.cp1.v.y;
                double cpTempZ = point.cp1.v.z;
                point.cp1.v.copyFrom(point.cp2.v);
                point.cp2.v.x = cpTempX;
                point.cp2.v.y = cpTempY;
                point.cp2.v.z = cpTempZ;
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
            double dist = v1.minus(v2).norm();
            sumSq += dist;
            sum += dist;
        }
        sum /= this.size();
//        resul = sumSq / this.size() - (sum * sum);
        resul = sum;
        return resul;
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
        double zmin = jmPathPoints.get(0).p.v.z;
        double xmax = jmPathPoints.get(0).p.v.x;
        double ymax = jmPathPoints.get(0).p.v.y;
        double zmax = jmPathPoints.get(0).p.v.z;
        for (JMPathPoint p : jmPathPoints) {
            double x = p.p.v.x;
            double y = p.p.v.y;
            double z = p.p.v.z;
            xmin = (x < xmin ? x : xmin);
            ymin = (y < ymin ? y : ymin);
            zmin = (z < zmin ? z : zmin);
            xmax = (x > xmax ? x : xmax);
            ymax = (y > ymax ? y : ymax);
            zmax = (z > zmax ? z : zmax);
            //Include also control points!
            double cx1 = p.cp1.v.x;
            double cy1 = p.cp1.v.y;
            double cz1 = p.cp1.v.z;
            xmin = (cx1 < xmin ? cx1 : xmin);
            ymin = (cy1 < ymin ? cy1 : ymin);
            zmin = (cz1 < zmin ? cz1 : zmin);
            xmax = (cx1 > xmax ? cx1 : xmax);
            ymax = (cy1 > ymax ? cy1 : ymax);
            zmax = (cz1 > zmax ? cz1 : zmax);
            double cx2 = p.cp2.v.x;
            double cy2 = p.cp2.v.y;
            double cz2 = p.cp2.v.z;
            xmin = (cx2 < xmin ? cx2 : xmin);
            ymin = (cy2 < ymin ? cy2 : ymin);
            zmin = (cz2 < zmin ? cz2 : zmin);
            xmax = (cx2 > xmax ? cx2 : xmax);
            ymax = (cy2 > ymax ? cy2 : ymax);
            zmax = (cz2 > zmax ? cz2 : zmax);
        }
        return new Rect(xmin, ymin, zmin, xmax, ymax, zmax);
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
     public void update(JMathAnimScene scene) {
        //This should do nothing, let their points to update by themselves
    }

    public void addJMPoints(JMPath path) {
        this.clear();
        this.addPointsFrom(path.rawCopy());
        this.pathType = path.pathType;
        this.visiblePoints.clear();
        this.visiblePoints.addAll(path.visiblePoints);

    }

    @Override
    public void restoreState() {
        for (JMPathPoint p : jmPathPoints) {
            p.restoreState();
        }
        this.pathType = pathBackup.pathType;
        this.visiblePoints.clear();
        this.visiblePoints.addAll(pathBackup.visiblePoints);

    }

    @Override
    public void saveState() {
        pathBackup = new JMPath();
        for (JMPathPoint p : jmPathPoints) {
            p.saveState();
        }
        pathBackup.pathType = this.pathType;
        pathBackup.visiblePoints.clear();
        pathBackup.visiblePoints.addAll(pathBackup.visiblePoints);
    }

    /**
     * Compute and returns a copy of the path (points referenced), given in
     * canonical form. The canonical form is an array of open, connected paths.
     * If the original path is closed, duplicates first vertex and opens it For
     * each invisible segment, separate the path in two. This method allows
     * better handling for Transform animations
     *
     * @return The array of paths
     */
    public CanonicalJMPath canonicalForm() {
        ArrayList<JMPath> resul = new ArrayList<>();
        JMPath workPath = this.copy();
        Integer offset = null;
        //Find backwards first invisible segment, if there is not, we have a closed path, so open it
        for (int n = 0; n < jmPathPoints.size(); n++) {
            JMPathPoint p = jmPathPoints.get(-n);
            if (!p.isThisSegmentVisible) {
                offset = n;
                break;
            }
        }
        if (offset == null) {
            //Ok, we have a CLOSED path with no invisible segments
            workPath.separate(0);
            offset = -1;
        }

        //A new path always begins with invisible point (that is, invisible segment TO that point) 
        //and ends with the previous to an invisible point
        JMPath connectedComponent = new JMPath();
        connectedComponent.pathType = JMPath.CONNECTED_COMPONENT;
        for (int n = 0; n < workPath.size(); n++) {
            JMPathPoint p = workPath.jmPathPoints.get(n - offset);
            if (!p.isThisSegmentVisible && connectedComponent.size() > 0) {
                resul.add(connectedComponent);
                connectedComponent = new JMPath();
                connectedComponent.pathType = JMPath.CONNECTED_COMPONENT;
            }
            connectedComponent.addJMPoint(p);
        }
        //add last component
        resul.add(connectedComponent);
        return new CanonicalJMPath(resul);
    }

    /**
     * Separate the path in 2 disconnected components at point k Creates a new
     * point a position k+1 Point k and k+1 share the same coordinates, k+1 is
     * not visible.
     *
     * @param k Where to separate path
     */
    public void separate(int k) {
        JMPathPoint p = getJMPoint(k);
        JMPathPoint pnew = p.copy();

        pnew.isThisSegmentVisible = false;
//        pnew.cp2.v.copyFrom(p.cp2.v);
        pnew.type = JMPathPointType.INTERPOLATION_POINT;
//        pnew.cp1.v.copyFrom(p.cp1.v);

        jmPathPoints.add(k + 1, pnew);

    }

    /**
     * Return the number of connected components. A circle has number 0, which
     * means a closed curve. An arc or segment has number 1 (an open curve) A
     * figure with 2 separate curves has number 2, etc.
     *
     * @return The number of connected components.
     */
    public int getNumberOfConnectedComponents() {
//        int resul = 0;
//        for (JMPathPoint p : jmPathPoints) {
//            if (!p.isThisSegmentVisible) {
//                resul++;
//            }
//        }
//        return resul;
        return (int) jmPathPoints.stream().filter((x)->!x.isThisSegmentVisible).count();
    }

    /**
     * Remove all hidden points followed by another hidden point This case may
     * lead to 0-length segments, which can cause errors when transforming, so
     * these (unnecessary) points are removed.
     */
    public void removeConsecutiveHiddenVertices() {
        ArrayList<JMPathPoint> toRemove = new ArrayList<>();
        for (int n = 0; n < jmPathPoints.size(); n++) {
            JMPathPoint p1 = jmPathPoints.get(n);
            JMPathPoint p2 = jmPathPoints.get(n + 1);
            if (!p1.isThisSegmentVisible & !p2.isThisSegmentVisible) {
                toRemove.add(p1);
            }
        }
        jmPathPoints.removeAll(toRemove);
    }

    /**
     * Returns a path with all points visible. This is used mainly for filling
     * it properly
     *
     * @return A raw copy of the path with all points visible
     */
    public JMPath allVisible() {
        JMPath resul = new JMPath();
        for (JMPathPoint p : jmPathPoints) {
            JMPathPoint pNew = p.copy();
            pNew.isThisSegmentVisible = true;
            resul.addJMPoint(pNew);
        }
        return resul;
    }

    public JMPath distille() {
        JMPath resul = rawCopy();
        //Delete points that are separated
        resul.removeConsecutiveHiddenVertices();
        ArrayList<JMPathPoint> toDelete = new ArrayList<>();

        double epsilon = .000001;
        int n = 0;
        while (n < resul.size()) {
            JMPathPoint p1 = resul.getJMPoint(n);
            JMPathPoint p2 = resul.getJMPoint(n + 1);
            if (pointEqual(p1.p, p2.p, epsilon)) {
                p1.cp2.copyFrom(p2.cp2);
//                if (p2.isThisSegmentVisible) {
                p1.isThisSegmentVisible = true;
//                }
                resul.jmPathPoints.remove(p2);
                n = 0;
            } else {
                n++;
            }
        }

        return resul;
    }

    public boolean pointEqual(Point p1, Point p2, double epsilon) {
        boolean resul = false;
        if ((Math.abs(p1.v.x - p2.v.x) < epsilon) & (Math.abs(p1.v.y - p2.v.y) < epsilon)) {
            resul = true;
        }
        return resul;
    }

}
