/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Animations.Animation;
import com.jmathanim.mathobjects.Circle;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
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

        Circle circ = new Circle(new Vec(0, 0), new Vec(1, 0));
//      
        RegularPolygon regPolyg = new RegularPolygon(7, .3d);
//        regPolyg.shift(new Vec(0, -2));
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
        add(pol);
        add(regPolyg);
        add(circ);
        Animation anim = new ShowCreation(regPolyg, 1);
        Animation anim2 = new ShowCreation(circ, 1.5);
        Animation anim3 = new ShowCreation(pol, 2);
//        play(anim,anim2,anim3);

        System.out.println("Objects in this scene:");
        for (MathObject ob : objects) {
            System.out.println(ob);
        }

//        Point puntoMola = regPolyg.getVertices().get(0);
        double dx = dt / 3.;

        while (!anim2.processAnimation(fps)) {
            regPolyg.shift(new Vec(0, dx, 0));
            advanceFrame();
        }
        
        waitSeconds(3);
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