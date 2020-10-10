/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.Vec;
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
//        test4(); //Circle to line, back and forth
//        test5a();
        test5();
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
        Shape seg = Shape.segment(Point.at(0, 0), Point.at(1.6, -.3)).drawColor("#edc988").thickness(2);
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
        Line seg = Shape.line(Point.at(1, 0), Point.at(1, -1)).drawColor("#edc988").thickness(2);
        add(circ, seg);
        Shape circ2 = circ.copy();
        play.transform(5, circ, Shape.segment(seg).scale(3));
        waitSeconds(1);
        play.transform(5, circ, circ2);
        waitSeconds(3);
        play.fadeOutAll();
    }

    public void test5a(){
        Shape circ = Shape.circle().drawColor("#de4463").thickness(2).scale(.7);
        Line l = Shape.line(Point.at(.3, 1), Point.at(1, -1)).shift(.6, .8).drawColor("#edc988").thickness(2);
        Shape seg=Shape.segment(l).scale(2);
        add(circ, seg);
        play.transform(5, circ, seg);
        waitSeconds(3);
        play.fadeOutAll();
    }
    
    public void test5() {
        Shape circ = Shape.circle().drawColor("#de4463").thickness(2).scale(.7);
        Shape seg = Shape.line(Point.at(.3, 1), Point.at(1, -1)).shift(.6, .8).drawColor("#edc988").thickness(2);
        add(circ, seg);
        Shape circ2 = circ.copy();
//        play.transform(5, circ, Shape.segment(seg).scale(3));
        waitSeconds(1);
//        play.transform(5, circ, circ2);
        //Vector of the rect
//        Vec vr = seg.getP1().to(seg.getP2());
        Vec vr = seg.getPoint(0).to(seg.getPoint(1));

        double angle = vr.getAngle();
        angle = (angle < 0 ? angle + 2 * PI : angle);
        angle = (angle > PI ? angle - PI : angle);
        AffineJTransform rot = AffineJTransform.create2DRotationTransform(circ.getCenter(), -angle + PI / 2);
        AffineJTransform shiftTr = AffineJTransform.createTranslationTransform(circ.getCenter().v.mult(-1).copy());
        AffineJTransform tr = rot.compose(shiftTr);
//        
        AffineJTransform trInv = tr.getInverse();
//            tr.applyTransform(circ);
//            tr.applyTransform(seg);
        circ.saveState();
        seg.saveState();
        for (double tt = 1; tt >= .01; tt -= .01) {
            circ.restoreState();
            seg.restoreState();
            tr.applyTransform(circ);
            tr.applyTransform(seg);
            //Rotate the circle so that point 0 lies in (r,0)
            Point p0 = circ.getPoint(0);
            circ.rotate(-p0.v.getAngle());
            final double lambda = 1d / (1 - tt);

            Point A = circ.getPoint(0).copy();
            Point B = A.copy().scale(Point.at(0, 0), lambda, lambda);
            circ.scale(lambda);
            circ.shift(B.to(A));
            Point pointOfRect = seg.getPoint(0).copy();
            pointOfRect.v.y = 0;
            circ.shift(A.to(pointOfRect).mult(tt));
//            advanceFrame();
            circ.rotate(p0.v.getAngle());
            trInv.applyTransform(circ);
            trInv.applyTransform(seg);
            advanceFrame();
        }

        waitSeconds(3);
    }
}
