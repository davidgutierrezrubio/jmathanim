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
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.ConfigLoader;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.Utils.MODrawProperties.DashStyle;
import com.jmathanim.jmathanim.Scene2DAwt;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.Dot;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.updateableObjects.AnchoredMathObject;
import com.jmathanim.mathobjects.updateableObjects.AveragePoint;
import com.jmathanim.mathobjects.updateableObjects.TransformedJMPath;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class TesterUpdaters extends Scene2DAwt {

    @Override
    public void setupSketch() {
        ConfigLoader.parseFile("preview.xml");
//        ConfigLoader.parseFile("production.xml");
        ConfigLoader.parseFile("dark.xml");
    }

    @Override
    public void runSketch() throws Exception {
//        averagePoint();
//        AnchoredToLower();
//        AnchoredToPoint();
//        transformedPath1();
        transformedPath2();
    }

    private void AnchoredToLower() {
        Shape sq = Shape.regularPolygon(5).drawColor("#3caea3").thickness(.7).dashStyle(DashStyle.DASHED);
        Shape c = Shape.circle().scale(.3).style("solidblue");
        AnchoredMathObject an = new AnchoredMathObject(c, Anchor.UPPER, sq, Anchor.LOWER);
        registerObjectToBeUpdated(an);
        add(c, sq);
        play.rotate(5, 120 * DEGREES, sq);
        waitSeconds(3);
        play.fadeOutAll();
    }

    private void AnchoredToPoint() {
        Shape sq = Shape.regularPolygon(5).drawColor("#3ceaea3").thickness(2);
        Shape c = Shape.circle().scale(.3).style("solidblue");
        AnchoredMathObject an = new AnchoredMathObject(c, Anchor.BY_CENTER, sq.getPoint(2));
        registerObjectToBeUpdated(an);
        add(c, sq);
        waitSeconds(1);
        play.shift(3, -1, 0, sq);
        waitSeconds(3);
        play.fadeOutAll();
    }

    private void transformedPath1() {
        Shape c = Shape.circle().scale(.5).style("solidblue");
        AffineJTransform tr = AffineJTransform.createAffineTransformation(Dot.at(0, 0), Dot.at(1, 0), Dot.at(0, 1), Dot.at(-.5, .3), Dot.at(1, 0), Dot.at(1, 1), 1);
        TransformedJMPath tpa = new TransformedJMPath(c, tr);
        tpa.style("solidred").fillAlpha(.5);
        add(c, tpa);
        waitSeconds(1);
        play.shift(3, -1, 0, c);
        waitSeconds(3);
        play.fadeOutAll();
    }

    private void transformedPath2() {
//        Shape c = Shape.square().scale(.5, 1.2).style("solidblue");
        LaTeXMathObject lat=LaTeXMathObject.make("$8$").scale(7);
        Shape c=lat.get(0);
        
        AffineJTransform tr = AffineJTransform.createAffineTransformation(Dot.at(0, 0), Dot.at(1, 0), Dot.at(0, 1), Dot.at(-.5, .3), Dot.at(1, 0), Dot.at(1, 1), 1);
        TransformedJMPath tpa = new TransformedJMPath(c, tr);
        tpa.style("solidred").fillAlpha(.5);
        add(c, tpa);
        waitSeconds(1);
        play.transform(5, c, Shape.square());
        waitSeconds(3);
        play.fadeOutAll();
    }

    private void averagePoint() {
        int numPoints = 30;
        Dot[] points = new Dot[numPoints];
        for (int n = 0; n < numPoints; n++) {
            points[n] = Dot.random().drawColor(JMColor.random());
        }
        add(points);
        AveragePoint avp = new AveragePoint(points);
        avp.style("dotBlueCross");
        add(avp);
        waitSeconds(3);
        
        //Create animations
        Animation[] anims=new Animation[numPoints];
        
         for (int n = 0; n < numPoints; n++) {
            anims[n]=Commands.shift(10,Math.random()*2-1,Math.random()*2-1,points[n]);
        }
         playAnimation(anims);
        waitSeconds(3);
        play.fadeOutAll();
    }

}
