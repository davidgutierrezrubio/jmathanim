/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.Transform;
import com.jmathanim.MathObjects.MathObjectGroup;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class GroupTransformStrategy extends TransformStrategy<MathObjectGroup> {
    private final AnimationGroup ag;
    public GroupTransformStrategy(double runTime, MathObjectGroup groupOrigin, MathObjectGroup groupDestiny) {
        super(runTime);
        setOrigin(groupOrigin);
        setDestiny(groupDestiny);
        ag = AnimationGroup.make();
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        for (int i = 0; i < getOriginObject().size(); i++) {
            Transform transformAnim = Transform.make(runTime, getOriginObject().get(i), getDestinyObject().get(i));
            this.copyEffectParametersTo(transformAnim);
            ag.add(transformAnim);

        }
        this.copyEffectParametersTo(ag);
        return ag.initialize(scene);
    }

    @Override
    public void cleanAnimationAt(double t) {
        super.cleanAnimationAt(t);
        ag.cleanAnimationAt(t);
    }

    @Override
    public void doAnim(double t) {
         super.doAnim(t);
         ag.doAnim(t);
    }


    @Override
    public void prepareForAnim(double t) {
        super.prepareForAnim(t);
        ag.prepareForAnim(t);
    }

}
