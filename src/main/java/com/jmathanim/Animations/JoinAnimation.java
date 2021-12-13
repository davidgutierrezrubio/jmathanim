/*
 * Copyright (C) 2021 David
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

import com.jmathanim.jmathanim.JMathAnimScene;
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
    double[] steps;

    public static JoinAnimation make(double runTime, Animation... anims) {
        return new JoinAnimation(runTime, anims);
    }

    public JoinAnimation(double runTime, Animation... anims) {
        super(runTime);
        animations = new ArrayList<>();
        animations.addAll(Arrays.asList(anims));
        this.setLambda(t -> t);//Default behaviour for this animation
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        //Compute vector of steps
        double totalSum = animations.stream().collect(Collectors.summingDouble(Animation::getRunTime));
        steps = new double[animations.size() + 1];
        steps[0] = 0;
        double partialSum = 0;
        for (int i = 0; i < animations.size(); i++) {
            partialSum += animations.get(i).getRunTime();
            steps[i + 1] = partialSum / totalSum;
        }
    }

    @Override
    public void doAnim(double t) {
        double lt = getLambda().applyAsDouble(t);

        int num = getAnimationNumberForTime(lt);

        //Now normalize from 0 to 1
        double ltNormalized = (lt - steps[num]) / (steps[num + 1] - steps[num]);
        Animation anim = animations.get(num);
        if (anim.getStatus() == Status.NOT_INITIALIZED) {
            if (num > 0) {
                //If the previous animations didn't start yet, I have to ensure to be
                //properly initalized and finished so that the next ones will save the 
                //correct states of the objects
                for (int k = 0; k < num; k++) {
                    if (animations.get(k).getStatus() == Status.NOT_INITIALIZED) {
                        animations.get(k).initialize(scene);
                        animations.get(k).doAnim(1);
                    }
                    animations.get(k).finishAnimation();
                }
            }
            anim.initialize(scene);
        }
        anim.doAnim(ltNormalized);
    }

    @Override
    public void finishAnimation() {
        animations.get(animations.size()-1).finishAnimation();
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

    public boolean add(Animation e) {
        return animations.add(e);
    }

    public void add(int index, Animation element) {
        animations.add(index, element);
    }

    public ArrayList<Animation> getAnimations() {
        return animations;
    }

}
