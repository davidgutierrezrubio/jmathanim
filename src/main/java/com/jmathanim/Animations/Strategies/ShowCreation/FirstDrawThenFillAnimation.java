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
package com.jmathanim.Animations.Strategies.ShowCreation;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.Commands;
import com.jmathanim.Animations.JoinAnimation;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 * Animation that draws and object and then changes its alpha fill from 0 to
 * current. If used in a multishape, a delayTime can be specified between
 * animating one shape and next one
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class FirstDrawThenFillAnimation extends AbstractCreationStrategy {

    private Shape[] originShapes;
    private final MathObject originObject;
    private Shape[] intermediateShapes;
    public final AnimationGroup anim;

    public FirstDrawThenFillAnimation(double runtime, MathObject origin) {
        super(runtime);
        this.originObject = origin;

        anim = AnimationGroup.make();
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        originShapes = converToShapeArray(originObject);
        this.intermediateShapes = new Shape[originShapes.length];
        for (int i = 0; i < originShapes.length; i++) {
            this.intermediateShapes[i] = originShapes[i].copy();

        }
        MODrawProperties[] mpDst = new MODrawProperties[originShapes.length];
        boolean shouldAnimateFill = false;
        for (int i = 0; i < originShapes.length; i++) {
            mpDst[i] = originShapes[i].getMp().copy();
            if (mpDst[i].getFillColor().getAlpha() > 0) {
                //If any of the shapes has fill color, animate 2 steps
                shouldAnimateFill = true;
            }
        }
        for (int index = 0; index < originShapes.length; index++) {
            anim.add(createSingleAnimation(shouldAnimateFill, this.intermediateShapes[index], mpDst[index]));
        }
        anim.addDelayEffect(.2);
        return anim.initialize(scene);
    }

    public Animation createSingleAnimation(boolean shouldAnimateFill, Shape sh, MODrawProperties mp) {
        Animation singleAnim;
        if (shouldAnimateFill) {
            sh.fillAlpha(0);
            SimpleShapeCreationAnimation anim1 = new SimpleShapeCreationAnimation(
                    3, sh);
            Animation anim2 = Commands.setMP(2, mp, sh);
            singleAnim = JoinAnimation.make(runTime, anim1, anim2);
        } else {
            singleAnim = new SimpleShapeCreationAnimation(runTime, sh);
        }
        copyAnimationParametersTo(singleAnim);
        return singleAnim;
    }

    @Override
    public void doAnim(double t) {
        anim.doAnim(t);
        super.doAnim(t);
    }

    @Override
    public void finishAnimation() {
        anim.finishAnimation();
        super.finishAnimation();
    }

    private Shape[] converToShapeArray(MathObject obj) {
        if (obj instanceof Shape) {
            Shape shape = (Shape) obj;
            return new Shape[]{shape};
        }
        if (obj instanceof MultiShapeObject) {
            MultiShapeObject multiShapeObject = (MultiShapeObject) obj;
            return multiShapeObject.toArray();
        }
        if (obj instanceof MathObjectGroup) {
            //This may lead to error if any element is not a Shape
            MathObjectGroup mg = (MathObjectGroup) obj;
            Shape[] shapes = new Shape[mg.size()];
            for (int i = 0; i < shapes.length; i++) {
                shapes[i] = (Shape) mg.get(i);
            }
            return shapes;
        }
        return null;
    }

    @Override
    public void cleanAnimationAt(double t) {
        //Child animation is not designed to properly clean, we must do it here
        double lt = getLT(t);
        anim.cleanAnimationAt(t);
        if (lt == 1) {
            removeObjectsFromScene(intermediateShapes);
            addObjectsToscene(originObject);
            return;
        }
        if (lt == 0) {
            removeObjectsFromScene(intermediateShapes);
            removeObjectsFromScene(originObject);
            return;
        }
        //Case 0<t<1
        addObjectsToscene(intermediateShapes);
        removeObjectsFromScene(originObject);
    }

    @Override
    public MathObject getIntermediateObject() {
        return anim.getIntermediateObject();
    }

    @Override
    public void prepareForAnim(double t) {
        removeObjectsFromScene(originObject);
    }

    @Override
    public void reset() {
        super.reset();
        anim.reset();
    }
}
