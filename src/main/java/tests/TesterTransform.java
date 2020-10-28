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

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.Transform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.Scene2DAwt;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MOProperties.ArcAttributes;
import com.jmathanim.mathobjects.Dot;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class TesterTransform extends Scene2DAwt {

    @Override
    public void setupSketch() {
        ConfigLoader.parseFile("preview.xml");
//        ConfigLoader.parseFile("production.xml");
        ConfigLoader.parseFile("dark.xml");
    }

    @Override
    public void runSketch() throws Exception {
        test1();//Circle and square, back and forth
        test2(); //Arcs, open curves vs closed
        test3(); //Circle to segment, back and forth
        test4(); //Circle to line, back and forth
        test5a();
        test5();
        test6();
        test8();//Latex object

    }

    public void test1() {
        Shape c = Shape.circle().drawColor("#de4463").thickness(.5).rotate(45*DEGREES);
        Shape sq = Shape.square().stackTo(Anchor.BY_CENTER).scale(1.4142).drawColor("#edc988").thickness(.5);
        Shape c2 = c.copy();
        add(Dot.at(0,0).style("dotYellowPlus"));
        add(c, sq);
         add(Shape.segment(c.getPoint(0), sq.getPoint(0)).drawColor(JMColor.BLUE));
        for (int n = 0; n < c.jmpath.size(); n++) {
            add(c.jmpath.getJMPoint(n).p.style("dotYellowPlus"));
        }
        for (int n = 0; n < sq.jmpath.size(); n++) {
            add(sq.getPoint(n).copy().style("dotBlueCross"));
        }
       
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
        Shape seg = Shape.segment(Dot.at(0, 0), Dot.at(1.6, -.3)).drawColor("#edc988").thickness(2);
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
        Line seg = Shape.line(Dot.at(1, 0), Dot.at(1, -1)).drawColor("#edc988").thickness(2);
        add(circ, seg);
        Shape circ2 = circ.copy();
        play.transform(5, circ, Shape.segment(seg).scale(3));
        waitSeconds(1);
        play.transform(5, circ, circ2);
        waitSeconds(3);
        play.fadeOutAll();
    }

    public void test5a() {
        Shape circ = Shape.circle().drawColor("#de4463").thickness(2).scale(.7);
        Line l = Shape.line(Dot.at(.3, 1), Dot.at(1, -1)).shift(.6, .8).drawColor("#edc988").thickness(2);
        Shape seg = Shape.segment(l).scale(2);
        add(circ, seg);
        play.transform(5, circ, seg);
        waitSeconds(3);
        play.fadeOutAll();
    }

    public void test5() {
        Shape circ = Shape.circle().drawColor("#de4463").thickness(2).scale(.7);
        Shape seg = Shape.line(Dot.at(.3, 1), Dot.at(1, -1)).shift(.6, .8).drawColor("#edc988").thickness(2);
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
            Dot p0 = circ.getPoint(0);
            circ.rotate(-p0.v.getAngle());
            final double lambda = 1d / (1 - tt);

            Dot A = circ.getPoint(0).copy();
            Dot B = A.copy().scale(Dot.at(0, 0), lambda, lambda);
            circ.scale(lambda);
            circ.shift(B.to(A));
            Dot pointOfRect = seg.getPoint(0).copy();
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

    private void test6() {
        double ang = 360 * DEGREES;
        double ang2 = 180 * DEGREES;
        Shape arc = Shape.arc(ang).rotate(Dot.at(0, 0), -.5 * ang).thickness(3);
        Shape seg = Shape.segment(Dot.at(1, -.5 * ang), Dot.at(1, .5 * ang)).thickness(2);
//        play.showCreation(arc, seg);
        add(arc, seg);
        play.adjustToObjects(10, arc, seg);
        play.cameraScale(10, 2);
        waitSeconds(1);
//        play.transform(6, arc, arc2);
//TODO: Make an animation that rectifies an arc into a segment with equal length
        play.transform(6, arc, seg);
        waitSeconds(3);
    }

    private void testArcContinous() {
        double ang = 360 * DEGREES;
        Shape arc = Shape.arc(ang);
        add(arc);
        camera.scale(3);
        Shape arcBase = arc.copy();
        add(arcBase);
        waitSeconds(1);
        double t = 0;
        while (t <= 1) {
            doa(arcBase, arc, t);
            t += .001;
            advanceFrame();
        }
        doa(arcBase, arc, 1);

    }

    private void doa(Shape arcbase, Shape arc, double t) {
        ArcAttributes attrs = (ArcAttributes) arcbase.attrs;
        Dot center = attrs.center;
        double angle = attrs.angle;
        double radius = attrs.radius;
        double angRot = center.to(arcbase.getPoint(0)).getAngle();
        if (t == 0) {//Do nothing
            return;
        }
        if (t == 1) {
            return;
        }
        double nRadius = radius / (1 - t);
        double nAng = angle * (1 - t);

        Shape nArc = Shape.arc(nAng).rotate(center, .5 * (angle - nAng)).scale(nRadius);//.shift(center.v);
//        Shape nArc = Shape.arc(nAng).rotate(-.5*nAng).scale(nRadius).shift(center.v);
        int size = arc.getPath().size();
        for (int n = 0; n < size; n++) {
            arc.getJMPoint(n).copyFrom(nArc.getJMPoint(n));
        }

    }

    private void test8() {
        LaTeXMathObject lat = new LaTeXMathObject("$2+3=5$");
        LaTeXMathObject lat2 = new LaTeXMathObject("$2+6=8$");
        LaTeXMathObject lat3 = new LaTeXMathObject("$3+2=5$");
        lat.stackTo(Anchor.BY_CENTER);
        lat2.stackTo(Anchor.BY_CENTER);
        lat3.stackTo(Anchor.BY_CENTER);
        add(lat);
        camera.adjustToAllObjects();
        waitSeconds(3);
        ArrayList<Animation> anims = new ArrayList<>();
        for (int n = 0; n < lat.shapes.size(); n++) {
            anims.add(new Transform(5, lat.get(n), lat2.get(n)));
        }
        playAnimation(anims);
        anims.clear();
        for (int n = 0; n < lat.shapes.size(); n++) {
            anims.add(new Transform(5, lat.get(n), lat3.get(n)));
        }
        playAnimation(anims);

        waitSeconds(3);
    }

}
