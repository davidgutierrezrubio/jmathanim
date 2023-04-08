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
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.Transform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;

/**
 * Animation strategy between 2 multishapes A and B. If size(A) equals size(B)
 * and one-to-one correspondence transform is done. If size(A)&lt;size(B),
 * copies of shapes of A are added to A to ensure A and B have the same size.
 * The copies are distributed uniformly in the shapes of A. For example if A has
 * 4 shapes (0,1,2,3) and B has 11 (0,1,...10) the augmented A will have the
 * shapes 0,0,0,1,1,1,2,2,2,3,3. A similar case happens if size(B)&lt;size(A).
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MultiShapeTransform extends TransformStrategy {

    private MultiShapeObject dst;
    private MultiShapeObject tr;
    private final MultiShapeObject origin;
    private final MultiShapeObject destiny;
    private final MultiShapeObject intermediate;
    private final AnimationGroup anim;

    public MultiShapeTransform(double runtime, MultiShapeObject origin, MultiShapeObject destiny) {
        super(runtime);
        this.destiny = destiny;
        this.origin = origin;
        this.intermediate = origin.copy();
        anim = new AnimationGroup();
    }

    @Override
    public boolean processAnimation() {
        return anim.processAnimation();
    }

    @Override
    public void doAnim(double t) {
        anim.doAnim(t);
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        tr = MultiShapeObject.make();
        dst = MultiShapeObject.make();
        int sizeTr = intermediate.size();
        int sizeDst = destiny.size();
        int numAnims = Math.max(sizeTr, sizeDst);

        if (sizeDst < sizeTr) {
            for (int i = 0; i < sizeTr; i++) {
                dst.add(destiny.get(i * sizeDst / sizeTr).copy());//remove copy
            }
            tr = intermediate;
        }
        if (sizeTr < sizeDst) {
            for (int i = 0; i < sizeDst; i++) {
                tr.add(intermediate.get(i * sizeTr / sizeDst).copy());
            }
            dst = destiny;
        }
        if (sizeDst == sizeTr) {
            dst = destiny;
            tr = intermediate;
        }

        for (int n = 0; n < numAnims; n++) {
            Transform transformAnim = new Transform(this.runTime, origin.get(n), dst.get(n));
            this.copyEffectParametersTo(transformAnim);
            anim.add(transformAnim);
            anim.setLambda(getTotalLambda());
        }
        anim.initialize(scene);
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        anim.finishAnimation();
        removeObjectsFromScene(tr, dst, origin);
        addObjectsToscene(destiny);
    }

    @Override
    public MathObject getIntermediateTransformedObject() {
        Shape[] shapes = new Shape[anim.getAnimations().size()];
        int k = 0;
        for (Animation animation : anim.getAnimations()) {
            Transform tr = (Transform) animation;
            shapes[k] = ((Shape) tr.getIntermediateTransformedObject());
            k++;
        }
        return MultiShapeObject.make(shapes);
    }

    @Override
    public MathObject getOriginObject() {
        return origin;
    }

    @Override
    public MathObject getDestinyObject() {
        return destiny;
    }

}
