/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.Circle;
import com.jmathanim.Animations.FadeIn;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Cameras.Camera2D;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectContainer;
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
        conf.setHighQuality();
        createRenderer();
    }
    
    @Override
    public void runSketch() {
//        PGraphics gre = createGraphics(800, 600);
        Point po = new Point(0, 0);
        Point or = new Point(0, 1);
        
        Point p2 = new Point(2, 0);
        Point p3 = new Point(2, 1);
        Polygon pol = new Polygon();
        pol.add(po);
        pol.add(or);
        pol.add(p3);
        pol.add(p2);
        pol.close();
        add(pol);
        Circle circ = new Circle(new Vec(0, 0), new Vec(1, 0));
//        add(po);
//        add(or);
//        add(circ);
//        for (int i = 0; i < 1*fps; i++) {
//            double dx=dt/3.;
//            po.shift(new Vec(dx,0,0));
//            //TODO: Compute dt 
//            advanceFrame();
//            System.out.println("dx="+dx+"   i="+i);
//        }
//        waitSeconds(1);
        //li.shift(new Vec(1,0,0));
        RegularPolygon li = new RegularPolygon(6,1.d);
        Point centro=new Point(li.getCenter());
        for (Line radio: li.getRadius()) {
            add(radio);
        }
        add(centro);
        add(li);
        camera.setCenter(1, 0);
        Animation anim = new ShowCreation(li, 2);
        Animation anim2 = new ShowCreation(circ, 2);
        play(anim);
        waitSeconds(3);
    }
    
}
//        play(anim, anim2);
//        Animation anim2=new FadeIn(pol,2);
//        play(anim2);
