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
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.jmathanim.Scene2D;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.updateableObjects.AnchoredMathObject;
import com.jmathanim.mathobjects.updateableObjects.AveragePoint;
import com.jmathanim.mathobjects.updateableObjects.TransformedJMPath;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class TesterUpdaters extends Scene2D {

    @Override
    public void setupSketch() {
        ConfigLoader.parseFile("preview.xml");
//        ConfigLoader.parseFile("production.xml");
        ConfigLoader.parseFile("dark.xml");
    }

    @Override
    public void runSketch() throws Exception {
        AnchoredToLower();
    }

    private void AnchoredToLower() {
        Shape sq=Shape.regularPolygon(5).drawColor("#3ceaea3").thickness(2);
        Shape c=Shape.circle().scale(.3).style("solidblue");
        AnchoredMathObject an=new AnchoredMathObject(c,Anchor.UPPER, sq,Anchor.LOWER);
        registerObjectToBeUpdated(an);
        add(c,sq);
        waitSeconds(1);
        play.shift(3,-1,0,sq);
        waitSeconds(3);
    }

    private void AnchoredToPoint() {
        Shape sq=Shape.regularPolygon(5).drawColor("#3ceaea3").thickness(2);
        Shape c=Shape.circle().scale(.3).style("solidblue");
        AnchoredMathObject an=new AnchoredMathObject(c,Anchor.BY_CENTER, sq.getPoint(2));
        registerObjectToBeUpdated(an);
        add(c,sq);
        waitSeconds(1);
        play.shift(3,-1,0,sq);
        waitSeconds(3);
    }

    private void transformedPath() {
        Shape c=Shape.circle().scale(.5).style("solidblue");
        AffineJTransform tr=AffineJTransform.createAffineTransformation(Point.at(0,0), Point.at(1,0), Point.at(0,1), Point.at(-.5,.3), Point.at(1,0), Point.at(1,1), 1);
        TransformedJMPath tpa=new TransformedJMPath(c, tr);
        tpa.style("solidred").fillAlpha(.5);
        add(c,tpa);
        waitSeconds(1);
        play.shift(3,-1,0,c);
        waitSeconds(3);
    }

    private void averagePoint() {
        Point p1=Point.random().drawColor(JMColor.random());
        Point p2=Point.random().drawColor(JMColor.random());
        Point p3=Point.random().drawColor(JMColor.random());
        AveragePoint avp = new AveragePoint(p1,p2,p3);
        avp.style("dotBlueCross");
        add(p1,p2,p3,avp);
        waitSeconds(3);
        play.shift(3, -1,.3, p1);
        play.shift(3, .3,-.6, p2);
        waitSeconds(3);
    }

}
