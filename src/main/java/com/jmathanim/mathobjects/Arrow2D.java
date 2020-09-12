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
public class Arrow2D extends MultiShapeObject {
    public Point p1,p2;
    public int arrowType=0;
    
    public Arrow2D(Point p1,Point p2) {
        this.p1=p1;
        this.p2=p2;
        shapes.add(new Segment(p1,p2));
    }
    
    
}
