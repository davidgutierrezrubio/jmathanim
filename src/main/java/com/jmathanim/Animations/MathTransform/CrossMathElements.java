/*
 * Copyright (C) 2022 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Animations.MathTransform;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.Commands;
import com.jmathanim.Animations.ShiftAnimation;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.Transform;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;
import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;
import javafx.scene.shape.StrokeLineCap;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CrossMathElements extends AnimationGroup {

    private final Renderer renderer;
    private final ArrayList<Shape> crossesShapes;
    private final MODrawProperties crossDrawProperties;
    private final ArrayList<int[]> crossIndices;
    private double ratio;
    public static CrossMathElements make(double runTime, MultiShapeObject formula, int... indices) {
        CrossMathElements resul = new CrossMathElements(runTime, formula);
        resul.shouldAddObjectsToScene = true;
        resul.addSmallCrosses(indices);
        resul.setLambda(t->t);
        return resul;
    }
    private final MultiShapeObject formula;
    private int[] createdCrossedIndices;

    private CrossMathElements(double runTime, MultiShapeObject formula) {
        super();
        this.runTime = runTime;
        this.formula = formula;
        renderer = JMathAnimConfig.getConfig().getRenderer();
        crossDrawProperties = MODrawProperties.createFromStyle("default");
        crossDrawProperties.setDrawColor(JMColor.parse("red"));
        crossDrawProperties.setFillColor(JMColor.parse("red"));
        this.crossesShapes = new ArrayList<>();
        this.crossIndices = new ArrayList<>();
        this.ratio=.05;//Default ratio
    }

    public double getCrossRatioWidth() {
        return ratio;
    }

    public CrossMathElements crossRatioWidth(double ratio) {
        this.ratio = ratio;
        return this;
    }

    public CrossMathElements addSmallCrosses(int... indices) {
        for (int index : indices) {
            this.crossIndices.add(new int[]{index});
        }
        return this;
    }

    public CrossMathElements addBigCross(int... indices) {
        this.crossIndices.add(indices);
        return this;
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        //Add created crosses to the multishape and store their indices
        createdCrossedIndices=new int[crossesShapes.size()];
        int offset=formula.size();
        int k=0;
        for (Shape cross : crossesShapes) {
            formula.add(cross);
            createdCrossedIndices[k]=k+offset;
            k++;
        }
    }

    public int[] getCreatedCrossedIndices() {
        return createdCrossedIndices;
    }

    
    
    
    @Override
    public void initialize(JMathAnimScene scene) {
        generateCrosses();
        for (Shape cross : crossesShapes) {
            final JMPath path = cross.getPath();
            Vec shiftVector = path.get(0).p.to(path.get(1).p);
            path.get(1).copyFrom(path.get(0));
            path.get(2).copyFrom(path.get(3));
            final ShiftAnimation animShift = Commands.shift(runTime, shiftVector, path.get(1), path.get(2));
            animShift.setLambda(lambda);
            this.add(animShift);
        }

        super.initialize(scene);
        Shape[] toArray = crossesShapes.toArray(Shape[]::new);
        addObjectsToscene(toArray);
    }

    private void generateCrosses() {
        for (int[] indices : crossIndices) {
            Rect formulaRect = formula.slice(false, indices).getBoundingBox();
            Shape cross = buildCrossFromRect(formulaRect);
            //Copy style from crossDrawProperties, but only color
            //TODO: If we change this to a rectangle, we can inherit all draw properties
//            cross.drawColor(crossDrawProperties.getDrawColor());
            cross.getMp().copyFrom(crossDrawProperties);
            crossesShapes.add(cross);
        }

    }

    private Shape buildCrossFromRect(Rect formulaRect) {
        final Point ur = formulaRect.getUR();
        final Point dl = formulaRect.getDL();
        Vec diag = ur.to(dl);
        Vec normal = Vec.to(-diag.y, diag.x).normalize();
        final double th = diag.norm() * ratio;
        normal.multInSite(th);
        Shape cross = Shape.polygon(
                ur.add(normal),
                dl.add(normal),
                dl.add(normal.multInSite(-1)),
                ur.add(normal)
        );

        return cross;
//return Shape.rectangle(formulaRect);
    }

    @Override
    public CrossMathElements setLambda(DoubleUnaryOperator lambda) {
        this.lambda=lambda;
        return this;
    }
    
    
    public MODrawProperties getMp() {
        return crossDrawProperties;
    }
    
}
