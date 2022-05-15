/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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

import com.jmathanim.Styling.JMColor;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.CircularArrayList;
import com.jmathanim.Utils.EmptyRect;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.PathUtils;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.JMPathPoint.JMPathPointType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class stores info for drawing a curve with control points, tension...
 * It's independent of the renderer, which should translate it to proper drawing
 * commands
 *
 * @author David Gutiérrez davidgutierrezrubio@gmail.com
 */
public class JMPath implements Stateable, Boxable, Iterable<JMPathPoint> {

    static public final int MATHOBJECT = 1; // Arc, line, segment...
    static public final int SVG_PATH = 2; // SVG import, LaTeX object...
    static public final int CONNECTED_COMPONENT = 3; // Connected, open component. Every path should be able to put in
    // this way
    public final CircularArrayList<JMPathPoint> jmPathPoints; // points from the curve
    public final CircularArrayList<Boolean> visiblePoints;// Whether this point is visible or not
    public int pathType; // Default value

    private JMPath pathBackup;
    private final ArrayList<Point> rectifiedPoints;
    private final ArrayList<Double> rectifiedPointDistances;
    private double computedPathLength;

    /**
     * Creates a new empty JMPath objectF
     */
    public JMPath() {
        this(new ArrayList<JMPathPoint>());
    }

    /**
     * Static constructor. Generates a new JMPath object with the given
     * JMpathPoints
     *
     * @param jmps Varargs with jmpathpoints to create the path
     * @return The created JMPath object
     */
    public static JMPath make(JMPathPoint... jmps) {
        JMPath resul = new JMPath();
        resul.addJMPoint(jmps);
        return resul;
    }

