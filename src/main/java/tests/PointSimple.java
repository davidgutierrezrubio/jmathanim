/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import com.jmathanim.Animations.AffineTransform;
import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.ApplyCommand;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.Highlight;
import com.jmathanim.Animations.Transform;
import com.jmathanim.Animations.TransformStrategies.PointInterpolationCanonical;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.updateableObjects.MiddlePoint;
import com.jmathanim.mathobjects.CanonicalJMPath;
import com.jmathanim.mathobjects.updateableObjects.TransformedPoint;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.updateableObjects.AnchoredMathObject;
import com.jmathanim.mathobjects.updateableObjects.TransformedJMPath;
import java.util.ArrayList;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class PointSimple extends Scene2D {

    long nanotime;

    @Override
    public void setupSketch() {
        ConfigLoader.parseFile("preview.xml");
//        ConfigLoader.parseFile("production.xml");
        ConfigLoader.parseFile("light.xml");
    }

    @Override
    public void runSketch() {
        System.out.println("Running sketch...");
        //       pruebaTransformHomotopy();
//        pentagonBuild();
//                pruebaTransformRegularPolygons();
        //        muchosCuadradosApilados();
//        pruebaTransformPathsWithHiddenElements();
        //        pruebaRelleno();
        //        pruebaSubpathCanon();
        //        pruebaDosPentagonos1Estrella();
        //        pruebaSimpleLatex();
        //        pruebaSimpleLatexOld();
        //        testAll();
//        pruebaLaTeXVariasEcuaciones();
//        pruebaSimpleEcuacionEstatica();
//        pruebaSVGImport();
//        pruebaConcatAnimations();
//        arcoYCirculo();
//        vectores();
//        pruebaShrinkOut();
        muchosCuadradosApilados();

    }

    public void pruebaShrinkOut() {
        Shape sq = Shape.square().style("solidblue");
//        add(sq);
        waitSeconds(1);
        playAnimation(Commands.growIn(sq,1));
        waitSeconds(1);
    }

    public void vectores() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(1, 1);
        p2.visible = false;
        Arrow2D ar = Arrow2D.makeSimpleArrow2D(p1, p2, Arrow2D.TYPE_1);
        Shape c = Shape.circle();
        add(ar, c);
//        playAnim.shift(p2, -2, 0,3);
        for (int n = 0; n <= 100; n++) {
            Vec v = c.getPath().getPointAt((n * 1d) / 100).v;
            p2.v.copyFrom(v);
            advanceFrame();
        }
        waitSeconds(2);
    }

    public void arcoYCirculo() {
        Shape circle = Shape.circle().shift(.7, 0);
        Shape pol = Shape.regularPolygon(5).scale(.3);
        pol.getPath().getJMPoint(2).isThisSegmentVisible = false;
        add(circle, pol);
        waitSeconds(5);

    }

    public void pruebaLinea() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 1);
        Shape line = Shape.line(p1, p2).style("solidred");
        add(line);
        Shape circle = Shape.circle(p1, 1).setLayer(-1);
        add(circle.style("solidred"));

        p1.visible = false;
        p2.visible = false;
        play.shift(p2, 1, 0, 3);
        playAnimation(Commands.setStyle(circle, "solidblue", 3));
        playAnimation(Commands.setStyle(circle, "default", 3));
        play.shift(p1, 0, 1.5, 3);
        waitSeconds(3);
    }

    public void pruebaConcatAnimations() {
        Shape sh = Shape.square().fillColor(JMColor.BLUE);
        Shape sh2 = sh.copy().shift(1, 0);
        add(sh, sh2);
        waitSeconds(2);
        ApplyCommand sc = Commands.scale(sh, sh.getCenter(), 1.2, 1);
        sc.initialize();
        play.highlight(sh);
        playAnimation(new Highlight(sh2));
        sh.drawColor(JMColor.RED);
        waitSeconds(2);
    }

    public void pentagonBuild() {
        Shape pentagon = Shape.square().fillColor(JMColor.BLUE);
        Shape pentagonDst = Shape.regularPolygon(5, new Point(0, 0), .3).thickness(2).drawColor(JMColor.BLUE);
        add(pentagon);
        LaTeXMathObject texto = new LaTeXMathObject("Esto es un pentagono");
        texto.setRelativeSize();//TODO: Doesnt show anything
        texto.stackTo(pentagon, Anchor.LEFT);
        play.transform(pentagon, pentagonDst, 2);
        waitSeconds(1);
        play.zoomToRect(pentagon.getBoundingBox().addGap(.3, .3), 2);
        for (Point p : pentagon.jmpath.getPoints()) {
//            p.thickness(3);
            play.fadeIn(p, .3);
        }
        waitSeconds(5);
        for (int n = 0; n < 10; n += 2) {
            Shape s = Shape.segment(pentagon.getPoint(n), pentagon.getPoint(n + 2));
            s.thickness(2).drawColor(JMColor.RED);
            s.mp.dashStyle = MathObjectDrawingProperties.DASHED;
            play.fadeIn(s, 1);
        }
        waitSeconds(2);
        play.rotate(pentagon, Math.PI / 4, 2);
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
        playAnimation(tr);
//        waitSeconds(tiempo);
    }

    public synchronized void resetScene() {
        waitSeconds(3);
        JMathAnimScene scene = JMathAnimConfig.getConfig().getScene();
        scene.getObjects().clear();
        scene.getObjectsToBeUpdated().clear();

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
        sh.jmpath.addJMPoint(new JMPathPoint(new Point(.6, 1.2), false, JMPathPoint.TYPE_VERTEX));
        sh.jmpath.addJMPoint(new JMPathPoint(new Point(-.2, .4), true, JMPathPoint.TYPE_VERTEX));
        sh.jmpath.removeConsecutiveHiddenVertices();
        Shape sq = Shape.square().style("solidred");
        Shape arc = Shape.arc(Math.PI).style("solidred");
        playAnimation(new ShowCreation(arc, 3));
        waitSeconds(7);
        add(sh.scale(.5, .5).shift(-1, 0).style("solidblue"));
        Shape sh2 = sh.copy();
        waitSeconds(3);
        play.transform(sh, arc, 3);
        play.transform(sh, sh2, 3);
        waitSeconds(2);
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
        pa.drawColor(JMColor.GREEN);
        add(pa);
        waitSeconds(3d);
        playAnimation(new ShowCreation(pa, 3d));
        waitSeconds(3d);
        pa.jmpath.jmPathPoints.get(2).isThisSegmentVisible = false;
        waitSeconds(3d);
    }

    public void pruebaBoundingBox() {
        Shape circle = Shape.circle();
//        camera.setCenter(0, );
        add(circle);
        circle.label = "Circle";
        Rect r = circle.getBoundingBox();
        Shape segment = Shape.segment(new Point(r.xmin, r.ymin), new Point(r.xmax, r.ymax));
        segment.label = "Segment";
        add(segment);
        waitSeconds(3);
    }

    public void variosCirculos() {
        Shape c1 = Shape.circle(new Point(-1, 0), .5);
        c1.mp.thickness = .005d;
        c1.drawColor(JMColor.GREEN);
        add(c1);

        Shape c2 = Shape.circle(new Point(0, 0), .5);
        add(c2);
        Shape c3 = Shape.circle(new Point(1, 0), .5);
        add(c3);
        Shape c4 = Shape.circle(new Point(.5, 1), .5);
        add(c4);
//        RegularPolygon pol=new RegularPolygon(7, .3);
//        add(pol);

        waitSeconds(1);
        ArrayList<Animation> anims = new ArrayList<>();
        anims.add(new ShowCreation(c1, 3));
        anims.add(new ShowCreation(c2, 3));
        anims.add(new ShowCreation(c3, 3));
        anims.add(new ShowCreation(c4, 3));
        playAnimation(anims);
        waitSeconds(2);
    }

    private void CircleToSquare() {

        Shape c = Shape.circle(new Point(0, 0), 1);
        Shape pol2 = Shape.regularPolygon(4, new Point(0, 0), 1.5);
        pol2.shift(1, 0);
        pol2.getJMPoint(0).shift(-2d, .5d);
        pol2.getJMPoint(1).shift(-2d, 0);
        pol2.getJMPoint(0).isCurved = true;
        pol2.getJMPoint(1).isCurved = true;
        pol2.getJMPoint(2).isCurved = true;
        pol2.getJMPoint(3).isCurved = true;
        pol2.getPath().generateControlPoints();
        add(c, pol2);
        play.scaleCamera(2, 10);
        System.out.println("Orientation of circle: " + c.getPath().getOrientation());
        System.out.println("Orientation of polygon: " + pol2.getPath().getOrientation());

//        c.jmpath.alignPaths(pol2.jmpath);
//        c.jmpath.minimizeSquaredDistance(pol2.jmpath);
        Transform transform = new Transform(c, pol2, 45);
        //        transform.shouldOptimizePathsFirst=false;
//        transform.forceChangeDirection = true;
        waitSeconds(2);
        playAnimation(transform);
        waitSeconds(3);
    }

    public void pruebaCopia() {
        Shape c = Shape.circle(new Point(0, 0), 1);
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

    public void pruebaNewTransform2() {
        Shape pol = Shape.regularPolygon(5).shift(-1, -1);
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
        double tiempo = 2;
        waitSeconds(tiempo);
        Transform tr = new Transform(sq, pol, tiempo * 4);
        playAnimation(tr);

        waitSeconds(tiempo);
    }

    public void pruebaNewTransform() {
        Shape pol = Shape.regularPolygon(8).scale(.3);
        pol.getJMPoint(2).isThisSegmentVisible = false;
        pol.getJMPoint(6).isThisSegmentVisible = false;
        pol.getJMPoint(4).isThisSegmentVisible = false;
        add(pol.drawColor(JMColor.RED));
        Shape sq = Shape.square().shift(-1, -1).drawColor(JMColor.BLUE);
//        add(sq);
        double tiempo = 2;
        waitSeconds(tiempo);
        play.transform(pol, sq, tiempo);

        play.transform(pol, Shape.regularPolygon(8, new Point(0, 0), .3).drawColor(JMColor.GREEN).fillColor(JMColor.RED), tiempo);
        System.out.println("Pol:" + pol);
        waitSeconds(tiempo);
    }

    public void pruebaConnectedComponents() {
        Shape sq = Shape.regularPolygon(8).scale(.3);
        sq.getJMPoint(2).isThisSegmentVisible = false;
        sq.getJMPoint(6).isThisSegmentVisible = false;
        sq.getJMPoint(4).isThisSegmentVisible = false;
        add(sq);
        double tiempo = 40;
        waitSeconds(tiempo);
        CanonicalJMPath canonicalForm = sq.jmpath.canonicalForm();

        for (JMPath pa : canonicalForm.getPaths()) {
            Shape sh1 = new Shape(pa, null);
            play.shift(sh1, new Vec(-1, 0), tiempo / 2);
        }

        waitSeconds(tiempo);
        waitSeconds(tiempo);
        waitSeconds(tiempo);
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
        waitSeconds(1);
        playAnimation(new Transform(circ, circ2, 3));
        waitSeconds(2);
    }

    public void muchosCuadradosApilados() {
        Shape previous = null;
        int[] anchors = {Anchor.RIGHT, Anchor.LEFT, Anchor.UPPER, Anchor.LOWER};
        int anchor = -1;
        ArrayList objectsToZoomAt = new ArrayList();
        for (int n = 0; n < 20; n++) {
            Shape sq = Shape.square().drawColor(JMColor.BLACK).fillColor(JMColor.random()).scale(.3);
            if (previous != null) {

                int kk = (int) (Math.random() * 4);
                while (anchors[kk] == Anchor.reverseAnchorPoint(anchor)) {

                    kk = (int) (Math.random() * 4);
                }
                System.out.println("kk=" + anchors[kk] + ", Anchor: " + anchor);
                anchor = anchors[kk];

                sq.stackTo(previous, anchor);
                playAnimation(Commands.growIn(sq, 1));
                objectsToZoomAt.add(sq);
                anchor++;
                if (anchor > 4) {
                    anchor = 3;
                }
                play.adjustToObjects(objectsToZoomAt, 1);
//                waitSeconds(1);
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
        play.transform(pol1, pol2, 5);
        waitSeconds(3);
    }

    public void pruebaDosPentagonos1Estrella() {
        Shape p1 = Shape.regularPolygon(5).drawColor(JMColor.GRAY);
        Shape p2 = Shape.regularPolygon(5).copy().scale(1).drawColor(JMColor.GRAY);
        Point centro = p1.getCenter().copy();
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
        playAnimation(anim);
        waitSeconds(1);
        play.rotate(p2, centro, Math.PI * .2, 5);
        add(p2.getCenter().drawColor(JMColor.RED));
        waitSeconds(1);
        play.scale(p2, centro, .40, 2);
        waitSeconds(1);
        ApplyCommand f1 = Commands.fadeOut(p1,1);
        ApplyCommand f2 = Commands.fadeOut(p2,1);
        playAnimation(f1, f2);
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

    public void pruebaSimpleEcuacionEstatica() {
        LaTeXMathObject eq3 = new LaTeXMathObject("$$\\int_0^\\infty \\exp(-x)=1$$");
        camera.setGaps(.1, .1);
        eq3.setRelativeSize();
        eq3.get(0).fillAlpha(0);
        add(eq3);
//        final Rect boundingBox = eq3.getBoundingBox();
//        add(Shape.rectangle(boundingBox));
        camera.zoomToObjects(eq3);
//        eq3.fillAlpha(0);

        waitSeconds(5);
    }

    public void pruebaLaTeXVariasEcuaciones() {
        LaTeXMathObject eq1 = new LaTeXMathObject("$$\\int_0^\\infty \\exp(-x)\\,dx=1$$");
        LaTeXMathObject eq2 = new LaTeXMathObject("$$\\int_0^\\infty \\exp(-y)\\,dy=1$$");
        LaTeXMathObject eq3 = new LaTeXMathObject("$$\\int_0^\\infty \\exp(-z)\\,dz=1$$");
        add(eq1);
        eq1.setRelativeSize();
        eq2.setRelativeSize();
        eq3.setRelativeSize();
        camera.setGaps(.2, .2);
        camera.adjustToObjects(eq1);
        waitSeconds(1);
        eq1.get(11).fillColor(JMColor.GREEN);
        eq1.get(8).fillColor(JMColor.GREEN);
        eq2.get(11).fillColor(JMColor.BLUE);
        eq2.get(8).fillColor(JMColor.BLUE);
        eq3.get(11).fillColor(JMColor.RED);
        eq3.get(8).fillColor(JMColor.RED);
        Transform tr1 = new Transform(eq1.get(11), eq2.get(11), 2);
        Transform tr2 = new Transform(eq1.get(8), eq2.get(8), 2);
        playAnimation(tr1, tr2);
        waitSeconds(1);
        tr1 = new Transform(eq1.get(11), eq3.get(11), 2);
        tr2 = new Transform(eq1.get(8), eq3.get(8), 2);
        playAnimation(tr1, tr2);
        waitSeconds(1);
        play.scaleCamera(2, 2);
        waitSeconds(1);
        play.highlight(eq1);
        waitSeconds(1);
    }

    public void pruebaSimpleLatex() {
        LaTeXMathObject eq1 = new LaTeXMathObject("$$8$$");
        LaTeXMathObject eq2 = new LaTeXMathObject("$$\\infty$$");
        add(eq1, eq2);
        eq1.setRelativeSize();
        eq2.setRelativeSize();
        eq1.scale(3);
        eq2.scale(3).shift(1, 0);
        waitSeconds(1);
        play.transform(eq1.get(0), eq2.get(0), 5);
        waitSeconds(10);
    }

    public void pruebaSimpleLatexOld() {
        LaTeXMathObject eq1 = new LaTeXMathObject("$$8$$");
        LaTeXMathObject eq2 = new LaTeXMathObject("$$\\infty$$");
        CanonicalJMPath c1 = eq1.get(0).jmpath.canonicalForm();
        CanonicalJMPath c2 = eq2.get(0).jmpath.canonicalForm();
        PointInterpolationCanonical tr = new PointInterpolationCanonical();
//        tr.alignNumberOfComponents(c1, c2);
        Shape sh1 = new Shape(c1.toJMPath(), null).scale(3);
        sh1.fillColor(JMColor.WHITE);
        add(sh1);
        waitSeconds(10);
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

        playAnimation(new Transform(x1, x2, tiempo));
        playAnimation(new Transform(x1, x3, tiempo));
        playAnimation(new Transform(x1, x4, tiempo));
        waitSeconds(3);
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
        Shape s1 = Shape.segment(B, A);
        Shape s2 = Shape.segment(C, D);
        add(s1, s2);
        Transform tr = new Transform(s1, s2, tiempo);
//        tr.setMethod(Transform.METHOD_INTERPOLATE_POINT_BY_POINT);
//        tr.shouldOptimizePathsFirst = false;
        playAnimation(tr);
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
        Shape pol = Shape.polygon(A, B, C);
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

        Shape pol = Shape.regularPolygon(5);
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
        Shape P = Shape.segment(new Point(0, 0), new Point(2, 1));
        Line L = new Line(new Point(0, 0), new Point(1, -1));
        Shape P2 = P.copy();
        add(P);
        Shape sq = Shape.square();
        P.shift(1, 0);
//        playShift(P, new Vec(.03, 0), 6);

//        playScale(P, new Point(), .3d, 6);
//        playAnim.rotate(P, new Point(), 2.5, 6);
//        add(sq);
//        playAnim.transform(Shape.circle(), sq, 15);
        System.out.println("Orientation square " + sq.jmpath.getOrientation());
        System.out.println("Orientation segment " + P.jmpath.getOrientation());
        Transform tr = new Transform(P, sq, 15);
//        Transform tr = new Transform(sq, P, 15);
        tr.shouldOptimizePathsFirst = false;
        playAnimation(tr);
        Transform tr2 = new Transform(P, P2, 15);
        tr2.setMethod(Transform.METHOD_INTERPOLATE_POINT_BY_POINT);
        playAnimation(tr2);
        System.out.println("End! " + P.jmpath);
        waitSeconds(20);
    }

    public void pruebaGirosCompuestos() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 1);
        Shape s1 = Shape.segment(p1, p2);
        Shape pol = Shape.regularPolygon(5);
        add(pol);
        LaTeXMathObject texto = new LaTeXMathObject("$x^2$");
        add(texto);
        texto.scale(.2, .2);

        Shape sq = Shape.square();
        add(sq);
        AnchoredMathObject anchor = new AnchoredMathObject(texto, Anchor.RIGHT, p2);
        AnchoredMathObject anchor2 = new AnchoredMathObject(pol, Anchor.BY_CENTER, texto, Anchor.LEFT);
        AnchoredMathObject anchor3 = new AnchoredMathObject(sq, Anchor.LEFT, pol, Anchor.RIGHT);
        registerObjectToBeUpdated(anchor);
        registerObjectToBeUpdated(anchor2);
        registerObjectToBeUpdated(anchor3);
        s1.label = "S1";
        ApplyCommand cmd1 = Commands.rotate(s1, p1, 2 * Math.PI, 20);
        playAnimation(cmd1);
    }

    void teselacionHexagonos() {
        Shape hex1 = Shape.regularPolygon(6).scale(.3);
        add(hex1);
//        hex1.shift(-1,0);
//        getCamera().setCenter(hex1.getCenter().v.x,hex1.getCenter().v.y);
        Shape hex2 = hex1.copy();
        add(hex2);
        Shape lado = Shape.segment(hex2.getPoint(0), hex2.getPoint(5));
        ApplyCommand cmd = Commands.reflectionByAxis(hex2, lado, 3);
        playAnimation(cmd);

        int n1 = 0;
        int n2 = 1;
        for (int n = 0; n < 5; n++) {
            hex2 = hex2.copy();
//            lado = new Segment(hex2.getPoint(n1), hex2.getPoint(n2));
            cmd = Commands.rotate(hex2, hex2.getPoint(n), Math.PI * 2 / 3, 1);
            //            SingleMathObjectCommand tr = Commands.homotopy(hex2, hex2.getJMPoint(0).p, hex2.getJMPoint(1).p, hex1.getJMPoint(n).p, hex1.getJMPoint(n+1).p);
            playAnimation(cmd);
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
        playAnimation(cmd);
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
        play.rotate(rect, rect.getCenter(), Math.PI / 3, 5);
//      
//        playAnim.transform(sq, sq2, 10);
//        sq.setObjectType(MathObject.OTHER);
        play.transform(sq, rect, 40);
        play.transform(sq, sq3, 10);
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
        Shape pol = Shape.regularPolygon(5);

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

        play.zoomToRect(pol.getBoundingBox(), 3 * timeScale);

//        double yCenter = camera.getMathBoundaries().getCenter().v.y;
//        for (double dx = 0; dx < 2; dx += .01) {
//            camera.setMathXY(-5 + .5 * dx, 5 - .5 * dx, yCenter);
//            advanceFrame();
//        }
        waitSeconds(3 * timeScale);
        play.scaleCamera(1.2, 10);

        play.rotate(pol, pol.getCenter(), Math.PI / 3, 10);
        play.scaleCamera(.9, 10);
        play.scaleCamera(2, 10);
        play.scaleCamera(5, 10);
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
        SVGMathObject svg = new SVGMathObject("C:\\media\\bolondro.svg");
        add(svg.get(0));
        svg.get(0).fillColor(JMColor.RED);
        svg.putAt(new Point(0, 0), Anchor.BY_CENTER);
        camera.adjustToObjects(svg);
//        add(svg.shapes.get(1));
//        add(svg.shapes.get(2));
        svg.drawColor(JMColor.WHITE);

        Shape s = Shape.square();
        add(s);
//        JMPath uno = s.jmpath;
//        JMPath dos = svg.shapes.get(0).jmpath;
        waitSeconds(3);
        play.transform(svg.get(0), s, 26);
        waitSeconds(3);
//        playAnim.transform(svg.get(0),s, 100); 

    }

    public void pruebaVisible() {
        Shape pol = Shape.regularPolygon(6);
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

        play.shift(p, new Vec(0, 2), 15);//TODO: How to easily specify that don't show this object?
        waitSeconds(30);
    }

}

//Cookbook:
//Procesa manualmente animacion y mueve al mismo tiempo una figura
//while (!anim2.processAnimation(fps)) {
//            regPolyg.shift(new Vec(0, dx, 0));
//            advanceFrame();
//        }
