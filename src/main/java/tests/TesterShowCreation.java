/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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

package tests;

import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Line;
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
//        ConfigLoader.parseFile("production.xml");
        ConfigLoader.parseFile("dark.xml");
    }

    @Override
    public void runSketch() throws Exception {
        test1();
        test2();
        test3();//Lines
        test4();//latex
        test5();//Polygons
        test6();//Arrows
        test7();//Circle
//Rect{xmin=-2.0, ymin=-1.125, xmax=2.0, ymax=1.125}
//    Shape s=Shape.segment(Point.at(-1.87,1.125),Point.at(2,.83));
//    play.showCreation(2,s);
//        waitSeconds(1);
    }

    private void test7() {
        Shape c = Shape.circle();
        play.showCreation(c);
        waitSeconds(3);
        play.fadeOutAll();
    }

    private void test6()   {
        Arrow2D arrow = Arrow2D.makeSimpleArrow2D(Point.at(0, 0), Point.at(1, 1), Arrow2D.TYPE_1);
        int numberOfLines = 15;
        MathObject[] objs = new MathObject[numberOfLines];
        for (int n = 0; n < numberOfLines; n++) {
            int numsides = (int) (3 + Math.random() * 7);
            double scale = Math.random();
            
            int randomType=(int) (1+Math.random()*2.9);
            Arrow2D r = Arrow2D.makeSimpleArrow2D(Point.random(), Point.random(),randomType).drawColor(JMColor.random());
            objs[n] = r;
        }
        play.showCreation(2, objs);
        waitSeconds(1);
        play.fadeOutAll();
    }

    private void test5()   {

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

    private void test4()   {

        LaTeXMathObject lat = LaTeXMathObject.make("This is a test").stackTo(Anchor.BY_CENTER).layer(1);
        Shape r = Shape.rectangle(lat.getBoundingBox().addGap(.2, .2)).layer(0).style("solidblue").fillAlpha(.6).drawColor(JMColor.RED);
        play.showCreation(lat, r);
        waitSeconds(1);
        play.fadeOutAll();
    }

    private void test3() {
        int numberOfLines =5;
        MathObject[] objs = new MathObject[numberOfLines];
        for (int n = 0; n < numberOfLines; n++) {
            Line r = Shape.line(Point.random(), Point.random()).drawColor(JMColor.random());
            objs[n] = r;
        }
        play.showCreation(objs);
        System.out.println("wait");
        waitSeconds(1);
        System.out.println("Fade all");
        play.fadeOutAll();
    }

    private void test2() {
        Shape s = Shape.square().stackTo(Anchor.BY_CENTER).style("solidred").layer(1);
        Shape c = Shape.circle().stackTo(Anchor.BY_CENTER).style("solidblue").layer(0);
        play.showCreation(s, c);
        waitSeconds(1);
        play.fadeOutAll();
    }

    private void test1() {
        Shape s = Shape.square().stackTo(Anchor.BY_CENTER).style("solidred").layer(1);
        play.showCreation(s);
        waitSeconds(1);
        play.fadeOutAll();
    }

}
