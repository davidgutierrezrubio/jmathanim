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

import com.jmathanim.Utils.Anchor;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;

/**
 * A simple layout which allocates the objects using fixed styles
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SimpleLayout extends GroupLayout {

	private final double horizontalGap;
	private final MathObjectGroup.Layout layout;
	private final double verticalGap;
	private final Point refPoint;

	/**
	 * Creates a simple layout
	 *
	 * @param layout Layout to apply. A value from the enum MathObjectGroup.Layout:
	 *               CENTER, RIGHT, LEFT, UPPER, LOWER, URIGHT, ULEFT, DRIGHT,
	 *               DLEFT, RUPPER, LUPPER, RLOWER, LLOWER, DIAG1, DIAG2, DIAG3,
	 *               DIAG4
	 * @param hgap   Horizontal gap between elements
	 * @param vgap   Vertical gap between elements
	 */
	public SimpleLayout(MathObjectGroup.Layout layout, double hgap, double vgap) {
		this(null, layout, hgap, vgap);
	}

	/**
	 * Creates a simple layout, starting from a reference point
	 *
	 * @param refPoint Reference point
	 * @param layout   Layout to apply. A value from the enum
	 *                 MathObjectGroup.Layout: CENTER, RIGHT, LEFT, UPPER, LOWER,
	 *                 URIGHT, ULEFT, DRIGHT, DLEFT, RUPPER, LUPPER, RLOWER, LLOWER,
	 *                 DIAG1, DIAG2, DIAG3, DIAG4
	 * @param hgap     Horizontal gap between elements
	 * @param vgap     Vertical gap between elements
	 */
	public SimpleLayout(Point refPoint, MathObjectGroup.Layout layout, double hgap, double vgap) {
		this.layout = layout;
		this.horizontalGap = hgap;
		this.verticalGap = vgap;
		this.refPoint = refPoint;
	}

	@Override
	public void executeLayout(MathObjectGroup group) {
		Anchor.Type anchor1 = Anchor.Type.CENTER;
		Anchor.Type anchor2 = Anchor.Type.CENTER;

		double hgap = 0;
		double vgap = 0;
		switch (layout) {
		case CENTER:
			anchor1 = Anchor.Type.CENTER;
			anchor2 = Anchor.Type.CENTER;
			break;
		case RIGHT:
			anchor1 = Anchor.Type.LEFT;
			anchor2 = Anchor.Type.RIGHT;
			hgap = this.horizontalGap;
			break;
		case LEFT:
			anchor1 = Anchor.Type.RIGHT;
			anchor2 = Anchor.Type.LEFT;
			hgap = this.horizontalGap;
			break;
		case UPPER:
			anchor1 = Anchor.Type.LOWER;
			anchor2 = Anchor.Type.UPPER;
			vgap = this.verticalGap;
			break;
		case LOWER:
			anchor1 = Anchor.Type.UPPER;
			anchor2 = Anchor.Type.LOWER;
			vgap = this.verticalGap;
			break;
		case URIGHT:
			anchor1 = Anchor.Type.ULEFT;
			anchor2 = Anchor.Type.URIGHT;
			hgap = this.horizontalGap;
			break;
		case ULEFT:
			anchor1 = Anchor.Type.URIGHT;
			anchor2 = Anchor.Type.ULEFT;
			hgap = this.horizontalGap;
			break;
		case DRIGHT:
			anchor1 = Anchor.Type.DLEFT;
			anchor2 = Anchor.Type.DRIGHT;
			hgap = this.horizontalGap;
			break;
		case DLEFT:
			anchor1 = Anchor.Type.DRIGHT;
			anchor2 = Anchor.Type.DLEFT;
			hgap = this.horizontalGap;
			break;
		case RUPPER:
			anchor1 = Anchor.Type.DRIGHT;
			anchor2 = Anchor.Type.URIGHT;
			vgap = this.verticalGap;
			break;
		case LUPPER:
			anchor1 = Anchor.Type.DLEFT;
			anchor2 = Anchor.Type.ULEFT;
			vgap = this.verticalGap;
			break;
		case RLOWER:
			anchor1 = Anchor.Type.URIGHT;
			anchor2 = Anchor.Type.DRIGHT;
			vgap = this.verticalGap;
			break;
		case LLOWER:
			anchor1 = Anchor.Type.ULEFT;
			anchor2 = Anchor.Type.DLEFT;
			vgap = this.verticalGap;
			break;
		case DIAG1:
			anchor1 = Anchor.Type.DLEFT;
			anchor2 = Anchor.Type.URIGHT;
			vgap = this.verticalGap;
			hgap = this.horizontalGap;
			break;
		case DIAG2:
			anchor1 = Anchor.Type.DRIGHT;
			anchor2 = Anchor.Type.ULEFT;
			vgap = this.verticalGap;
			hgap = this.horizontalGap;
			break;
		case DIAG3:
			anchor1 = Anchor.Type.URIGHT;
			anchor2 = Anchor.Type.DLEFT;
			vgap = this.verticalGap;
			hgap = this.horizontalGap;
			break;
		case DIAG4:
			anchor1 = Anchor.Type.ULEFT;
			anchor2 = Anchor.Type.DRIGHT;
			vgap = this.verticalGap;
			hgap = this.horizontalGap;
			break;
		default:
			JMathAnimScene.logger.error("Layout not recognized, reverting to CENTER");
			break;
		}
		if (this.refPoint != null) {
			group.get(0).stackTo(anchor1, this.refPoint, Anchor.Type.CENTER, hgap, vgap);
		}
		for (int n = 1; n < group.size(); n++) {
			group.get(n).stackTo(anchor1, group.get(n - 1), anchor2, hgap, vgap);
		}
	}

	@Override
	public SimpleLayout copy() {
		if (refPoint != null) {
			return new SimpleLayout(refPoint.copy(), layout, verticalGap, verticalGap);
		} else {
			return new SimpleLayout(layout, verticalGap, verticalGap);
		}
	}

}
