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
package com.jmathanim.Constructible;

import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 * This class representas a constructible object, derived from another ones. For
 * example a circle that pass for 3 points is a constructible object. It cannot
 * be transformed nor animated by itself, only chaning the objects from which
 * depend
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Constructible extends MathObject {

	@Override
	public <T extends MathObject> T applyAffineTransform(AffineJTransform transform) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T align(Boxable obj, Align type) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T moveTo(double x, double y) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T stackToScreen(Anchor.Type anchorType, double xMargin, double yMargin) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T stackTo(Boxable obj, Anchor.Type anchorType, double gap) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T stackToRW(Boxable dstObj, Anchor.Type anchorType, double gap) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T stackToRH(Boxable dstObj, Anchor.Type anchorType, double gap) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T stackToRH(Anchor.Type anchorObj, Boxable dstObj, Anchor.Type anchorType,
			double gap) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T stackToRW(Anchor.Type anchorObj, Boxable dstObj, Anchor.Type anchorType,
			double gap) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T stackTo(Anchor.Type anchorObj, Boxable dstObj, Anchor.Type anchorType, double hgap,
			double vgap) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T stackTo(Anchor.Type anchorObj, Boxable dstObj, Anchor.Type anchorType, double gap) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T rotate(double angle) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T rotate(Point center, double angle) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T scale(Point scaleCenter, double sx, double sy, double sz) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T scale(double s) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T scale(double sx, double sy) {
		return (T) this;
	}

	@Override
	public <T extends MathObject> T shift(Vec shiftVector) {
		return (T) this;
	}

	/**
	 * Returns a proper Mathobject to work with animations
	 *
	 * @return
	 */
	public abstract MathObject getMathObject();

	@Override
	public Stylable getMp() {
		return getMathObject().getMp();
	}

	@Override
	public void update(JMathAnimScene scene) {
		rebuildShape();
	}

	abstract public void rebuildShape();

	@Override
	public Rect getBoundingBox() {
		rebuildShape();
		return getMathObject().getBoundingBox();
	}

}
