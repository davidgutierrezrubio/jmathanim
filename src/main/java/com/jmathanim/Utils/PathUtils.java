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
package com.jmathanim.Utils;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Cameras.Camera3D;
import com.jmathanim.Enum.DashStyle;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.JMPathPoint.JMPathPointType;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

import java.util.ArrayList;

/**
 * Class with static methods to perform interpolations on path
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PathUtils {

    public static final double DEFAULT_TENSION = 0.7d;

    /**
     * Overloaded method. Generate control points from a bezier cubic curve, so that control points of point n are
     * parallel to the line from point n-1 and n+1.The distance from point n to the control points is multiplied by the
     * 0 to 1 tension parameter. A 1 tension means straight lines. If first and last point are not connected, an
     * approximation is used based on the control point of their neighbour points. Default tension 0.7 is used
     *
     * @param path Path to compute control points
     */
    public static void generateControlPointsBySimpleSlopes(JMPath path) {
        generateControlPointsBySimpleSlopes(path, DEFAULT_TENSION);
    }

    /**
     * Generate control points from a bezier cubic curve, so that control points of point n are parallel to the line
     * from point n-1 and n+1.The distance from point n to the control points is multiplied by the 0 to 1 tension
     * parameter. A 1 tension means straight lines. If first and last point are not connected, an approximation is used
     * based on the control point of their neighbour points
     *
     * @param path    Path to compute control points
     * @param tension The tension to apply to the curve
     */
    public static void generateControlPointsBySimpleSlopes(JMPath path, double tension) // For now, only one method
    {
        for (JMPathPoint jMPathPoint : path) {
            jMPathPoint.isCurved = true;
        }

        int numPoints = path.jmPathPoints.size();

        for (int n = 0; n < numPoints + 1; n++) {
            int i = n - 1;
            int k = n + 1;
            int L = n + 2;
            JMPathPoint p1 = path.jmPathPoints.get(i);
            JMPathPoint p2 = path.jmPathPoints.get(n);// Compute cp1 for this
            JMPathPoint p3 = path.jmPathPoints.get(k);// Compute cp2 for this
            JMPathPoint p4 = path.jmPathPoints.get(L);

            double x1 = p1.p.v.x;
            double y1 = p1.p.v.y;
            double z1 = p1.p.v.z;
            double x2 = p2.p.v.x;
            double y2 = p2.p.v.y;
            double z2 = p2.p.v.z;
            double x3 = p3.p.v.x;
            double y3 = p3.p.v.y;
            double z3 = p3.p.v.z;
            double x4 = p4.p.v.x;
            double y4 = p4.p.v.y;
            double z4 = p4.p.v.z;
//            if (p3.isCurved) {
//                double mod31 = Math.sqrt((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1));//||p1-p3||
//                double mod42 = Math.sqrt((x4 - x2) * (x4 - x2) + (y4 - y2) * (y4 - y2));//||p2-p4||
//                double mod23 = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));//||p2-p3||
            double mod31 = p1.p.to(p3.p).norm();
            double mod42 = p4.p.to(p2.p).norm();
            double mod23 = p2.p.to(p3.p).norm();
            double cx1 = x2 + mod23 / mod31 * (1 - tension) * (x3 - x1);
            double cy1 = y2 + mod23 / mod31 * (1 - tension) * (y3 - y1);
            double cz1 = z2 + mod23 / mod31 * (1 - tension) * (z3 - z1);
            double cx2 = x3 - mod23 / mod42 * (1 - tension) * (x4 - x2);
            double cy2 = y3 - mod23 / mod42 * (1 - tension) * (y4 - y2);
            double cz2 = z3 - mod23 / mod42 * (1 - tension) * (z4 - z2);
            p2.cpExit.v.x = cx1;
            p2.cpExit.v.y = cy1;
            p2.cpExit.v.z = cz1;
            p3.cpEnter.v.x = cx2;
            p3.cpEnter.v.y = cy2;
            p3.cpEnter.v.z = cz2;
//            } else {
//                // If this path is straight, control points becomes vertices. Although this is
//                // not used
//                // when drawing straight paths, it becomes handy when doing transforms from
//                // STRAIGHT to CURVED paths
//                p2.cpExit.v.copyFrom(p2.p.v);
//                p3.cpEnter.v.copyFrom(p3.p.v);
//            }

        }
        JMPathPoint jp0, jp1;
        Vec v;
        // Compute cp1 and cp2 from first and last points
        jp0 = path.jmPathPoints.get(0);
        if (!jp0.isThisSegmentVisible) {
            jp1 = path.jmPathPoints.get(1);
            v = jp0.p.to(jp1.cpEnter).multInSite(PathUtils.DEFAULT_TENSION);
            jp0.cpExit.v.copyFrom(jp0.p.add(v).v);

            jp1 = path.jmPathPoints.get(numPoints - 2);
            jp0 = path.jmPathPoints.get(numPoints - 1);
//            if (jp0.isCurved) {
            v = jp0.p.to(jp1.cpExit).multInSite(PathUtils.DEFAULT_TENSION);
            jp0.cpEnter.v.copyFrom(jp0.p.add(v).v);
//            }
        }
    }

    public static void addJMPathPointsToScene(JMPath path, JMathAnimScene scene) {
        for (int i = 0; i < path.size(); i++) {
            addJMPathPointToScene(path.get(i), scene);
        }
    }

    private static void addJMPathPointToScene(JMPathPoint p, JMathAnimScene scene) {
        scene.add(p.p.drawColor("green"));//Point of the curve
        scene.add(p.cpEnter.dotStyle(Point.DotSyle.CROSS).drawColor("blue"));//Control point that "enters" into the point
        scene.add(p.cpExit.dotStyle(Point.DotSyle.PLUS).drawColor("red"));//Control point that "exits" from the point
        scene.add(Shape.segment(p.p, p.cpExit)
                .dashStyle(DashStyle.DASHED)
                .drawColor("gray"));
        scene.add(Shape.segment(p.p, p.cpEnter)
                .dashStyle(DashStyle.DASHED)
                .drawColor("gray"));
    }

    public static double pathLength(JMPath path) {
        double resul = 0;
        for (int i = 1; i < path.size(); i++) {
            resul += path.get(i - 1).p.to(path.get(i).p).norm();
        }
        return resul;
    }

    /**
     * Rectify given path, removing curvature flag and settings control points to curve points
     *
     * @param path Path to rectify
     */
    public static void rectifyPath(JMPath path) {
        for (JMPathPoint jmp : path) {
            jmp.isCurved = false;
            jmp.cpEnter.v.copyFrom(jmp.p.v);
            jmp.cpExit.v.copyFrom(jmp.p.v);
        }
    }

    public static Shape convertToStraightSegment(Shape sh, int pivotalSegment) {

        Shape resul = new Shape();
        Shape sh2 = sh.copy();
        sh2.getPath().openPath();
        //First point
        Point p = Point.origin();
        resul.getPath().addPoint(p);
        resul.get(0).isThisSegmentVisible=false;
        for (int i = 1; i < sh2.getPath().size(); i++) {
            double dist = sh2.getPoint(i - 1).to(sh2.getPoint(i)).norm();
            p = p.copy().shift(dist, 0);
            resul.getPath().addPoint(p);
        }
        int shSize = sh2.size()-1;

        while (pivotalSegment < 0) {
            pivotalSegment += shSize;
        }
        while (pivotalSegment >= shSize) {
            pivotalSegment -= shSize;
        }
        Point A = resul.getPoint(pivotalSegment);
        Point B = resul.getPoint(pivotalSegment + 1);
        Point C = sh2.getPoint(pivotalSegment);
        Point D = sh2.getPoint(pivotalSegment + 1);
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(A, B, C, D, 1);
        resul.applyAffineTransform(tr);

        return resul;
    }

    public JMPathPoint getInterpolatedPoint(JMPathPoint jmp1, JMPathPoint jmp2, double alpha) {
        JMPathPoint interpolate;
        if (jmp2.isCurved) {
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

        } else {
            Point K = jmp1.p.interpolate(jmp2.p, alpha);
            interpolate = new JMPathPoint(K, jmp2.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
        }
        interpolate.isCurved = jmp2.isCurved;
        return interpolate;
    }

    public void determineStraightSegments(JMPath path) {
        CircularArrayList<JMPathPoint> jmPathPoints = path.jmPathPoints;
        for (int n = 0; n < path.size(); n++) {
            JMPathPoint p1 = jmPathPoints.get(n);
            JMPathPoint p2 = jmPathPoints.get(n + 1);
            p2.isCurved = !((p1.p.to(p1.cpExit).norm() < .0001) && (p2.p.to(p2.cpEnter).norm() < .0001));
        }
    }

    public Shape rectify(Camera cam, Shape shape) {
        ArrayList<ArrayList<Point>> arPoints = computePolygonalPieces(cam, shape.getPath());
        int size = 0;
        ArrayList<Point> flattened = new ArrayList<>();
        for (int i = 0; i < arPoints.size(); i++) {
            ArrayList<Point> subArray = arPoints.get(i);
            for (int j = 0; j < subArray.size() - 1; j++) {//TODO: -1 if closed path!!
                Point point = subArray.get(j);
                flattened.add(point);
            }

        }
        Point[] pointsArray = flattened.toArray(new Point[0]);


        Shape resul = Shape.polygon(pointsArray);//TODO: Add invisible points here
        return resul;
    }

    public ArrayList<ArrayList<Point>> computePolygonalPieces(Camera cam, JMPath path) {
        CircularArrayList<JMPathPoint> jmPathPoints = path.jmPathPoints;
        ArrayList<ArrayList<Point>> resul = new ArrayList<>();
        ArrayList<Point> connectedSegments = new ArrayList<>();
        for (int n = 0; n < path.size(); n++) {
            JMPathPoint p = jmPathPoints.get(n);
            JMPathPoint q = jmPathPoints.get(n + 1);

            if (q.isThisSegmentVisible) {
                computeStraightenedPoints(connectedSegments, p, q, cam);
//                connectedSegments.addAll(seg);
            } else {
                resul.add(connectedSegments);
                connectedSegments = new ArrayList<>();
            }
        }
        if (!connectedSegments.isEmpty()) {
            resul.add(connectedSegments);
        }
        return resul;
    }

    private void computeStraightenedPoints(ArrayList<Point> connectedSegments, JMPathPoint p, JMPathPoint q, Camera cam) {
        if (connectedSegments.isEmpty()) {
            connectedSegments.add(p.p);
        }
        if (q.isCurved) {
            int num = appropiateSubdivisionNumber(p.p.v, q.p.v, cam);
            for (int n = 1; n < num; n++) {
                connectedSegments.add(p.interpolate(q, n * 1d / num).p.drawColor("blue"));
            }

        }
        connectedSegments.add(q.p);
    }

    private int appropiateSubdivisionNumber(Vec v1, Vec v2, Camera cam) {
        double mathviewHeight;
        if (cam instanceof Camera3D) {
            Camera3D cam3D = (Camera3D) cam;

            double zDepth = v1.interpolate(v2, .5).minus(cam3D.eye.v).norm();
            mathviewHeight = cam3D.getMathViewHeight3D(zDepth);
        } else {
            mathviewHeight = cam.getMathView().getHeight();
        }

        //An estimation of subdivision number, depending on the covered area
        double d = (Math.abs(v1.x - v2.x) + Math.abs(v1.y - v2.y) + Math.abs(v1.z - v2.z)) / mathviewHeight;
        if (d >= 1) {
            return 30;
        }
        if (d > .5) {
            return 20;
        }
        if (d > .2) {
            return 15;
        }
        if (d > .1) {
            return 10;
        }
        if (d > .05) {
            return 7;
        }
        return 5;
    }


}
