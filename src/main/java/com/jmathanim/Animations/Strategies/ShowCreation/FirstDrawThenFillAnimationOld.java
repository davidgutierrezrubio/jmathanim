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
import com.jmathanim.Animations.Concatenate;
import com.jmathanim.Animations.JoinAnimation;
import com.jmathanim.Animations.WaitAnimation;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;

/**
 * Animation that draws and object and then changes its alpha fill from 0 to
 * current. If used in a multishape, a delayTime can be specified between
 * animating one shape and next one
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class FirstDrawThenFillAnimationOld extends AbstractCreationStrategy {

    /**
     * Percentage (0 to 1) of time dedicated to drawing the path. The rest of
     * time is dedicated to fill the paths.
     */
    public static final double PERCENT_DRAWING = 0.6d;
    /**
     * Delay percent. The delay is the time between starting one shape and the
     * next one, in multishape objects. A DELAY_PERCENT of 0.4d means that the
     * last shape begins to draw when reached the 40% of the runtime, and each
     * individual shape will be drawed in the 60% of the runtime.
     */
    public final double delayPercent = 0.1d;
    private final MathObject originObject;
    private final MathObjectGroup intermediateObjects;
    private double timegap;
    public AnimationGroup anim;
    private double delayFactor;

    public FirstDrawThenFillAnimationOld(double runtime, MathObject obj) {
        super(runtime);
        this.originObject = obj;
        this.delayFactor = .2;
        intermediateObjects = MathObjectGroup.make();
        intermediateObjects.objectLabel=originObject.objectLabel+"_intermediate";

    }

    @Override
    public MathObject getIntermediateObject() {
        return anim.getIntermediateObject();
    }

    /**
     * Creates the animation to run in processAnimation
     *
     * @param obj
     * @param runtime
     */
    private AnimationGroup createAnimation(MathObject obj, double runtime) {
        AnimationGroup ag = AnimationGroup.make();
        if (obj instanceof Shape) {
            ag.add(singleShapeAnimation(runtime, (Shape) obj));
            return ag;
        }
        if (obj instanceof MultiShapeObject) {
            MultiShapeObject msh = (MultiShapeObject) obj;
            for (Shape sh : msh.getShapes()) {
                ag.add(singleShapeAnimation(runtime, sh));
            }
            ag.addDelayEffect(delayFactor);
            return ag;
        }

        if (obj instanceof MathObjectGroup) {
            MathObjectGroup mog = (MathObjectGroup) obj;
            for (MathObject sh : mog.getObjects()) {
                ag.add(singleShapeAnimation(runtime, (Shape) sh));
            }
            return ag;
        }

        // Returns null if the object type is not supported
        return null;
    }

    public Animation singleShapeAnimation(double runtime, Shape obj) {
        JoinAnimation join = JoinAnimation.make(runtime);
//    Concatenate join=Concatenate.make();
        MODrawProperties mpDst = obj.getMp().copy();
        Shape intermediateObject = obj.copy();
        intermediateObject.fillAlpha(0);
        intermediateObjects.add(intermediateObject);
        double alpha = obj.getMp().getFillColor().getAlpha();
        double percentDrawing = alpha * PERCENT_DRAWING + (1 - alpha);
        SimpleShapeCreationAnimation anim1 = new SimpleShapeCreationAnimation(runtime * percentDrawing, intermediateObject);
//        anim1.setAddObjectsToScene(false);
        intermediateObjects.add(anim1.getIntermediateObject());
        join.add(anim1);
        if (percentDrawing < 1) {
            Animation anim2 = Commands.setMP(runtime * (1 - percentDrawing), mpDst, intermediateObject);
//             anim2.setAddObjectsToScene(false);
            join.add(anim2);
        }
        return join;
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        anim = createAnimation(originObject, this.runTime);
        if (anim == null) {
            JMathAnimScene.logger.error("Could'n create FirstDrawThenFillAnimation for object type "
                    + originObject.getClass().getCanonicalName() + ". Animation will not be performed");
        }
//        originObject.getMp().getFillColor().setAlpha(0); // Sets alpha to 0, to first draw objects without filling
//        anim.setLambda(getTotalLambda());
        copyAnimationParametersTo(anim);

        anim.initialize(scene);
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

    public double getTimegap() {
        return timegap;
    }

    public FirstDrawThenFillAnimationOld setTimegap(double timegap) {
        this.timegap = timegap;
        return this;
    }

    /**
     * Static constructor
     *
     * @param runtime Run time in seconds of animation
     * @param obj Object to animate. Currently, a Shape, MultiShape or
     * MathObjectGroup object
     * @return The animation to be played with the JMathimScene.playAnimation
     * method
     */
    public static FirstDrawThenFillAnimationOld make(double runtime, MathObject obj) {
        return new FirstDrawThenFillAnimationOld(runtime, obj);
    }

    public FirstDrawThenFillAnimationOld setDelayEffect(double delay) {
        delayFactor = delay;
        return this;
    }

    @Override
    public <T extends Animation> T setLambda(DoubleUnaryOperator lambda) {
        super.setLambda(lambda);
        try {
            anim.setLambda(lambda);
        } catch (NullPointerException e) {
        }
        return (T) this;
    }

    @Override
    public void cleanAnimationAt(double t) {
        //TODO anim intermediate objects are encapsulated and NOT deleted
        anim.cleanAnimationAt(t);
       
        double lt = getLT(t);
        if (lt == 1) {
            removeObjectsFromScene(intermediateObjects);
            addObjectsToscene(originObject);
        }
        if (lt == 0) {
            removeObjectsFromScene(originObject, intermediateObjects);

        }

    }

    @Override
    public void prepareForAnim(double t) {
         ArrayList<MathObject> aa = scene.getMathObjects();
        removeObjectsFromScene(originObject);
//        addObjectsToscene(intermediateObjects);
    }

}
