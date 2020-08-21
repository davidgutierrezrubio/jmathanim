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
    public boolean isVisible;
    public int type;

    public JMPathPoint(Point p, boolean isVisible, int type) {
        this.p = p;
        this.isVisible = isVisible;
        this.type = type;
    }

    public JMPathPoint copy()
    {
        return new JMPathPoint(p.copy(), isVisible, type);
    }
    
}
