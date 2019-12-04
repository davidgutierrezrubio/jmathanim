/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmathanim;

import mathobjects.Line;
import mathobjects.Point;


/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class PointSimple extends JMathAnimScene {

    @Override
    public void setupSketch() {

    }

    @Override
    public void mainLoop() {
//        PGraphics gre = createGraphics(800, 600);
        Point po = new Point(400, 300);
        Point or=new Point(400,0);
        add(po);
        add(or);
        Line li=new Line(po,or);
        add(li);
        for (int i = 0; i < 10; i++) {
            po.x += 10;
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
