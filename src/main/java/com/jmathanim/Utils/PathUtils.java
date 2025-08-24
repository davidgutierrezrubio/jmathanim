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
import com.jmathanim.Enum.DotStyle;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
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

            double x1 = p1.v.x;
            double y1 = p1.v.y;
            double z1 = p1.v.z;
            double x2 = p2.v.x;
            double y2 = p2.v.y;
            double z2 = p2.v.z;
            double x3 = p3.v.x;
            double y3 = p3.v.y;
            double z3 = p3.v.z;
            double x4 = p4.v.x;
            double y4 = p4.v.y;
            double z4 = p4.v.z;
//            if (p3.isCurved) {
//                double mod31 = Math.sqrt((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1));//||p1-p3||
//                double mod42 = Math.sqrt((x4 - x2) * (x4 - x2) + (y4 - y2) * (y4 - y2));//||p2-p4||
//                double mod23 = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));//||p2-p3||
            double mod31 = p1.v.minus(p3.v).norm();
            double mod42 = p4.v.minus(p2.v).norm();
            double mod23 = p2.v.minus(p3.v).norm();
            double cx1 = x2 + mod23 / mod31 * (1 - tension) * (x3 - x1);
            double cy1 = y2 + mod23 / mod31 * (1 - tension) * (y3 - y1);
            double cz1 = z2 + mod23 / mod31 * (1 - tension) * (z3 - z1);
            double cx2 = x3 - mod23 / mod42 * (1 - tension) * (x4 - x2);
            double cy2 = y3 - mod23 / mod42 * (1 - tension) * (y4 - y2);
            double cz2 = z3 - mod23 / mod42 * (1 - tension) * (z4 - z2);
            p2.vExit.x = cx1;
            p2.vExit.y = cy1;
            p2.vExit.z = cz1;
            p3.vEnter.x = cx2;
            p3.vEnter.y = cy2;
            p3.vEnter.z = cz2;
