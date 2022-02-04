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
import java.util.function.DoubleUnaryOperator;

/**
 * Animation that draws and object and then changes its alpha fill from 0 to
 * current. If used in a multishape, a delayTime can be specified between
 * animating one shape and next one
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class FirstDrawThenFillAnimation extends AbstractCreationStrategy {

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
    private final MathObject obj;
    private boolean visible;
    private double timegap;
    private Animation anim;
    private double delayFactor;

    public FirstDrawThenFillAnimation(double runtime, MathObject obj) {
        super(runtime);
        this.obj = obj;
        this.delayFactor = .2;
        visible=obj.isVisible();

    }

    @Override
    public boolean processAnimation() {
        return anim.processAnimation();
    }

    /**
     * Creates the animation to run in processAnimation
     *
     * @param obj
     * @param runtime
     */
    private Animation createAnimation(MathObject obj, double runtime) {
        if (obj instanceof Shape) {
            JoinAnimation join = JoinAnimation.make(runtime);
            MODrawProperties mpDst = obj.getMp().copy();
            final Shape sh = (Shape) obj;
            double alpha = sh.getMp().getFillColor().getAlpha();
            double percentDrawing = alpha * PERCENT_DRAWING + (1 - alpha);
            join.add(new SimpleShapeCreationAnimation(runtime * percentDrawing, sh));
            if (percentDrawing < 1) {
                join.add(Commands.setMP(runtime * (1 - percentDrawing), mpDst, sh));
            }
            return join;
        }
        if (obj instanceof MultiShapeObject) {
            MultiShapeObject msh = (MultiShapeObject) obj;
            AnimationGroup ag = new AnimationGroup();
            for (Shape sh : msh.getShapes()) {
                JoinAnimation con = JoinAnimation.make(
                        runtime,
                        new FirstDrawThenFillAnimation(runtime, sh));
                ag.add(con);
            }
            ag.addDelayEffect(delayFactor);
            return ag;
        }

        if (obj instanceof MathObjectGroup) {
            MathObjectGroup mog = (MathObjectGroup) obj;
            double time = runtime * (1 - delayPercent);
            if (time <= 0) {
                JMathAnimScene.logger
                        .error("Time too short for draw-and-fill multishape, please take a higher runtime");
                return null;
            }
            AnimationGroup ag = new AnimationGroup();

            // time to start las shape: DELAY_TIME*(msh.getShapes().size()-1);
            double delay = 0;
            for (MathObject sh : mog.getObjects()) {
                Concatenate con = new Concatenate();
                con.add(new WaitAnimation(delay));
                con.add(new FirstDrawThenFillAnimation(time, sh));
                ag.add(con);
                delay += delayPercent;
            }
            return ag;
        }

        // Returns null if the object type is not supported
        return null;
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        anim = createAnimation(obj, this.runTime);
        if (anim == null) {
            JMathAnimScene.logger.error("Could'n crate FirstDrawThenFillAnimation for object type "
                    + obj.getClass().getCanonicalName() + ". Animation will not be performed");
        }
        obj.getMp().getFillColor().setAlpha(0); // Sets alpha to 0, to first draw objects without filling
        anim.setLambda(lambda);
        anim.initialize(scene);
    }

    @Override
    public void doAnim(double t) {
        anim.doAnim(t);
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        anim.finishAnimation();
    }

    public double getTimegap() {
        return timegap;
    }

    public FirstDrawThenFillAnimation setTimegap(double timegap) {
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
    public static FirstDrawThenFillAnimation make(double runtime, MathObject obj) {
        return new FirstDrawThenFillAnimation(runtime, obj);
    }

    public FirstDrawThenFillAnimation setDelayEffect(double delay) {
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
}
