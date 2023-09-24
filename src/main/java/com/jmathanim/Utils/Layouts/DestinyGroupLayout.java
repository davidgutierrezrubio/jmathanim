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
package com.jmathanim.Utils.Layouts;

import com.jmathanim.mathobjects.MathObjectGroup;

/**
 * A layout setting individual destiny locations for each object of the group
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class DestinyGroupLayout extends GroupLayout {

	private final MathObjectGroup destinyGroup;

	/**
	 * Creates a new DestinyGroupLayout. The destinty of n-th object of the
	 * MathObjectGroup to apply the layout is given by the n-th object of the
	 * destiny group
	 *
	 * @param destinyGroup Destiny group. The center of each object will be the
	 *                     center of group to apply the layout
	 */
	public DestinyGroupLayout(MathObjectGroup destinyGroup) {
		this.destinyGroup = destinyGroup;
	}

	@Override
	public void executeLayout(MathObjectGroup group) {
            int numElementsToApply=Math.min(group.size(),destinyGroup.size());
		for (int n = 0; n < numElementsToApply; n++) {
			group.get(n).moveTo(destinyGroup.get(n).getCenter());
		}
	}

	@Override
	public DestinyGroupLayout copy() {
		return new DestinyGroupLayout(destinyGroup);
	}
}