//            } else {
//                // If this path is straight, control points becomes vertices. Although this is
//                // not used
//                // when drawing straight paths, it becomes handy when doing transforms from
//                // STRAIGHT to CURVED paths
//                p2.cpExit.copyCoordinatesFrom(p2.p.v);
//                p3.cpEnter.copyFrom(p3.p.v);
//            }

        }
        JMPathPoint jp0, jp1;
        Vec v;
        // Compute cp1 and cp2 from first and last points
        jp0 = path.jmPathPoints.get(0);
        if (!jp0.isThisSegmentVisible) {
            jp1 = path.jmPathPoints.get(1);
            v = jp1.vEnter.minus(jp0.v).multInSite(PathUtils.DEFAULT_TENSION);
            jp0.vExit.copyCoordinatesFrom(jp0.v.add(v));

            jp1 = path.jmPathPoints.get(numPoints - 2);
            jp0 = path.jmPathPoints.get(numPoints - 1);
//            if (jp0.isCurved) {
            v = jp1.vExit.minus(jp0.v).multInSite(PathUtils.DEFAULT_TENSION);
            jp0.vEnter.copyCoordinatesFrom(jp0.v.add(v));
//            }
        }
    }

    public static void addJMPathPointsToScene(JMPath path, JMathAnimScene scene) {
        for (int i = 0; i < path.size(); i++) {
            addJMPathPointToScene(path.get(i), scene);
        }
    }

    private static void addJMPathPointToScene(JMPathPoint p, JMathAnimScene scene) {
        scene.add(p.getPoint().drawColor("green"));//Point of the curve
        Point pointCPEnter = Point.at(p.vEnter).dotStyle(DotStyle.CROSS).drawColor("blue");
        scene.add(pointCPEnter);//Control point that "enters" into the point
        Point pointCPExit = Point.at(p.vExit).dotStyle(DotStyle.PLUS).drawColor("red");
        scene.add(pointCPExit);//Control point that "exits" from the point
        scene.add(Shape.segment(p.getPoint(), pointCPExit)
                .dashStyle(DashStyle.DASHED)
                .drawColor("gray"));
        scene.add(Shape.segment(p.getPoint(), pointCPEnter)
                .dashStyle(DashStyle.DASHED)
                .drawColor("gray"));
    }

    public static double pathLength(JMPath path) {
        double resul = 0;
        for (int i = 1; i < path.size(); i++) {
            resul += path.get(i - 1).v.minus(path.get(i).v).norm();
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
            jmp.vEnter.copyCoordinatesFrom(jmp.v);
            jmp.vExit.copyCoordinatesFrom(jmp.v);
        }
    }

    public static Shape convertToStraightSegment(Shape sh, int pivotalSegment) {

        Shape resul = new Shape();
        Shape sh2 = sh.copy();
        sh2.getPath().openPath();
        //First point
        Point p = Point.origin();
        resul.getPath().addPoint(p);
        resul.get(0).isThisSegmentVisible = false;
        for (int i = 1; i < sh2.getPath().size(); i++) {
            double dist = sh2.getPoint(i - 1).to(sh2.getPoint(i)).norm();
            p = p.copy().shift(dist, 0);
            resul.getPath().addPoint(p);
        }
        int shSize = sh2.size() - 1;

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
//
//    public JMPathPoint getInterpolatedPoint(JMPathPoint jmp1, JMPathPoint jmp2, double alpha) {
//        JMPathPoint interpolate;
//        if (jmp2.isCurved) {
//            // De Casteljau's Algorithm:
//            // https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
//            Vec E = jmp1.p.interpolate(jmp1.cpExit, alpha); // New cp1 of v1
//            Point G = jmp2.cpEnter.interpolate(jmp2.p, alpha); // New cp2 of v2
//            Point F = jmp1.cpExit.interpolate(jmp2.cpEnter, alpha);
//            Point H = E.interpolate(F, alpha);// cp2 of interpolation point
//            Point J = F.interpolate(G, alpha);// cp1 of interpolation point
//            Point K = H.interpolate(J, alpha); // Interpolation point
//            interpolate = new JMPathPoint(K, jmp2.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
//            interpolate.cpExit.copyFrom(J.v);
//            interpolate.cpEnter.copyFrom(H.v);
//
//        } else {
//            Point K = jmp1.p.interpolate(jmp2.p, alpha);
//            interpolate = new JMPathPoint(K, jmp2.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
//        }
//        interpolate.isCurved = jmp2.isCurved;
//        return interpolate;
//    }

    public void determineStraightSegments(JMPath path) {
        CircularArrayList<JMPathPoint> jmPathPoints = path.jmPathPoints;
        for (int n = 0; n < path.size(); n++) {
            JMPathPoint p1 = jmPathPoints.get(n);
            JMPathPoint p2 = jmPathPoints.get(n + 1);
            p2.isCurved = !((p1.v.minus(p1.vExit).norm() < .0001) && (p2.v.minus(p2.vEnter).norm() < .0001));
        }
    }

    public Shape rectify(Camera cam, Shape shape) {
        ArrayList<ArrayList<Vec>> arPoints = computePolygonalPieces(cam, shape.getPath());
        int size = 0;
        ArrayList<Vec> flattened = new ArrayList<>();
        for (int i = 0; i < arPoints.size(); i++) {
            ArrayList<Vec> subArray = arPoints.get(i);
            for (int j = 0; j < subArray.size() - 1; j++) {//TODO: -1 if closed path!!
                Vec point = subArray.get(j);
                flattened.add(point);
            }

        }
        Vec[] pointsArray = flattened.toArray(new Vec[0]);
        return Shape.polygon(pointsArray);
    }

    public ArrayList<ArrayList<Vec>> computePolygonalPieces(Camera cam, JMPath path) {
        CircularArrayList<JMPathPoint> jmPathPoints = path.jmPathPoints;
        ArrayList<ArrayList<Vec>> resul = new ArrayList<>();
        ArrayList<Vec> connectedSegments = new ArrayList<>();
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

    private void computeStraightenedPoints(ArrayList<Vec> connectedSegments, JMPathPoint p, JMPathPoint q, Camera cam) {
        if (connectedSegments.isEmpty()) {
            connectedSegments.add(p.v);
        }
        if (q.isCurved) {
            int num = appropiateSubdivisionNumber(p.v, q.v, cam);
            for (int n = 1; n < num; n++) {
                connectedSegments.add(p.interpolate(q, n * 1d / num).v);
            }

        }
        connectedSegments.add(q.v);
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
