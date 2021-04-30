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

import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.AnimationWithEffects;
import com.jmathanim.Animations.Transform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MultiShapeObject;

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
public class MultiShapeTransform extends AnimationWithEffects {

	private MultiShapeObject dst;
	private MultiShapeObject tr;
	private final MultiShapeObject mobjTransformed;
	private final MultiShapeObject mobjDestiny;
	private final AnimationGroup anim;

	public MultiShapeTransform(double runtime, MultiShapeObject mobjTransformed, MultiShapeObject mobjDestiny) {
		super(runtime);
		this.mobjDestiny = mobjDestiny;
		this.mobjTransformed = mobjTransformed;
		anim = new AnimationGroup();
	}

	@Override
	public boolean processAnimation() {
		return anim.processAnimation();
	}

	@Override
	public void initialize(JMathAnimScene scene) {
		super.initialize(scene);
		tr = new MultiShapeObject();
		dst = new MultiShapeObject();
		int sizeTr = mobjTransformed.shapes.size();
		int sizeDst = mobjDestiny.shapes.size();
		int numAnims = Math.max(sizeTr, sizeDst);

		if (sizeDst < sizeTr) {
			for (int i = 0; i < sizeTr; i++) {
				dst.add(mobjDestiny.get(i * sizeDst / sizeTr).copy());
			}
			tr = mobjTransformed.copy();
		}
		if (sizeTr < sizeDst) {
			for (int i = 0; i < sizeDst; i++) {
				tr.add(mobjTransformed.get(i * sizeTr / sizeDst).copy());
			}
			dst = mobjDestiny.copy();
		}
		if (sizeDst == sizeTr) {
			dst = mobjDestiny.copy();
			tr = mobjTransformed.copy();
		}

		for (int n = 0; n < numAnims; n++) {
			Transform transformAnim = new Transform(this.runTime, tr.get(n), dst.get(n));
			transformAnim.copyEffectParametersFrom(this);
			anim.add(transformAnim);
		}
		anim.initialize(scene);
		removeObjectsToscene(mobjTransformed);
	}

	@Override
	public void finishAnimation() {
		super.finishAnimation();
		anim.finishAnimation();
		removeObjectsToscene(tr, dst);
		addObjectsToscene(mobjDestiny);
	}

}
