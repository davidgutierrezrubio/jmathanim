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
package com.jmathanim.Utils;

import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.JMPathPoint.JMPathPointType;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class PathInterpolator {

    public static final double DEFAULT_TENSION = 0.7d;

    public static void generateControlPointsBySimpleSlopes(JMPath path) {
        generateControlPointsBySimpleSlopes(path, DEFAULT_TENSION);
    }

    /**
     * Generate control points from a bezier cubic curve, so that control points
     * of point n are parallel to the line from point n-1 and n+1. The distance
     * from point n to the control points is multiplied by the 0 to 1 tension
     * parameter. A 1 tension means straight lines. If first and last point are
     * not connected, an approximation is used based on the control point of
     * their neighbour points
     *
     * @param tension The tension to apply to the curve
     */
    public static void generateControlPointsBySimpleSlopes(JMPath path, double tension) //For now, only one method
    {
        //If this is a SVG path, don't generate control points
        if (path.pathType == JMPath.SVG_PATH) {
            return;
        }
        int numPoints = path.jmPathPoints.size();

        for (int n = 0; n < numPoints + 1; n++) {
            int i = n - 1;
            int k = n + 1;
            int L = n + 2;
            JMPathPoint p1 = path.jmPathPoints.get(i);
            JMPathPoint p2 = path.jmPathPoints.get(n);//Compute cp1 for this
            JMPathPoint p3 = path.jmPathPoints.get(k);//Compute cp2 for this
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
            if (p3.isCurved) {
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
                p2.cp1.v.x = cx1;
                p2.cp1.v.y = cy1;
                p2.cp1.v.z = cz1;
                p3.cp2.v.x = cx2;
                p3.cp2.v.y = cy2;
                p3.cp2.v.z = cz2;
            } else {
                //If this path is straight, control points becomes vertices. Although this is not used
                //when drawing straight paths, it becomes handy when doing transforms from STRAIGHT to CURVED paths
                p2.cp1.v.copyFrom(p2.p.v);
                p3.cp2.v.copyFrom(p3.p.v);
            }

        }
        JMPathPoint jp0, jp1;
        Vec v;
        //Compute cp1 and cp2 from first and last points
        jp0 = path.getJMPoint(0);
        if (!jp0.isThisSegmentVisible) {
            jp1 = path.getJMPoint(1);
            v = jp0.p.to(jp1.cp2).multInSite(PathInterpolator.DEFAULT_TENSION);
            jp0.cp1.copyFrom(jp0.p.add(v));

            jp1 = path.getJMPoint(numPoints - 2);
            jp0 = path.getJMPoint(numPoints - 1);
            if (jp0.isCurved) {
                v = jp0.p.to(jp1.cp1).multInSite(PathInterpolator.DEFAULT_TENSION);
                jp0.cp2.copyFrom(jp0.p.add(v));
            }
        }
    }

    public JMPathPoint getInterpolatedPoint(JMPathPoint jmp1, JMPathPoint jmp2, double alpha) {
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

        } else {
            Point K = jmp1.p.interpolate(jmp2.p, alpha);
            interpolate = new JMPathPoint(K, jmp2.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
        }
        interpolate.isCurved = jmp2.isCurved;
        return interpolate;
    }

}
