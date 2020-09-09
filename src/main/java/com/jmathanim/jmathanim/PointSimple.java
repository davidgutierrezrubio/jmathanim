/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Animations.AffineTransform;
import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.ApplyCommand;
import com.jmathanim.Animations.FadeIn;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.Transform;
import com.jmathanim.Animations.commands.AbstractCommand;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.SVGImporter;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Circle;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathMathObject;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.updateableObjects.MiddlePoint;
import com.jmathanim.mathobjects.AveragePoint;
import com.jmathanim.mathobjects.updateableObjects.TransformedPoint;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Polygon;
import com.jmathanim.mathobjects.RegularPolygon;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Segment;
import com.jmathanim.mathobjects.updateableObjects.TransformedJMPath;
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
        setCreateMovie(false);
        setShowPreviewWindow(true);
        createRenderer();
    }

    @Override
    public void runSketch() {
        System.out.println("Running sketch...");
        pruebaShiftCommand();
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
        pa.mp.drawColor = Color.GREEN;
        add(pa);
        waitSeconds(3d);
        play(new ShowCreation(pa, 3d));
        waitSeconds(3d);
        pa.jmpath.jmPathPoints.get(2).isVisible = false;
        waitSeconds(3d);
    }

    public void pruebaBoundingBox() {
        Circle circle = new Circle(new Point(-0, 0), 1);
//        camera.setCenter(0, );
        add(circle);
        Rect r = circle.getBoundingBox();
        add(new Segment(new Point(r.xmin, r.ymin), new Point(r.xmax, r.ymax)));
        waitSeconds(3);
    }

    public void variosCirculos() {
        Circle c1 = new Circle(new Point(-1, 0), .5);
        c1.mp.thickness = .005d;
        c1.mp.drawColor = Color.MAGENTA;
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
        ArrayList<Animation> anims = new ArrayList<>();
        anims.add(new ShowCreation(c1, 3));
        anims.add(new ShowCreation(c2, 3));
        anims.add(new ShowCreation(c3, 3));
        anims.add(new ShowCreation(c4, 3));
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

    private void CircleToSquare() {
        Circle c = new Circle(new Point(0, 0), 1);
        RegularPolygon pol2 = new RegularPolygon(4, 1.5);
        pol2.shift(1, 0);
        pol2.getPoint(0).shift(-2d, .5d);
        pol2.getPoint(1).shift(-2d, 0);
        pol2.getPoint(0).isCurved = true;
        pol2.getPoint(1).isCurved = true;
        pol2.getPoint(2).isCurved = true;
        pol2.getPoint(3).isCurved = true;
        pol2.getPath().generateControlPoints();
        add(pol2);

        System.out.println("Orientation of circle: " + c.getPath().getOrientation());
        System.out.println("Orientation of polygon: " + pol2.getPath().getOrientation());

//        c.jmpath.alignPaths(pol2.jmpath);
//        c.jmpath.minimizeSquaredDistance(pol2.jmpath);
        Transform transform = new Transform(c, pol2, 15);
        //        transform.shouldOptimizePathsFirst=false;
        transform.forceChangeDirection = true;
        waitSeconds(2);
        play(transform);
        waitSeconds(3);
    }

    public void pruebaCopia() {
        Circle c = new Circle(new Point(0, 0), 1);
        add(c);
        waitSeconds(.5);
        JMPath path = c.jmpath.rawCopy();
        JMPathMathObject r = new JMPathMathObject(path, null);
        add(r);
        waitSeconds(.5);

    }

    public void pruebaPuntosInterpolacion() {
        Circle circ = new Circle(new Point(-.5, .5), 2);
        circ.mp.thickness /= 3;
        circ.mp.drawColor = Color.YELLOW;

        JMPathPoint pp = circ.jmpath.jmPathPoints.get(0);
        pp.shift(new Vec(-2, 0));
        Circle circ2 = circ.copy();
        circ2.mp.drawColor = Color.GREEN;
        add(circ2);
        JMPathPoint p0 = circ.jmpath.jmPathPoints.get(0);
        JMPathPoint p1 = circ.jmpath.jmPathPoints.get(1);
        add(circ);
        for (double alpha = .2; alpha < 1; alpha += .1) {
            waitSeconds(1);
            JMPathPoint po = circ.jmpath.interpolateBetweenTwoPoints(p0, p1, alpha);
            add(po.p);
        }
        waitSeconds(30);
    }

    public void pruebaInterpolacion() {
        Circle circ = new Circle(new Point(-.5, .5), 2);
        circ.mp.thickness /= 3;
        circ.mp.drawColor = Color.YELLOW;
        JMPathPoint pp = circ.jmpath.jmPathPoints.get(0);
        pp.shift(new Vec(-2, 0));
        Circle circ2 = circ.copy();
        circ2.mp.drawColor = Color.BLUE;
        add(circ, circ2);
        RegularPolygon pol4 = new RegularPolygon(12, 3.d / 16);
        circ.jmpath.alignPaths(pol4.jmpath);
        waitSeconds(30);
    }

    public void pruebaTransform2Circles() {
        Circle circ = new Circle(new Point(0, 0), 2);
        Circle circ2 = new Circle(new Point(1.5, -.5), .5);
        add(circ, circ2);
        play(new Transform(circ, circ2, 5));
    }

    public void pruebaTransform() {
//        RegularPolygon pol1 = new RegularPolygon(3, 1d);
//        pol1.mp.drawColor=Color.BLUE;
//        RegularPolygon pol2 = new RegularPolygon(4, 3.d / 4);
//        pol2.mp.drawColor=Color.GREEN;
        RegularPolygon pol3 = new RegularPolygon(3, 3.d / 5);
        pol3.mp.drawColor = Color.ORANGE;
        RegularPolygon pol4 = new RegularPolygon(12, 3.d / 16);
        pol4.mp.drawColor = Color.PINK;
        pol4.mp.thickness /= 3;
        Circle circ = new Circle(new Point(-.5, -.5), 1);
        circ.mp.thickness /= 3;
        circ.mp.drawColor = Color.YELLOW;
        JMPathPoint pp = circ.jmpath.jmPathPoints.get(0);
        pp.shift(new Vec(-1, 0));
//        pol1.shift(new Vec(-1, 0));
//        add(pol1);
//        add(pol2);
//        add(pol3);
        add(pol4);
        add(circ);
        AffineTransform tr = AffineTransform.create2DRotationTransform(new Point(1, 0), Math.PI * 1.2);
        TransformedJMPath pol5 = new TransformedJMPath(pol4, tr);
        add(pol5);

        double tiempo = 3;
//        play(new Transform(pol1, pol2, tiempo));
//        remove(pol2);
//        waitSeconds(1);
//        play(new Transform(pol1, pol3, tiempo));
//        remove(pol3);
//        waitSeconds(1);
//        play(new Transform(pol1, pol4, tiempo));
//        remove(pol4);
        waitSeconds(1);
        play(new Transform(pol4, circ, tiempo));
        remove(circ);
        waitSeconds(1);
        play(new Transform(pol4, pol3, tiempo));
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

        linea2.mp.drawColor = Color.MAGENTA;
        linea3.mp.drawColor = Color.GREEN;
        linea4.mp.drawColor = Color.YELLOW;
        linea5.mp.drawColor = Color.CYAN;

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

    private void pruebaImportSVGFile() {
        SVGMathObject svgObject = new SVGMathObject(this, "c:\\media\\tex\\o.svg");

        add(svgObject);
        waitSeconds(3);
//        for (JMPathMathObject p:svgObject.jmps)
//        {
//            p.jmpath.generateControlPoints();
//        }
        waitSeconds(1);
        RegularPolygon pol = new RegularPolygon(5, 1d);
        play(new Transform(svgObject.get(2), svgObject.get(0), 5d));
        waitSeconds(3d);
    }

    private void pruebaRelleno() {
        Circle circ = new Circle(new Point(0, 0), 1);
        circ.mp.fill = true;
        play(new ShowCreation(circ, 3));
        play(new FadeIn(circ, 3));
//        for (float al = 0; al <= 1; al += .01) {
//            circ.mp.setFillAlpha(al);
//            advanceFrame();
//        }
        waitSeconds(2);
    }

    private void pruebaLaTeX() {
        LaTeXMathObject lm = new LaTeXMathObject(this, "$$\\int_0^\\infty x\\,dx=\\infty$$");
//        lm.shift(-1, 0);
        lm.scale(1, 1);
        add(lm);
        waitSeconds(1);
//        camera.adjustToRect(lm.getBoundingBox());
        waitSeconds(1);
        Rect rOrigin = camera.getMathBoundaries();
        Rect rDst = camera.getRectView(lm.getBoundingBox());
        for (double t = 0; t < 1; t += .01) {
            Rect r = rOrigin.interpolate(rDst, t);
            camera.setMathXY(r);
            advanceFrame();
        }

        waitSeconds(1);
//        JMPathMathObject xcopia = (JMPathMathObject) lm.jmps.get(3).copy();
//        JMPathMathObject xIgual = (JMPathMathObject) lm.jmps.get(6).copy();
//        JMPathMathObject xDst = (JMPathMathObject) lm.jmps.get(3);
//        xcopia.shift(new Vec(0, -2));
//        add(xcopia);
//        waitSeconds(2);
//        play(new Transform(xIgual, xDst, 7));
        waitSeconds(2);
    }

    private void pruebaLaTeXEcuacion() {
        LaTeXMathObject eq1 = new LaTeXMathObject(this, "$$x=2$$");
        LaTeXMathObject eq2 = new LaTeXMathObject(this, "$$x=4$$");
        LaTeXMathObject eq3 = new LaTeXMathObject(this, "$$x=8$$");
        LaTeXMathObject eq4 = new LaTeXMathObject(this, "$$x=1$$");
        double sc = 1;
        eq1.scale(sc, sc);
        eq2.scale(sc, sc);
        eq3.scale(sc, sc);
        eq4.scale(sc, sc);

        eq1.shift(-1, 0);
        eq2.shift(-1, 0);
        eq3.shift(-1, 0);
        eq4.shift(-1, 0);

        play(new ShowCreation(eq1, 2));
//        waitSeconds(1);
        JMPathMathObject x1 = eq1.jmps.get(2);
        JMPathMathObject x2 = eq2.jmps.get(2);
        JMPathMathObject x3 = eq3.jmps.get(2);
        JMPathMathObject x4 = eq4.jmps.get(2);

        play(new Transform(x1, x2, 1));
        play(new Transform(x1, x3, 1));
        play(new Transform(x1, x4, 1));
        waitSeconds(3);
    }

    public void pruebaTransformSegmentos() {
        RegularPolygon cir = new RegularPolygon(5, 1);
//        cir.mp.fill = true;
        cir.mp.fillColor = Color.yellow;
        add(cir);
        cir.shift(-1, -1);

//        cir.mp.thickness = .5;
        cir.mp.drawColor = Color.GREEN;
//        Segment s1 = new Segment(new Point(-1, 1), new Point(0, .5));
        RegularPolygon pol = new RegularPolygon(5, 1);
        pol.mp.dashStyle = MathObjectDrawingProperties.DASHED;
//        pol.mp.thickness = .5;
        pol.mp.drawColor = Color.blue;
        pol.mp.fillColor = Color.magenta;
        pol.mp.setFillAlpha(.5f);
        pol.mp.fill = false;
        add(pol);
        ArrayList<Segment> radius = pol.getApothem();
//        waitSeconds(3);
//        Segment s1 = (Segment) radius.get(0).copy();
//        s1.mp.thickness = .5;
//        s1.mp.dashStyle = MathObjectDrawingProperties.DOTTED;
//        play(new FadeIn(s1, 5));
        for (Segment s : radius) {
//            s.mp.copyFrom(s1.mp);
//            Transform tr = new Transform(s1, s, 3);
////            tr.setMethod(Transform.METHOD_INTERPOLATE_POINT_BY_POINT);
//            play(tr);
            add(s);
        }
//        remove(s1);
        waitSeconds(1);
        MiddlePoint mp1 = new MiddlePoint(pol.getCenter(), new Point(0, 0));
//        MiddlePoint mp1=new MiddlePoint(radius.get(1).getPoint(1).p, new Point(0,0));
        add(mp1);
        play(new Transform(pol, cir, 30));
        waitSeconds(3);
    }

    public void pruebaMatrix() {
        Point center = new Point(2, 1);
        add(center);
        AffineTransform tr = AffineTransform.create2DScaleTransform(center, 1, 1);
        tr = tr.compose(AffineTransform.create2DRotationTransform(center, Math.PI / 180 * 15));
//        AffineTransform rotation = AffineTransform.create2DRotationTransform(new Point(0, 1), Math.PI / 3);

        Circle circ = new Circle();
        RegularPolygon pol = new RegularPolygon(5, 1);
        add(pol, circ);
        JMPathMathObject circTrans = circ.copy();
        JMPathMathObject polTrans = pol.copy();
        for (double alpha = 0; alpha < 2 * Math.PI; alpha += Math.PI / 10) {
            tr = AffineTransform.create2DRotationTransform(center, alpha);
            camera.setCenter(center);
            circTrans = tr.getTransformedObject(circ);
            polTrans = tr.getTransformedObject(pol);
            circTrans.mp.setRandomDrawColor();
            polTrans.mp.setRandomDrawColor();
            add(polTrans, circTrans);
//            play(new ShowCreation(polTrans,1), new ShowCreation(circTrans,1));

        }
        waitSeconds(10);

    }

    public void pruebaHomotopia() {
        double tiempo = 20;
        Point A = new Point(1, -0);
        Point B = new Point(0, 0);
        Point C = new Point(1, 0);
        Point D = new Point(1, 1);
        Point M = new MiddlePoint(A, B);
        Point N = new MiddlePoint(A, M);
        add(M, N);
        A.mp.drawColor = Color.GREEN;
        B.mp.drawColor = Color.GREEN;
        C.mp.drawColor = Color.RED;
        D.mp.drawColor = Color.RED;
        add(A, B, C, D);
        Segment s1 = new Segment(B, A);
        Segment s2 = new Segment(C, D);
        add(s1, s2);
        Transform tr = new Transform(s1, s2, tiempo);
//        tr.setMethod(Transform.METHOD_INTERPOLATE_POINT_BY_POINT);
//        tr.shouldOptimizePathsFirst = false;
        play(tr);
        waitSeconds(10);
    }

    public void pruebaDependencias() {
        //Muchos middlepoint
        Point A = new Point(0, 0);
        Point B = new Point(0, 1);
        Point C = new Point(-1, 1);
        add(A, B, C);
        AffineTransform tr2 = AffineTransform.createTranslationTransform(new Vec(1, 0, 0));
        AffineTransform tr = AffineTransform.create2DRotationTransform(new Point(1, -1), Math.PI / 180 * 45);
        AffineTransform tr3 = AffineTransform.create2DRotationTransform(new Point(0, 0), -Math.PI / 180 * 45);
        TransformedPoint X = new TransformedPoint(A, tr);
        TransformedPoint Y = new TransformedPoint(B, tr);
        TransformedPoint Z = new TransformedPoint(C, tr);
        X.mp.drawColor = Color.GREEN;
        Y.mp.drawColor = Color.GREEN;
        Z.mp.drawColor = Color.GREEN;
        TransformedPoint T = new TransformedPoint(X, tr2);
        T.mp.drawColor = Color.BLUE;
        add(X, Y, Z);
        add(T);
        MiddlePoint W = new MiddlePoint(Y, Z);
        W.mp.drawColor = Color.RED;
        add(W);
        Polygon pol = new Polygon(A, B, C);
        add(pol);
        TransformedJMPath pol2 = new TransformedJMPath(pol, tr3);
        add(pol2);
        double dy = .01;
        for (double y = 0; y < 1.5; y += .01) {
            A.shift(0, dy);
            B.shift(0, dy / 2);
            C.shift(0, dy / 3);
            advanceFrame();
            waitSeconds(1);
        }

    }

    public void pruebaReflection() {

        RegularPolygon pol = new RegularPolygon(5, 1);
        pol.shift(0, 1);
        pol.mp.drawColor = Color.YELLOW;
        add(pol);

        AffineTransform tr = AffineTransform.createReflection(new Point(1, 1), new Point(2, -1), 1);

        Point p = new Point(0, 0);
        Point reflectedPoint = tr.getTransformedPoint(p);
        reflectedPoint.mp.drawColor = Color.GREEN;
        add(reflectedPoint, p);

        TransformedJMPath pol2 = new TransformedJMPath(pol, tr);
        pol2.mp.drawColor = Color.GRAY;
        add(pol2);
        for (double dx = 0; dx < 2; dx += .001) {
            pol.shift(.001, 0);

            advanceFrame();
        }
        waitSeconds(300);
    }

    public void pruebaReflectionLambda() {

        RegularPolygon pol = new RegularPolygon(5, 1);
        pol.shift(0, 1);
        pol.mp.drawColor = Color.YELLOW;
        add(pol);
        JMPath jmpathOrig = pol.getPath().rawCopy();
        for (double lambda = 0; lambda < 1; lambda += .001) {
            affineTransform(jmpathOrig, pol, lambda);

            advanceFrame();
        }
        waitSeconds(300);
    }

    private void affineTransform(JMPath jmpathOrig, JMPathMathObject mobj1, double t) {
        JMPathPoint interPoint, basePoint, dstPoint;

        AffineTransform tr = AffineTransform.createReflection(new Point(1, 1), new Point(2, -1), t);
        for (int n = 0; n < mobj1.jmpath.jmPathPoints.size(); n++) {
            interPoint = mobj1.jmpath.jmPathPoints.get(n);
            basePoint = jmpathOrig.jmPathPoints.get(n);
            //Interpolate point
            interPoint.p.v = tr.getTransformedPoint(basePoint.p).v;

            //Interpolate control point 1
            interPoint.cp1.v = tr.getTransformedPoint(basePoint.cp1).v;

            //Interpolate control point 2
            interPoint.cp2.v = tr.getTransformedPoint(basePoint.cp2).v;

        }
    }

    public void pruebaShiftCommand() {
        RegularPolygon P = new RegularPolygon(5, 1);
        shift(P, new Vec(1, 0), 6);

        scale(P, P.getPoint(0).p, 2d, 6);

        rotate(P, P.getPoint(0).p, 2.5, 6);
        System.out.println("End!");
        waitSeconds(10);
    }

}

//Cookbook:
//Procesa manualmente animacion y mueve al mismo tiempo una figura
//while (!anim2.processAnimation(fps)) {
//            regPolyg.shift(new Vec(0, dx, 0));
//            advanceFrame();
//        }