    /**
     * Creates a new JMPath with specified points. All segments will be marked
     * visible and not curved
     *
     * @param points An ArrayList of points to add
     */
    public JMPath(ArrayList<JMPathPoint> points) {
        this.jmPathPoints = new CircularArrayList<>();
        for (JMPathPoint p : points) {
            this.jmPathPoints.add(p);
        }
        this.visiblePoints = new CircularArrayList<>();
        pathType = JMPath.MATHOBJECT;// Default value
        rectifiedPoints = new ArrayList<>();
        rectifiedPointDistances = new ArrayList<>();
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

    public void clear() {
        jmPathPoints.clear();
    }

    private void computeRectifiedPoints() {
        PathUtils pu = new PathUtils();
        rectifiedPoints.clear();
        rectifiedPointDistances.clear();
        computedPathLength = 0;
        ArrayList<ArrayList<Point>> arArRectPoints = pu.computePolygonalPieces(JMathAnimConfig.getConfig().getCamera(), this);
        for (ArrayList<Point> arArRectPoint : arArRectPoints) {
            if (!arArRectPoint.isEmpty()) {
                Point previous = arArRectPoint.get(0);
                for (Point rectPoint : arArRectPoint) {
                    rectifiedPoints.add(rectPoint);
                    double distance = rectPoint.to(previous).norm();
                    rectifiedPointDistances.add(distance);
                    computedPathLength += distance;
                    previous = rectPoint;
                }
            }
        }
    }

    /**
     * Return a point at a given percentage of total arclenth of path. Note
     * that, as path are composed of cubic Bezier curves, a rectified path must
     * be computed. This is done automatically only once at first call of this
     * method, so if you change the path after a a first call, the next calls to
     * this method may give wrong results.
     *
     * @param t A value from 0 to 1. 0 means starting point and 1 ending point
     * @return A new Point object with the computed location
     */
    public Point getParametrizedPointAt(double t) {
        if (rectifiedPoints.isEmpty()) {
            computeRectifiedPoints();
        }
        if (t == 0) {
            return rectifiedPoints.get(0).copy();
        }
        if (t == 1) {
            return rectifiedPoints.get(rectifiedPoints.size() - 1).copy();
        }
        double td = t * computedPathLength;
        int n = 0;
        double sum = 0;
        while (sum < td) {
            sum += rectifiedPointDistances.get(n);
            n++;
        }
        n--;
        sum -= rectifiedPointDistances.get(n);
        double tLocal = (td - sum) / rectifiedPointDistances.get(n);
        return rectifiedPoints.get(n - 1).interpolate(rectifiedPoints.get(n), tLocal);
    }

//    /**
//     * Remove interpolation points from path and mark it as no interpolated
//     */
//    public void removeInterpolationPoints() {
//        ArrayList<JMPathPoint> toRemove = new ArrayList<>();
//        for (JMPathPoint p : jmPathPoints) {
//            if (p.type == JMPathPointType.INTERPOLATION_POINT) {
//                toRemove.add(p);
//            }
//        }
//        jmPathPoints.removeAll(toRemove);// Remove all interpolation points
//        // Now, restore old control points
//        // for curved paths control points are modified so that a backup is necessary
//        for (JMPathPoint p : jmPathPoints) {
//            if (p.cpExitvBackup != null) {
//                p.cpExit.v.copyFrom(p.cpExitvBackup);
//                p.cpExitvBackup = null;
//            }
//            if (p.cpEntervBackup != null) {
//                p.cpEnter.v.copyFrom(p.cpEntervBackup);
//                p.cpEntervBackup = null;
//            }
//        }
//
////        generateControlPoints();//Recompute control points
//    }
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
    public void addJMPointsFrom(JMPath jmpathTemp) {
        jmPathPoints.addAll(jmpathTemp.jmPathPoints);
    }

    /**
     * Proceeds to subdidivide paths to ensure the path has exactly the given
     * number of elements Invisible pieces of path are not interpolated. New
     * generated points are marked as INTERPOLATION_POINT
     *
     * @param newNumPoints New number of points. Must be greater or equal than
     * actual number of points in the path
     */
    public void alignPathsToGivenNumberOfElements(int newNumPoints) {
        if (newNumPoints <= this.size()) {
            return; // Nothing to do here!
        }
        // First compute how many visible segments are
        ArrayList<JMPathPoint> pointsToInterpolate = new ArrayList<>();
        // Loop is from 1 because I want to add extra point to the first segment (point
        // 1) and not the last (point 0)
        for (int n = 1; n < 1 + jmPathPoints.size(); n++) {
            JMPathPoint p = jmPathPoints.get(n);
            if (p.isThisSegmentVisible) {
                pointsToInterpolate.add(p);
            }
        }
        int numVisibleSegments = pointsToInterpolate.size();
        int numPoints = jmPathPoints.size();
        int toCreate = newNumPoints - numPoints;// Number of points to create, to put in the numVisibleSegments segments

        int numDivs = (toCreate / numVisibleSegments); // Euclidean quotient
        int rest = toCreate % numVisibleSegments;// Euclidean rest

        for (int n = 0; n < pointsToInterpolate.size(); n++) {
            JMPathPoint p = pointsToInterpolate.get(n);
            p.numDivisions = numDivs + 1;// it is number of divisions, not number of points to be created. 1 new point
            // means divide in 2
            p.numDivisions += (n < rest ? 1 : 0);
        }
        // Once I have the number of segments to interpolate, subdivide all visible
        // segments

        for (JMPathPoint p : pointsToInterpolate) {
            int k = jmPathPoints.indexOf(p);// Position of this point in the path
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
        dividePathSegment(k + 1, numDivForThisVertex - 1);// Keep subdividing until numDivForThisVertex=1
    }

//    public Point getPointAt(double t){
//        Point pointAt;
//        //First, get the segment
//         int numVisibleSegments=(int)(jmPathPoints.stream().filter((x)->x.isThisSegmentVisible).count()-1);
//        double tTotal=numVisibleSegments*t;
//        int n1=(int) Math.floor(tTotal);
//        double alpha=tTotal-n1;
//        JMPathPoint jmp1 = getJMPoint(n1);
//        JMPathPoint jmp2 = getJMPoint(n1+1);
//        if (jmp2.isCurved) {
//            //De Casteljau's Algorithm: https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
//            Point E = jmp1.p.interpolate(jmp1.cp1, alpha); //New cp1 of v1
//            Point G = jmp2.cp2.interpolate(jmp2.p, alpha); //New cp2 of v2
//            Point F = jmp1.cp1.interpolate(jmp2.cp2, alpha);
//            Point H = E.interpolate(F, alpha);//cp2 of interpolation point
//            Point J = F.interpolate(G, alpha);//cp1 of interpolation point
//            pointAt = H.interpolate(J, alpha); //Interpolation point
//        } else {
//            //Straight interpolation
//            pointAt = new Point(jmp1.p.v.interpolate(jmp2.p.v, alpha));
//        }
//        
//        return pointAt;
//    }
//    
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
        JMPathPoint jmp1 = jmPathPoints.get(k - 1);
        JMPathPoint jmp2 = jmPathPoints.get(k);
        JMPathPoint interpolate;
        if (jmp2.isCurved) {//TODO: Replace this with interpolation method from JMPathPoint
            // De Casteljau's Algorithm:
            // https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
            Point E = jmp1.p.interpolate(jmp1.cpExit, alpha); // New cp1 of v1
            Point G = jmp2.cpEnter.interpolate(jmp2.p, alpha); // New cp2 of v2
            Point F = jmp1.cpExit.interpolate(jmp2.cpEnter, alpha);
            Point H = E.interpolate(F, alpha);// cp2 of interpolation point
            Point J = F.interpolate(G, alpha);// cp1 of interpolation point
            Point K = H.interpolate(J, alpha); // Interpolation point
            interpolate = new JMPathPoint(K, jmp2.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
            interpolate.cpExit.v.copyFrom(J.v);
            interpolate.cpEnter.v.copyFrom(H.v);
            // Change control points from v1 and v2,save
            // backup values to restore after removing interpolation points
            if (jmp1.cpExitvBackup == null) {
                jmp1.cpExitvBackup = jmp1.cpExit.v;
            }
            if (jmp2.cpEntervBackup == null) {
                jmp2.cpEntervBackup = jmp2.cpEnter.v;
            }

            jmp1.cpExit.v.copyFrom(E.v);
            jmp2.cpEnter.v.copyFrom(G.v);

        } else {
            // Straight interpolation
            Point interP = new Point(jmp1.p.v.interpolate(jmp2.p.v, alpha));
            // Interpolation point is visible iff v2 is visible
            // Control points are by default the same as v1 and v2 (straight line)
            interpolate = new JMPathPoint(interP, jmp2.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
        }
        interpolate.isCurved = jmp2.isCurved; // The new point is curved iff v2 is
        jmPathPoints.add(k, interpolate); // Now v2 is in position k+1!
        return interpolate;
    }

    /**
     * Get point (interpolated if necessary) that lies at position alpha where
     * alpha=0 denotes beginning of path and alpha=1 denotes the end. Path is
     * unaltered
     *
     * @param alpha from 0 to 1, relative position inside the path
     * @return A new JMPathPoint that describes the curve at relative position
     * alpha.
     */
    public JMPathPoint getJMPointAt(double alpha) {
        while (alpha > 1) {
            alpha -= 1;
        }
        while (alpha < 0) {
            alpha += 1;
        }
        JMPathPoint resul;
        final double size = (jmPathPoints.get(0).isThisSegmentVisible ? alpha * size() : alpha * (size() - 1));
        int k = (int) Math.floor(size);
        double t = size - k;
        JMPathPoint v1 = jmPathPoints.get(k);
        JMPathPoint v2 = jmPathPoints.get(k + 1);
        resul = getJMPointBetween(v1, v2, t);
        return resul;
    }

    public static JMPathPoint getJMPointBetween(JMPathPoint v1, JMPathPoint v2, double t) {
        JMPathPoint resul;
        if (v2.isCurved) {//TODO: This is buggy. Sometimes the jmpathpoint is curved but marked as not curved!
//		if (v1.isCurved) {
            // De Casteljau's Algorithm:
            // https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
            Point E = v1.p.interpolate(v1.cpExit, t); // New cpEnter of v1
            Point G = v2.cpEnter.interpolate(v2.p, t); // New cpExit of v2
            Point F = v1.cpExit.interpolate(v2.cpEnter, t);
            Point H = E.interpolate(F, t);// cpEnter of interpolation point
            Point J = F.interpolate(G, t);// cpExit of interpolation point
//            resul = H.interpolate(J, t); //Interpolation point
            resul = JMPathPoint.curveTo(H.interpolate(J, t));

            resul.cpExit.copyFrom(J);
            resul.cpEnter.copyFrom(H);
        } else {
            resul = JMPathPoint.lineTo(v1.p.interpolate(v2.p, t));
            resul.cpExit.copyFrom(v1.p);
            resul.cpEnter.copyFrom(v2.p);
        }
        return resul;
    }

    /**
     * Returns a full copy of the path. JMPathPoint objects are also copied
     *
     * @return A copy of the path
     */
    public JMPath copy() {
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
     * @return A referencedCopy of the path
     */
    public JMPath referencedCopy() {
        JMPath resul = new JMPath();
        resul.jmPathPoints.addAll(jmPathPoints);

        // Copy attributes
        resul.pathType = pathType;
        return resul;

    }

    /**
     * Cycle points in path.Point(0) becomes Point(step), Point(1) becomes
     * Point(step+1)... Useful to align paths minimizing distances
     *
     * @param step Initial gap to apply
     * @param reverse If true, reverse the path
     */
    public void cyclePoints(int step, boolean reverse) {
        distille();
        JMPath tempPath = this.referencedCopy();
        jmPathPoints.clear();
        int direction = (reverse ? -1 : 1);
        final int size = tempPath.size();

        boolean[] curveds = new boolean[size];
        boolean[] visibles = new boolean[size];
        for (int n = 0; n < size; n++) {
            curveds[n] = tempPath.jmPathPoints.get(n).isCurved;
            visibles[n] = tempPath.jmPathPoints.get(n).isThisSegmentVisible;
        }

        for (int n = 0; n < size; n++) {
            final int k = direction * n + step;
            JMPathPoint point = tempPath.jmPathPoints.get(k);
            if (reverse) // If the path is reversed, we must swap control points
            {
                double cpTempX = point.cpExit.v.x;
                double cpTempY = point.cpExit.v.y;
                double cpTempZ = point.cpExit.v.z;
                point.cpExit.v.copyFrom(point.cpEnter.v);
                point.cpEnter.v.x = cpTempX;
                point.cpEnter.v.y = cpTempY;
                point.cpEnter.v.z = cpTempZ;
                point.isCurved = curveds[(k + 1 + size) % size];
                point.isThisSegmentVisible = visibles[(k + 1 + size) % size];
            }
            jmPathPoints.add(point);
        }

    }

    /**
     * Reverse the points of the path
     */
    public void reverse() {
        this.cyclePoints(-1, true);
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
//        double sumSq = 0;
        double sum = 0;
        for (int n = 0; n < this.size(); n++) {
            Vec v1 = jmPathPoints.get(n).p.v;
            Vec v2 = path2.jmPathPoints.get(n).p.v;
            double dist = v1.minus(v2).norm();
//            sumSq += dist;
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

    /**
     * Returns the width of the path
     *
     * @return The width. If the path is empty, returns 0.
     */
    public double getWidth() {
        Rect r = getBoundingBox();
        if (r == null) {
            return 0;
        } else {
            return r.getWidth();
        }
    }

    /**
     * Returns the height of the path
     *
     * @return The height. If the path is empty, returns 0.
     */
    public double getHeight() {
        Rect r = getBoundingBox();
        if (r == null) {
            return 0;
        } else {
            return r.getHeight();
        }
    }

    /**
     * Returns an array with the critical points of the Shape, that is, points
     * where its derivative with respect to x (or y) is 0. These points are used
     * to properly compute the bounding box.
     *
     * @return An ArrayList with all critical points. The bounding box of these
     * point is the bounding box of the Shape.
     */
    public ArrayList<Point> getCriticalPoints() {
        ArrayList<Point> criticalPoints = new ArrayList<>();
        for (int n = 0; n < jmPathPoints.size(); n++) {
            JMPathPoint jmp = jmPathPoints.get(n);
            if (jmp.isThisSegmentVisible) {
                if (jmp.isCurved) {
                    criticalPoints.addAll(getCriticalPoints(jmPathPoints.get(n - 1), jmp));
                }
                criticalPoints.add(jmp.p);

            }
        }
        return criticalPoints;
    }

    @Override
    public Rect getBoundingBox() {
        if (jmPathPoints.isEmpty()) {
            return new EmptyRect();
        }
        ArrayList<Point> points = new ArrayList<>();
        for (JMPathPoint jmp : jmPathPoints) {
            points.add(jmp.p.copy().thickness(2));
        }
//        List<Point> points = jmPathPoints.stream().map(p -> p.p).collect(Collectors.toList());
        points.addAll(getCriticalPoints());
//        for (Point p: getCriticalPoints()) {
//            System.out.println(""+p);
//        }
//        System.out.println("Rect "+Rect.make(points));

        return Rect.make(points);
    }

    @Override
    public boolean isEmpty() {
        return jmPathPoints.isEmpty();
    }

    /**
     * Determine orientation of the path
     *
     * @return 1 if clockwise, -1 if counterwise
     */
    public int getOrientation() {
        // https://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in-clockwise-order/1180256#1180256

        // get the point with lowest y and, in case of tie, max x
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

    /**
     * Replaces the JMPathPoints of the path with copies of points from another
     * path. path.
     *
     * @param path The path with the JMPathPoints to add.
     */
    public void setJMPoints(JMPath path) {
        this.clear();
        this.addJMPointsFrom(path.copy());
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
        if (this.size() == 0) {
            return new CanonicalJMPath();
        }
        ArrayList<JMPath> resul = new ArrayList<>();
        JMPath workPath = this.referencedCopy();
        Integer offset = null;
        // Find backwards first invisible segment, if there is not, we have a closed
        // path, so open it
        for (int n = 0; n < jmPathPoints.size(); n++) {
            JMPathPoint p = jmPathPoints.get(-n);
            if (!p.isThisSegmentVisible) {
                offset = n;
                break;
            }
        }
        if (offset == null) {
            // Ok, we have a CLOSED path with no invisible segments
            workPath.separate(0);
            offset = -1;
        }

        // A new path always begins with invisible point (that is, invisible segment TO
        // that point)
        // and ends with the previous to an invisible point
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
        // add last component
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
        JMPathPoint p = jmPathPoints.get(k);
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
        return (int) jmPathPoints.stream().filter((x) -> !x.isThisSegmentVisible).count();
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
     * @return A raw referencedCopy of the path with all points visible
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

    /**
     * Removes unnecessary points from the path. Duplicated points or
     * consecutive hidden ones.
     */
    public void distille() {
        // Delete points that are separated
        this.removeConsecutiveHiddenVertices();
        double epsilon = .000001;
        int n = 0;
        while (n < this.size() - 1) {
            JMPathPoint p1 = this.jmPathPoints.get(n);
            JMPathPoint p2 = this.jmPathPoints.get(n + 1);
            if (p1.p.isEquivalentTo(p2.p, epsilon)) {
                p2.cpEnter.copyFrom(p1.cpEnter);
                p2.isThisSegmentVisible = p1.isThisSegmentVisible;
                p2.isCurved = p1.isCurved;
                this.jmPathPoints.remove(p1);
                n = 0;
            } else {
                n++;
            }
        }
        for (int i = 0; i < jmPathPoints.size(); i++) {
            JMPathPoint p = jmPathPoints.get(i);
            JMPathPoint q = jmPathPoints.get(i - 1);
            if (!p.isCurved) {
                p.cpEnter.copyFrom(p.p);
                q.cpExit.copyFrom(q.p);
            }
        }

    }

    /**
     * Performs a comparison point-to-point with another path. This method is
     * used to determine if another path is the affine transformation of
     * another, for example.
     *
     * @param obj The other path to compare.
     * @param epsilon A threshold value to compare.
     * @return True if all distances are smaller than the threshold value. False
     * otherwise
     */
    public boolean isEquivalentTo(JMPath obj, double epsilon) {
        if (size() != obj.size()) {
            return false;
        }
        for (int n = 0; n < size(); n++) {
            JMPathPoint pa1 = jmPathPoints.get(n);
            JMPathPoint pa2 = obj.jmPathPoints.get(n);
            if (!pa1.isEquivalentTo(pa2, epsilon)) {
                return false;
            }
        }
        return true;
    }

    private Vec evaluateBezier(Vec P0, Vec P1, Vec P2, Vec P3, double t) {
        Vec a = P3.add(P2.mult(-3)).add(P1.mult(3)).add(P0.mult(-1));
        Vec b = P2.mult(3).add(P1.mult(-6)).add(P0.mult(3));
        Vec c = P1.mult(3).add(P0.mult(-3));
        Vec d = P0.copy();
        return d.add(c.mult(t)).add(b.mult(t * t)).add(a.mult(t * t * t));
    }

    private ArrayList<Point> getCriticalPoints(JMPathPoint pOrig, JMPathPoint pDst) {
        // https://floris.briolas.nl/floris/2009/10/bounding-box-of-cubic-bezier/
        ArrayList<Point> resul = new ArrayList<>();
        Vec P0 = pOrig.p.v;
        Vec P1 = pOrig.cpExit.v;
        Vec P2 = pDst.cpEnter.v;
        Vec P3 = pDst.p.v;
        Vec v;
        // a,b,c are the coefficients of the derivative of the Bezier function
        // f'(t)=at^2+bt+c
        Vec a = P3.add(P2.mult(-3)).add(P1.mult(3)).add(P0.mult(-1)).mult(3);
        Vec b = P2.mult(3).add(P1.mult(-6)).add(P0.mult(3)).mult(2);
        Vec c = P1.mult(3).add(P0.mult(-3));
        // We compute the roots for this derivative
        // First, for x
        double[] solsX = quadraticSolutions(a.x, b.x, c.x);
        for (double tCrit : solsX) {
            if ((tCrit > 0) && (tCrit < 1)) {
//                v = getJMPointBetween(pOrig, pDst, tCrit).p.v;
                if (pDst.isCurved) {
                    v = evaluateBezier(P0, P1, P2, P3, tCrit);
                } else {
                    v = pOrig.p.interpolate(pDst.p, tCrit).v;
                }

                resul.add(Point.at(v.x, v.y).drawColor(JMColor.BLUE));
            }
        }
        // Now for y
        double[] solsY = quadraticSolutions(a.y, b.y, c.y);
        for (double tCrit : solsY) {
            if ((tCrit > 0) && (tCrit < 1)) {
//                Vec v = getJMPointBetween(pOrig, pDst, tCrit).p.v;
                if (pDst.isCurved) {
                    v = evaluateBezier(P0, P1, P2, P3, tCrit);
                } else {
                    v = pOrig.p.interpolate(pDst.p, tCrit).v;
                }
                resul.add(Point.at(v.x, v.y).drawColor(JMColor.RED));
            }
        }
        return resul;
    }

    /**
     * Computes the roots of a quadratic (maybe linear) equation
     *
     * @param a Coefficient of term with degree 2
     * @param b Coefficient of term with degree 1
     * @param c Coefficient of term with degree 0
     * @return An array of results (empty if no solutions found).
     */
    private double[] quadraticSolutions(double a, double b, double c) {
        // If a=0 we are dealing with a lineal
        if (a == 0) {
            if (c != 0) {
                return new double[]{-c / b};
            } else {
                return new double[]{};// No solutions
            }
        }
        // We deal with the case a!=0 so we have a quadratic equation
        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            return new double[]{};// No real solutions
        }
        if (discriminant == 0) {// One solution
            return new double[]{-.5 * b / a};
        }
        if (discriminant > 0) {// two solutions
            double rdisc = Math.sqrt(discriminant);
            return new double[]{-.5 * (b + rdisc) / a, -.5 * (b - rdisc) / a};
        }

        return new double[]{};
    }

    /**
     * Gets the points of the shape that lies in the boundary of the bounding
     * box
     *
     * @param type What side of the bounding box: UPPER, LOWER, RIGHT or LEFT.
     * The other types return null.
     * @return A List with all the points that lies in the specified side of the
     * boundary box
     */
    public List<Point> getBorderPoints(Anchor.Type type) {
        Stream<Point> stream = getCriticalPoints().stream();
        Rect bb = getBoundingBox();
        List<Point> li = null;
        switch (type) {
            case UPPER:
                li = stream.filter(p -> p.v.y == bb.ymax).collect(Collectors.toList());
                break;
            case LOWER:
                li = stream.filter(p -> p.v.y == bb.ymin).collect(Collectors.toList());
                break;
            case RIGHT:
                li = stream.filter(p -> p.v.x == bb.xmax).collect(Collectors.toList());
                break;
            case LEFT:
                li = stream.filter(p -> p.v.x == bb.xmin).collect(Collectors.toList());
                break;
        }

        return li;
    }

    /**
     * Get the nth-element of the path
     *
     * @param index Index of element of path to be retrieved
     * @return A JMPathPoint with the nth-element of the path
     */
    public JMPathPoint get(int index) {
        return jmPathPoints.get(index);
    }

    @Override
    public Iterator<JMPathPoint> iterator() {
        return jmPathPoints.iterator();
    }

    /**
     * Returns a subpath of the given path. If beginning is greater than ending,
     * the path is reversed
     *
     * @param a Beginning parameter, from 0 to 1
     * @param b Ending parameter, from 0 to 1.
     * @return The subpath
     */
    public JMPath getSubPath(double a, double b) {
        if (a > b) {
            JMPath tempPath = this.copy();
            tempPath.reverse();
            return tempPath.getSubPath(b, a);
        }
        JMPath tempPath = this.copy();

        tempPath.openPath();

        //Stranges and buggy cases
        if ((a == 1) && (b == 1)) {
            JMPath res = JMPath.make();
            res.addPoint(this.get(0).p.copy(), this.get(0).p.copy());
            return res;
        }

        int size = tempPath.size();

        JMPathPoint beginning = tempPath.get(0);
        JMPathPoint ending = tempPath.get(-1);

        int k1 = (int) Math.floor(a * (size - 1));
        double alpha1 = a * (size - 1) - k1;

        int k2 = (int) Math.floor(b * (size - 1));
        double alpha2 = b * (size - 1) - k2;

        if (a > 0) {
            beginning = tempPath.insertJMPointAt(k1, alpha1);
            beginning.isThisSegmentVisible = false;
            if (k1 == k2) {
                alpha2 = (alpha2 - alpha1) / (1 - alpha1);
            }

            k2 = k2 + 1;
        }

        if (b < 1) {
            ending = tempPath.insertJMPointAt(k2, alpha2);
        }

        int nBegin = tempPath.jmPathPoints.indexOf(beginning);
        int nEnd = tempPath.jmPathPoints.indexOf(ending);
        JMPath subPath = new JMPath();
        subPath.jmPathPoints.addAll(tempPath.jmPathPoints.subList(nBegin, nEnd + 1));
        return subPath;
    }

    /**
     * Opens the path. This process adds a new JMPathPoint (copy of the first)
     * at the end. If the path is already opened (at index 0) this method has no
     * effect.
     *
     */
    public void openPath() {
        //Open the path if it is closed
        if (isEmpty()) {
            return;//Nothing to open
        }
        final JMPathPoint firstP = jmPathPoints.get(0);
        if (firstP.isThisSegmentVisible) {
            addJMPoint(firstP.copy());
            firstP.isThisSegmentVisible = false;
        }
    }

    /**
     * Closes the path and removes redundant points if necessary
     */
    public void closePath() {
        if (isEmpty()) {
            return;//Nothing to close
        }
        final JMPathPoint jmp = jmPathPoints.get(0);
        jmp.isThisSegmentVisible = true;
        jmp.cpEnter.v.copyFrom(jmp.p.v);
        jmPathPoints.get(-1).cpExit.v.copyFrom(jmPathPoints.get(-1).p.v);
        distille();
    }

    /**
     * Inserts a new JMPathPoint right after a given one, interpolating
     * properly.
     *
     * @param k The index of the JMPathPoint to insert. New point will be at
     * location k+1
     * @param alpha Alpha parameter, between 0 to 1 to interpolate between point
     * k and point k+1
     * @return The new point created
     */
    public JMPathPoint insertJMPointAt(int k, double alpha) {
        JMPathPoint v1 = jmPathPoints.get(k);
        JMPathPoint v2 = jmPathPoints.get(k + 1);
        JMPathPoint newPoint = getJMPointBetween(v1, v2, alpha);
        jmPathPoints.add(k + 1, newPoint);
        newPoint.isThisSegmentVisible = v2.isThisSegmentVisible;
        //Adjust the control points of v1 and v2
        Point E = v1.p.interpolate(v1.cpExit, alpha); // New cpExit of v1
        Point G = v2.cpEnter.interpolate(v2.p, alpha); // New cpEnter of v2
        v1.cpExit.copyFrom(E);
        v2.cpEnter.copyFrom(G);
        return newPoint;
    }

    /**
     * Merge this path A with another one B
     *
     * @param secondPath The second path to merge
     * @param connectAtoB If true, the end of A will be connected to the
     * beginning of B by a straight line
     * @param connectBtoA If true, the end of B will be connected to the
     * beginning of A by a straight line
     * @return This object
     */
    public JMPath merge(JMPath secondPath, boolean connectAtoB, boolean connectBtoA) {
        JMPath pa = secondPath.copy();
        //Special case: if this path is empty
        if (isEmpty()) {
            jmPathPoints.addAll(pa.jmPathPoints);
            return this;
        }

        // If the first path is already a closed one, open it
        // with 2 identical points (old-fashioned style of closing shapes)
        final JMPathPoint jmPoint = jmPathPoints.get(0);
        if (jmPoint.isThisSegmentVisible) {
            jmPathPoints.add(jmPoint.copy());
            jmPoint.isThisSegmentVisible = false;
        }

        // Do the same with the second path
        final JMPathPoint jmPoint2 = pa.jmPathPoints.get(0);
        if (jmPoint2.isThisSegmentVisible) {
            pa.jmPathPoints.add(jmPoint2.copy());
        }

        //If connectAtoB, make last
        jmPoint2.isThisSegmentVisible = connectAtoB;
        if (connectAtoB) {
            jmPoint2.isCurved = false;//Connect by a straight line
        }
        get(0).isThisSegmentVisible = connectBtoA;
        if (connectBtoA) {
            get(0).isCurved = false;//Connect by a straight line
        }

        // Now you can add the points
        jmPathPoints.addAll(pa.jmPathPoints);
        return this;
    }

    public void copyStateFrom(JMPath path) {
        long count = path.jmPathPoints.stream().filter(t -> t.type == JMPathPointType.VERTEX).count();
        if (count == jmPathPoints.size()) {
            //Equal number of vertices 
            for (int i = 0; i < size(); i++) {
                get(i).copyStateFrom(path.get(i));
            }
        } else { //If there are too many discrepances, better clear it and copy
            jmPathPoints.clear();
            for (JMPathPoint jp : path) {
                jmPathPoints.add(jp.copy());
            }
        }
    }

}
