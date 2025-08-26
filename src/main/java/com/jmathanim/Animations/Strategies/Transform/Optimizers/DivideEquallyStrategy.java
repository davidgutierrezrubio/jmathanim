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
package com.jmathanim.Animations.Strategies.Transform.Optimizers;

import com.jmathanim.mathobjects.AbstractShape;
import com.jmathanim.mathobjects.JMPath;

/**
 * Optimizes the paths when the transformed path has a number of connected
 * componentes is greter than 1 and the destiny is 0. In this case, try to
 * divide the destiny path equally
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class DivideEquallyStrategy implements OptimizePathsStrategy {

	@Override
	public void optimizePaths(AbstractShape<?> sh1, AbstractShape<?> sh2) {
		JMPath pa1 = sh1.getPath();
		JMPath pa2 = sh2.getPath();
		int n1 = pa1.getNumberOfConnectedComponents();
		int n2 = pa2.getNumberOfConnectedComponents();
		if ((n1 < 2) | (n2 > 1)) {
			return; // Do nothing
		}

		int numSegments = pa2.size();

		int step = numSegments / n1;
		// If there are more connected componentes than segments in destiny shape, do
		// nothing
		if (step == 0) {
			return;
		}

		for (int n = n2; n < n1; n++) {
			pa2.separate(n + n * step);
		}
	}

}
