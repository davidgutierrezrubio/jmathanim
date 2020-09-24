/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.text.DecimalFormat;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class JMPathPoint extends MathObject implements Updateable, Stateable {

    public static final int TYPE_NONE = 0;
    public static final int TYPE_VERTEX = 1;
    public static final int TYPE_INTERPOLATION_POINT = 2;
    public static final int TYPE_CONTROL_POINT = 3;

    public final Point p;
    public final Point cp1, cp2; //Cómo debe entrar (cp2) y cómo debe salir (cp1)
    public Vec cp1vBackup, cp2vBackup;//Backup values, to restore after removing interpolation points
    public boolean isVisible;
    public boolean isCurved;
    public int type; //Vertex, interpolation point, etc.

    public int numDivisions = 0;//This number is used for convenience to store easily number of divisions when subdiving a path
    private JMPathPoint pState;

    //Builders
    public static JMPathPoint lineTo(double x, double y) {
        return lineTo(new Point(x, y));
    }

    public static JMPathPoint lineTo(Point p) {
        //Default values: visible, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(p, true, TYPE_VERTEX);
        jmp.isCurved = false;
        return jmp;
    }

    public static JMPathPoint curveTo(Point p) {
        //Default values: visible, type vertex, straight
        JMPathPoint jmp = new JMPathPoint(p, true, TYPE_VERTEX);
        jmp.isCurved = true;
        return jmp;
    }

    public JMPathPoint(Point p, boolean isVisible, int type) {
        super();
        this.p = p;
        cp1 = p.copy();
        cp2 = p.copy();
        isCurved = false;//By default, is not curved
        this.isVisible = isVisible;
        this.type = type;
        if (!p.jmPoints.contains(this)) {//TODO: Implement delete method in MathObject
            p.jmPoints.add(this);
        }
    }

    public JMPathPoint copy() {
        Point pCopy = p.copy();
        JMPathPoint resul = new JMPathPoint(pCopy, isVisible, type);
        resul.cp1.v.x = this.cp1.v.x;
        resul.cp1.v.y = this.cp1.v.y;
        resul.cp2.v.x = this.cp2.v.x;
        resul.cp2.v.y = this.cp2.v.y;

        try { //cp1vBackup and cp2vBackup may be null, so I enclose with a try-catch
            resul.cp1vBackup = cp1vBackup.copy();
            resul.cp2vBackup = cp2vBackup.copy();
        } catch (NullPointerException e) {
        }
        resul.isCurved = this.isCurved;
        resul.isVisible = this.isVisible;
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
        if (label != "") {
            labelStr = "[" + label + "]";
        } else {
            labelStr = label;
        }
        String resul = labelStr + "(" + decimalFormat.format(p.v.x) + ", " + decimalFormat.format(p.v.y) + ")";
        if (type == TYPE_INTERPOLATION_POINT) {
            resul = "I" + resul;
        }
        if (type == TYPE_VERTEX) {
            resul = "V" + resul;
        }
        if (!isVisible) {
            resul += "*";
        }
        return resul;
    }

    public void shift(double x, double y, double z) {
        shift(new Vec(x, y, z));
    }

    public <T extends MathObject> T shift(double x, double y) {
        return shift(new Vec(x, y));
    }

    public <T extends MathObject> T shift(Vec shiftVector) {
        p.v.addInSite(shiftVector);
        cp1.v.addInSite(shiftVector);
        cp2.v.addInSite(shiftVector);
        return (T) this;
    }


    @Override
    public void update() {
//        //Update descendents from Point and control points
//        p.updateDependents();
//        cp1.updateDependents();
//        cp2.updateDependents();
    }

    @Override
    public int getUpdateLevel() {
        return Math.max(Math.max(p.getUpdateLevel(), cp1.getUpdateLevel()), cp2.getUpdateLevel());
    }

    @Override
    public void saveState() {
        pState = new JMPathPoint(p, isVisible, type);
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
        pState.isVisible = this.isVisible;
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
        pState.isVisible = this.isVisible;
        pState.isCurved = this.isCurved;
        pState.type = this.type;
    }

    @Override
    public Point getCenter() {
        return p;
    }

    @Override
    public <T extends MathObject> T moveTo(Vec coords) {
        p.moveTo(coords);
        cp1.moveTo(coords);
        cp2.moveTo(coords);
        return (T) this;
    }

    @Override
    public void prepareForNonLinearAnimation() {
    }

    @Override
    public void processAfterNonLinearAnimation() {
    }

    @Override
    public void setDrawParam(double t, int sliceType) {
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
}
