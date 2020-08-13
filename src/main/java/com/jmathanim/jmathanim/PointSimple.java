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
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Polygon;
import com.jmathanim.mathobjects.RegularPolygon;

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
//        Point puntoMola = regPolyg.getVertices().get(0);
//        double dx = dt / 3.;
//        waitSeconds(2);
//        regPolyg.prepareForNonLinearAnimation();
//        while (!anim2.processAnimation(fps)) {
//            regPolyg.shift(new Vec(0, dx, 0));
//            advanceFrame();
//        }
//        regPolyg.processAfterNonLinearAnimation();
        RegularPolygon pol1 = new RegularPolygon(3, .3d);
        RegularPolygon pol2 = new RegularPolygon(4, .3d);
        RegularPolygon pol3 = new RegularPolygon(5, .3d);
        RegularPolygon pol4 = new RegularPolygon(16, .3d);
        add(pol1);
//        play(new Transform(pol1, pol2, 3));
//        play(new Transform(pol1, pol3, 3));
        play(new Transform(pol1, pol4, 3));
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
