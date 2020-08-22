/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class JMPathPoint {

    public static final int TYPE_NONE = 0;
    public static final int TYPE_VERTEX = 1;
    public static final int TYPE_INTERPOLATION_POINT = 2;
    public static final int TYPE_CONTROL_POINT = 3;

    public final Point p;
    public final Point cp1, cp2; //Cómo debe entrar (cp2) y cómo debe salir (cp1)
    public boolean isVisible;
    public boolean isCurved;
    public int type; //Vertex, interpolation point, etc.

    public JMPathPoint(Point p, boolean isVisible, int type) {
        this.p = p;
        cp1 = p.copy();
        cp2 = p.copy();
        isCurved = false;//By default, is not curved
        this.isVisible = isVisible;
        this.type = type;
    }

    public JMPathPoint copy() {
        JMPathPoint resul = new JMPathPoint(p.copy(), isVisible, type);
        resul.cp1.v.x = this.cp1.v.x;
        resul.cp1.v.y = this.cp1.v.y;
        resul.cp2.v.x = this.cp2.v.x;
        resul.cp2.v.y = this.cp2.v.y;
        resul.isCurved = this.isCurved;
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
        String resul = "(" + p.v.x + ", " + p.v.y + ")";
        if (type == TYPE_INTERPOLATION_POINT) {
            resul = "I" + resul;
        }
        if (type == TYPE_VERTEX) {
            resul = "V" + resul;
        }
        return resul;
    }

}
