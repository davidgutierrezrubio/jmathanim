/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class myScene extends Scene2D {

    @Override
    public void setupSketch() {
//        conf.setResourcesDir(".");
//        conf.setOutputDir("c:\\media");
//        ConfigLoader.parseFile("production.xml");
        ConfigLoader.parseFile("preview.xml");
        ConfigLoader.parseFile("dark.xml");
    }

    @Override
    public void runSketch() {
        LaTeXMathObject B=new LaTeXMathObject("B");
        Shape s=new Shape(B.shapes.get(0).getPath().allVisible(),null);
        add(B);
        B.fillAlpha(0.5);
        camera.adjustToObjects(B);
        camera.scale(1.5);
        waitSeconds(5);
    }

    private void transformAxes() {
        //Create axis
        MultiShapeObject axes = new MultiShapeObject();
        for (double xx = -10; xx < 10; xx += .3) {
            final Shape lineaV = Shape.line(Point.make(xx, 0), Point.make(xx, 1)).style("axisblue1");
            axes.addShape(lineaV);
            final Shape lineaH = Shape.line(Point.make(0, xx), Point.make(1, xx)).style("axisblue2");
            axes.addShape(lineaH);
        }
//        add(axes);
//        add(Shape.circle());
Shape lineaPrueba = Shape.line(Point.make(0, -1.5), Point.make(2, -1.5)).drawColor(JMColor.RED);
//        waitSeconds(3);
Point a = Point.make(0, 0);
Point b = Point.make(1, 0);
Point c = Point.make(0, 1);
Point d = Point.make(0, 0);
Point e = Point.make(1, 0);
Point f = Point.make(1, 1);
playAnimation(Commands.affineTransform(axes, a, b, c, d, e, f, 5));
play.rotate(axes, PI / 3, 5);
play.scaleCamera(.5, 3);
playAnimation(Commands.reflectionByAxis(axes, new Point(0, 0), new Point(0, 1), 5));
// Lineas chungas

waitSeconds(3);
    }

}
