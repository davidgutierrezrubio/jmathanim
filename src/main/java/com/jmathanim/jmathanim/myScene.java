/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class myScene extends Scene2D {

    @Override
    public void setupSketch() {
        ConfigLoader.parseFile("preview.xml");
        ConfigLoader.parseFile("dark.xml");
    }

    @Override
    public void runSketch() {
//        Shape sq=Shape.square().style("solidblue");
        LaTeXMathObject sq = new LaTeXMathObject("$$x=\\pi$$");
        sq.putAt(new Point(0,0), Anchor.BY_CENTER);
        add(sq);
        camera.adjustToObjects(sq);
        //TODO: Create default styles:
        //default: shapes
        //defaultEq: Equations
        play.growIn(sq);
        waitSeconds(1);
        play.rotate(sq, PI / 3, 10);
        waitSeconds(1);
        play.shrinkOut(sq);
        waitSeconds(1);
    }

}
