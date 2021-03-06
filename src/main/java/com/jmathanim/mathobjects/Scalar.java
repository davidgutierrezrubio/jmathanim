/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * A value
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Scalar extends MathObject {

	public final Double value;

	public Scalar(double scalar) {
		this.value = scalar;
	}

	@Override
	public Scalar copy() {
		double sc = this.value;
		return new Scalar(sc);
	}

	@Override
	public void draw(JMathAnimScene scene, Renderer r) {
		// Nothing to do here
	}

	@Override
	public Rect getBoundingBox() {
		return null;// Nothing
	}

	@Override
	public String toString() {
		return "Scalar{" + "value=" + value + '}';
	}

}
