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

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.ApplyCommand;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.ShowCreation.ShowCreationStrategy;
import com.jmathanim.Animations.Transform;
import com.jmathanim.Animations.Transform.TransformMethod;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.JMImage;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Point.DotSyle;
import com.jmathanim.mathobjects.Shape;
import com.sun.webkit.ContextMenu;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class docDrawings extends Scene2D {

    @Override
    public void setupSketch() {
        ConfigLoader.parseFile("preview.xml");
        ConfigLoader.parseFile("light.xml");
        conf.setCreateMovie(true);
    }

    @Override
    public void runSketch() throws Exception {
        Shape circle = Shape.circle().scale(-1, 1).scale(.6).shift(-.5, .3);
        Shape circle2 = circle.copy();
        Shape square = Shape.square().shift(.5, 0).scale(.6).rotate(45*DEGREES);
        add(LaTeXMathObject.make("With optimization").stackToScreen(Anchor.LOWER, .1, .1));
        Transform tr = new Transform(3, circle, square);
        playAnimation(tr);
        waitSeconds(1);
        play.fadeOutAll();
        add(LaTeXMathObject.make("Without optimization").stackToScreen(Anchor.LOWER, .1, .1));
        Transform tr2 = new Transform(3, circle2, square);
        tr2.optimizePaths(false);
        playAnimation(tr2);
        waitSeconds(1);
        play.fadeOutAll();

    }

    private void homothecyTransform() {
        Shape pentagon = Shape.regularPolygon(5).thickness(3).scale(.5).shift(-1, -1);
        Shape pentagonDst = Shape.regularPolygon(5).thickness(3).scale(.8).shift(.5, -.5).rotate(45 * DEGREES);
        Transform tr = new Transform(3, pentagon, pentagonDst);
        playAnimation(tr);
        waitSeconds(1);
    }

    private void transform1() {
        Shape circle = Shape.circle().shift(-1, 0).scale(.5);
        Shape pentagon = Shape.regularPolygon(5).shift(.5, -.5).style("solidblue");
        play.transform(3, circle, pentagon);
        waitSeconds(3);
    }

    private void fadeHighShrinkDemo() {
        LaTeXMathObject text;
        Shape sq = Shape.square().fillColor("#87556f").thickness(2).center();//
        text = LaTeXMathObject.make("{\\tt play.fadeIn(sq)}").stackToScreen(Anchor.LOWER, .1, .1);
        add(text);
        play.fadeIn(sq);
        waitSeconds(1);
        remove(text);
        text = LaTeXMathObject.make("{\\tt play.highlight(sq)}").stackToScreen(Anchor.LOWER, .1, .1);
        add(text);
        play.highlight(sq);
        waitSeconds(1);
        remove(text);
        text = LaTeXMathObject.make("{\\tt play.shrinkOut(1,45*DEGREES, sq)}").stackToScreen(Anchor.LOWER, .1, .1);
        add(text);
        play.shrinkOut(1, 45 * DEGREES, sq);
        waitSeconds(1);
    }

    private void AffineTransformExample1() {
        Shape sq = Shape.square();
        Shape circ = Shape.circle().scale(.5).shift(.5, .5);//A circle inscribed into the square
        Point A = Point.at(0, 0); //A maps to D
        Point B = Point.at(1, 0); //B maps to E
        Point C = Point.at(0, 1); //C maps to F
        Point D = Point.at(1.5, -.5).dotStyle(DotSyle.CROSS);
        Point E = Point.at(2, 0).dotStyle(DotSyle.CROSS);
        Point F = Point.at(1.75, .75).dotStyle(DotSyle.CROSS);
        add(sq, circ, A, B, C, D, E, F);
        AffineJTransform transform = AffineJTransform.createAffineTransformation(A, B, C, D, E, F, 1);
        add(transform.getTransformedObject(sq));
        add(transform.getTransformedObject(circ));
        camera.adjustToAllObjects();
        waitSeconds(5);
    }

    private void reflectionExample1() {
        Shape sq = Shape.regularPolygon(5);
        Point A = sq.getPoint(0);//First vertex of the pentagon(lower-left corner)
        Point B = A.copy().shift(.5, -.2);
        add(sq, A, B);
        for (double alpha = 0; alpha <= 1; alpha += .2) {
            AffineJTransform transform = AffineJTransform.createReflection(A, B, alpha);
            add(transform.getTransformedObject(sq));
        }
        camera.adjustToAllObjects();
        waitSeconds(5);
    }

    private void HomothecyExample1() {
        Shape sq = Shape.square().shift(-1.5, -1);
        Point A = sq.getPoint(0);//First vertex of the square (lower-left corner)
        Point B = sq.getPoint(1);//First vertex of the square (lower-right corner)
        Point C = Point.at(1.5, -1);//Destiny point of A
        Point D = Point.at(1.7, .5);//Destiny point of B
        add(A, B, C, D);
        for (double alpha = 0; alpha <= 1; alpha += .2) {
            AffineJTransform transform = AffineJTransform.createDirect2DHomothecy(A, B, C, D, alpha);
            add(transform.getTransformedObject(sq));
        }
        waitSeconds(5);
    }

    private void rotateExample1() {
        Shape ellipse = Shape.circle().scale(.5, 1);//Creates an ellipse
        for (int n = 0; n < 180; n += 20) {
            add(ellipse.copy().rotate(Point.at(.5, 0), n * DEGREES));
        }
        waitSeconds(5);
    }

    private void scaleExample1() {
        add(Shape.circle().shift(-1, 0).scale(.5, 1));
        add(Shape.circle().shift(0, 1).scale(Point.at(0, 0), 1.3, .2));
        add(Shape.square().shift(1, 0).scale(.3));
        waitSeconds(5);
    }

    private void StackToScreenExample() {
        Shape sq = Shape.square();
        add(sq.stackToScreen(Anchor.LEFT));//Stack square to the left of the screen, with no gaps
        add(sq.copy().stackToScreen(Anchor.RIGHT, .3, .1));//Stack a copy of square to the left of the screen,with gaps of .3 horizontal and .1 vertical
        add(Shape.circle().stackToScreen(Anchor.UL));//Stack a unit circle to the upper left corner of the screen, with no gaps
        waitSeconds(5);
    }

    private void stackToExample2() {
        Shape previousPol = Shape.regularPolygon(3);
        add(previousPol);
        for (int n = 4; n < 10; n++) {
            Shape pol = Shape.regularPolygon(n).stackTo(previousPol, Anchor.RIGHT);
            add(pol);
            previousPol = pol;
        }
        camera.adjustToAllObjects();//Everyone should appear in the photo
        waitSeconds(5);//Time for screenshot, but you already should know that
    }

    private void stackToExample1() {
        Shape c1 = Shape.circle();
        Shape c2 = c1.copy();
        Shape c3 = c1.copy();
        Shape c4 = c1.copy();
        Shape sq = Shape.square();
        c1.stackTo(sq, Anchor.LEFT, .1);//Stacks circle to the left of the square, with a gap of .1 units
        c2.stackTo(sq, Anchor.RIGHT, .1);//Stacks circle to the right of the square, with a gap of .1 units
        c3.stackTo(sq, Anchor.UPPER);//Stacks circle to the upper side of the square, with no gap
        c4.stackTo(sq, Anchor.BY_CENTER);//Stacks circle center-to-center with the square
        add(c1, c2, c3, c4, sq);//Add everything to the scene
        camera.adjustToAllObjects();//Everyone should appear in the photo
        waitSeconds(5);//Time for screenshot, but you already should know that
    }

    private void PutAtExample() {
        Point A = Point.at(.5, .5);
        Shape circ = Shape.circle().putAt(A, Anchor.UPPER);//Set upper point of circle to A
        Shape arc = Shape.arc(120 * DEGREES).putAt(A, Anchor.UR);//Set up-right point of arc to A
        Shape sq = Shape.square().putAt(A, Anchor.BY_CENTER);//Set center of square to A
        add(A, circ, arc, sq);//Add everything to the scene
        waitSeconds(5);//Give me time to make a screenshot!
    }

    private void imageExample() {
        JMImage img = JMImage.make("c:/media/Galois.jpg").center();
        add(img);
        waitSeconds(5);
    }

    private void basicFlow() {
        Point p = Point.at(0, 0);
        play.shift(2, Vec.to(1, 0), p);
        waitSeconds(3);
    }

    private void Latex_1() {
        LaTeXMathObject text = new LaTeXMathObject("$$\\int_0^\\infty e^x\\,dx=1$$");
        add(text);
        waitSeconds(5);
    }

    private void BasicShapes() {
        Shape circ = Shape.circle();//Generates a circle with radius 1 and centered at (0,0)
        Shape sq = Shape.square();//Generates a unit-square, with lower left cornet at (0,0)
        Shape reg = Shape.regularPolygon(5);//A regular pentagon, with 2 first vertices at (0,0) and (1,0)
        Shape rect = Shape.rectangle(Point.at(1, 2), Point.at(3, 5));//A rectangle with their sides parallel to the axes, with lower left and upper right vertices at (1,2) and (3,5) respectively.
        Shape seg = Shape.segment(Point.at(-1, -1), Point.at(-.5, 1.5));//A segment specified by the given points
        Shape arc = Shape.arc(PI / 4);//An arc centered at (0,0) with radius 1, and arclength of PI/4 radians
        add(circ, sq, reg, rect, seg, arc);
        waitSeconds(5);
    }

    private void ThreeDots() {
        Point A = Point.at(-.5, 0).dotStyle(DotSyle.CIRCLE);
        Point B = Point.at(0, 0).dotStyle(DotSyle.CROSS);
        Point C = Point.at(.5, 0).dotStyle(DotSyle.PLUS);
        add(A, B, C);
        waitSeconds(5);
    }

}
