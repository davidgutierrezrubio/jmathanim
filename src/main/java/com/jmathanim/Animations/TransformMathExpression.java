/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Animations;

import com.jmathanim.Animations.Strategies.Transform.MultiShapeTransform;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import sun.security.util.ArrayUtil;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformMathExpression extends Animation {

    private final LaTeXMathObject latexDestiny;
    private final LaTeXMathObject latexTransformed;
    private final MultiShapeObject mshDst;
    private final MultiShapeObject mshOrig;
    private AnimationGroup anim;

    public TransformMathExpression(double runTime, LaTeXMathObject latexTransformed, LaTeXMathObject latexDestiny) {
        super(runTime);
        this.latexTransformed = latexTransformed;
        this.latexDestiny = latexDestiny;
        this.mshOrig = new MultiShapeObject();
        this.mshDst = new MultiShapeObject();
    }

    @Override
    public void initialize() {
        //0,0,1,2,3 ------> 1) Trans: merge 0-1, Dst: 4-...
        //maps 0 onto 0 and 1
        //maps 1 onto 2
        //maps 2 onto 3
        //maps 3 onto 4 to end
        Integer[] ind = latexTransformed.getTransformIndices();
        //First, do some checks
        Integer max = Collections.max(Arrays.asList(ind));
        if (max > latexDestiny.shapes.size()) {
            JMathAnimScene.logger.error("Destiny formula has less elements than the transform indices. Animation will not be done.");
            isEnded = true;
            return;
        }
        if (ind.length != latexTransformed.shapes.size()) {
            JMathAnimScene.logger.error("Transform indices don't match transformed formula. Animation will not be done.");
            isEnded = true;
            return;
        }

        //Compute mshTransformed
        int previousShape = ind[0];
        Shape sh = latexTransformed.get(0);
        for (int n = 1; n < latexTransformed.shapes.size(); n++) {
            int currentIndex = ind[n];
            if (currentIndex == previousShape) {
                sh.merge(latexTransformed.get(n));
                System.out.println("Merging " + n + " into " + currentIndex);
            } else {
                mshOrig.addShape(sh);
                sh = latexTransformed.get(n);
                previousShape = currentIndex;
            }
        }
        mshOrig.addShape(sh);

        //Now, compute mshDestiny
        //Creates an arraylist removing duplicates
        int index = 0;
        int shNumber = 0;
        int val = 0;
        int valPrevious = 0;
        sh = latexDestiny.get(shNumber);
        while (index < ind.length) {
            //First, advance until finding a different number

            valPrevious = ind[index];
            val = ind[index];
            while (val == valPrevious) {
                index++;
                if (index < ind.length) {
                    val = ind[index];
                } else {
                    break;
                }
            }

            int dif = val - valPrevious;

            //if difference is 2 or more, merge
            for (int n = 1; n < dif; n++) {
                sh.merge(latexDestiny.get(shNumber + n));
                System.out.println("Merging Destiny " + (shNumber + n) + " into " + shNumber);
            }
            mshDst.addShape(sh);

            shNumber = val;
            sh = latexDestiny.get(shNumber);
        }

        //Finally, add the rest of the shapes
        int dif = latexDestiny.shapes.size() - val;
        for (int n = 1; n < dif; n++) {
            sh.merge(latexDestiny.get(shNumber + n));
            System.out.println("Merging Destiny " + (shNumber + n) + " into " + shNumber);
        }
        mshDst.addShape(sh);

        anim = new AnimationGroup();

        final Transform anim2 = new Transform(runTime * .8, mshOrig, mshDst);
        anim2.setLambda(x -> x);
        anim.add(anim2);

//        MODrawProperties mpOrig = latexTransformed.getMp().copy();
//        MODrawProperties mpDst = latexDestiny.getMp().copy();
//        mpOrig.setFillAlpha(0);
//        mpOrig.thickness = 8d;
//        for (Shape shape : mshDst.getShapes()) {
//            shape.getMp().copyFrom(latexDestiny.getMp());
//            anim.add(Commands.changeFillAlpha(runTime * .2, shape));
//        }

        anim.initialize();
        scene.add(mshOrig);
    }

    @Override
    public boolean processAnimation() {
        return anim.processAnimation(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doAnim(double t) {

    }

    @Override
    public void finishAnimation() {
        anim.finishAnimation();
        scene.remove(mshDst);
        scene.add(latexDestiny);
    }


}
