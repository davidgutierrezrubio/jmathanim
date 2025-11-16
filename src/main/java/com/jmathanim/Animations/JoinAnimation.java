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

import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.Shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Creates an animation that considers all contained animations as one.
 *
 * @author David Gutiérrez Rubio
 */
public class JoinAnimation extends Animation {

    public Shape mierda;
    ArrayList<Animation> animations;
    Animation previous;
    double[] steps;

    public static JoinAnimation make(Animation... anims) {
        JoinAnimation resul = new JoinAnimation(-1, anims);
        resul.setLambda(t -> t);//Default behaviour for this animation
        return resul;
    }

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
        if (getRunTime() < 0) {//If runtime of JoinAnimation is <0, take the sum
            setRunTime(totalSum);
        }
        steps = new double[animations.size() + 1];
        steps[0] = 0;
        double partialSum = 0;
        for (int i = 0; i < animations.size(); i++) {
            partialSum += animations.get(i).getRunTime();
            steps[i + 1] = partialSum / totalSum;
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

//        if (lt == 1) {
//            System.out.println("JOin animation Do lt=" + lt);
//        }
// if (lt >.4) {
//            System.out.println("JOin animation Do lt=" + lt);
//        }
        int num = getAnimationNumberForTime(lt);
        //Now normalize from 0 to 1
        double ltNormalized = 0;
        if (num+1 < steps.length) {
            double delta = steps[num + 1] - steps[num];
            if (delta > 0) {
                ltNormalized = (lt - steps[num]) / delta;
            }else
                ltNormalized=0;
        }

        Animation anim = animations.get(num);
//        if ((previous != null) && (anim != previous)) { //if we changed animations between previous frame and actual...

        for (int k = 0; k < num; k++) {
            Animation an = animations.get(k);
            if (an.getStatus() != Status.FINISHED) {
                an.initialize(scene);
                an.doAnim(1);
                an.t = 1;
//                    an.cleanAnimationAt(1);
                an.finishAnimation();
            }
        }
        if (num + 1 < animations.size()) {
            for (int k = num + 1; k < animations.size(); k++) {
                Animation an = animations.get(k);
                if (an.t != 0) {
                    an.doAnim(0);
                    an.t = 0;
                    an.finishAnimation();
                }
                if (an instanceof SingleCommandAnimation) {
                    SingleCommandAnimation sc = (SingleCommandAnimation) an;
                    if (sc.cmdStatus == SingleCommandAnimation.cmdStatusType.DONE) {
                        sc.undo();
                        sc.cmdStatus = SingleCommandAnimation.cmdStatusType.UNDONE;
                    }

                }
            }
        }

//        int numPrev = animations.indexOf(previous);
//        if (numPrev != -1) {
//            if (numPrev > num) {
////                previous.cleanAnimationAt(0);
//                animations.get(num + 1).cleanAnimationAt(0);
//            }
//            if (numPrev < num) {
//                animations.get(num - 1).cleanAnimationAt(1);
//            }
//        }
        if (anim.getStatus() == Status.NOT_INITIALIZED) {
            anim.initialize(scene);
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
        double thisT = steps[num + 1];

        //This loop is necessary to handle 0-time animations like SingleCommandAnimation
        while ((num + 1 < steps.length) && (steps[num + 1] == thisT)) {
            //Now normalize from 0 to 1

            double ltNormalized;

            if (steps[num + 1] != steps[num]) {
                ltNormalized = (lt - steps[num]) / (steps[num + 1] - steps[num]);
            } else {
                ltNormalized = (lt == 0 ? 0 : 1);
            }
            animations.get(num).cleanAnimationAt(ltNormalized);

            num++;
        }
    }

    @Override
    public void prepareForAnim(double t) {
//        double lt = getLT(t);
//        int num = getAnimationNumberForTime(lt);
//        //Now normalize from 0 to 1
//        double ltNormalized = (lt - steps[num]) / (steps[num + 1] - steps[num]);
//        Animation an = animations.get(num);
//        if (an.getStatus() == Status.NOT_INITIALIZED) {
//            an.initialize(scene);
//        }
//        an.prepareForAnim(ltNormalized);
    }

   private int getAnimationNumberForTime(double t) {
        if (t == 0) {
            return 0;
        }
        int num = 0;
        try {
            while (steps[num]<=t) {
                num++;
            }
             } catch (ArrayIndexOutOfBoundsException e) {
                 num--;
        }
       
        num--;
        return num;
    }

    public boolean add(Animation... anims) {
        return animations.addAll(Arrays.asList(anims));
    }

    public void add(int index, Animation element) {
        animations.add(index, element);
    }

    public ArrayList<Animation> getAnimations() {
        return animations;
    }

    @Override
    public MathObject<?>  getIntermediateObject() {
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
