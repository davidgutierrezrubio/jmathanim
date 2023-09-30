/*
 * Copyright (C) 2021 David Gutiérrez Rubio
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

import com.jmathanim.mathobjects.MathObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Creates an animation that considers all contained animations as one.
 *
 * @author David Gutiérrez Rubio
 */
public class JoinAnimation extends Animation {

    ArrayList<Animation> animations;
    Animation previous;
    double[] steps;

    public static JoinAnimation make(double runTime, Animation... anims) {
        JoinAnimation resul = new JoinAnimation(runTime, anims);
        resul.setLambda(t -> t);//Default behaviour for this animation
        return resul;
    }

    protected JoinAnimation(double runTime, Animation... anims) {
        super(runTime);
        setDebugName("JoinAnimation");
        previous = null;
        animations = new ArrayList<>();
        animations.addAll(Arrays.asList(anims));

    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        //Compute vector of steps
        double totalSum = animations.stream().collect(Collectors.summingDouble(Animation::getRunTime));
        steps = new double[animations.size() + 1];
        steps[0] = 0;
        double partialSum = 0;
        for (int i = 0; i < animations.size(); i++) {
            partialSum += animations.get(i).getRunTime();
            steps[i + 1] = partialSum / totalSum;
        }
        //Performs an initialization of all animations
        for (int i = 0; i < animations.size(); i++) {
            Animation anim = animations.get(i);
            anim.initialize(scene);
            anim.doAnim(1);
            anim.cleanAnimationAt(1);
//            anim.finishAnimation();
        }

        //Now "rewind"
        for (int i = animations.size() - 1; i >= 0; i--) {
            Animation anim = animations.get(i);
            anim.doAnim(0);
            anim.cleanAnimationAt(0);

        }
        return true;
    }

    public void doAnimOld(double t) {
        double lt = getLT(t);
        int num = getAnimationNumberForTime(lt);
        //Now normalize from 0 to 1
        double ltNormalized = (lt - steps[num]) / (steps[num + 1] - steps[num]);
        Animation anim = animations.get(num);
        if (anim.getStatus() == Status.NOT_INITIALIZED || anim.getStatus() == Status.FINISHED) {
            if (num > 0) {
                //If the previous animations didn't start yet, I have to ensure to be
                //properly initalized and finished so that the next ones will save the 
                //correct states of the objects
                for (int k = 0; k < num; k++) {
//                    if (animations.get(k).getStatus() == Status.NOT_INITIALIZED) {
                    animations.get(k).initialize(scene);
                    animations.get(k).doAnim(1);
//                    }
                    animations.get(k).finishAnimation();
                }
            }
            //I have to ensure that (if playing reversal for example), latter animations are properly finished
//            if (num+1<animations.size()) { //There is an animation that needs to be properly finished at t=0
//                Animation animNext = animations.get(num+1);
//                //If status is NOT_INITIALIZED, no need to do anything
//                if (animNext.getStatus()==Status.RUNNING||animNext.getStatus()==Status.FINISHED) {
//                    animNext.doAnim(0);
//                    animNext.finishAnimation();
//                } 
//            }
//            

            anim.initialize(scene);
        }
        anim.doAnim(ltNormalized);
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        double lt = getLT(t);
        int num = getAnimationNumberForTime(lt);
        //Now normalize from 0 to 1
        double ltNormalized = (lt - steps[num]) / (steps[num + 1] - steps[num]);

        for (int k = 0; k < num; k++) {
            animations.get(k).doAnim(0);
            animations.get(k).cleanAnimationAt(0);
            animations.get(k).doAnim(1);
            animations.get(k).cleanAnimationAt(1);
        }

        Animation anim = animations.get(num);
        if ((previous != null) && (anim != previous)) { //if we changed animations between previous frame and actual...
            int numPrev = animations.indexOf(previous);

            if (numPrev > num) {
                previous.cleanAnimationAt(0);
            }
            if (numPrev < num) {
                previous.cleanAnimationAt(1);
            }
        }
        anim.prepareForAnim(ltNormalized);
        anim.doAnim(ltNormalized);
        previous = anim;
    }

    @Override
    public void finishAnimation() {
        doAnim(t);
        cleanAnimationAt(t);

    }

    @Override
    public void cleanAnimationAt(double t) {
        double lt = getLT(t);
        int num = getAnimationNumberForTime(lt);
        //Now normalize from 0 to 1
        double ltNormalized = (lt - steps[num]) / (steps[num + 1] - steps[num]);
        animations.get(num).cleanAnimationAt(ltNormalized);
    }

    @Override
    public void prepareForAnim(double t) {
        double lt = getLT(t);
        int num = getAnimationNumberForTime(lt);
        //Now normalize from 0 to 1
        double ltNormalized = (lt - steps[num]) / (steps[num + 1] - steps[num]);
        animations.get(num).prepareForAnim(ltNormalized);
    }

    private int getAnimationNumberForTime(double t) {
        if (t == 0) {
            return 0;
        }
        int num = 0;
        while (t > steps[num]) {
            num++;
        }
        num--;
        return num;
    }

    public boolean add(Animation...anims) {
        return animations.addAll(Arrays.asList(anims));
    }

    public void add(int index, Animation element) {
        animations.add(index, element);
    }

    public ArrayList<Animation> getAnimations() {
        return animations;
    }

    @Override
    public MathObject getIntermediateObject() {
        return previous.getIntermediateObject();
    }
     @Override
    public void reset() {
        super.reset();
        for (Animation anim : animations) {
            anim.reset();
            
        }
    }

}
