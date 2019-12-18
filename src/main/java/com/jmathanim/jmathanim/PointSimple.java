/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.ShowCreation;
import Cameras.Camera;
import com.jmathanim.Animations.Circle;
import com.jmathanim.Animations.FadeIn;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Arc;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.Point;


/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class PointSimple extends JMathAnimScene {

    @Override
    public void setupSketch() {
        Camera c=new Camera(800, 600);
        testCamera(c, -2, 2);
        testCamera(c, 2, 2);
        testCamera(c, -2, -2);
        testCamera(c, 2, -2);
        

    }

    private void testCamera(Camera c, double xx, double yy) {
        int cxx[]=c.mathToScreen(xx,yy);
        System.out.println("[Test camera]: x,y="+xx+","+yy+"  sx="+cxx[0]+","+cxx[1]);
    }

    @Override
    public void runSketch() {
//        PGraphics gre = createGraphics(800, 600);
        Point po = new Point(0, 0);
        Point or=new Point(0,1.5);
        Circle circ=new Circle(new Vec(0,0), new Vec(1,0));
        add(po);
        add(or);
        add(circ);
        Line li=new Line(po,or);
        add(li);
//        for (int i = 0; i < 100; i++) {
//            li.shift(new Vec(.01,0,0));
//            doDraws();
//            advanceFrame();
//            System.out.println(i);
//        }
        waitSeconds(1);
        //li.shift(new Vec(1,0,0));
        Animation anim=new ShowCreation(circ,1.);
        
//        play(anim);
        Animation anim2=new FadeIn(li,2);
        play(anim,anim2);
    }

}
