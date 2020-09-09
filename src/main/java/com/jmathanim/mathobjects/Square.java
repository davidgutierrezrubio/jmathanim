/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.Vec;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Square extends RegularPolygon {

    public Square() {
        super(4, 1);
        Vec v=jmpath.getPoint(0).p.v;
        this.shift(v.mult(-1));
    }

}
