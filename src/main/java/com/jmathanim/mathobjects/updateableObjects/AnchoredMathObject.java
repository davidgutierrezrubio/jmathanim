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
    private Type dstType;
    private Type origType;
    private double gap;
    private int updateLevel;

    public AnchoredMathObject(MathObject mobj, Type origType, MathObject dstObject, Type dstType) {
        this(mobj, origType, dstObject, dstType, 0);
    }

    public AnchoredMathObject(MathObject mobj, Type origType, MathObject dstObject, Type dstType, double gap) {
        this.mobj = mobj;
        this.dstObject = dstObject;
        this.origType = origType;
        this.dstType = dstType;
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
        return updateLevel;
    }

    @Override
    public void update(JMathAnimScene scene) {
        mobj.stackTo(origType, dstObject, dstType, gap);
    }

    public Type getAnchorType() {
        return dstType;
    }

    public void setAnchorType(Type anchorType) {
        this.dstType = anchorType;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(mobj, dstObject);
        setUpdateLevel(Math.max(mobj.getUpdateLevel(), dstObject.getUpdateLevel()) + 1);
    }

    @Override
    public void unregisterUpdateableHook(JMathAnimScene scene) {

    }

    @Override
    public void setUpdateLevel(int level) {
        updateLevel = level;
    }
}
