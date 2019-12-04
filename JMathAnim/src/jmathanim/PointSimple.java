/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmathanim;

import Cameras.Camera;
import mathobjects.Arc;
import mathobjects.Line;
import mathobjects.Point;


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
        Arc arc=new Arc(0, 0, 1, 3.14159);
        add(po);
        add(or);
        add(arc);
        Line li=new Line(po,or);
        add(li);
        for (int i = 0; i < 10; i++) {
            po.x += .2;
            doDraws();
            advanceFrame();
            System.out.println(i);
//            try {
//
//                Thread.sleep(300);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(MathProcScene.class.getName()).log(Level.SEVERE, null, ex);
//            }

        }

    }

}
