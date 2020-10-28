/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Dot;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformedPoint extends Dot{

    private AffineJTransform transform;
    private final Dot dstPoint;
    
    public TransformedPoint(Dot p,AffineJTransform tr) {
        super();
        this.dstPoint=p;
        this.transform=tr;
    }

    public AffineJTransform getTransform() {
        return transform;
    }

    public void setTransform(AffineJTransform transform) {
        this.transform = transform;
    }

    @Override
    public int getUpdateLevel() {
        return dstPoint.getUpdateLevel()+1;
    }

    @Override
    public void update(JMathAnimScene scene) {
        Dot tempPoint = transform.getTransformedObject(this.dstPoint);
        this.v.x=tempPoint.v.x;
        this.v.y=tempPoint.v.y;
        this.v.z=tempPoint.v.z;
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        scene.registerObjectToBeUpdated(this.dstPoint);
    }
    
    
}
