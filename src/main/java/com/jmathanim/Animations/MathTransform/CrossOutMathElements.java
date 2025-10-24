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

import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.Commands;
import com.jmathanim.Animations.ShiftAnimation;
import com.jmathanim.MathObjects.AbstractMultiShapeObject;
import com.jmathanim.MathObjects.Shape;
import com.jmathanim.MathObjects.Shapes.JMPath;
import com.jmathanim.MathObjects.Shapes.MultiShapeObject;
import com.jmathanim.MathObjects.Text.AbstractLatexMathObject;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;

import java.util.ArrayList;

/**
 * Animates a cross out effect over a LaTeXFormula (more generally, any
 * MultiShapeObject class). Once animation is done these crosses will be added
 * as new shapes to the formula (this behaviour can be configured).
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CrossOutMathElements extends AnimationGroup {

    public final ArrayList<Shape> crossesShapes;
    private final MODrawProperties crossDrawProperties;
    private final ArrayList<int[]> crossIndices;
    private double ratio;
    private boolean shoulAddCrossesToFormulaAtEnd;

    /**
     * Static builder. Creates a new cross out animation.
     *
     * @param runTime Time in seconds
     * @param formula Formula to apply the cross out
     * @param indices Optional. Indices that should be crossed out (one cross
     * per index)
     * @return The created animation.
     */
    public static CrossOutMathElements make(double runTime, AbstractLatexMathObject<?> formula, int... indices) {
        CrossOutMathElements resul = new CrossOutMathElements(runTime, formula);
        resul.shouldAddObjectsToScene = true;
        resul.addSmallCrosses(indices);
        resul.setLambda(t -> t);
        return resul;
    }
    private final AbstractLatexMathObject<?> formula;
    private final MultiShapeObject generatedCrosses;
    private int[] createdCrossedIndices;

    private CrossOutMathElements(double runTime, AbstractLatexMathObject<?> formula) {
        super();
        this.runTime = runTime;
        this.formula = formula;
        crossDrawProperties = MODrawProperties.createFromStyle("default");
        crossDrawProperties.setDrawColor(JMColor.parse("red"));
        crossDrawProperties.setFillColor(JMColor.parse("red"));
        this.crossesShapes = new ArrayList<>();
        this.crossIndices = new ArrayList<>();
        this.ratio = .05;//Default ratio
        this.generatedCrosses = MultiShapeObject.make();
        shoulAddCrossesToFormulaAtEnd = true;
    }

    /**
     * Whether created cross out should be added to the list of shapes of the
     * formula or not. In this case they will be added at the end
     *
     * @param shoulAddCrossesToFormulaAtEnd True to add, false otherwise
     */
    public void setShoulAddCrossesToFormulaAtEnd(boolean shoulAddCrossesToFormulaAtEnd) {
        this.shoulAddCrossesToFormulaAtEnd = shoulAddCrossesToFormulaAtEnd;
    }

    /**
     * Gets the ratio height/width of cross out.
     *
     * @return The ratio.
     */
    public double getCrossRatioWidth() {
        return ratio;
    }

    /**
     * Sets the ratio height/width of the cross out. A value of 1 draws squared
     * ones. Small values of this ratio will give a thinner cross out. The
     * default value is 0.05
     *
     * @param ratio The desired ratio
     * @return This object
     */
    public CrossOutMathElements crossRatioWidth(double ratio) {
        this.ratio = ratio;
        return this;
    }

    /**
     * Add individual cross out to the given indices (one per index).
     *
     * @param indices Indices to cross out (varargs)
     * @return This object
     */
    public CrossOutMathElements addSmallCrosses(int... indices) {
        for (int index : indices) {
            this.crossIndices.add(new int[]{index});
        }
        return this;
    }

    /**
     * Add one cross out that covers all given indices
     *
     * @param indices Indices to cross out (varargs)
     * @return This object
     */
    public CrossOutMathElements addBigCross(int... indices) {
        this.crossIndices.add(indices);
        return this;
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();

        //Add created crosses to the multishape and store their indices
        createdCrossedIndices = new int[crossesShapes.size()];
        int offset = formula.size();
        int k = 0;
        for (Shape cross : crossesShapes) {
            generatedCrosses.add(cross);
            if (shoulAddCrossesToFormulaAtEnd) {
                scene.remove(cross);
                formula.add(formula.shapeToLatexShape(cross));
                createdCrossedIndices[k] = k + offset;
                k++;
            }
        }
    }

    /**
     * Returns an int array with the indices of the generated crosses that will
     * keep after animation is finished in the given formula. If the original
     * formula had n elements, these cross should be added at positions n, n+1,
     * ...
     *
     * @return The int array with the positions
     */
    public int[] getCreatedCrossedIndices() {
        return createdCrossedIndices;
    }

    /**
     * Returns a MultiShapeObject that holds all generated crosses out.
     *
     * @return The MultiShapeObject with all cross out.
     */
    public MultiShapeObject getGeneratedCrosses() {
        return generatedCrosses;
    }

    @Override
    public boolean doInitialization() {

        generateCrosses();
        for (Shape cross : crossesShapes) {
            final JMPath path = cross.getPath();
            Vec shiftVector = path.get(0).getV().to(path.get(1).getV());
            path.get(1).copyControlPointsFrom(path.get(0));
            path.get(2).copyControlPointsFrom(path.get(3));
            final ShiftAnimation animShift = Commands.shift(runTime, shiftVector, path.get(1), path.get(2));
//            animShift.initialize(scene);
            this.add(animShift);
        }

        Shape[] toArray = crossesShapes.toArray(new Shape[0]);
        addObjectsToscene(toArray);
        super.doInitialization();
        return true;
    }

    private void generateCrosses() {
        for (int[] indices : crossIndices) {
            AbstractMultiShapeObject<?,?> slice = formula.slice(false, indices);
            Rect formulaRect = slice.getBoundingBox();
            Shape cross = buildCrossFromRect(formulaRect);
            cross.getMp().copyFrom(crossDrawProperties);
            crossesShapes.add(cross);
        }

    }

    public Shape buildCrossFromRect(Rect formulaRect) {
        final Vec ur = formulaRect.getUpperRight();
        final Vec dl = formulaRect.getLowerLeft();
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

    /**
     * Return the current drawing attributes object
     *
     * @return The drawing attributes object
     */
    public MODrawProperties getMp() {
        return crossDrawProperties;
    }

}
