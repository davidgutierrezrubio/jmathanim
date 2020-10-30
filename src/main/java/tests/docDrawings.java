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

import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Point.DotSyle;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class docDrawings extends Scene2D {

    @Override
    public void setupSketch() {
        conf.setCreateMovie(true);
        conf.setLowQuality();
    }

    @Override
    public void runSketch() throws Exception {
        Point p=Point.at(0,0);
        play.shift(2,Vec.to(1,0),p);
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
        add(circ,sq,reg,rect,seg,arc);
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
