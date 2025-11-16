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
import com.jmathanim.MathObjects.AbstractShape;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.MathObjectGroup;
import com.jmathanim.MathObjects.hasShapes;
import com.jmathanim.Styling.MODrawProperties;

/**
 * Animation that draws and object and then changes its alpha fill from 0 to current. If used in a multishape, a
 * delayTime can be specified between animating one shape and next one
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class FirstDrawThenFillAnimation extends AbstractCreationStrategy {

    public final AnimationGroup anim;
    private final hasShapes originObject;
    private AbstractShape<?>[] originShapes;
    private AbstractShape<?>[] intermediateShapes;

    public FirstDrawThenFillAnimation(double runtime, hasShapes origin) {
        super(runtime);
        this.originObject = origin;

        anim = AnimationGroup.make();
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        originShapes=originObject.toShapesArray();
        this.intermediateShapes = new AbstractShape<?>[originShapes.length];
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

    public Animation createSingleAnimation(boolean shouldAnimateFill, AbstractShape<?> sh, MODrawProperties mp) {
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

    @Override
    public void cleanAnimationAt(double t) {
        //Child animation is not designed to properly clean, we must do it here
        double lt = getLT(t);
        anim.cleanAnimationAt(t);
        if (lt == 1) {
            removeObjectsFromScene(intermediateShapes);
            addObjectsToscene((MathObject<?>) originObject);
            return;
        }
        if (lt == 0) {
            removeObjectsFromScene(intermediateShapes);
            removeObjectsFromScene((MathObject<?>) originObject);
            return;
        }
        //Case 0<t<1
        addObjectsToscene(intermediateShapes);
        removeObjectsFromScene((MathObject<?>)originObject);
    }

    @Override
    public MathObjectGroup getIntermediateObject() {
        return anim.getIntermediateObject();
    }

    @Override
    public void prepareForAnim(double t) {
        removeObjectsFromScene((MathObject<?>)originObject);
    }

    @Override
    public void reset() {
        super.reset();
        anim.reset();
    }
}
