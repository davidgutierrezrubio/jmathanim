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

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author David
 */
public class JoinAnimation extends Animation {

    ArrayList<Animation> animations;

    public static JoinAnimation make(double runTime, Animation... anims) {
        return new JoinAnimation(runTime, anims);
    }

    public JoinAnimation(double runTime, Animation... anims) {
        super(runTime);
        animations = new ArrayList<>();
        animations.addAll(Arrays.asList(anims));
    }

    @Override
    public void doAnim(double t) {
        int size = animations.size();
        double lt = getLambda().applyAsDouble(t);
        int num = (int) (lt * size);
        if (num == size) {
            num--;
        }
        double lt2 = size * lt - num;
        Animation anim = animations.get(num);
        if (anim.getStatus() == Status.NOT_INITIALIZED) {
            if (num>0) {
                animations.get(num-1).finishAnimation();
            }
             anim.initialize(scene);
        }
        anim.doAnim(lt2);
    }

}
