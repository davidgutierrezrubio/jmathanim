/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Renderers.FXRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;
import javafx.scene.shape.*;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class holds several methods to distille paths created with JavaFX
 * routines
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class FXPathUtils {

    public static double EPSILON = 0.0001;

    public JMPath createJMPathFromFXPath(Path pa, Camera cam) {
        JMPath resul = new JMPath();
        JMPathPoint previousPP = JMPathPoint.curveTo(Point.origin());
        JMPathPoint currentMoveToPoint = null;
        for (PathElement el : pa.getElements()) {
            if (el instanceof MoveTo) {
                MoveTo c = (MoveTo) el;
                double[] xy = cam.screenToMath(c.getX(), c.getY());
                JMPathPoint pp = JMPathPoint.lineTo(Point.at(xy[0], xy[1]));
                pp.isThisSegmentVisible = false;
                resul.addJMPoint(pp);
                previousPP = pp;
                currentMoveToPoint = pp;
            }
            if (el instanceof CubicCurveTo) {
                CubicCurveTo c = (CubicCurveTo) el;
                double[] xy = cam.screenToMath(c.getX(), c.getY());
                JMPathPoint pp = JMPathPoint.curveTo(Point.at(xy[0], xy[1]));
                xy = cam.screenToMath(c.getControlX2(), c.getControlY2());
                pp.cpEnter.v.x = xy[0];
                pp.cpEnter.v.y = xy[1];
                xy = cam.screenToMath(c.getControlX1(), c.getControlY1());
                previousPP.cpExit.v.x = xy[0];
                previousPP.cpExit.v.y = xy[1];
                resul.addJMPoint(pp);
                previousPP = pp;
            }
            if (el instanceof LineTo) {
                LineTo c = (LineTo) el;
                double[] xy = cam.screenToMath(c.getX(), c.getY());
                JMPathPoint pp = JMPathPoint.lineTo(Point.at(xy[0], xy[1]));
                resul.addJMPoint(pp);
                previousPP = pp;
            }
            if (el instanceof ClosePath) {
                if (currentMoveToPoint != null) {
                    // if (currentMoveToPoint == resul.getJMPoint(0)) {
                    // resul.getJMPoint(0).isThisSegmentVisible=true;
                    // }
                    // else
                    // {
                    JMPathPoint cc = currentMoveToPoint.copy();
                    cc.isThisSegmentVisible = true;
                    resul.addJMPoint(cc);
                    // }
                }
            }
        }
        // //Be sure the last point is connected with the first (if closed)
        if (!resul.jmPathPoints.isEmpty()) {
            if (resul.jmPathPoints.get(0).p.isEquivalentTo(resul.jmPathPoints.get(-1).p, 1.0E-6)) {
                JMPathPoint fp = resul.jmPathPoints.get(0);
                JMPathPoint lp = resul.jmPathPoints.get(-1);
                fp.cpEnter.v.x = lp.cpEnter.v.x;
                fp.cpEnter.v.y = lp.cpEnter.v.y;
                fp.isThisSegmentVisible = true;
                // Delete last point
                resul.jmPathPoints.remove(lp);
            }
            // Finally, distille the path, removing unnecessary points
            resul.distille();
        }
        return resul;
    }

    /**
     * Convert a JMPath into a JavaFX path
     *
     * @param jmpath JMPath to convert
     * @param camera Camera to convert from math coordinates to screen
     * coordinates
     * @return
     */
    public static Path createFXPathFromJMPath(JMPath jmpath, Camera camera) {
        Path path = new Path();
        Vec p = jmpath.jmPathPoints.get(0).p.v;
        double[] prev = camera.mathToScreen(p.x, p.y);
        path.getElements().add(new MoveTo(prev[0], prev[1]));
        for (int n = 1; n < jmpath.size() + 1; n++) {
            Vec point = jmpath.jmPathPoints.get(n).p.v;
            Vec cpoint1 = jmpath.jmPathPoints.get(n - 1).cpExit.v;
            Vec cpoint2 = jmpath.jmPathPoints.get(n).cpEnter.v;

            double[] xy, cxy1, cxy2;

            xy = camera.mathToScreenFX(point);
            cxy1 = camera.mathToScreenFX(cpoint1);
            cxy2 = camera.mathToScreenFX(cpoint2);

            if (jmpath.jmPathPoints.get(n).isThisSegmentVisible) {
                JMPathPoint jp = jmpath.jmPathPoints.get(n);
                //JavaFX has problems drawin CubicCurves when control points are equal than points
                if ((!jp.isCurved) || ((isAbsEquiv(prev, cxy1, .1)) && (isAbsEquiv(xy, cxy2, .1)))) {
                    final LineTo el = new LineTo(xy[0], xy[1]);
                    path.getElements().add(el);
                } else {
                    final CubicCurveTo el = new CubicCurveTo(cxy1[0], cxy1[1], cxy2[0], cxy2[1], xy[0], xy[1]);
                    path.getElements().add(el);
                }
            } else {
                if (n < jmpath.size() + 1) {
                    final MoveTo el = new MoveTo(xy[0], xy[1]);
                    // If it is the last point, don't move (it creates a strange point at the
                    // beginning)
                    path.getElements().add(el);
                }
            }
            prev[0] = xy[0];
            prev[1] = xy[1];
        }
        return path;
    }

    protected static boolean isAbsEquiv(double[] a, double[] b, double epsilon) {
        final double nn = Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
        return nn < epsilon;
    }

    /**
     * Remove redundant elements from a JavaFX Path
     *
     * @param path Path to distille
     */
    public void distille(Path path) {
        int n = 0;
        Double[] xyPrevious = new Double[]{null, null};
        while (n < path.getElements().size() - 1) {
            PathElement el1 = path.getElements().get(n);
            PathElement el2 = path.getElements().get(n + 1);
            if (isFirstElementRedundant(xyPrevious, el1, el2)) {
                path.getElements().remove(el1);
                n = 0;
                continue;
            }
            if (isSecondElementRedundant(xyPrevious, el1, el2)) {
                path.getElements().remove(el2);
                n = 0;
                continue;
            }
            xyPrevious = getXYFromPathElement(el1);
            n++;
        }
    }

    private static boolean isSecondElementRedundant(Double[] xyPrevious, PathElement el1, PathElement el2) {

        // If the second element doesn't move from the first, is redundant
        if (sameXY(el1, el2)) {
            // Buuuuuuut...if they are both CubicCurve elements, make sure the first one
            // copies relevant data from the second...
            // copy control1 from second element to the first one
            if ((el1 instanceof CubicCurveTo) && (el2 instanceof CubicCurveTo)) {
                CubicCurveTo cc1 = (CubicCurveTo) el1;
                CubicCurveTo cc2 = (CubicCurveTo) el2;
                cc1.setControlX2(cc2.getControlX2());
                cc1.setControlY2(cc2.getControlY2());
            }
            return true;
        }
        // A MoveTo with a closepath immediately after

        return (el1 instanceof MoveTo) && (el2 instanceof ClosePath);
    }

    private static boolean isFirstElementRedundant(Double[] xyPrevious, PathElement el1, PathElement el2) {
        // 2 consecutive MoveTo
        if ((el1 instanceof MoveTo) && (el2 instanceof MoveTo)) {
            return true;
        }
        // If 2 consecutive lines form a straight one (save the previous point from
        // before)
        if ((el1 instanceof LineTo) && (el2 instanceof LineTo)) {
            Double[] xy1 = getXYFromPathElement(el1);
            Double[] xy2 = getXYFromPathElement(el2);

            Vec v1 = Vec.to(xy1[0] - xyPrevious[0], xy1[1] - xyPrevious[1]);
            Vec v2 = Vec.to(xy2[0] - xyPrevious[0], xy2[1] - xyPrevious[1]);
            double n1 = v1.norm();
            double n2 = v2.norm();
            return v1.dot(v2) / (n1 * n2) == 1;
        }

        return false;
    }

    private static boolean sameXY(PathElement el1, PathElement el2) {
        Double[] xy1 = getXYFromPathElement(el1);
        Double[] xy2 = getXYFromPathElement(el2);

        return ((Math.abs(xy1[0] - xy2[0]) < EPSILON) && (Math.abs(xy1[1] - xy2[1]) < EPSILON));

    }

    private static Double[] getXYFromPathElement(PathElement el) {
        Double[] resul = new Double[2];
        if (el instanceof MoveTo) {
            MoveTo elTyped = (MoveTo) el;
            resul[0] = elTyped.getX();
            resul[1] = elTyped.getY();
        }
        if (el instanceof LineTo) {
            LineTo elTyped = (LineTo) el;
            resul[0] = elTyped.getX();
            resul[1] = elTyped.getY();
        }
        if (el instanceof CubicCurveTo) {
            CubicCurveTo elTyped = (CubicCurveTo) el;
            resul[0] = elTyped.getX();
            resul[1] = elTyped.getY();
        }
        if (el instanceof ClosePath) {
            resul[0] = Double.NaN;
            resul[1] = Double.NaN;
        }
        return resul;
    }

    public static Affine camToScreenAffineTransform(Camera cam) {
        double[] m0 = cam.mathToScreen(0, 0);
        double[] mx = cam.mathToScreen(1, 0);
        double[] my = cam.mathToScreen(0, 1);
        Affine resul = new Affine(mx[0] - m0[0], mx[1] - m0[1], m0[0],
                -my[0] + m0[0], -my[1] + m0[1], m0[1]
        );
//        Affine resul = new Affine(mx[0] - m0[0], my[0] - m0[0], m0[0],
//                -mx[1] +m0[1], -my[1] + m0[1], m0[1]
//        );
        return resul;
    }

    public static Affine screenToCamAffineTransfrom(Camera cam) {
        try {
            Affine resul = camToScreenAffineTransform(cam);
            resul.invert();
            return resul;
        } catch (NonInvertibleTransformException ex) {
            Logger.getLogger(FXPathUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Matrix that stores the transform, with the following form: {{1, x, y, z},
     * {0, vx, vy, vz},{0, wx, wy, wz},{0 tx ty tz}} Where x,y,z is the image of
     * (0,0,0) and v,w,t are the images of canonical vectors.
     */
    public static Affine affineJToAffine(AffineJTransform tr) {
        double[] orig = tr.getMatrix().getRow(0);
        double[] vx = tr.getMatrix().getRow(1);
        double[] vy = tr.getMatrix().getRow(2);
        double[] vz = tr.getMatrix().getRow(3);
        return new Affine(
                vx[1], vy[1], vz[1], orig[1],
                vx[2], vy[2], vz[2], orig[2],
                vx[3], vy[3], vz[3], orig[3]
        );
    }
}
