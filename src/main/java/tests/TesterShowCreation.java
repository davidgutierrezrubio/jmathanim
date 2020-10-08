/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * Class to test animations
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class TesterShowCreation extends Scene2D {

    @Override
    public void setupSketch() {
        ConfigLoader.parseFile("preview.xml");
        ConfigLoader.parseFile("dark.xml");
    }

    @Override
    public void runSketch() {
//        test1();
//        test2();
//        test3();
//        test4();
//        test5();
        Arrow2D arrow = Arrow2D.makeSimpleArrow2D(Point.at(0,0), Point.at(1,1), Arrow2D.TYPE_1);
int numberOfLines = 15;
        MathObject[] objs = new MathObject[numberOfLines];
        for (int n = 0; n < numberOfLines; n++) {
            int numsides = (int) (3 + Math.random() * 7);
            double scale = Math.random();
            Arrow2D r = Arrow2D.makeSimpleArrow2D(Point.random(),Point.random());//.drawColor(JMColor.random());
            objs[n] = r;
        }
        play.showCreation(objs);
        waitSeconds(3);
        play.fadeOutAll();
    }

    private void test5() {

        int numberOfLines = 15;
        MathObject[] objs = new MathObject[numberOfLines];
        for (int n = 0; n < numberOfLines; n++) {
            int numsides = (int) (3 + Math.random() * 7);
            double scale = Math.random();
            Shape r = Shape.regularPolygon(numsides).scale(scale).shift(Point.random().v).drawColor(JMColor.random());
            objs[n] = r;
        }
        play.showCreation(objs);
        waitSeconds(3);
        play.fadeOutAll();
    }

    private void test4() {

        LaTeXMathObject lat = LaTeXMathObject.make("This is a test").stackTo(Anchor.BY_CENTER).layer(1);
        Shape r = Shape.rectangle(lat.getBoundingBox().addGap(.2, .2)).layer(0).style("solidblue").fillAlpha(.6).drawColor(JMColor.RED);
        play.showCreation(lat, r);
        waitSeconds(3);
        play.fadeOutAll();
    }

    private void test3() {
        int numberOfLines = 45;
        MathObject[] objs = new MathObject[numberOfLines];
        for (int n = 0; n < numberOfLines; n++) {
            Shape r = Shape.line(Point.random(), Point.random()).drawColor(JMColor.random());
            objs[n] = r;
        }
        play.showCreation(objs);
        waitSeconds(3);
        play.fadeOutAll();
    }

    private void test2() {
        Shape s = Shape.square().stackTo(Anchor.BY_CENTER).style("solidred").layer(1);
        Shape c = Shape.circle().stackTo(Anchor.BY_CENTER).style("solidblue").layer(0);
        play.showCreation(s, c);
        waitSeconds(3);
        play.fadeOutAll();
    }

    private void test1() {
        Shape s = Shape.square().stackTo(Anchor.BY_CENTER).style("solidred").layer(1);
        play.showCreation(s);
        waitSeconds(1);
        play.fadeOutAll();
    }

}
