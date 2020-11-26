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

import com.jmathanim.Utils.Anchor.Type;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class AnchoredMathObject implements Updateable {

    private final MathObject mobj;
    private final MathObject dstObject;
    private Type anchorType;
    private double gap;

    public AnchoredMathObject(MathObject mobj, MathObject dstObject, Type anchorType) {
        this(mobj, dstObject, anchorType, 0);
    }

    public AnchoredMathObject(MathObject mobj, MathObject dstObject, Type anchorType, double gap) {
        this.mobj = mobj;
        this.dstObject = dstObject;
        this.anchorType = anchorType;
        this.gap = gap;
    }

    public double getGap() {
        return gap;
    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    @Override
    public int getUpdateLevel() {
        return Math.max(this.mobj.getUpdateLevel(), dstObject.getUpdateLevel()) + 1;
    }

    @Override
    public void update(JMathAnimScene scene) {
        mobj.stackTo(dstObject, anchorType, gap);
    }

    public Type getAnchorType() {
        return anchorType;
    }

    public void setAnchorType(Type anchorType) {
        this.anchorType = anchorType;
    }

}
