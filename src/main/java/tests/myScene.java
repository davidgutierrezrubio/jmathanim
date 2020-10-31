/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package tests;

import com.jmathanim.Animations.ApplyCommand;
import com.jmathanim.Animations.Strategies.Transform.PointInterpolationCanonical;
import com.jmathanim.Animations.Transform;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Renderers.JavaFXRenderer;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.Utils.MODrawProperties.DashStyle;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.Arrow2D.ArrowType;
import com.jmathanim.mathobjects.Axes;
import com.jmathanim.mathobjects.CanonicalJMPath;
import com.jmathanim.mathobjects.FunctionGraph;
import com.jmathanim.mathobjects.JMImage;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.updateableObjects.CameraAlwaysAdjusting;
import com.jmathanim.mathobjects.updateableObjects.PointOnFunctionGraph;
import java.util.ArrayList;
import javafx.scene.shape.StrokeLineCap;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class myScene extends Scene2D {

    @Override
    public void setupSketch() {
//        conf.setResourcesDir(".");
//        conf.setOutputDir("c:\\media");
//        ConfigLoader.parseFile("production.xml");
        ConfigLoader.parseFile("preview.xml");
        ConfigLoader.parseFile("dark.xml");
//        ConfigLoader.parseFile("axes_and_functions_light.xml");
//        conf.setHighQuality();
//        conf.setCreateMovie(true);
    }
    @Override
    public void runSketch() {
      pruebaSVGImport();
        
    }

    public void variosThickness() {
        double th = 1;
        int nmax = 4;
        for (double n = 0; n < nmax; n++) {
            Shape s = Shape.segment(Point.at(-1.5 + n * 2d / nmax, -1), Vec.to(0, 1)).thickness(th).shift(.3, 0);
            s.mp.absoluteThickness = false;
            LaTeXMathObject t = LaTeXMathObject.make("$" + th + "$");
            t.stackTo(s, Anchor.LOWER);
            th += 1;
            add(s, t);
        }
        waitSeconds(1);
        play.cameraScale(5, .2);
        waitSeconds(3);
    }

    public void pruebaAbsoluteThickness() {
        Shape c = Shape.circle().style("solidblue").shift(-1, 0);
        Shape c2 = Shape.circle().style("solidblue").shift(1, 0);
        c2.mp.absoluteThickness = false;
//        c2.scale(.5);
        play.scale(5, .5, c, c2);
//        System.out.println(c.mp.thickness + ",  " + c2.mp.thickness);
        play.showCreation(c, c2);

        waitSeconds(3);
    }

    public void pruebaSVGImport() {
        camera.scale(2);
        SVGMathObject balanza = new SVGMathObject("C:\\media\\Balanza3.svg");
        balanza.setHeight(3);
        balanza.drawColor(JMColor.RED);
        balanza.fillColor(JMColor.RED);

        balanza.stackToScreen(Anchor.BY_CENTER);

//        SVGMathObject candy = new SVGMathObject("C:\\media\\candy.svg");
//        candy.scale(.01);
//        candy.stackToScreen(Anchor.RIGHT);

        play.showCreation(balanza);
        play.scale(5, .5,  balanza);

        waitSeconds(3);
    }

    public void JMImage() {
        JMImage img = new JMImage("c:\\media\\math.png");
        Point p = Point.at(.3, .1);
        Shape c = Shape.arc(120 * DEGREES).style("solidblue");
        play.fadeIn(img, c);
        img.stackTo(c, Anchor.RIGHT);
        add(p.style("dotBlueCross"));
        play.scale(5, .3, img, c);
        img.rotate(15 * DEGREES);
        waitSeconds(.5);
        img.rotate(30 * DEGREES);
        waitSeconds(.5);
        img.rotate(45 * DEGREES);
        waitSeconds(.5);

        play.rotate(5, img.getCenter(), 45 * DEGREES, img, c);
        play.fadeOutAll();
        waitSeconds(3);
    }

    private void regularPolygons() {
        Shape reg1 = Shape.regularPolygon(6).scale(.7, -1).shift(-1, -1.1).rotate(6 * PI / 5);
        Shape reg2 = Shape.regularPolygon(5).scale(.4).shift(1, 0);
        add(reg2, reg1);
        playAnimation(Transform.make(10, reg1, reg2).optimizePaths(true));
        waitSeconds(2);
        play.fadeOutAll();

        Shape c = Shape.circle();
        Shape sq = Shape.square().scale(-1, 1).rotate(60 * DEGREES).shift(1.5, -.2);
        add(c, sq);
        waitSeconds(1);
        playAnimation(Transform.make(10, c, sq).optimizePaths(true));
        waitSeconds(2);
    }

    public void manual3DRotateCamera() {
        Shape c = Shape.circle().style("solidblue");
        add(c);
        waitSeconds(1);
        int nfps = 300;

        for (int n = 0; n <= nfps; n++) {
            renderer.FxCamerarotateX = 45d * n / nfps;
            renderer.FxCamerarotateY = 45d * n / nfps;
            renderer.FxCamerarotateZ = 45d * n / nfps;
            advanceFrame();
        }
        waitSeconds(3);
    }

    public void TaylorSeriesSin() {
        final Axes axes = new Axes();
        axes.xAxis.mp.castShadows = false;
        axes.yAxis.mp.castShadows = false;
        add(axes);
        double xmin = -2 * PI;
        double xmax = 2 * PI;
        FunctionGraph sin = new FunctionGraph((x) -> Math.sin(x), xmin, xmax);
        FunctionGraph sin1 = new FunctionGraph((x) -> x, xmin, xmax).style("taylor");
        FunctionGraph sin2 = new FunctionGraph((x) -> x - Math.pow(x, 3) / factorial(3), xmin, xmax).style("taylor");
        FunctionGraph sin3 = new FunctionGraph((x) -> x - Math.pow(x, 3) / factorial(3) + Math.pow(x, 5) / factorial(5), xmin, xmax).style("taylor");
        FunctionGraph sin4 = new FunctionGraph((x) -> x - Math.pow(x, 3) / factorial(3) + Math.pow(x, 5) / factorial(5) - Math.pow(x, 7) / factorial(7), xmin, xmax).style("taylor");
        FunctionGraph sin5 = new FunctionGraph((x) -> x - Math.pow(x, 3) / factorial(3) + Math.pow(x, 5) / factorial(5) - Math.pow(x, 7) / factorial(7) + Math.pow(x, 9) / factorial(9), xmin, xmax).style("taylor");
        FunctionGraph sin6 = new FunctionGraph((x) -> x - Math.pow(x, 3) / factorial(3) + Math.pow(x, 5) / factorial(5) - Math.pow(x, 7) / factorial(7) + Math.pow(x, 9) / factorial(9) - Math.pow(x, 11) / factorial(11), xmin, xmax).style("taylor");
        FunctionGraph sin7 = new FunctionGraph((x) -> x - Math.pow(x, 3) / factorial(3) + Math.pow(x, 5) / factorial(5) - Math.pow(x, 7) / factorial(7) + Math.pow(x, 9) / factorial(9) - Math.pow(x, 11) / factorial(11) + Math.pow(x, 13) / factorial(13), xmin, xmax).style("taylor");

        play.showCreation(sin);
        play.showCreation(sin1);
        play.adjustToObjects(sin);
//        PointOnFunctionGraph pg = new PointOnFunctionGraph(fg);
//        pg.shift(-.5, 0);
//        pg.v.x = 0;
//        add(Shape.segment(pg, pg.slopePointRight).drawColor(JMColor.BLUE).thickness(.5));
//        add(Shape.segment(pg, pg.slopePointLeft).drawColor(JMColor.RED).thickness(.5));
//        play.shift(5, 1, 1, pg);
        waitSeconds(1);
        play.transform(1, sin1, sin2);
        waitSeconds(1);
        play.transform(1, sin1, sin3);
        waitSeconds(1);
        play.transform(1, sin1, sin4);
        waitSeconds(1);
        play.transform(1, sin1, sin5);
        waitSeconds(1);
        play.transform(1, sin1, sin6);
        waitSeconds(1);
        play.transform(1, sin1, sin7);
        waitSeconds(1);
    }

    private void drawCircleAndMarco() {
        final Shape c = Shape.circle();
        c.style("solidblue");
        c.dashStyle(DashStyle.DASHED);
        add(c);
//        Shape c = Shape.segment(Point.at(0,0),Point.at(1,1));
        Shape cr = Shape.rectangle(camera.getMathView());
        cr.dashStyle(DashStyle.DOTTED);
        cr.thickness(1);
        cr.drawColor(JMColor.RED);
        cr.mp.castShadows = false;

//        add(c,cr);
//        play.transform(5, c, Shape.square());
        play.showCreation(c, cr);
        play.cameraShift(3, 1, 0);
        play.cameraScale(3, .2);
    }

    private void absoluteThings() {

        Arrow2D ar = Arrow2D.makeSimpleArrow2D(Point.at(0, 1), Point.at(0, 0), ArrowType.TYPE_3);
        Arrow2D ar2 = Arrow2D.makeSimpleArrow2D(Point.at(-1, -.61), Point.at(-.1, -.1), ArrowType.TYPE_1);
//        Shape s = Shape.segment(Point.at(0, 1), Point.at(0, 0));
        Shape c = Shape.circle().drawColor(JMColor.RED);
//        ar.thickness(3);
        add(ar, ar2, c);
        System.out.println("1");
        LaTeXMathObject lat = LaTeXMathObject.make("Absolutely!").scale(2).stackToScreen(Anchor.BY_CENTER);
        LaTeXMathObject lat2 = LaTeXMathObject.make("Relatively!").scale(1).stackToScreen(Anchor.BY_CENTER);
        System.out.println("2");
        add(lat, lat2);
        lat.setAbsoluteSize(Anchor.BY_CENTER);
        play.cameraScale(3, .2);
        System.out.println("3");
        play.highlight(lat);
        waitSeconds(3);
    }

    public void manyDots() {
        for (int n = 0; n < 15; n++) {
            Point p = Point.random();
            p.dotStyle(Point.DotSyle.CROSS);
            p.thickness(.5);
            System.out.println("n " + n);
//            p.thickness(1 + Math.random() * 1);
            p.drawColor(JMColor.random());
            play.fadeIn(.3, p);
        }
        System.out.println("2");
        waitSeconds(1);
        play.cameraScale(3, 5);

    }

    private void Big2() {
        LaTeXMathObject lat2 = new LaTeXMathObject("$2+x$");
        LaTeXMathObject lat = new LaTeXMathObject("$5+x$");
        lat.stackToScreen(Anchor.BY_CENTER);
        lat2.stackToScreen(Anchor.BY_CENTER);
        final JMPath pa = lat.get(0).getPath().rawCopy();
        add(lat);
        camera.zoomToObjects(lat);
        final Shape sh = lat.get(0);
        JMPath jm = sh.getPath();
//        Transform aa = new Transform(5, lat.get(0), lat2.get(0));
//        playAnimation(aa);
        PointInterpolationCanonical can = new PointInterpolationCanonical(sh, lat2.get(0), this);
        can.prepareObjects();
        System.out.println("sizes before/after  " + pa.size() + "   " + jm.size());
        for (int n = 0; n < jm.size(); n++) {
            System.out.println(n + "before/after  : " + pa.getJMPoint(n) + "     " + jm.getJMPoint(n));
        }

        waitSeconds(5);
    }

    private void tran() {
        LaTeXMathObject lat = new LaTeXMathObject("$8+y$");
        LaTeXMathObject lat2 = new LaTeXMathObject("$x+y$");
        lat.stackToScreen(Anchor.BY_CENTER);
        lat2.stackToScreen(Anchor.BY_CENTER);

        add(lat2);
        camera.zoomToObjects(lat);
        add(Shape.circle().scale(.1).stackToScreen(Anchor.RIGHT));
//        playAnimation(Commands.setStyle(.3, "latextransparent",lat.get(0), lat2.get(0)));
        play.transform(5, lat2.get(0), lat.get(0));
//        playAnimation(Commands.setStyle(.3, "latexdefault",lat.get(0), lat2.get(0)));
        waitSeconds(3);
    }

    private void creationMultishape() {
        Shape c = Shape.circle();
        Shape sq = Shape.square();
        Shape tr = Shape.regularPolygon(3);
        MultiShapeObject m = new MultiShapeObject();
        m.addShape(c);
        m.addShape(sq);
        m.addShape(tr);
        play.showCreation(m);

        waitSeconds(5);
    }

    private void createLatex() {
        LaTeXMathObject t = new LaTeXMathObject("This is a test of how much a long paragraph should be drawn");
//        LaTeXMathObject t = new LaTeXMathObject("$$\\int_0^\\infty x\\,dx\\geq\\pi$$");
//        LaTeXMathObject t=new LaTeXMathObject("$${\\color{red}x}+\\color{blue}1$$");
        t.scale(1).stackToScreen(Anchor.LEFT, .2, .2);
        play.showCreation(10, t);
        waitSeconds(5);
    }

    public void circuloCanonical() {
        Shape c = Shape.circle();//.rotate(180*DEGREES);
        CanonicalJMPath canonicalForm = c.getPath().canonicalForm();
        final JMPath pa = c.getPath().canonicalForm().subpath(0, .9995);
        Shape s = new Shape(pa);
        add(s);
        s.rotate(90 * DEGREES);
//        s.getPoint(4).shift(-.5,0);
//        play.rotate(s, 2*PI, 20);
        ArrayList<Point> points = s.getPath().getPoints();
        for (Point p : points) {
            add(Shape.circle().scale(.005).shift(p.v).fillColor(JMColor.RED).drawColor(JMColor.random()));
            System.out.println("" + p.v.x + ", " + p.v.y);
        }
        waitSeconds(5);
    }

    private void lineToSegment() {
        Line l = Line.XYBisector();
//        add(l);
        Shape s = Shape.segment(new Point(1, -.6), new Point(1.3, 0));
        add(s.drawColor(JMColor.RED));

        play.transform(5, Shape.segment(l), s);
        waitSeconds(3);
    }

    public void theBigB() {
        LaTeXMathObject B = new LaTeXMathObject("B");
        CanonicalJMPath ms = B.shapes.get(0).getPath().canonicalForm();//.createMultiShape(B.mp.copy());
        Shape s1 = new Shape(ms.get(0), JMathAnimConfig.getConfig().getDefaultMP());
        Shape s2 = new Shape(ms.get(1), JMathAnimConfig.getConfig().getDefaultMP());
        Shape s3 = new Shape(ms.get(2), JMathAnimConfig.getConfig().getDefaultMP());
        B.shapes.get(0).setMp(JMathAnimConfig.getConfig().getDefaultMP());
        add(B.shapes.get(0).fillAlpha(0));
//        B.fillAlpha(0).drawColor(JMColor.RED).thickness(1);
        camera.adjustToObjects(B);
        camera.scale(1.5);
//        playAnimation(new ShowCreation(s,5));
//        play.fadeIn(B);
//        add(s1.drawColor(JMColor.RED));
//        add(s2.drawColor(JMColor.BLUE));
//        add(s3.drawColor(JMColor.GREEN));

//        s.drawColor(JMColor.RED);
//s.fillColor(JMColor.WHITE);
//        for (double dt=0;dt<=1;dt+=.01)
//        {
//            s.fillAlpha(dt);
//            advanceFrame();
//        }
        waitSeconds(5);
    }

    private void transformAxes() {
        //Create axis
        MultiShapeObject axes = new MultiShapeObject();
        for (double xx = -10; xx < 10; xx += .3) {
            final Shape lineaV = new Line(Point.at(xx, 0), Point.at(xx, 1)).style("axisblue1");
            axes.addShape(lineaV);
            final Shape lineaH = new Line(Point.at(0, xx), Point.at(1, xx)).style("axisblue2");
            axes.addShape(lineaH);
        }
//        add(axes);
//        add(Shape.circle());
        Shape lineaPrueba = new Line(Point.at(0, -1.5), Point.at(2, -1.5)).drawColor(JMColor.RED);
//        waitSeconds(3);
        Point a = Point.at(0, 0);
        Point b = Point.at(1, 0);
        Point c = Point.at(0, 1);
        Point d = Point.at(0, 0);
        Point e = Point.at(1, 0);
        Point f = Point.at(1, 1);
        playAnimation(Commands.affineTransform(5, a, b, c, d, e, f, axes));
        play.rotate(5, PI / 3, axes);
        play.cameraScale(.5, 3);
        playAnimation(Commands.reflectionByAxis(5, new Point(0, 0), new Point(0, 1), axes));
// Lineas chungas

        waitSeconds(3);
    }

    private void teoremaPitagoras() {
        ConfigLoader.parseFile("pitagoras.xml");
        drawPitagorasProof(0, true, 2);
        waitSeconds(5);
        play.fadeOutAll();
        drawPitagorasProof(-.7, false, 2);
        waitSeconds(5);
        play.fadeOutAll();
        drawPitagorasProof(1, false, 2);
        waitSeconds(5);
        play.fadeOutAll();
        
    }

    public void drawPitagorasProof(double xt,boolean correct,double animationTime) {
        //Triangle
        Shape triangle1 = Shape.polygon(Point.at(0, 0), Point.at(3, 0), Point.at(xt, 4));
        triangle1.style("triangulo").layer(3);
        //        CameraAlwaysAdjusting c = new CameraAlwaysAdjusting(camera,.1,.1);
//        registerObjectToBeUpdated(c);
        camera.setCenter(5.3, 1.3);
        camera.setWidth(26.156);
        Shape sqC1 = Shape.square().style("cateto").layer(1);
        Shape sqC2 = Shape.square().style("cateto").layer(1);
        Shape sqHip = Shape.square().style("hipotenusa").layer(1);
        LaTeXMathObject aSquared = LaTeXMathObject.make("$a^2$").setHeight(.5).putAt(sqC1.getCenter(), Anchor.BY_CENTER).layer(2).rotate(-90 * DEGREES);
        LaTeXMathObject bSquared = LaTeXMathObject.make("$b^2$").setHeight(.5).putAt(sqC2.getCenter(), Anchor.BY_CENTER).layer(2).rotate(180 * DEGREES);
        double angle = -triangle1.getPoint(2).to(triangle1.getPoint(1)).getAngle();
        LaTeXMathObject cSquared = LaTeXMathObject.make("$c^2$").setHeight(.5).putAt(sqHip.getCenter(), Anchor.BY_CENTER).layer(2).rotate(angle);
//        add(aSquared, bSquared, cSquared);
        play.showCreation(animationTime, triangle1);
        ApplyCommand com1 = Commands.homothecy(3, sqC1.getPoint(0), sqC1.getPoint(1), triangle1.getPoint(0), triangle1.getPoint(2), sqC1, aSquared);
        ApplyCommand com2 = Commands.homothecy(3, sqC2.getPoint(0), sqC2.getPoint(1), triangle1.getPoint(1), triangle1.getPoint(0), sqC2, bSquared);
        ApplyCommand com3 = Commands.homothecy(3, sqHip.getPoint(0), sqHip.getPoint(1), triangle1.getPoint(2), triangle1.getPoint(1), sqHip, cSquared);
        playAnimation(com1, com2, com3);
        waitSeconds(1);
        play.shift(animationTime, 10, sqC2.getBoundingBox().ymin - sqHip.getBoundingBox().ymin, sqHip, cSquared);
        Shape triangle2 = triangle1.copy();
        playAnimation(Commands.homothecy(animationTime, triangle2.getPoint(1), triangle2.getPoint(2), triangle1.getPoint(2), triangle1.getPoint(1), triangle2));
        Shape triangle3 = triangle1.copy();
        Shape triangle4 = triangle2.copy();
        playAnimation(Commands.homothecy(animationTime, triangle1.getPoint(0), triangle1.getPoint(2), sqC1.getPoint(3), sqC1.getPoint(0), triangle3, triangle4));
//Triangles from hypothenuse squared
        Shape triangle5 = triangle2.copy();
        playAnimation(Commands.homothecy(animationTime, triangle5.getPoint(1), triangle5.getPoint(2), sqHip.getPoint(0), sqHip.getPoint(3), triangle5));
        Shape triangle6 = triangle5.copy();
        playAnimation(Commands.homothecy(animationTime, triangle6.getPoint(1), triangle6.getPoint(2), sqHip.getPoint(3), sqHip.getPoint(2), triangle6));
        Shape triangle7 = triangle6.copy();
        playAnimation(Commands.homothecy(animationTime, triangle7.getPoint(1), triangle7.getPoint(2), sqHip.getPoint(2), sqHip.getPoint(1), triangle7));
        Shape triangle8 = triangle7.copy();
        playAnimation(Commands.homothecy(animationTime, triangle8.getPoint(1), triangle8.getPoint(2), sqHip.getPoint(1), sqHip.getPoint(0), triangle8));
        MultiShapeObject msh = new MultiShapeObject();
        for (MathObject obj : this.getObjects()) {
            if (obj instanceof Shape) {
                msh.addShape((Shape) obj);
            }
        }
        Point center = msh.getBoundingBox().getCenter();
        System.out.println(camera.getMathView());
        System.out.println(camera.getMathView().getCenter());
        System.out.println(camera.getMathView().getWidth());
//Create balance
        SVGMathObject balance = SVGMathObject.make("c:\\media\\balanza3.svg");
        balance.fillColor(JMColor.parseColorID("#da6d42"));
        balance.setHeight(20).stackTo(center, Anchor.BY_CENTER);
        balance.shift(0, -.5).layer(0);
        play.fadeIn(balance);
        

        if (correct) {//Pitagoras correct
            play.adjustCameraToAllObjects();
            play.fadeOut(animationTime, triangle1, triangle8);
            play.fadeOut(animationTime, triangle2, triangle6);
            play.fadeOut(animationTime, triangle3, triangle5);
            play.fadeOut(animationTime, triangle4, triangle7);
        } else {
            Rect bbox = balance.getBoundingBox();
            Shape s1 = Shape.segment(bbox.getUL(), bbox.getDR()).scale(.5).linecap(StrokeLineCap.BUTT).drawColor(JMColor.RED).layer(Integer.MAX_VALUE);
            Shape s2 = Shape.segment(bbox.getUR(), bbox.getDL()).scale(.5).linecap(StrokeLineCap.BUTT).drawColor(JMColor.RED).layer(Integer.MAX_VALUE);
            double longi = .25 * s1.getPoint(0).to(s1.getPoint(1)).norm();
            double width = renderer.getThicknessForMathWidth(longi);
            s1.thickness(width).mp.absoluteThickness = false;
            s2.thickness(width).mp.absoluteThickness = false;
            play.showCreation(.25, s1);
            play.showCreation(.25, s2);
        }
    }

    public void pruebaThickness() {
        camera.scale(5);
        final Point B = Point.at(0, .5).drawColor(JMColor.BLACK);
        final Point A = Point.at(0, -.5).drawColor(JMColor.BLACK);
        camera.scale(.5);
        Shape s1 = Shape.segment(A, B).linecap(StrokeLineCap.BUTT);
        double longi = s1.getPoint(0).to(s1.getPoint(1)).norm();
        double width = renderer.getThicknessForMathWidth(longi);
        s1.thickness(width);
        s1.mp.absoluteThickness = false;
        add(s1); //3.4???
        add(A, B);
        waitSeconds(3);
        play.scale(4, .5, s1);
        play.rotate(4, 90 * DEGREES, s1);
        waitSeconds(3);
    }

    public int factorial(int n) {
        int resul = 1;
        for (int i = 1; i <= n; i++) {
            resul *= i;
        }
        return resul;
    }
}
