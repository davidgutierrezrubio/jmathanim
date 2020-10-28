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
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Dot;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class TransformedJMPath extends Shape{

    private final AffineJTransform transform;
    private final Shape srcOBj;

    public TransformedJMPath(Shape jmpobj, AffineJTransform tr) {
        super();
        this.transform=tr;
        this.srcOBj=jmpobj;
        this.jmpath.addJMPoints(jmpobj.jmpath);
    }

    @Override
    public int getUpdateLevel() {
        return srcOBj.getUpdateLevel()+1;
    }

    @Override
    public void update(JMathAnimScene scene) {
          int size = srcOBj.jmpath.size();
          //TODO: This is already implemented
            for (int n = 0; n < size; n++) {
                JMPathPoint jmPDst = getJMPoint(n);
                JMPathPoint pSrc = srcOBj.getJMPoint(n);
                Dot pDst = transform.getTransformedObject(pSrc.p);
                Dot cp1Dst = transform.getTransformedObject(pSrc.cp1);
                Dot cp2Dst = transform.getTransformedObject(pSrc.cp2);

                jmPDst.p.v.x = pDst.v.x;
                jmPDst.p.v.y = pDst.v.y;
                jmPDst.p.v.z = pDst.v.z;

                jmPDst.cp1.v.x = cp1Dst.v.x;
                jmPDst.cp1.v.y = cp1Dst.v.y;
                jmPDst.cp1.v.z = cp1Dst.v.z;

                jmPDst.cp2.v.x = cp2Dst.v.x;
                jmPDst.cp2.v.y = cp2Dst.v.y;
                jmPDst.cp2.v.z = cp2Dst.v.z;
            }
        }
        

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
        scene.registerObjectToBeUpdated(srcOBj);
    }
    
    
}
