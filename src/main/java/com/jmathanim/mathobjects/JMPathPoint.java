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

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.text.DecimalFormat;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMPathPoint extends MathObject implements Updateable, Stateable {

    public enum JMPathPointType {
        VERTEX, INTERPOLATION_POINT
    }

    public final Point p;
    public final Point cpExit, cpEnter; // Entering control point (cpFrom) and exit control point (cpTo)
    public Vec cpExitvBackup, cpEntervBackup;// Backup values, to restore after removing interpolation points
    public boolean isThisSegmentVisible;//if false, segment ending in this point is not visible
    public boolean isCurved;
    public JMPathPointType type; // Vertex, interpolation point, etc.

    public int numDivisions = 0;// This number is used for convenience to store easily number of divisions when
    // subdiving a path
    private JMPathPoint pState;

    /**
     * Builds a new JMPathPoint with given coordinates of vertex and control
     * points. The result is marked as curved and visible.
     *
     * @param x1 x-coordinate of vertex
     * @param y1 y-coordinate of vertex
     * @param x2 x-coordinate of enter control point
     * @param y2 y-coordinate of enter control point
     * @param x3 x-coordinate of exit control point
     * @param y3 y-coordinate of exit control point
     * @return The new jmpathpoint created
     */
    public static JMPathPoint make(double x1, double y1, double x2, double y2, double x3, double y3) {
        JMPathPoint resul = curveTo(Point.at(x1, y1));
        resul.cpEnter.v.x = x2;
        resul.cpEnter.v.y = y2;
        resul.cpExit.v.x = x3;
        resul.cpExit.v.y = y3;
        return resul;
    }

    // Builders
    public static JMPathPoint lineTo(double x, double y) {
        return lineTo(new Point(x, y));
    }

    public static JMPathPoint lineTo(Point p) {
        // Default values: visibleFlag, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(p, true, JMPathPointType.VERTEX);
        jmp.isCurved = false;
        return jmp;
    }

    public static JMPathPoint curveTo(Point p) {
        // Default values: visibleFlag, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(p, true, JMPathPointType.VERTEX);
        jmp.isCurved = true;
        return jmp;
    }

    public JMPathPoint(Point p, boolean isVisible, JMPathPointType type) {
        super();
        this.p = p;
//        this.p.visibleFlag = false;
        cpExit = p.copy();
        cpEnter = p.copy();
        isCurved = false;// By default, is not curved
        this.isThisSegmentVisible = isVisible;
        this.type = type;
    }

    @Override
    public JMPathPoint copy() {
        Point pCopy = p.copy();
        JMPathPoint resul = new JMPathPoint(pCopy, isThisSegmentVisible, type);
        resul.copyStateFrom(this);
        return resul;
    }

    public void copyStateFrom(JMPathPoint jp) {
        p.copyStateFrom(jp.p);
        cpExit.copyStateFrom(jp.cpExit);
        cpEnter.copyStateFrom(jp.cpEnter);
        isCurved = jp.isCurved;
        isThisSegmentVisible = jp.isThisSegmentVisible;
        try { // cp1vBackup and cp2vBackup may be null, so I enclose with a try-catch
            cpExitvBackup = jp.cpExitvBackup.copy();
            cpEntervBackup = jp.cpEntervBackup.copy();
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
        String resul = labelStr + "(" + decimalFormat.format(p.v.x) + ", " + decimalFormat.format(p.v.y) + ")";
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

    @Override
    public void update(JMathAnimScene scene) {
    }

    @Override
    public void saveState() {
        pState = new JMPathPoint(p, isThisSegmentVisible, type);
        p.saveState();
        cpExit.saveState();
        cpEnter.saveState();

        try {
            pState.cpExitvBackup.saveState();
        } catch (NullPointerException e) {
        }
        try {
            pState.cpEntervBackup.saveState();
        } catch (NullPointerException e) {
        }
        pState.isThisSegmentVisible = this.isThisSegmentVisible;
        pState.isCurved = this.isCurved;
        pState.type = this.type;
    }

    @Override
    public void restoreState() {
        p.restoreState();
        cpExit.restoreState();
        cpEnter.restoreState();
        if (pState != null) {
            try {
                pState.cpExitvBackup.restoreState();
            } catch (NullPointerException e) {
            }
            try {
                pState.cpEntervBackup.restoreState();
            } catch (NullPointerException e) {
            }
            this.isThisSegmentVisible = pState.isThisSegmentVisible;
            this.isCurved = pState.isCurved;
            this.type = pState.type;
        }
    }

    @Override
    public Point getCenter() {
        return p;
    }

    @Override
    public Rect computeBoundingBox() {
        return p.computeBoundingBox();
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        // Nothing to draw
    }

    public void copyFrom(JMPathPoint jmPoint) {
        this.p.copyFrom(jmPoint.p);
        this.cpExit.copyFrom(jmPoint.cpExit);
        this.cpEnter.copyFrom(jmPoint.cpEnter);
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        if (!(obj instanceof JMPathPoint)) {
            return;
        }

        JMPathPoint jmp2 = (JMPathPoint) obj;
        this.p.copyStateFrom(jmp2.p);
        this.cpExit.copyStateFrom(jmp2.cpExit);
        this.cpEnter.copyStateFrom(jmp2.cpEnter);
    }

    public boolean isEquivalentTo(JMPathPoint p2, double epsilon) {
        if (p2.isThisSegmentVisible != isThisSegmentVisible) {
            return false;
        }
        if (!p.isEquivalentTo(p2.p, epsilon)) {
            return false;
        }
        if (!cpExit.isEquivalentTo(p2.cpExit, epsilon)) {
            return false;
        }
        return cpEnter.isEquivalentTo(p2.cpEnter, epsilon);
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
        JMPathPoint pSrc = this.copy();
        Point pDst = tr.getTransformedObject(pSrc.p);
        Point cp1Dst = tr.getTransformedObject(pSrc.cpExit);
        Point cp2Dst = tr.getTransformedObject(pSrc.cpEnter);

        this.p.v.copyFrom(pDst.v);
        this.cpExit.v.copyFrom(cp1Dst.v);
        this.cpEnter.v.copyFrom(cp2Dst.v);

        tr.applyTransformsToDrawingProperties(this);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T rotate(Point center, double angle) {
        p.rotate(center, angle);
        cpEnter.rotate(center, angle);
        cpExit.rotate(center, angle);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T shift(Vec shiftVector) {
        p.shift(shiftVector);
        cpEnter.shift(shiftVector);
        cpExit.shift(shiftVector);
        return (T) this;
    }

    /**
     * Computes an interpolated JMPathPoint between this and another one
     *
     * @param q Second JMPathPoint to interpolate
     * @param alpha Interpolation parameter. 0 returns a copy of this object. 1
     * returns a copy of q.
     * @return The interpolated JMPathPoint.
     */
    public JMPathPoint interpolate(JMPathPoint q, double alpha) {
        JMPathPoint interpolate;
        if (q.isCurved) {
            // De Casteljau's Algorithm:
            // https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
            Point E = this.p.interpolate(this.cpExit, alpha); // New cp1 of v1
            Point G = q.cpEnter.interpolate(q.p, alpha); // New cp2 of v2
            Point F = this.cpExit.interpolate(q.cpEnter, alpha);
            Point H = E.interpolate(F, alpha);// cp2 of interpolation point
            Point J = F.interpolate(G, alpha);// cp1 of interpolation point
            Point K = H.interpolate(J, alpha); // Interpolation point
            interpolate = new JMPathPoint(K, q.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
            interpolate.cpExit.v.copyFrom(J.v);
            interpolate.cpEnter.v.copyFrom(H.v);

        } else {
            // Straight interpolation
            Point interP = new Point(this.p.v.interpolate(q.p.v, alpha));
            // Interpolation point is visible iff v2 is visible
            // Control points are by default the same as v1 and v2 (straight line)
            interpolate = new JMPathPoint(interP, q.isThisSegmentVisible, JMPathPointType.INTERPOLATION_POINT);
        }
        interpolate.isCurved = q.isCurved; // The new point is curved iff v2 is
        return interpolate;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        int m = Math.max(p.getUpdateLevel(), cpEnter.getUpdateLevel());
        m = Math.max(m, cpExit.getUpdateLevel());
        setUpdateLevel(m + 1);

    }

}
