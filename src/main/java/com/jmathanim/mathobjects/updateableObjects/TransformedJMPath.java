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
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformedJMPath extends Shape {

	private final AffineJTransform transform;
	private final Shape srcOBj;

	public TransformedJMPath(Shape jmpobj, AffineJTransform tr) {
		super();
		this.transform = tr;
		this.srcOBj = jmpobj;
		this.getPath().setJMPoints(jmpobj.getPath());
	}

	@Override
	public int getUpdateLevel() {
		return srcOBj.getUpdateLevel() + 1;
	}

	@Override
	public void update(JMathAnimScene scene) {
		int size = srcOBj.getPath().size();
		// TODO: This is already implemented
		for (int n = 0; n < size; n++) {
			JMPathPoint jmPDst = get(n);
			JMPathPoint pSrc = srcOBj.get(n);
			Point pDst = transform.getTransformedObject(pSrc.p);
			Point cp1Dst = transform.getTransformedObject(pSrc.cpExit);
			Point cp2Dst = transform.getTransformedObject(pSrc.cpEnter);

			jmPDst.p.v.x = pDst.v.x;
			jmPDst.p.v.y = pDst.v.y;
			jmPDst.p.v.z = pDst.v.z;

			jmPDst.cpExit.v.x = cp1Dst.v.x;
			jmPDst.cpExit.v.y = cp1Dst.v.y;
			jmPDst.cpExit.v.z = cp1Dst.v.z;

			jmPDst.cpEnter.v.x = cp2Dst.v.x;
			jmPDst.cpEnter.v.y = cp2Dst.v.y;
			jmPDst.cpEnter.v.z = cp2Dst.v.z;
		}
	}

	@Override
	public void registerChildrenToBeUpdated(JMathAnimScene scene) {
		scene.registerUpdateable(srcOBj);
	}

}
