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
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class TesterTransform extends Scene2D {

    @Override
    public void setupSketch() {
        ConfigLoader.parseFile("preview.xml");
//        ConfigLoader.parseFile("production.xml");
        ConfigLoader.parseFile("dark.xml");
    }

    @Override
    public void runSketch() throws Exception {
//        test1();//Circle and square, back and forth
//        test2(); //Arcs, open curves vs closed
//        test3(); //Circle to segment, back and forth
        test4(); //Circle to line, back and forth
    }

    public void test1() {
        Shape c = Shape.circle().drawColor("#de4463").thickness(2);
        Shape sq = Shape.square().stackTo(Anchor.BY_CENTER).drawColor("#edc988").thickness(2);
        Shape c2 = c.copy();
        add(c, sq);
        //The segment doesn't move on the second transform. Why?
        //The second animation is not the reverse of the first one. Why?
        add(Shape.segment(c.getPoint(0), sq.getPoint(0)).drawColor(JMColor.BLUE));
        play.transform(5, c, sq);
        waitSeconds(1);
        play.transform(5, c, c2);
        waitSeconds(3);
        play.fadeOutAll();
    }

    private void test2() {
        Shape arc = Shape.arc(120 * DEGREES).drawColor("#de4463").thickness(2);
        Shape sq = Shape.square().stackTo(Anchor.BY_CENTER).drawColor("#edc988").thickness(2);
        add(arc, sq);
        Shape arc2 = arc.copy();
        play.transform(5, arc, sq);
        waitSeconds(1);
        play.transform(5, arc, arc2);
        waitSeconds(3);
        play.fadeOutAll();
    }

    private void test3() {
        Shape circ = Shape.circle().drawColor("#de4463").thickness(2);
        Shape seg= Shape.segment(Point.at(0,0),Point.at(1.6,-.3)).drawColor("#edc988").thickness(2);
        add(circ, seg);
        Shape circ2 = circ.copy();
        play.transform(5, circ, seg);
        waitSeconds(1);
        play.transform(5, circ, circ2);
        waitSeconds(3);
        play.fadeOutAll();
    }
     private void test4() {
         //TODO: Treat special case to lines
        Shape circ = Shape.circle().drawColor("#de4463").thickness(2);
        Line seg= Shape.line(Point.at(0,0),Point.at(1.6,-.3)).drawColor("#edc988").thickness(2);
        add(circ, seg);
        Shape circ2 = circ.copy();
        play.transform(5, circ, seg);
        waitSeconds(1);
        play.transform(5, circ, circ2);
        waitSeconds(3);
        play.fadeOutAll();
    }
}
