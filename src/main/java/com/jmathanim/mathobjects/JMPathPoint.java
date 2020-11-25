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
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMPathPoint extends MathObject implements Updateable, Stateable {

    public enum JMPathPointType {
        NONE, VERTEX, INTERPOLATION_POINT, CONTROL_POINT
    }
    public final Point p;
    public final Point cp1, cp2; //Cómo debe entrar (cp2) y cómo debe salir (cp1)
    public Vec cp1vBackup, cp2vBackup;//Backup values, to restore after removing interpolation points
    public boolean isThisSegmentVisible;
    public boolean isCurved;
    public JMPathPointType type; //Vertex, interpolation point, etc.

    public int numDivisions = 0;//This number is used for convenience to store easily number of divisions when subdiving a path
    private JMPathPoint pState;

    //Builders
    public static JMPathPoint lineTo(double x, double y) {
        return lineTo(new Point(x, y));
    }

    public static JMPathPoint lineTo(Point p) {
        //Default values: visible, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(p, true, JMPathPointType.VERTEX);
        jmp.isCurved = false;
        return jmp;
    }

    public static JMPathPoint curveTo(Point p) {
        //Default values: visible, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(p, true, JMPathPointType.VERTEX);
        jmp.isCurved = true;
        return jmp;
    }

    public JMPathPoint(Point p, boolean isVisible, JMPathPointType type) {
        super();
        this.p = p;
//        this.p.visible = false;
        cp1 = p.copy();
        cp2 = p.copy();
        isCurved = false;//By default, is not curved
        this.isThisSegmentVisible = isVisible;
        this.type = type;
    }

    @Override
    public JMPathPoint copy() {
        Point pCopy = p.copy();
        JMPathPoint resul = new JMPathPoint(pCopy, isThisSegmentVisible, type);
        resul.cp1.v.copyFrom(cp1.v);
        resul.cp2.v.copyFrom(cp2.v);

        try { //cp1vBackup and cp2vBackup may be null, so I enclose with a try-catch
            resul.cp1vBackup = cp1vBackup.copy();
            resul.cp2vBackup = cp2vBackup.copy();
        } catch (NullPointerException e) {
        }
        resul.isCurved = this.isCurved;
        resul.isThisSegmentVisible = this.isThisSegmentVisible;
        return resul;
    }

    void setControlPoint1(Point cp) {
        cp1.v.x = cp.v.x;
        cp1.v.y = cp.v.y;
    }

    void setControlPoint2(Point cp) {
        cp2.v.x = cp.v.x;
        cp2.v.y = cp.v.y;
    }

    @Override
    public String toString() {
        String pattern = "##0.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String labelStr;
        if (!"".equals(label)) {
            labelStr = "[" + label + "]";
        } else {
            labelStr = label;
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
        return resul;
    }

    @Override
    public void update(JMathAnimScene scene) {
    }

    @Override
    public int getUpdateLevel() {
        return Math.max(Math.max(p.getUpdateLevel(), cp1.getUpdateLevel()), cp2.getUpdateLevel());
    }

    @Override
    public void saveState() {
        pState = new JMPathPoint(p, isThisSegmentVisible, type);
        p.saveState();
        cp1.saveState();
        cp2.saveState();

        try {
            pState.cp1vBackup.saveState();
        } catch (NullPointerException e) {
        }
        try {
            pState.cp2vBackup.saveState();
        } catch (NullPointerException e) {
        }
        pState.isThisSegmentVisible = this.isThisSegmentVisible;
        pState.isCurved = this.isCurved;
        pState.type = this.type;
    }

    @Override
    public void restoreState() {
        p.restoreState();
        cp1.restoreState();
        cp2.restoreState();

        try {
            pState.cp1vBackup.restoreState();
        } catch (NullPointerException e) {
        }
        try {
            pState.cp2vBackup.restoreState();
        } catch (NullPointerException e) {
        }
        pState.isThisSegmentVisible = this.isThisSegmentVisible;
        pState.isCurved = this.isCurved;
        pState.type = this.type;
    }

    @Override
    public Point getCenter() {
        return p;
    }

    @Override
    public <T extends MathObject> T moveTo(Point p) {
        p.moveTo(p);
        cp1.moveTo(p);
        cp2.moveTo(p);
        return (T) this;
    }

    @Override
    public Rect getBoundingBox() {
        return p.getBoundingBox();
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
    }

    @Override
    public void draw(Renderer r) {
        p.draw(r);
    }

    public void copyFrom(JMPathPoint jmPoint) {
        this.p.copyFrom(jmPoint.p);
        this.cp1.copyFrom(jmPoint.cp1);
        this.cp2.copyFrom(jmPoint.cp2);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    public boolean isEquivalentTo(JMPathPoint p2, double epsilon) {
        if (p2.isThisSegmentVisible != isThisSegmentVisible) {
            return false;
        }
        if (!p.isEquivalenTo(p2.p, epsilon)) {
            return false;
        }
        if (!cp1.isEquivalenTo(p2.cp1, epsilon)) {
            return false;
        }
        return cp2.isEquivalenTo(p2.cp2, epsilon);
    }

}
