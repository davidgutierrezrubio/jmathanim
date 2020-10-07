/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import com.jmathanim.Animations.ApplyCommand;
import com.jmathanim.Animations.Concatenate;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.Strategies.ShowCreation.FirstDrawThenFillStrategy;
import static com.jmathanim.Animations.Strategies.ShowCreation.FirstDrawThenFillStrategy.PERCENT_TO_DIVIDE_ANIMATION;
import com.jmathanim.Animations.WaitAnimation;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.CanonicalJMPath;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

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
//        ConfigLoader.parseFile("preview.xml");
//        ConfigLoader.parseFile("dark.xml");
//        conf.setHighQuality();
    }
    
    @Override
    public void runSketch() {
//        SVGMathObject s = new SVGMathObject("c:\\media\\inkscape\\dibujo.svg");
        Shape s = Shape.square();
//        s.stackTo(Anchor.BY_CENTER);
//        JMPath c = s.get(0).getPath();
//        s.get(0).style("default");
//        add(s);
//        play.adjustToObjects(s);
//        camera.scale(3);
        play.showCreation(s);
                Shape a=null;
        a.draw(renderer);
        waitSeconds(5);
    }
    
    private void creationMultishape() {
        Shape c = Shape.circle();
        Shape sq = Shape.square();
        Shape tr = Shape.regularPolygon(3);
        MultiShapeObject m = new MultiShapeObject();
        m.addShape(c);
        m.addShape(sq);
        m.addShape(tr);
        final ShowCreation sc = new ShowCreation(m, 2);
        
        playAnimation(sc);
        
        waitSeconds(5);
    }
    
    private void createLatex() {
        LaTeXMathObject t = new LaTeXMathObject("This is a test of how much a long paragraph should be drawn");
//        LaTeXMathObject t = new LaTeXMathObject("$$\\int_0^\\infty x\\,dx\\geq\\pi$$");
//        LaTeXMathObject t=new LaTeXMathObject("$${\\color{red}x}+\\color{blue}1$$");
        t.scale(1).stackTo(Anchor.LEFT, .2, .2);
        ShowCreation sc = new ShowCreation(t, 10);
        playAnimation(sc);
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

        play.transform(Shape.segment(l), s, 5);
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
            final Shape lineaV = new Line(Point.make(xx, 0), Point.make(xx, 1)).style("axisblue1");
            axes.addShape(lineaV);
            final Shape lineaH = new Line(Point.make(0, xx), Point.make(1, xx)).style("axisblue2");
            axes.addShape(lineaH);
        }
//        add(axes);
//        add(Shape.circle());
        Shape lineaPrueba = new Line(Point.make(0, -1.5), Point.make(2, -1.5)).drawColor(JMColor.RED);
//        waitSeconds(3);
        Point a = Point.make(0, 0);
        Point b = Point.make(1, 0);
        Point c = Point.make(0, 1);
        Point d = Point.make(0, 0);
        Point e = Point.make(1, 0);
        Point f = Point.make(1, 1);
        playAnimation(Commands.affineTransform(axes, a, b, c, d, e, f, 5));
        play.rotate(axes, PI / 3, 5);
        play.scaleCamera(.5, 3);
        playAnimation(Commands.reflectionByAxis(axes, new Point(0, 0), new Point(0, 1), 5));
// Lineas chungas

        waitSeconds(3);
    }
    
}
