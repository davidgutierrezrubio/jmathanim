/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.Strategies.ShowCreation;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.ApplyCommand;
import com.jmathanim.Animations.Concatenate;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.Animations.WaitAnimation;
import com.jmathanim.Utils.MathObjectDrawingProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class FirstDrawThenFillStrategy extends TransformStrategy {

    /**
     * Percentage (0 to 1) of time dedicated to drawing the path. The rest of
     * time is dedicated to fill the paths.
     */
    public static final double PERCENT_TO_DIVIDE_ANIMATION = .6;
    private final MultiShapeObject obj;
    final ArrayList<Animation> animations;
    private final double runtime;
    private final double timegap;
    private double realRuntime;

    public FirstDrawThenFillStrategy(MultiShapeObject obj, double percentGap, double runtime, JMathAnimScene scene) {
        super(scene);
        this.obj = obj;
        animations = new ArrayList<>();
        this.runtime = runtime;
        this.timegap = percentGap * this.runtime / obj.shapes.size();
    }

    @Override
    public void prepareObjects() {
        this.realRuntime = this.runtime - this.timegap * obj.shapes.size();
        if (this.realRuntime <= 0) {
            JMathAnimScene.logger.warn("Warning, computed runtime negative for FirstDrawThenFillStrategy. Nothing will be shown");
        }
        double dt = 0;
        for (Shape s : obj.shapes) {
            Concatenate anim = new Concatenate();
            anim.add(new WaitAnimation(dt));
            dt += this.timegap;
            final ShowCreation cr = new ShowCreation(PERCENT_TO_DIVIDE_ANIMATION * realRuntime,s);
            anim.add(cr);
            MathObjectDrawingProperties mpBase = s.mp.copy();
            s.mp.fillColor.alpha = 0;
            final ApplyCommand st = Commands.setMP((1 - PERCENT_TO_DIVIDE_ANIMATION) * realRuntime, mpBase, s);
            anim.add(st);
            animations.add(anim);
            anim.initialize();
        }
        scene.remove(obj);

    }

    @Override
    public void applyTransform(double t, double lt) {
        for (Animation anim : animations) {
            anim.doAnim(t, lt);
        }
    }

    @Override
    public void finish() {
        for (Animation anim : animations) {
            anim.finishAnimation();
        }
        //Remove all subelements and returns to the whole multipath object
        for (Shape s : obj.shapes) {
            scene.remove(s);
        }
        scene.add(obj);
    }

    @Override
    public void addObjectsToScene() {
    }

}
