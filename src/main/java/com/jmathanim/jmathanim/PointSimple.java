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
import com.jmathanim.Animations.FadeOut;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.Transform;
import com.jmathanim.Animations.commands.AbstractCommand;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Animations.commands.SingleMathObjectCommand;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.Circle;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.updateableObjects.MiddlePoint;
import com.jmathanim.mathobjects.AveragePoint;
import com.jmathanim.mathobjects.CanonicalJMPath;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.updateableObjects.TransformedPoint;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Polygon;
import com.jmathanim.mathobjects.RegularPolygon;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Segment;
import com.jmathanim.mathobjects.Square;
import com.jmathanim.mathobjects.updateableObjects.AnchoredMathObject;
import com.jmathanim.mathobjects.updateableObjects.TransformedJMPath;
import com.jmathanim.mathobjects.updateableObjects.AbsoluteSizeUpdater;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.awt.Color;
import java.util.ArrayList;
import static javafx.scene.paint.Color.color;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class PointSimple extends Scene2D {

    long nanotime;

    @Override
    public void setupSketch() {
        nanotime = System.nanoTime();
//        conf.setHighQuality();
//        conf.setMediumQuality();
        conf.setLowQuality();
        conf.setCreateMovie(false);
//        conf.setCreateMovie(true);

        conf.setAdjustPreviewToFPS(true);
//        conf.setAdjustPreviewToFPS(false);

//        conf.setShowPreviewWindow(false);
        conf.setShowPreviewWindow(true);
        clockTick("Show preview window");

//        conf.drawShadow = true;
        conf.setBackgroundColor(JMColor.hex("#192841"));
//        conf.backGroundImage="c:\\media\\hoja.jpg";
    }

    @Override
    public void runSketch() {
        System.out.println("Running sketch...");
        //       pruebaTransformHomotopy();
//                pentagonBuild();
        //        pruebaShapeClosedToCanonicalForm();
        //        pruebaTransformRegularPolygons();
//        muchosCuadradosApilados();
//        pruebaVariosTransforms();
//        pruebaRelleno();
//        pruebaSubpathCanon();
        pruebaDosPentagonos1Estrella();
    }

    public void pruebaSubpathCanon() {
        Shape sh = Shape.circle();
        CanonicalJMPath can = sh.jmpath.canonicalForm();
        System.out.println("Path " + sh.jmpath);
        for (double t = 0; t < 1; t += .1) {
            JMPath sb = can.subpath(0, t);
            System.out.println("Subpath " + t + "  :" + sb);
        }

    }

    public void testAll() {
        pruebaFixedCamera();
        resetScene();
        pruebaNewTransform2();
        resetScene();
        pruebaLaTeX();
        resetScene();
        pruebaTransformPathsWithHiddenElements();
        resetScene();
        pruebaSimpleJMPathObject();
        resetScene();
        pruebaBoundingBox();
        resetScene();
        pruebaAlignNew();
        resetScene();
        pruebaLaTeXEcuacion();
        resetScene();
        pruebaSizeLaTeX();
        resetScene();
        pruebaTransform2Circles();
    }

    public void pentagonBuild() {
        Shape pentagon = Shape.square().fillColor(JMColor.BLUE);
        Shape pentagonDst = Shape.regularPolygon(5, new Point(0, 0), .3).thickness(2).drawColor(JMColor.BLUE);
        add(pentagon);
        LaTeXMathObject texto = new LaTeXMathObject("Esto es un pentagono");
        texto.setRelativeSize();//TODO: Doesnt show anything
        texto.stackTo(pentagon, Anchor.LEFT);
        playAnim.transform(pentagon, pentagonDst, 2);
        waitSeconds(1);
        playAnim.zoomToRect(pentagon.getBoundingBox().addGap(.3, .3), 2);
        for (Point p : pentagon.jmpath.getPoints()) {
//            p.thickness(3);
            playAnim.fadein(p, .3);
        }
        waitSeconds(5);
        for (int n = 0; n < 10; n += 2) {
            Segment s = new Segment(pentagon.getPoint(n), pentagon.getPoint(n + 2));
            s.thickness(2).drawColor(JMColor.RED);
            s.mp.dashStyle = MathObjectDrawingProperties.DASHED;
            playAnim.fadein(s, 1);
        }
        waitSeconds(2);
        playAnim.rotate(pentagon, Math.PI / 4, 2);
        waitSeconds(1);
    }

    public void pruebaVariosTransforms() {
        Shape reg = Shape.regularPolygon(5, new Point(0, 0), .3).scale(-1, 1).rotate(-Math.PI / 3).drawColor(JMColor.BLACK);
        Shape sq = Shape.regularPolygon(5, new Point(0, 0), .3).shift(1, 0).thickness(2);
        add(sq.drawColor(JMColor.BLACK));
        double tiempo = 10;
//        waitSeconds(tiempo);
//        play(new FadeIn(reg, tiempo));
//        add(reg);
        Transform tr = new Transform(reg, sq, tiempo);
//        tr.setMethod(Transform.METHOD_INTERPOLATE_POINT_BY_POINT);
        play(tr);
//        waitSeconds(tiempo);
    }

    public synchronized void resetScene() {
        waitSeconds(3);
        JMathAnimScene scene = JMathAnimConfig.getConfig().getScene();
        scene.objects.clear();
        scene.objectsToBeUpdated.clear();

    }

    public void clockTick(String mensaje) {
        nanotime = System.nanoTime() - nanotime;
        System.out.println("[TIME] " + mensaje + " :  " + (nanotime / 1000000000.d) + " s");
    }

    public void pruebaTransformPathsWithHiddenElements() {
        Shape sh = new Shape();
        sh.jmpath.addJMPoint(new JMPathPoint(new Point(0, 0), true, JMPathPoint.TYPE_VERTEX));
        sh.jmpath.addJMPoint(new JMPathPoint(new Point(1, 0), true, JMPathPoint.TYPE_VERTEX));
        sh.jmpath.addJMPoint(new JMPathPoint(new Point(1, 1), true, JMPathPoint.TYPE_VERTEX));
        sh.jmpath.addJMPoint(new JMPathPoint(new Point(.6, 1.2), true, JMPathPoint.TYPE_VERTEX));
        sh.jmpath.addJMPoint(new JMPathPoint(new Point(-.2, .4), true, JMPathPoint.TYPE_VERTEX));

        Shape sq = Shape.square();
        add(sh.scale(.5, .5).shift(-1, 0).drawColor(JMColor.RED), sq);
        waitSeconds(10);
        playAnim.transform(sh, sq, 30);
        waitSeconds(20);
    }

    public void pruebaSimpleJMPathObject() throws ArrayIndexOutOfBoundsException {
        Shape pa = new Shape();
        JMPathPoint p;
        p = new JMPathPoint(new Point(0, 0), true, JMPathPoint.TYPE_VERTEX);
        pa.jmpath.addJMPoint(p);
        p = new JMPathPoint(new Point(1, 0), true, JMPathPoint.TYPE_VERTEX);
        pa.jmpath.addJMPoint(p);
        p = new JMPathPoint(new Point(1, 1), false, JMPathPoint.TYPE_VERTEX);
        pa.jmpath.addJMPoint(p);
        p = new JMPathPoint(new Point(0, 1), true, JMPathPoint.TYPE_VERTEX);
        pa.jmpath.addJMPoint(p);
        pa.jmpath.close();
        pa.drawColor(JMColor.GREEN);
        add(pa);
        waitSeconds(3d);
        play(new ShowCreation(pa, 3d));
        waitSeconds(3d);
        pa.jmpath.jmPathPoints.get(2).isThisSegmentVisible = false;
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
        c1.drawColor(JMColor.GREEN);
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

    private void CircleToSquare() {

        Circle c = new Circle(new Point(0, 0), 1);
        RegularPolygon pol2 = new RegularPolygon(4, 1.5);
        pol2.shift(1, 0);
        pol2.getJMPoint(0).shift(-2d, .5d);
        pol2.getJMPoint(1).shift(-2d, 0);
        pol2.getJMPoint(0).isCurved = true;
        pol2.getJMPoint(1).isCurved = true;
        pol2.getJMPoint(2).isCurved = true;
        pol2.getJMPoint(3).isCurved = true;
        pol2.getPath().generateControlPoints();
        add(c, pol2);
        playAnim.scaleCamera(2, 10);
        System.out.println("Orientation of circle: " + c.getPath().getOrientation());
        System.out.println("Orientation of polygon: " + pol2.getPath().getOrientation());

//        c.jmpath.alignPaths(pol2.jmpath);
//        c.jmpath.minimizeSquaredDistance(pol2.jmpath);
        Transform transform = new Transform(c, pol2, 45);
        //        transform.shouldOptimizePathsFirst=false;
//        transform.forceChangeDirection = true;
        waitSeconds(2);
        play(transform);
        waitSeconds(3);
    }

    public void pruebaCopia() {
        Circle c = new Circle(new Point(0, 0), 1);
        add(c);
        waitSeconds(.5);
        JMPath path = c.jmpath.rawCopy();
        Shape r = new Shape(path, null);
        add(r);
        waitSeconds(.5);

    }

    public void pruebaPuntosInterpolacion() {
        Shape sq = Shape.square();
        add(sq);
        double tiempo = 40;
        waitSeconds(tiempo);
        sq.jmpath.dividePathSegment(1, 3);
        waitSeconds(tiempo);
        sq.jmpath.dividePathSegment(5, 4);
        waitSeconds(tiempo);
        sq.jmpath.dividePathSegment(3, 4);
        waitSeconds(tiempo);
    }

    public void pruebaAlignNew() {
        RegularPolygon sq = new RegularPolygon(6, .3);
        sq.getJMPoint(0).isThisSegmentVisible = false;
        sq.getJMPoint(4).isThisSegmentVisible = false;
        add(sq);
        double tiempo = 40;
        waitSeconds(tiempo);
        sq.jmpath.alignPathsToGivenNumberOfElements(11);
        waitSeconds(tiempo);
        waitSeconds(tiempo);
        waitSeconds(tiempo);
    }

    public void pruebaNewTransform2() {
        RegularPolygon pol = new RegularPolygon(5, 1).shift(-1, -1);
//        pol.getJMPoint(0).isCurved=true;
//        pol.getJMPoint(1).isCurved=true;
//        pol.getJMPoint(2).isCurved=true;
//        pol.getJMPoint(3).isCurved=true;
//        pol.getJMPoint(4).isCurved=true;
//        pol.jmpath.generateControlPoints();
//        pol.getJMPoint(2).isVisible = false;
////        pol.getJMPoint(6).isVisible = false;
//        pol.getJMPoint(4).isVisible = false;

//        Shape sq = Shape.square().shift(-1, -1);
        Shape sq = Shape.regularPolygon(4, new Point(0, 0), .5).shift(-1, -1);
        add(pol, sq);
//        add(pol,sq);
        double tiempo = 30;
        waitSeconds(tiempo);
        Transform tr = new Transform(sq, pol, tiempo * 4);
        play(tr);

        waitSeconds(tiempo);

        waitSeconds(tiempo);
        waitSeconds(tiempo);
        waitSeconds(tiempo);
        waitSeconds(tiempo);
    }

    public void pruebaNewTransform() {
        RegularPolygon pol = new RegularPolygon(8, .3);
        pol.getJMPoint(2).isThisSegmentVisible = false;
        pol.getJMPoint(6).isThisSegmentVisible = false;
        pol.getJMPoint(4).isThisSegmentVisible = false;
        add(pol.drawColor(JMColor.RED));
        Shape sq = Shape.square().shift(-1, -1).drawColor(JMColor.BLUE);
//        add(sq);
        double tiempo = 40;
        waitSeconds(tiempo);
        playAnim.transform(pol, sq, tiempo);

        playAnim.transform(pol, Shape.regularPolygon(8, new Point(0, 0), .3).drawColor(JMColor.GREEN).fillColor(JMColor.RED), tiempo);
        System.out.println("Pol:" + pol);
        waitSeconds(tiempo);
    }

    public void pruebaConnectedComponents() {
        RegularPolygon sq = new RegularPolygon(8, .3);
        sq.getJMPoint(2).isThisSegmentVisible = false;
        sq.getJMPoint(6).isThisSegmentVisible = false;
        sq.getJMPoint(4).isThisSegmentVisible = false;
        add(sq);
        double tiempo = 40;
        waitSeconds(tiempo);
        CanonicalJMPath canonicalForm = sq.jmpath.canonicalForm();

        for (JMPath pa : canonicalForm.getPaths()) {
            Shape sh1 = new Shape(pa, null);
            playAnim.shift(sh1, new Vec(-1, 0), tiempo / 2);
        }

        waitSeconds(tiempo);
        waitSeconds(tiempo);
        waitSeconds(tiempo);
    }

    public void pruebaInterpolacion() {
        Circle circ = new Circle(new Point(-.5, .5), 2);
        circ.mp.thickness /= 3;
        circ.drawColor(JMColor.GREEN);
        JMPathPoint pp = circ.jmpath.jmPathPoints.get(0);
        pp.shift(new Vec(-2, 0));
        Circle circ2 = circ.copy();
        circ2.drawColor(JMColor.BLUE);
        add(circ, circ2);
        RegularPolygon pol4 = new RegularPolygon(12, 3.d / 16);
//        circ.jmpath.alignPaths(pol4.jmpath);
        waitSeconds(30);
    }

    public void pruebaShapeClosedToCanonicalForm() {
        Shape circ = Shape.arc(Math.PI).scale(.5).drawColor(JMColor.GRAY).shift(-.5, 0);
        add(circ);

        drawControlPoint(circ, 0);
        drawControlPoint(circ, -1);

        waitSeconds(30);
        CanonicalJMPath cf = circ.copy().jmpath.canonicalForm();

        Shape can = new Shape(cf.get(0), null);

        drawControlPoint(can, 0);
        drawControlPoint(can, -1);

        can.shift(.5, 0);
        add(can, circ);
        waitSeconds(90);

    }

    private void drawControlPoint(Shape circ, int n) {
        Point p = circ.getJMPoint(n).p;
        Point cp1 = circ.getJMPoint(n).cp1;
        Point cp2 = circ.getJMPoint(n).cp2;

        Segment s1 = new Segment(p, cp1);
        s1.drawColor(JMColor.GREEN);
        Segment s2 = new Segment(p, cp2);
        s2.drawColor(JMColor.RED);
        add(s1, s2);
    }

    public void pruebaTransform2Circles() {
        Shape circ = Shape.arc(Math.PI).scale(2);
        Shape circ2 = Shape.circle().scale(.5);//.shift(1.5, -.5);
        add(circ);
        add(circ2);
        camera.adjustToObjects(circ, circ2);
        camera.scale(1.5);
//        drawControlPoint(circ2, 0);
//        drawControlPoint(circ2, -1);
        waitSeconds(30);
        play(new Transform(circ, circ2, 90));
        waitSeconds(30);
    }

    public void muchosCuadradosApilados() {
        Shape previous = null;
        int[] anchors = {Anchor.RIGHT, Anchor.LEFT, Anchor.UPPER, Anchor.LOWER};
        int anchor = -1;
        ArrayList objectsToZoomAt = new ArrayList();
        for (int n = 0; n < 20; n++) {
            Shape sq = Shape.circle().drawColor(JMColor.BLACK).fillColor(JMColor.random()).scale(.3);
            if (previous != null) {

                int kk = (int) (Math.random() * 4);
                while (anchors[kk] == Anchor.reverseAnchorPoint(anchor)) {

                    kk = (int) (Math.random() * 4);
                }
                System.out.println("kk=" + anchors[kk] + ", Anchor: " + anchor);
                anchor = anchors[kk];

                sq.stackTo(previous, anchor);
                add(sq);
                objectsToZoomAt.add(sq);
                anchor++;
                if (anchor > 4) {
                    anchor = 3;
                }
                playAnim.adjustToObjects(objectsToZoomAt, 1);
                waitSeconds(1);
            }
            previous = sq;
        }
        waitSeconds(3);
    }

    public void pruebaTransformRegularPolygons() {
//        add(Shape.circle());
        Shape pol1 = Shape.regularPolygon(5).scale(-.4, .4).drawColor(JMColor.RED).thickness(3);
        Shape pol2 = Shape.regularPolygon(6).shift(2, 0).rotate(Math.PI / 5).drawColor(JMColor.GRAY);
        add(pol1, pol2);
        camera.adjustToObjects(pol1, pol2);
        waitSeconds(3);
        playAnim.transform(pol1, pol2, 5);
        waitSeconds(3);
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
        Rect r = renderer.camera.getMathView();
        Line linea2 = new Line(new Point(r.xmin + h, 0), new Point(r.xmin + h, 1));
        Line linea3 = new Line(new Point(r.xmax - h, 0), new Point(r.xmax - h, 1));
        Line linea4 = new Line(new Point(0, r.ymax - h), new Point(1, r.ymax - h));
        Line linea5 = new Line(new Point(0, r.ymin + h), new Point(1, r.ymin + h));

        linea2.mp.thickness = .1;
        linea3.mp.thickness = .1;
        linea4.mp.thickness = .1;
        linea5.mp.thickness = .1;

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
        SVGMathObject svgObject = new SVGMathObject("c:\\media\\tex\\o.svg");

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

    public void pruebaDosPentagonos1Estrella() {
        Shape p1 = Shape.regularPolygon(5).drawColor(JMColor.GRAY);
        Shape p2 = Shape.regularPolygon(5).copy().scale(1).drawColor(JMColor.GRAY);;
        Point centro=p1.getCenter().copy();
        add(centro);
        add(p1, p2);
        p2.putAt(centro, Anchor.BY_CENTER);
        camera.adjustToObjects(p1, p2);
        camera.scale(1.8);
        waitSeconds(1);

        waitSeconds(1);
        Shape sh = new Shape();
        sh.drawColor(JMColor.BLACK).thickness(2);
        for (int n = 0; n < 5; n++) {
            sh.jmpath.addJMPoint(p1.getJMPoint(n));
            sh.jmpath.addJMPoint(p2.getJMPoint(n));
        }
        ShowCreation anim = new ShowCreation(sh, 3);
        play(anim);
        waitSeconds(1);
        playAnim.rotate(p2,centro, Math.PI * .2, 5);
        add(p2.getCenter().drawColor(JMColor.RED));
        waitSeconds(1);
        playAnim.scale(p2, centro, .40, 2);
        waitSeconds(1);
        FadeOut f1 = new FadeOut(p1, 1);
        FadeOut f2 = new FadeOut(p2, 1);
        play(f1, f2);
        waitSeconds(5);
    }

    private void pruebaRelleno() {
//        Shape circ = Shape.circle().drawColor(JMColor.BLACK);
        Shape circ = Shape.regularPolygon(8).drawColor(JMColor.BLACK);
        circ.getJMPoint(3).isThisSegmentVisible = false;
        circ.getJMPoint(6).isThisSegmentVisible = false;
//        Shape circ = Shape.square().drawColor(JMColor.BLACK);
//        circ.mp.setFillAlpha(1);
        add(circ);
        camera.adjustToObjects(circ);
        camera.scale(1.8);
        camera.setCenter(circ);
        waitSeconds(1);
        play(new ShowCreation(circ, 6));
        circ.drawColor(JMColor.RED);
//        play(new FadeIn(circ, 1));
//        for (float al = 0; al <= 1; al += .01) {
//            circ.mp.setFillAlpha(al);
//            advanceFrame();
//        }
        waitSeconds(5);
    }

    private void pruebaLaTeX() {
        LaTeXMathObject lm = new LaTeXMathObject("$$\\int_0^\\infty x\\,dx=\\infty$$");
        lm.drawColor(JMColor.BLACK).fillColor(JMColor.BLACK);
//        lm.shift(-1, 0);
        lm.scale(2, 2);
        add(lm);
        waitSeconds(1);
        add(Shape.circle().drawColor(JMColor.RED));
//        camera.adjustToRect(lm.getBoundingBox());
        waitSeconds(1);
        Rect rOrigin = camera.getMathView();
        Rect rDst = camera.getRectThatContains(lm.getBoundingBox());
        for (double t = 0; t < 1; t += .01) {
            Rect r = rOrigin.interpolate(rDst, t);
            camera.setMathView(r);
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
        LaTeXMathObject eq1 = new LaTeXMathObject("$$x=2$$");
        LaTeXMathObject eq2 = new LaTeXMathObject("$$x=4$$");
        LaTeXMathObject eq3 = new LaTeXMathObject("$$x=8$$");
        LaTeXMathObject eq4 = new LaTeXMathObject("$$x=1$$");
        double sc = 1;
        eq1.scale(sc, sc);
        eq2.scale(sc, sc);
        eq3.scale(sc, sc);
        eq4.scale(sc, sc);

        eq1.shift(-1, 0);
        eq2.shift(-1, 0);
        eq3.shift(-1, 0);
        eq4.shift(-1, 0);
        double tiempo = 20;
//        play(new ShowCreation(eq1, 5));
//        waitSeconds(1);
        Shape x1 = eq1.shapes.get(2);
        Shape x2 = eq2.shapes.get(2);
        Shape x3 = eq3.shapes.get(2);
        Shape x4 = eq4.shapes.get(2);

        play(new Transform(x1, x2, tiempo));
        play(new Transform(x1, x3, tiempo));
        play(new Transform(x1, x4, tiempo));
        waitSeconds(3);
    }

    public void pruebaTransformSegmentos() {
        RegularPolygon cir = new RegularPolygon(5, 1);
//        cir.mp.fill = true;
        add(cir);
        cir.shift(-1, -1);

//        cir.mp.thickness = .5;
//        Segment s1 = new Segment(new Point(-1, 1), new Point(0, .5));
        RegularPolygon pol = new RegularPolygon(5, 1);
        pol.mp.dashStyle = MathObjectDrawingProperties.DASHED;
//        pol.mp.thickness = .5;
        pol.mp.setFillAlpha(.5f);
        pol.mp.setFilled(true);
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
        Shape circTrans = circ.copy();
        Shape polTrans = pol.copy();
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

    public void pruebaTransformHomotopy() {
        double tiempo = 20;
        Point A = new Point(1, -0);
        Point B = new Point(0, 0);
        Point C = new Point(1, 0);
        Point D = new Point(1, 1);
        Point M = new MiddlePoint(A, B);
        Point N = new MiddlePoint(A, M);
        add(M, N);
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
        TransformedPoint T = new TransformedPoint(X, tr2);
        add(X, Y, Z);
        add(T);
        MiddlePoint W = new MiddlePoint(Y, Z);
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
        add(pol);

        AffineTransform tr = AffineTransform.createReflection(new Point(1, 1), new Point(2, -1), 1);

        Point p = new Point(0, 0);
        Point reflectedPoint = tr.getTransformedPoint(p);
        add(reflectedPoint, p);

        TransformedJMPath pol2 = new TransformedJMPath(pol, tr);
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
        add(pol);
        JMPath jmpathOrig = pol.getPath().rawCopy();
        for (double lambda = 0; lambda < 1; lambda += .001) {
            affineTransform(jmpathOrig, pol, lambda);

            advanceFrame();
        }
        waitSeconds(300);
    }

    private void affineTransform(JMPath jmpathOrig, Shape mobj1, double t) {
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
        Segment P = new Segment(new Point(0, 0), new Point(2, 1));
        Line L = new Line(new Point(0, 0), new Point(1, -1));
        Segment P2 = P.copy();
        add(P);
        Square sq = new Square();
        P.shift(1, 0);
//        playShift(P, new Vec(.03, 0), 6);

//        playScale(P, new Point(), .3d, 6);
//        playAnim.rotate(P, new Point(), 2.5, 6);
//        add(sq);
//        playAnim.transform(new Circle(), sq, 15);
        System.out.println("Orientation square " + sq.jmpath.getOrientation());
        System.out.println("Orientation segment " + P.jmpath.getOrientation());
        Transform tr = new Transform(P, sq, 15);
//        Transform tr = new Transform(sq, P, 15);
        tr.shouldOptimizePathsFirst = false;
        play(tr);
        Transform tr2 = new Transform(P, P2, 15);
        tr2.setMethod(Transform.METHOD_INTERPOLATE_POINT_BY_POINT);
        play(tr2);
        System.out.println("End! " + P.jmpath);
        waitSeconds(20);
    }

    public void pruebaGirosCompuestos() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 1);
        Segment s1 = new Segment(p1, p2);
        RegularPolygon pol = new RegularPolygon(5, 1);
        add(pol);
        LaTeXMathObject texto = new LaTeXMathObject("$x^2$");
        add(texto);
        texto.scale(.2, .2);

        Square sq = new Square();
        add(sq);
        AnchoredMathObject anchor = new AnchoredMathObject(texto, Anchor.RIGHT, p2);
        AnchoredMathObject anchor2 = new AnchoredMathObject(pol, Anchor.BY_CENTER, texto, Anchor.LEFT);
        AnchoredMathObject anchor3 = new AnchoredMathObject(sq, Anchor.LEFT, pol, Anchor.RIGHT);
        registerObjectToBeUpdated(anchor);
        registerObjectToBeUpdated(anchor2);
        registerObjectToBeUpdated(anchor3);
        s1.label = "S1";
        ApplyCommand cmd1 = Commands.rotate(s1, p1, 2 * Math.PI, 20);
        play(cmd1);
    }

    void teselacionHexagonos() {
        RegularPolygon hex1 = new RegularPolygon(6, .3);
        add(hex1);
//        hex1.shift(-1,0);
//        getCamera().setCenter(hex1.getCenter().v.x,hex1.getCenter().v.y);
        Shape hex2 = hex1.copy();
        add(hex2);
        Segment lado = new Segment(hex2.getPoint(0), hex2.getPoint(5));
        ApplyCommand cmd = Commands.reflectionByAxis(hex2, lado, 3);
        play(cmd);

        int n1 = 0;
        int n2 = 1;
        for (int n = 0; n < 5; n++) {
            hex2 = hex2.copy();
//            lado = new Segment(hex2.getPoint(n1), hex2.getPoint(n2));
            cmd = Commands.rotate(hex2, hex2.getPoint(n), Math.PI * 2 / 3, 1);
            //            SingleMathObjectCommand tr = Commands.homotopy(hex2, hex2.getJMPoint(0).p, hex2.getJMPoint(1).p, hex1.getJMPoint(n).p, hex1.getJMPoint(n+1).p);
            play(cmd);
        }

        hex2 = gira(hex2, 6);
        hex2 = gira(hex2, 7);
        hex2 = gira(hex2, 8);

        hex2 = gira(hex2, 10);
        hex2 = gira(hex2, 11);

        hex2 = gira(hex2, 13);
        hex2 = gira(hex2, 14);

        hex2 = gira(hex2, 16);
        hex2 = gira(hex2, 17);

        hex2 = gira(hex2, 19);
        hex2 = gira(hex2, 20);
        hex2 = gira(hex2, 22);

        hex2 = gira(hex2, 24);
        hex2 = gira(hex2, 25);

        hex2 = gira(hex2, 27);
        hex2 = gira(hex2, 28);

        hex2 = gira(hex2, 30);

//         hex2=gira(hex2,10);
//         hex2=gira(hex2,11);
//         hex2=gira(hex2,12);
        waitSeconds(10);
    }

    private Shape gira(Shape hex2, int n) {
        ApplyCommand cmd;
        System.out.println("Rotating " + n);
        hex2 = hex2.copy();
        cmd = Commands.rotate(hex2, hex2.getPoint(n), Math.PI * 2 / 3, 1);
        play(cmd);
        return hex2.copy();
    }

    private void pruebaTransformRotateXY() {
        Shape sq = Shape.square();
        Point A = new Point(1, 1);
        Point B = new Point(0, -1);
        Point C = new Point(1.5, -.7);
        Shape sq2 = Shape.square(A, .5).drawColor(JMColor.RED).fillColor(JMColor.GREEN);
        Shape sq3 = sq.copy().drawColor(JMColor.BLUE).fillColor(JMColor.RED);
        Shape rect = Shape.rectangle(B, C);

        rect.fillAlpha(0);
        sq.fillAlpha(1);
        add(rect, sq);
        waitSeconds(35);
        waitSeconds(35);
        playAnim.rotate(rect, rect.getCenter(), Math.PI / 3, 5);
//      
//        playAnim.transform(sq, sq2, 10);
//        sq.setObjectType(MathObject.OTHER);
        playAnim.transform(sq, rect, 40);
        playAnim.transform(sq, sq3, 10);
//        playAnim.transform(rect, Shape.Circle(), dt);
        waitSeconds(5);
    }

    private void pruebaFixedCamera() {

//        for (double x = -3; x < 3; x += .5) {
//            Line l = new Line(new Point(x, 0), new Point(x, 1));
//            l.mp.dashStyle = MathObjectDrawingProperties.DOTTED;
//            l.drawColor(Color.gray);
//            Line l2 = new Line(new Point(0, x), new Point(1, x));
//            l2.mp.dashStyle = MathObjectDrawingProperties.DOTTED;
//            l2.drawColor(Color.gray);
//            add(l, l2);
//        }
        RegularPolygon pol = new RegularPolygon(5, 1);

        LaTeXMathObject la1 = new LaTeXMathObject("$P_1$");
        LaTeXMathObject la2 = new LaTeXMathObject("$P_2$");
        LaTeXMathObject la3 = new LaTeXMathObject("$P_3$");
        LaTeXMathObject la4 = new LaTeXMathObject("$P_4$");
        LaTeXMathObject la5 = new LaTeXMathObject("$P_5$");
//        double sca=.2;
//        la1.scale(sca, sca);
//        la2.scale(sca, sca);
//        la3.scale(sca, sca);
//        la4.scale(sca, sca);
//        la5.scale(sca, sca);
        double gap = .1;
        la1.putAt(pol.getPoint(0), Anchor.UL, gap);//TODO: Add a space gap to this
        la2.putAt(pol.getPoint(1), Anchor.LEFT, gap);//TODO: Move this constants to Anchor class
        la3.putAt(pol.getPoint(2), Anchor.LOWER, gap);
        la4.putAt(pol.getPoint(3), Anchor.RIGHT, gap);
        la5.putAt(pol.getPoint(4), Anchor.UR, gap);
        camera.adjustToRect(pol.getBoundingBox().addGap(1, 1));
        for (Point p : pol.jmpath.getPoints()) {
            add(Arrow2D.makeSimpleArrow2D(pol.getCenter(), p, Arrow2D.TYPE_1));
        }

        add(pol, la1, la2, la3, la4, la5);

//        la1.setAbsoluteSize();
//        la2.setAbsoluteSize();
//        la3.setAbsoluteSize();
        la4.setAbsoluteSize();
        la5.setAbsoluteSize();
        int timeScale = 10;
        waitSeconds(timeScale);
        Rect rr = camera.getRectThatContains(pol.getBoundingBox());
        add(Shape.rectangle(rr).drawColor(JMColor.RED));

        playAnim.zoomToRect(pol.getBoundingBox(), 3 * timeScale);

//        double yCenter = camera.getMathBoundaries().getCenter().v.y;
//        for (double dx = 0; dx < 2; dx += .01) {
//            camera.setMathXY(-5 + .5 * dx, 5 - .5 * dx, yCenter);
//            advanceFrame();
//        }
        waitSeconds(3 * timeScale);
        playAnim.scaleCamera(1.2, 10);

        playAnim.rotate(pol, pol.getCenter(), Math.PI / 3, 10);
        playAnim.scaleCamera(.9, 10);
        playAnim.scaleCamera(2, 10);
        playAnim.scaleCamera(5, 10);
        waitSeconds(3 * timeScale);
    }

    public void pruebaCopiaPath() {
        Shape sq = Shape.square();
        sq.fillColor(JMColor.WHITE);
        Shape sq2 = sq.copy();
        sq.shift(-1, -1);
        add(sq, sq2);
        waitSeconds(50);
    }

    public void pruebaSVGImport() {
        SVGMathObject svg = new SVGMathObject("C:\\media\\cocacola.svg");
        add(svg.get(0));
//        add(svg.shapes.get(1));
//        add(svg.shapes.get(2));
        svg.drawColor(JMColor.WHITE);

        Shape s = Shape.square();
//        add(s);
//        JMPath uno = s.jmpath;
//        JMPath dos = svg.shapes.get(0).jmpath;
        waitSeconds(3);
        playAnim.transform(svg.get(0), s, 26);
        waitSeconds(3);
//        playAnim.transform(svg.get(0),s, 100); 

    }

    public void pruebaVisible() {
        RegularPolygon pol = new RegularPolygon(6, 1);
        add(pol);
        advanceFrame();
        waitSeconds(15);
        for (int n = 0; n < 12; n++) {
            pol.getJMPoint(n - 1).isThisSegmentVisible = true;
            pol.getJMPoint(n).isThisSegmentVisible = false;
            waitSeconds(15);
        }
        waitSeconds(55);
    }

    private void pruebaSizeLaTeX() {

        Point p = new Point(0, 0);
        LaTeXMathObject la = new LaTeXMathObject("X");
        add(la);
        int v = camera.screenHeight;//Numero de puntos
        double vm = camera.getMathView().getHeight();//Altura math
        //v---vm
        //10---tamaño        

        //Tamaño de X en puntos 6.807795
        double size = .025 * vm / 6.807795;
        final double escalaFinal = size;
//        la.scale(new Point(0,0),escalaFinal,escalaFinal);
        System.out.println("XRect: " + la.getBoundingBox());
        System.out.println("Tamaño X:" + la.getBoundingBox().getHeight());
        System.out.println("Tamaño X en puntos:" + camera.mathToScreen(la.getBoundingBox().getHeight()));
        System.out.println("Need to scale 14*vm/v/" + la.getBoundingBox().getHeight());

        add(Arrow2D.makeSimpleArrow2D(new Point(-1, .5), p, Arrow2D.TYPE_2).scale(3));

        playAnim.shift(p, new Vec(0, 2), 15);//TODO: How to easily specify that don't show this object?
        waitSeconds(30);
    }

}

//Cookbook:
//Procesa manualmente animacion y mueve al mismo tiempo una figura
//while (!anim2.processAnimation(fps)) {
//            regPolyg.shift(new Vec(0, dx, 0));
//            advanceFrame();
//        }
