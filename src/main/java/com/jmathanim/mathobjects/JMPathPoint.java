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

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import com.jmathanim.mathobjects.updaters.Coordinates;

import java.text.DecimalFormat;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMPathPoint extends MathObject<JMPathPoint> implements Updateable, Stateable, Coordinates<JMPathPoint> {

    //    public final Point p; // The vertex point
    public final Vec v; // The vertex point
    public final Vec vExit, vEnter; // Exit and Enter control points for Bezier curves
    public Vec vExitvBackup, vEntervBackup;// Backup values, to restore after removing interpolation points
    /**
     * If false, the segment from the previous point to this one is not visible.
     */
    public boolean isThisSegmentVisible;
    public boolean isCurved;
    public JMPathPointType type; // Vertex, interpolation point, etc.
    public int numDivisions = 0;// This number is used for convenience to store easily number of divisions when
    private Point pCenter; // The vertex point
    // subdiving a path
    private JMPathPoint pState;


    public JMPathPoint(Coordinates p, boolean isVisible, JMPathPointType type) {
        super();
        this.v = p.getVec();
        if (p instanceof Point) {
            this.pCenter = (Point) p;
        }
        else {
            this.pCenter = null;
        }
        vExit = v.copy();
        vEnter = v.copy();
        isCurved = false;// By default, is not curved
        this.isThisSegmentVisible = isVisible;
        this.type = type;
    }

    /**
     * Builds a new JMPathPoint with given coordinates of vertex and control points. The result is marked as curved and
     * visible.
     *
     * @param x1     x-coordinate of vertex
     * @param y1     y-coordinate of vertex
     * @param enterx x-coordinate of enter control point
     * @param entery y-coordinate of enter control point
     * @param exitx  x-coordinate of exit control point
     * @param exity  y-coordinate of exit control point
     * @return The new jmpathpoint created
     */
    public static JMPathPoint make(double x1, double y1, double enterx, double entery, double exitx, double exity) {
        JMPathPoint resul = curveTo(Vec.to(x1, y1));
        resul.vEnter.x = enterx;
        resul.vEnter.y = entery;
        resul.vExit.x = exitx;
        resul.vExit.y = exity;
        return resul;
    }

    // Builders
    public static JMPathPoint lineTo(double x, double y) {
        return lineTo(Vec.to(x, y));
    }

    public static JMPathPoint lineTo(Vec v) {
        // Default values: visibleFlag, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(v, true, JMPathPointType.VERTEX);
        jmp.isCurved = false;
        return jmp;
    }

    public static JMPathPoint lineTo(Coordinates coord) {
        Coordinates p;
        if (coord instanceof Point) {
            p= (Point) coord;
        } else if (coord instanceof JMPathPoint) {
            p=((JMPathPoint)coord).v;
        } else {
            p=coord.getVec();
        }

        // Default values: visibleFlag, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(p, true, JMPathPointType.VERTEX);
        jmp.isCurved = false;
        return jmp;
    }

    public static JMPathPoint curveTo(Vec v) {
        // Default values: visibleFlag, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(v, true, JMPathPointType.VERTEX);
        jmp.isCurved = true;
        return jmp;
    }

    public static JMPathPoint curveTo(Point p) {
        // Default values: visibleFlag, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(p, true, JMPathPointType.VERTEX);
        jmp.isCurved = true;
        return jmp;
    }

    @Override
    public JMPathPoint copy() {
        JMPathPoint resul = new JMPathPoint(v.copy(), isThisSegmentVisible, type);
        if (pCenter != null) resul.pCenter = pCenter.copy();
        resul.copyStateFrom(this);
        return resul;
    }

    public void copyStateFrom(JMPathPoint jp) {
        v.copyFrom(jp.v);
        vExit.copyFrom(jp.vExit);
        vEnter.copyFrom(jp.vEnter);
        isCurved = jp.isCurved;
        isThisSegmentVisible = jp.isThisSegmentVisible;

        if (jp.pCenter != null) {
            if (pCenter != null) {
                pCenter.copyStateFrom(jp.pCenter);
            } else {
                pCenter = jp.pCenter.copy();
            }
        }

        try { // cp1vBackup and cp2vBackup may be null, so I enclose with a try-catch
            vExitvBackup = jp.vExitvBackup.copy();
            vEntervBackup = jp.vEntervBackup.copy();
        } catch (NullPointerException e) {
        }
    }

    @Override
    public String toString() {
        String pattern = "##0.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String labelStr;
        if (!"".equals(objectLabel)) {
            labelStr = "[" + objectLabel + "]";
        } else {
            labelStr = objectLabel;
        }
        String resul = labelStr + "(" + decimalFormat.format(v.x) + ", " + decimalFormat.format(v.y) + ")";
        if (type == JMPathPointType.INTERPOLATION_POINT) {
            resul = "I" + resul;
        }
        if (type == JMPathPointType.VERTEX) {
            resul = "V" + resul;
        }
        if (!isThisSegmentVisible) {
            resul += "*";
        }
        if (!isCurved) {
            resul += "-";
        }
        return resul;
    }

    public boolean isSegmentToThisPointVisible() {
        return isThisSegmentVisible;
    }

    public void setSegmentToThisPointVisible(boolean segmentToThisPointVisible) {
        isThisSegmentVisible = segmentToThisPointVisible;
    }

    @Override
    public void saveState() {
        pState = new JMPathPoint(v, isThisSegmentVisible, type);
        v.saveState();
        vExit.saveState();
        vEnter.saveState();

        try {
            pState.vExitvBackup.saveState();
        } catch (NullPointerException e) {
        }
        try {
            pState.vEntervBackup.saveState();
        } catch (NullPointerException e) {
        }
        pState.isThisSegmentVisible = this.isThisSegmentVisible;
        pState.isCurved = this.isCurved;
        pState.type = this.type;
    }

    public boolean isSegmentToThisPointCurved() {
        return isCurved;
    }

    public void setSegmentToThisPointCurved(boolean curved) {
        isCurved = curved;
    }

    @Override
    public void restoreState() {
        v.restoreState();
        vExit.restoreState();
        vEnter.restoreState();
        if (pState != null) {
            try {
                pState.vExitvBackup.restoreState();
            } catch (NullPointerException e) {
            }
            try {
                pState.vEntervBackup.restoreState();
            } catch (NullPointerException e) {
            }
            this.isThisSegmentVisible = pState.isThisSegmentVisible;
            this.isCurved = pState.isCurved;
            this.type = pState.type;
        }
    }

    @Override
    public Point getCenter() {
        return Point.at(v.x, v.y);
    }

    @Override
    protected Rect computeBoundingBox() {
        return Rect.makeFromVec(v);
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera cam) {
        // Nothing to draw
    }

    public void copyControlPointsFrom(JMPathPoint jmPoint) {
        this.v.copyFrom(jmPoint.v);
        this.vExit.copyFrom(jmPoint.vExit);
        this.vEnter.copyFrom(jmPoint.vEnter);
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        super.copyStateFrom(obj);
        if (!(obj instanceof JMPathPoint)) {
            return;
        }

        JMPathPoint jmp2 = (JMPathPoint) obj;
        this.v.copyFrom(jmp2.v);
        this.vExit.copyFrom(jmp2.vExit);
        this.vEnter.copyFrom(jmp2.vEnter);
    }

    public boolean isEquivalentTo(JMPathPoint p2, double epsilon) {
        if (p2.isThisSegmentVisible != isThisSegmentVisible) {
            return false;
        }
        if (!v.isEquivalentTo(p2.v, epsilon)) {
            return false;
        }
        if (!vExit.isEquivalentTo(p2.vExit, epsilon)) {
            return false;
        }
        return vEnter.isEquivalentTo(p2.vEnter, epsilon);
    }

    @Override
    public JMPathPoint applyAffineTransform(AffineJTransform tr) {
//        JMPathPoint pSrc = this.copy();

        this.v.applyAffineTransform(tr);
        this.vExit.applyAffineTransform(tr);
        this.vEnter.applyAffineTransform(tr);

        return this;
    }

    @Override
    public JMPathPoint rotate(Coordinates center, double angle) {
        applyAffineTransform(AffineJTransform.create2DRotationTransform(center, angle));
//        AffineJTransform tr = AffineJTransform.create2DRotationTransform(center, angle);
//        p.applyAffineTransform(tr);
//        cpEnter.applyAffineTransform(tr);
//        cpExit.applyAffineTransform(tr);
        return this;
    }

    @Override
    public JMPathPoint shift(Vec shiftVector) {
//        AffineJTransform tr = AffineJTransform.createTranslationTransform(shiftVector);
        applyAffineTransform(AffineJTransform.createTranslationTransform(shiftVector));
//        p.applyAffineTransform(tr);
//        cpEnter.applyAffineTransform(tr);
//        cpExit.applyAffineTransform(tr);
        return this;
    }

    /**
     * Computes an interpolated JMPathPoint between this and another one. The interpolation is not the usual linear one,
     * but Bezier interpolation if it is curved.
     *
     * @param coords2     Second JMPathPoint to interpolate
     * @param alpha Interpolation parameter. 0 returns a copy of this object. 1 returns a copy of q.
     * @return The interpolated JMPathPoint.
     */
    public JMPathPoint interpolate(Coordinates coords2, double alpha) {
        JMPathPoint q;
        if (coords2 instanceof JMPathPoint) {
             q = (JMPathPoint) coords2;
        } else {
            q = new JMPathPoint(coords2, true, JMPathPointType.VERTEX);
        }
    JMPathPoint interpolate;
        if (q.isCurved) {
            // De Casteljau's Algorithm:
            // https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
            Vec E = this.v.interpolate(this.vExit, alpha); // New cp1 of v1
            Vec G = q.vEnter.interpolate(q.v, alpha); // New cp2 of v2
            Vec F = this.vExit.interpolate(q.vEnter, alpha);
            Vec H = E.interpolate(F, alpha);// cp2 of interpolation point
            Vec J = F.interpolate(G, alpha);// cp1 of interpolation point
            Vec K = H.interpolate(J, alpha); // Interpolation point
            interpolate = new JMPathPoint(Point.at(K), q.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
            interpolate.vExit.copyFrom(J);
            interpolate.vEnter.copyFrom(H);

        } else {
            // Straight interpolation
            Vec vInterp = this.v.interpolate(q.v, alpha);
            Point interP = new Point(vInterp.x, vInterp.y);
            // Interpolation point is visible iff v2 is visible
            // Control points are by default the same as v1 and v2 (straight line)
            interpolate = new JMPathPoint(interP, q.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
        }
        interpolate.isCurved = q.isCurved; // The new point is curved iff v2 is
        return interpolate;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
//        int m = Math.max(p.getUpdateLevel(), cpEnter.getUpdateLevel());
//        m = Math.max(m, cpExit.getUpdateLevel());
//        setUpdateLevel(p.getUpdateLevel());

    }

    public Point getPoint() {
        if (pCenter == null) {
            return Point.at(v);
        } else {
            return pCenter;
        }
    }

    @Override
    public Vec getVec() {
        return v;
    }

    public enum JMPathPointType {
        VERTEX, INTERPOLATION_POINT
    }

}
