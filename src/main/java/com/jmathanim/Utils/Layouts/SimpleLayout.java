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

import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.LayoutType;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.AbstractMathGroup;
import com.jmathanim.mathobjects.Point;

/**
 * A simple layout which allocates the objects using fixed styles
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SimpleLayout extends GroupLayout {

	private final double horizontalGap;
	private final LayoutType layoutType;
	private final double verticalGap;
	private final Point refPoint;

	/**
	 * Creates a simple layout
	 *
	 * @param layoutType Layout to apply. A value from the enum Layout:
	 *               CENTER, RIGHT, LEFT, UPPER, LOWER, URIGHT, ULEFT, DRIGHT,
	 *               DLEFT, RUPPER, LUPPER, RLOWER, LLOWER, DIAG1, DIAG2, DIAG3,
	 *               DIAG4
	 * @param hgap   Horizontal gap between elements
	 * @param vgap   Vertical gap between elements
	 */
	public SimpleLayout(LayoutType layoutType, double hgap, double vgap) {
		this(null, layoutType, hgap, vgap);
	}

	/**
	 * Creates a simple layout, starting from a reference point
	 *
	 * @param refPoint Reference point
	 * @param layoutType   Layout to apply. A value from the enum
	 *                 Layout: CENTER, RIGHT, LEFT, UPPER, LOWER,
	 *                 URIGHT, ULEFT, DRIGHT, DLEFT, RUPPER, LUPPER, RLOWER, LLOWER,
	 *                 DIAG1, DIAG2, DIAG3, DIAG4
	 * @param hgap     Horizontal gap between elements
	 * @param vgap     Vertical gap between elements
	 */
	public SimpleLayout(Point refPoint, LayoutType layoutType, double hgap, double vgap) {
		this.layoutType = layoutType;
		this.horizontalGap = hgap;
		this.verticalGap = vgap;
		this.refPoint = refPoint;
	}

	@Override
	public void executeLayout(AbstractMathGroup<?> group) {
		AnchorType anchor1 = AnchorType.CENTER;
		AnchorType anchor2 = AnchorType.CENTER;

		double hgap = 0;
		double vgap = 0;
		switch (layoutType) {
		case CENTER:
			anchor1 = AnchorType.CENTER;
			anchor2 = AnchorType.CENTER;
			break;
		case RIGHT:
			anchor1 = AnchorType.LEFT;
			anchor2 = AnchorType.RIGHT;
			hgap = this.horizontalGap;
			break;
		case LEFT:
			anchor1 = AnchorType.RIGHT;
			anchor2 = AnchorType.LEFT;
			hgap = this.horizontalGap;
			break;
		case UPPER:
			anchor1 = AnchorType.LOWER;
			anchor2 = AnchorType.UPPER;
			vgap = this.verticalGap;
			break;
		case LOWER:
			anchor1 = AnchorType.UPPER;
			anchor2 = AnchorType.LOWER;
			vgap = this.verticalGap;
			break;
		case URIGHT:
			anchor1 = AnchorType.ULEFT;
			anchor2 = AnchorType.URIGHT;
			hgap = this.horizontalGap;
			break;
		case ULEFT:
			anchor1 = AnchorType.URIGHT;
			anchor2 = AnchorType.ULEFT;
			hgap = this.horizontalGap;
			break;
		case DRIGHT:
			anchor1 = AnchorType.DLEFT;
			anchor2 = AnchorType.DRIGHT;
			hgap = this.horizontalGap;
			break;
		case DLEFT:
			anchor1 = AnchorType.DRIGHT;
			anchor2 = AnchorType.DLEFT;
			hgap = this.horizontalGap;
			break;
		case RUPPER:
			anchor1 = AnchorType.DRIGHT;
			anchor2 = AnchorType.URIGHT;
			vgap = this.verticalGap;
			break;
		case LUPPER:
			anchor1 = AnchorType.DLEFT;
			anchor2 = AnchorType.ULEFT;
			vgap = this.verticalGap;
			break;
		case RLOWER:
			anchor1 = AnchorType.URIGHT;
			anchor2 = AnchorType.DRIGHT;
			vgap = this.verticalGap;
			break;
		case LLOWER:
			anchor1 = AnchorType.ULEFT;
			anchor2 = AnchorType.DLEFT;
			vgap = this.verticalGap;
			break;
		case DIAG1:
			anchor1 = AnchorType.DLEFT;
			anchor2 = AnchorType.URIGHT;
			vgap = this.verticalGap;
			hgap = this.horizontalGap;
			break;
		case DIAG2:
			anchor1 = AnchorType.DRIGHT;
			anchor2 = AnchorType.ULEFT;
			vgap = this.verticalGap;
			hgap = this.horizontalGap;
			break;
		case DIAG3:
			anchor1 = AnchorType.URIGHT;
			anchor2 = AnchorType.DLEFT;
			vgap = this.verticalGap;
			hgap = this.horizontalGap;
			break;
		case DIAG4:
			anchor1 = AnchorType.ULEFT;
			anchor2 = AnchorType.DRIGHT;
			vgap = this.verticalGap;
			hgap = this.horizontalGap;
			break;
		default:
			JMathAnimScene.logger.error("Layout not recognized, reverting to CENTER");
			break;
		}
		if (this.refPoint != null) {
			group.get(0).stackTo(anchor1, this.refPoint, AnchorType.CENTER, hgap, vgap);
		}
		for (int n = 1; n < group.size(); n++) {
			group.get(n).stackTo(anchor1, group.get(n - 1), anchor2, hgap, vgap);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public SimpleLayout copy() {
		if (refPoint != null) {
			return new SimpleLayout(refPoint.copy(), layoutType, verticalGap, verticalGap);
		} else {
			return new SimpleLayout(layoutType, verticalGap, verticalGap);
		}
	}

}
