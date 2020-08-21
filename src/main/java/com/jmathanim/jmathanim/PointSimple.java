/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.Transform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Circle;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Polygon;
import com.jmathanim.mathobjects.RegularPolygon;
import com.jmathanim.mathobjects.Segment;
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

        camera.setCenter(0, 0);
        waitSeconds(1);

        Circle circ = new Circle(new Point(0, 0), 1);
        circ.mp.thickness = .005d;
        circ.mp.color = Color.MAGENTA;
        circ.mp.alpha = .7d;
        circ.mp.layer = 2;
        add(circ);
        RegularPolygon pol=new RegularPolygon(7, .3);
        add(pol);
        play(new ShowCreation(circ),new ShowCreation(pol));
        waitSeconds(3);
    }

    public void pruebaTransform() {
        RegularPolygon pol1 = new RegularPolygon(3, 1d);
        RegularPolygon pol2 = new RegularPolygon(4, 3.d/4);
        RegularPolygon pol3 = new RegularPolygon(5, 3.d/5);
        RegularPolygon pol4 = new RegularPolygon(16, 3.d/16);
        RegularPolygon pol5 = new RegularPolygon(16, 3.d/16);
        pol1.shift(new Vec(-1, 0));
        add(pol4);
        add(pol2);
        play(new Transform(pol4, pol3, 1));
        play(new Transform(pol4, pol2, 1));
        play(new Transform(pol4, pol1, 1));
        play(new Transform(pol4, pol5, 1));
    }

    public void pruebaLine() {
        //        for (Segment se: pol1.getRadius())
//        {
//            add(se);
//        }
Point puntoLineaA = new Point(0, 0);
Point puntoLineaB = new Point(1, 0);
Line linea = new Line(puntoLineaA, puntoLineaB);
add(linea);
int steps = 60;
double h = 0; //Draw line doesn't work for EXACTLY lines in boundaries
Rect r = renderer.camera.getMathBoundaries();
Line linea2 = new Line(new Point(r.xmin + h, 0), new Point(r.xmin + h, 1));
Line linea3 = new Line(new Point(r.xmax - h, 0), new Point(r.xmax - h, 1));
Line linea4 = new Line(new Point(0, r.ymax - h), new Point(1, r.ymax - h));
Line linea5 = new Line(new Point(0, r.ymin + h), new Point(1, r.ymin + h));

linea2.mp.thickness = .1;
linea3.mp.thickness = .1;
linea4.mp.thickness = .1;
linea5.mp.thickness = .1;

linea2.mp.color = Color.MAGENTA;
linea3.mp.color = Color.GREEN;
linea4.mp.color = Color.YELLOW;
linea5.mp.color = Color.CYAN;

add(linea2);
add(linea3);
add(linea4);
add(linea5);

for (int n = 0; n < steps; n++) {
    puntoLineaB.v.x = Math.cos(n * 6.28 / steps);
    puntoLineaB.v.y = Math.sin(n * 6.28 / steps);
    System.out.println("n=" + n);
    advanceFrame();
}
    }

    public void pruebaPoligono() {
        //        PGraphics gre = createGraphics(800, 600);
        Point p0 = new Point(1, 0);
        Point p1 = new Point(0, 1);

        Point p2 = new Point(1.5, 0);
        Point p3 = new Point(1.8, 1);
        Polygon pol = new Polygon();
        pol.addVertex(p0);
        pol.addVertex(p1);
        pol.addVertex(p3);
        pol.addVertex(p2);
        pol.close();

//        Circle circ = new Circle(new Vec(0, 0), new Vec(1, 0));
//
        RegularPolygon regPolyg = new RegularPolygon(15, .3d);
        regPolyg.shift(new Vec(-1, -1));
        for (Segment radio : regPolyg.getRadius()) {
            add(radio);
        }
        for (Segment apotema : regPolyg.getApothem()) {
            add(apotema);
        }
        for (Point vertex : regPolyg.getVertices()) {
            add(vertex);
        }

        Point centro = regPolyg.getCenter();
        add(centro);
    }

}

//Cookbook:
//Procesa manualmente animacion y mueve al mismo tiempo una figura
//while (!anim2.processAnimation(fps)) {
//            regPolyg.shift(new Vec(0, dx, 0));
//            advanceFrame();
//        }
