/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class myScene extends Scene2D {

    @Override
    public void setupSketch() {
//        conf.setResourcesDir(".");
//        conf.setOutputDir("c:\\media");
        ConfigLoader.parseFile("production.xml");
        ConfigLoader.parseFile("dark.xml");
        conf.setCreateMovie(true);
    }

    @Override
    public void runSketch() {
//        Shape sq=Shape.square().style("solidblue");
//        LaTeXMathObject sq = new LaTeXMathObject("$$x=\\pi$$");
//        sq.putAt(new Point(0,0), Anchor.BY_CENTER);
        Arrow2D sq = Arrow2D.makeSimpleArrow2D(new Point(0, 0), new Point(1, .4), Arrow2D.TYPE_1);
        add(sq);
//        waitSeconds(3);
//        sq.scale(.3);
//        waitSeconds(3);
//        camera.adjustToObjects(sq);
        //TODO: Create default styles:
        //default: shapes
        //defaultEq: Equations
        play.fadeIn(sq);
        waitSeconds(1);
        play.rotate(sq, PI / 3, 3);
        play.shift(sq, .5, -.3, 3);
        waitSeconds(1);
        play.fadeOut(sq);
        waitSeconds(1);
    }

}
