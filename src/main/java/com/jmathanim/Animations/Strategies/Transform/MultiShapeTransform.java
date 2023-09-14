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
    private final MultiShapeObject mshOrigin;
    private final MultiShapeObject mshDestiny;
    private final AnimationGroup anim;
//    public boolean isOriginInScene;

    public MultiShapeTransform(double runtime, MultiShapeObject origin, MultiShapeObject destiny) {
        super(runtime);
        this.destiny = destiny;
        this.origin = origin;
        this.intermediate = MultiShapeObject.make();
        this.mshOrigin = origin;
        this.mshDestiny = destiny;
        anim = new AnimationGroup();
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        anim.doAnim(t);
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);

        dst = MultiShapeObject.make();
        tr = MultiShapeObject.make();
        int sizeTr = mshOrigin.size();
        int sizeDst = mshDestiny.size();
        int numAnims = Math.max(sizeTr, sizeDst);

        if (sizeDst < sizeTr) {
            for (int i = 0; i < sizeTr; i++) {
                dst.add(mshDestiny.get(i * sizeDst / sizeTr).copy());//remove copy
            }
             tr.copyStateFrom(mshOrigin);
        }
        
        if (sizeTr < sizeDst) {
            tr.getShapes().clear();
            for (int i = 0; i < sizeDst; i++) {
                tr.add(mshOrigin.get(i * sizeTr / sizeDst).copy());
            }
            dst = mshDestiny.copy();
        }
        if (sizeDst == sizeTr) {
            dst = mshDestiny.copy();
            tr.copyStateFrom(mshOrigin);
        }

        for (int n = 0; n < numAnims; n++) {
            Transform transformAnim = new Transform(this.runTime, tr.get(n), dst.get(n));
            this.copyEffectParametersTo(transformAnim);
            anim.add(transformAnim);
            anim.setLambda(getTotalLambda());
        }
        anim.initialize(scene);
    }

    @Override
    public void prepareForAnim(double t) {
        anim.prepareForAnim(t);
        super.prepareForAnim(t);
    }

    @Override
    public void cleanAnimationAt(double t) {
        anim.cleanAnimationAt(t);
        super.cleanAnimationAt(t);
        removeObjectsFromScene(dst);
        
        
    }

}
