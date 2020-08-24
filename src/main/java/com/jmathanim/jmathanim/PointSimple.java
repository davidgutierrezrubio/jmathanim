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
import com.jmathanim.Utils.SVGImporter;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Circle;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathMathObject;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Polygon;
import com.jmathanim.mathobjects.RegularPolygon;
import com.jmathanim.mathobjects.Segment;
import java.awt.Color;
import java.util.ArrayList;

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
        System.out.println("Running sketch...");
        pruebaSVGImporter();
    }

    public void pruebaSimpleJMPathObject() throws ArrayIndexOutOfBoundsException {
        JMPathMathObject pa = new JMPathMathObject();
        JMPathPoint p;
        p = new JMPathPoint(new Point(0, 0), true, JMPathPoint.TYPE_VERTEX);
        pa.jmpath.addPoint(p);
        p = new JMPathPoint(new Point(1, 0), true, JMPathPoint.TYPE_VERTEX);
        pa.jmpath.addPoint(p);
        p = new JMPathPoint(new Point(1, 1), false, JMPathPoint.TYPE_VERTEX);
        pa.jmpath.addPoint(p);
        p = new JMPathPoint(new Point(0, 1), true, JMPathPoint.TYPE_VERTEX);
        pa.jmpath.addPoint(p);
        pa.jmpath.close();
        pa.mp.color = Color.GREEN;
        add(pa);
        waitSeconds(3);
        play(new ShowCreation(pa, 1, 3));
        waitSeconds(3);
        pa.jmpath.points.get(2).isVisible = false;
        waitSeconds(3);
    }

    public void pruebaSVGImporter() throws ArrayIndexOutOfBoundsException {
        camera.setMathXY(-5, 5, 2);
        double ymax = camera.getMathBoundaries().ymax;
//        camera.setCenter(0, );

        SVGImporter svg = new SVGImporter();
        JMPath pa1 = svg.PSVGtoPath("M174.11346 -2.719801C174.11346 -3.755915 173.416075 -4.403487 172.519438 -4.403487C171.184444 -4.403487 169.84945 -2.988792 169.84945 -1.574097C169.84945 -0.587796 170.516947 0.109589 171.443472 0.109589C172.768504 0.109589 174.11346 -1.265255 174.11346 -2.719801M171.453435 -0.109589C171.025042 -0.109589 170.586685 -0.418431 170.586685 -1.195517C170.586685 -1.683686 170.845714 -2.759651 171.164519 -3.267746C171.662651 -4.034869 172.230521 -4.184309 172.509475 -4.184309C173.087308 -4.184309 173.386187 -3.706102 173.386187 -3.108344C173.386187 -2.719801 173.186934 -1.673724 172.808354 -1.026152C172.459662 -0.448319 171.911717 -0.109589 171.453435 -0.109589Z");
//        JMPath pa1 = svg.PSVGtoPath("M174.11346 -2.719801C174.11346 -3.755915 173.416075 -4.403487 172.519438 -4.403487C171.184444 -4.403487 169.84945 -2.988792 169.84945 -1.574097C169.84945 -0.587796 170.516947 0.109589 171.443472 0.109589C172.768504 0.109589 174.11346 -1.265255 174.11346 -2.719801ZM171.453435 -0.109589C171.025042 -0.109589 170.586685 -0.418431 170.586685 -1.195517C170.586685 -1.683686 170.845714 -2.759651 171.164519 -3.267746C171.662651 -4.034869 172.230521 -4.184309 172.509475 -4.184309C173.087308 -4.184309 173.386187 -3.706102 173.386187 -3.108344C173.386187 -2.719801 173.186934 -1.673724 172.808354 -1.026152C172.459662 -0.448319 171.911717 -0.109589 171.453435 -0.109589Z");
        JMPath pa2 = svg.PSVGtoPath("M171.477434 -3.716065H172.603212C172.274445 -2.241594 172.184781 -1.8132 172.184781 -1.145704C172.184781 -0.996264 172.184781 -0.727273 172.264482 -0.388543C172.364109 0.049813 172.473698 0.109589 172.623137 0.109589C172.82239 0.109589 173.031606 -0.069738 173.031606 -0.268991C173.031606 -0.328767 173.031606 -0.348692 172.97183 -0.488169C172.682913 -1.205479 172.682913 -1.853051 172.682913 -2.132005C172.682913 -2.660025 172.752652 -3.198007 172.862241 -3.716065H173.997982C174.127496 -3.716065 174.486151 -3.716065 174.486151 -4.054795C174.486151 -4.293898 174.276936 -4.293898 174.087646 -4.293898H170.750161C170.530983 -4.293898 170.152403 -4.293898 169.714047 -3.825654C169.365354 -3.437111 169.106326 -2.978829 169.106326 -2.929016C169.106326 -2.919054 169.106326 -2.82939 169.225877 -2.82939C169.305578 -2.82939 169.325504 -2.86924 169.385279 -2.948941C169.873449 -3.716065 170.451282 -3.716065 170.650535 -3.716065H171.218405C170.899601 -2.510585 170.361618 -1.305106 169.943187 -0.398506C169.863486 -0.249066 169.863486 -0.229141 169.863486 -0.159402C169.863486 0.029888 170.022888 0.109589 170.152403 0.109589C170.451282 0.109589 170.530983 -0.169365 170.650535 -0.537983C170.790012 -0.996264 170.790012 -1.016189 170.919526 -1.514321L171.477434 -3.716065Z");
        System.out.println(pa1);

        JMPathMathObject p1 = new JMPathMathObject(pa1, null);
        JMPathMathObject p2 = new JMPathMathObject(pa2, null);
        //Move path to center of the screen, upper
        double xx = pa1.points.get(0).p.v.x;
        double yy = pa1.points.get(0).p.v.y;
        p1.shift(new Vec(-xx,-yy+ymax*.5d));
        p2.shift(new Vec(-xx+5,-yy+ymax*.5d));
        
        pa1.isFilled=false;
        pa2.isFilled=false;
        add(p1);
        waitSeconds(3);
        p1.jmpath.interpolate(20);
        p2.jmpath.interpolate(20);
        add(p2);
        play(new ShowCreation(p1, 2,5),new ShowCreation(p2, 2,5));
        waitSeconds(3);
    }

    public void variosCirculos() {
        Circle c1 = new Circle(new Point(-1, 0), .5);
        c1.mp.thickness = .005d;
        c1.mp.color = Color.MAGENTA;
        c1.mp.alpha = .7d;
        c1.mp.layer = 2;
        add(c1);

        Circle c2 = new Circle(new Point(0, 0), .5);
        add(c2);
        Circle c3 = new Circle(new Point(1, 0), .5);
        add(c3);
        Circle c4 = new Circle(new Point(.5, 1), .5);
        add(c4);
//        RegularPolygon pol=new RegularPolygon(7, .3);
//        add(pol);

        waitSeconds(1);
        ArrayList<Animation> anims = new ArrayList<Animation>();
        anims.add(new ShowCreation(c1, 1, 3));
        anims.add(new ShowCreation(c2, 2, 3));
        anims.add(new ShowCreation(c3, 3, 3));
        anims.add(new ShowCreation(c4, 4, 3));
        play(anims);
        waitSeconds(2);
    }

    public void pruebaInterpolateCircle() {
        Circle circ = new Circle(new Point(-.5, -.5), 1);
        add(circ);
        waitSeconds(1);
        circ.jmpath.interpolate(3);
        waitSeconds(1);
    }

    public void pruebaTransform() {
        RegularPolygon pol1 = new RegularPolygon(3, 1d);
        RegularPolygon pol2 = new RegularPolygon(4, 3.d / 4);
        RegularPolygon pol3 = new RegularPolygon(5, 3.d / 5);
        RegularPolygon pol4 = new RegularPolygon(16, 3.d / 16);
        Circle circ = new Circle(new Point(-.5, -.5), 1);
        pol1.shift(new Vec(-1, 0));
        add(pol1);
        add(pol2);
        add(pol3);
        add(pol4);
        add(circ);
        play(new Transform(pol1, pol2, 3));
        remove(pol2);
        waitSeconds(1);
        play(new Transform(pol1, pol3, 3));
        remove(pol3);
        waitSeconds(1);
        play(new Transform(pol1, pol4, 3));
        remove(pol4);
        waitSeconds(1);
        play(new Transform(pol1, circ, 3));
        remove(circ);
        waitSeconds(1);
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
