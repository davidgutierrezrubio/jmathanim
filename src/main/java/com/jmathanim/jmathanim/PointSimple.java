/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.Transform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Circle;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Polygon;
import com.jmathanim.mathobjects.RegularPolygon;
import java.awt.Color;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class PointSimple extends Scene2D {

    @Override
    public void setupSketch() {
//        conf.setHighQuality();
        conf.setLowQuality();
        createRenderer();
    }

    @Override
    public void runSketch() {
//        PGraphics gre = createGraphics(800, 600);
        Point p0 = new Point(1, 0);
        Point p1 = new Point(0, 1);

        Point p2 = new Point(1.5, 0);
        Point p3 = new Point(1.8, 1);
        Polygon pol = new Polygon();
        pol.add(p0);
        pol.add(p1);
        pol.add(p3);
        pol.add(p2);
        pol.close();

//        Circle circ = new Circle(new Vec(0, 0), new Vec(1, 0));
//      
        RegularPolygon regPolyg = new RegularPolygon(15, .3d);
        regPolyg.shift(new Vec(-1, -1));
//        Point centro = regPolyg.getCenter();

//        for (Line radio: regPolyg.getRadius()) {
//            add(radio);
//        }
//        for (Line apotema: regPolyg.getApothem()) {
//            add(apotema);
//        }
//        for (Point vertex: regPolyg.getVertices()) {
//            add(vertex);
//        }
//        add(centro);
        camera.setCenter(0, 0);
//        add(pol);
//        add(regPolyg);
////        add(circ);
//        Animation anim = new ShowCreation(regPolyg, 2);
////        Animation anim2 = new ShowCreation(circ, 1.5);
//        Animation anim3 = new ShowCreation(pol, 2);
////        play(anim,anim2,anim3);

        waitSeconds(1);

//        regPolyg.processAfterNonLinearAnimation();
//        RegularPolygon pol1 = new RegularPolygon(3, 1d);
//        RegularPolygon pol2 = new RegularPolygon(4, 3.d/4);
//        RegularPolygon pol3 = new RegularPolygon(5, 3.d/5);
//        RegularPolygon pol4 = new RegularPolygon(16, 3.d/16);
//        RegularPolygon pol5 = new RegularPolygon(16, 3.d/16);
//        pol1.shift(new Vec(-1, 0));
//        add(pol4);
////        add(pol2);
//        play(new Transform(pol4, pol3, 1));
//        play(new Transform(pol4, pol2, 1));
//        play(new Transform(pol4, pol1, 1));
//        play(new Transform(pol4, pol5, 1));
////        play(new Transform(pol4, pol1, 3));
        Circle circ = new Circle(new Point(-1.2, 0), 1);
        circ.mp.thickness = .02d;
        circ.mp.color = Color.MAGENTA;
        circ.mp.alpha = .7d;
        circ.mp.layer = 2;
        RegularPolygon pol1 = new RegularPolygon(3, 1);
        RegularPolygon pol2 = new RegularPolygon(5, .6d);
        pol2.shift(new Vec(-1, 0));

        pol1.mp.color = Color.BLUE;
        pol2.mp.color = Color.YELLOW;

        add(pol2);
//        add(circ);
        add(pol1);
//        play(new Transform(pol1, circ, 2));
        pol1.prepareForNonLinearAnimation();
        Animation animat = new Transform(pol1, pol2, 2);
        play(animat);
//        Animation animat2 = new ShowCreation(pol1, 2);

//        Point puntoMola = pol2.getVertices().get(0);
//        double dx = dt / 3.;
//        while (!animat.processAnimation(fps)) {
////            animat2.processAnimation(fps);
//            pol2.shift(new Vec(0, -dx, 0));
//            advanceFrame();
//        }
        remove(pol1);
        waitSeconds(1);
    }

}
//        play(anim, anim2);
//        Animation anim2=new FadeIn(pol,2);
//        play(anim2);

//Cookbook:
//Procesa manualmente animacion y mueve al mismo tiempo una figura
//while (!anim2.processAnimation(fps)) {
//            regPolyg.shift(new Vec(0, dx, 0));
//            advanceFrame();
//        }
