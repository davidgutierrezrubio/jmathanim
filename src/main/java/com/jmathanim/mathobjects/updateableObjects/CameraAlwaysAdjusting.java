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

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CameraAlwaysAdjusting implements Updateable {

	Camera camera;
	double hgap, vgap;

	public CameraAlwaysAdjusting(Camera cam, double hgap, double vgap) {
		this.camera = cam;
		this.hgap = hgap;
		this.vgap = vgap;
	}

	@Override
	public int getUpdateLevel() {
		return Integer.MAX_VALUE;// This always should be updated last
	}

	@Override
	public void update(JMathAnimScene scene) {
		if (!scene.getObjects().isEmpty()) {
			Rect bbox = camera.getMathView().addGap(-hgap, -vgap);
			for (MathObject obj : scene.getObjects()) {
				bbox = Rect.union(bbox, obj.getBoundingBox());
			}
			camera.adjustToRect(bbox.addGap(hgap, vgap));
		}
	}

}
