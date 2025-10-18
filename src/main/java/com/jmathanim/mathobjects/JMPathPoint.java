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

import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updateableObjects.Updateable;

import java.text.DecimalFormat;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMPathPoint implements
        Updateable, Boxable, Linkable,
        Coordinates<JMPathPoint>,
        AffineTransformable<JMPathPoint>,
        Interpolable<JMPathPoint>,
        Stateable {

    //    public final Point p; // The vertex point
    private final Vec v; // The vertex point
    private final Vec vExit;
    private final Vec vEnter; // Exit and Enter control points for Bezier curves
    public int numDivisions = 0;// This number is used for convenience to store easily number of divisions when
    private boolean isSegmentToThisPointVisible;
    private boolean isSegmentToThisPointCurved;
    private Point pCenter; // The vertex Point object, created on demand
    // subdiving a path
    private JMPathPoint pState;


    public JMPathPoint(Coordinates<?> p, boolean isVisible) {
        this.v = p.getVec();
        if (p instanceof Point) {
            this.pCenter = (Point) p;
        } else {
            this.pCenter = null;
        }
        vExit = getV().copy();
        vEnter = getV().copy();
        setSegmentToThisPointCurved(false);// By default, is not curved
        this.setSegmentToThisPointVisible(isVisible);
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
        resul.getVEnter().x = enterx;
        resul.getVEnter().y = entery;
        resul.getVExit().x = exitx;
        resul.getVExit().y = exity;
        return resul;
    }

    // Builders
    public static JMPathPoint lineTo(double x, double y) {
        return lineTo(Vec.to(x, y));
    }

    public static JMPathPoint lineTo(Vec v) {
        // Default values: visibleFlag, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(v, true);
        jmp.setSegmentToThisPointCurved(false);
        return jmp;
    }

    public static JMPathPoint lineTo(Coordinates coord) {
        Coordinates p;
        if (coord instanceof Point) {
            p = (Point) coord;
        } else if (coord instanceof JMPathPoint) {
            p = ((JMPathPoint) coord).getV();
        } else {
            p = coord.getVec();
        }

        // Default values: visibleFlag, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(p, true);
        jmp.setSegmentToThisPointCurved(false);
        return jmp;
    }

    public static JMPathPoint curveTo(Vec v) {
        // Default values: visibleFlag, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(v, true);
        jmp.setSegmentToThisPointCurved(true);
        return jmp;
    }

    public static JMPathPoint curveTo(Point p) {
        // Default values: visibleFlag, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(p, true);
        jmp.setSegmentToThisPointCurved(true);
        return jmp;
    }

    @Override
    public JMPathPoint copy() {
        JMPathPoint resul = new JMPathPoint(getV().copy(), isSegmentToThisPointVisible());
        if (pCenter != null)
            resul.pCenter = pCenter.copy();
        resul.copyStateFrom(this);
        return resul;
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof JMPathPoint)) return;
        JMPathPoint jp = (JMPathPoint) obj;
        getV().copyCoordinatesFrom(jp.getV());
        getVExit().copyCoordinatesFrom(jp.getVExit());
        getVEnter().copyCoordinatesFrom(jp.getVEnter());
        setSegmentToThisPointCurved(jp.isSegmentToThisPointCurved());
        setSegmentToThisPointVisible(jp.isSegmentToThisPointVisible());

        if (jp.pCenter != null) {
            if (pCenter != null) {
                pCenter.copyStateFrom(jp.pCenter);
            } else {
                pCenter = jp.pCenter.copy();
            }
        }

    }

    @Override
    public String toString() {
        String pattern = "##0.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String resul = "(" + decimalFormat.format(getV().x) + ", " + decimalFormat.format(getV().y) + ")";
        if (!isSegmentToThisPointVisible()) {
            resul += "*";
        }
        if (!isSegmentToThisPointCurved()) {
            resul += "-";
        }
        return resul;
    }

    public void setSegmentToThisPointVisible(boolean thisSegmentVisible) {
        isSegmentToThisPointVisible = thisSegmentVisible;
    }

    public void copyControlPointsFrom(JMPathPoint jmPoint) {
        this.getV().copyCoordinatesFrom(jmPoint.getV());
        this.getVExit().copyCoordinatesFrom(jmPoint.getVExit());
        this.getVEnter().copyCoordinatesFrom(jmPoint.getVEnter());
    }


    public boolean isEquivalentTo(JMPathPoint p2, double epsilon) {
        if (p2.isSegmentToThisPointVisible() != isSegmentToThisPointVisible()) {
            return false;
        }
        if (!getV().isEquivalentTo(p2.getV(), epsilon)) {
            return false;
        }
        if (!getVExit().isEquivalentTo(p2.getVExit(), epsilon)) {
            return false;
        }
        return getVEnter().isEquivalentTo(p2.getVEnter(), epsilon);
    }

    @Override
    public JMPathPoint applyAffineTransform(AffineJTransform tr) {
//        JMPathPoint pSrc = this.copy();

        this.getV().applyAffineTransform(tr);
        this.getVExit().applyAffineTransform(tr);
        this.getVEnter().applyAffineTransform(tr);

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
    public JMPathPoint shift(Coordinates<?> shiftVector) {
//        AffineJTransform tr = AffineJTransform.createTranslationTransform(shiftVector);
        applyAffineTransform(AffineJTransform.createTranslationTransform(shiftVector));
//        p.applyAffineTransform(tr);
//        cpEnter.applyAffineTransform(tr);
//        cpExit.applyAffineTransform(tr);
        return this;
    }

    /**
     * Computes an interpolated JMPathPoint between this and another one. The interpolation is not the usual linear one,
     * but Bezier interpolation second point  it segment defined is curved.
     *
     * @param coords2 Second JMPathPoint to interpolate
     * @param alpha   Interpolation parameter. 0 returns a copy of this object. 1 returns a copy of q.
     * @return The interpolated JMPathPoint.
     */
    public JMPathPoint interpolate(Coordinates<?> coords2, double alpha) {
        JMPathPoint q;
        if (coords2 instanceof JMPathPoint) {
            q = (JMPathPoint) coords2;
        } else {
            q = new JMPathPoint(coords2, true);
        }
        JMPathPoint interpolate;
//        if (q.isCurved())

        boolean qCurvedTo = !q.getV().isEquivalentTo(q.getVEnter().getVec(), 1e-4);
        boolean thisCurvedFrom = !getV().isEquivalentTo(getVExit().getVec(), 1e-4);
        if (qCurvedTo || thisCurvedFrom) {
            // De Casteljau's Algorithm:
            // https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
            Vec E = this.getV().interpolate(this.getVExit(), alpha); // New cp1 of v1
            Vec G = q.getVEnter().interpolate(q.getV(), alpha); // New cp2 of v2
            Vec F = this.getVExit().interpolate(q.getVEnter(), alpha);
            Vec H = E.interpolate(F, alpha);// cp2 of interpolation point
            Vec J = F.interpolate(G, alpha);// cp1 of interpolation point
            Vec K = H.interpolate(J, alpha); // Interpolation point
            interpolate = new JMPathPoint(K, q.isSegmentToThisPointVisible());
            interpolate.getVExit().copyCoordinatesFrom(J);
            interpolate.getVEnter().copyCoordinatesFrom(H);
        } else {
            // Straight interpolation
            Vec vInterp = this.getV().interpolate(q.getV(), alpha);
            Point interP = new Point(vInterp.x, vInterp.y);
            // Interpolation point is visible iff v2 is visible
            // Control points are by default the same as v1 and v2 (straight line)
            interpolate = new JMPathPoint(interP, q.isSegmentToThisPointVisible());
        }
        interpolate.setSegmentToThisPointCurved(q.isSegmentToThisPointCurved()); // The new point is curved iff v2 is
        return interpolate;
    }

    @Override
    public int getUpdateLevel() {
        return 0;
    }

    @Override
    public void setUpdateLevel(int level) {

    }

    @Override
    public void update(JMathAnimScene scene) {

    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
//        int m = Math.max(p.getUpdateLevel(), cpEnter.getUpdateLevel());
//        m = Math.max(m, cpExit.getUpdateLevel());
//        setUpdateLevel(p.getUpdateLevel());

    }

    @Override
    public void unregisterUpdateableHook(JMathAnimScene scene) {

    }

    public Point getPoint() {
        if (pCenter == null) {
            return Point.at(getV());
        } else {
            return pCenter;
        }
    }

    @Override
    public Vec getVec() {
        return getV();
    }

    @Override
    public JMPathPoint addInSite(Coordinates<?> coords) {
        //Add to the 3 components
        getV().addInSite(coords);
        getVEnter().addInSite(coords);
        getVExit().addInSite(coords);
        return this;
    }

    @Override
    public JMPathPoint minusInSite(Coordinates<?> coords) {
        //Substract from the 3 components
        getV().minusInSite(coords);
        getVEnter().minusInSite(coords);
        getVExit().minusInSite(coords);
        return this;
    }

    @Override
    public void copyCoordinatesFrom(Coordinates<?> coords) {
        //Copy from the 3 components
        getV().copyCoordinatesFrom(coords);
        getVEnter().copyCoordinatesFrom(coords);
        getVExit().copyCoordinatesFrom(coords);
    }

    @Override
    public JMPathPoint minus(Coordinates<?> v2) {
        JMPathPoint copy = copy();
        copy.getV().minusInSite(v2);
        copy.getVEnter().minusInSite(v2);
        copy.getVExit().minusInSite(v2);
        return copy;
    }

    @Override
    public JMPathPoint add(Coordinates<?> v2) {
        JMPathPoint copy = copy();
        //Substract from the 3 components
        copy.getV().addInSite(v2);
        copy.getVEnter().addInSite(v2);
        copy.getVExit().addInSite(v2);
        return copy;
    }

    @Override
    public JMPathPoint mult(double lambda) {
        JMPathPoint copy = copy();
        //Multiply the 3 components
        copy.getV().multInSite(lambda);
        copy.getVEnter().multInSite(lambda);
        copy.getVExit().multInSite(lambda);
        return copy;
    }

    public Vec getV() {
        return v;
    }

    public Vec getVExit() {
        return vExit;
    }

    public Vec getVEnter() {
        return vEnter;
    }

    /**
     * If false, the segment from the previous point to this one is not visible.
     */
    public boolean isSegmentToThisPointVisible() {
        return isSegmentToThisPointVisible;
    }



    public boolean isSegmentToThisPointCurved() {
        return isSegmentToThisPointCurved;
    }

    public void setSegmentToThisPointCurved(boolean segmentToThisPointCurved) {
        isSegmentToThisPointCurved = segmentToThisPointCurved;
    }

    @Override
    public Rect getBoundingBox() {
        return new Rect(v.x, v.y, v.x, v.y);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
