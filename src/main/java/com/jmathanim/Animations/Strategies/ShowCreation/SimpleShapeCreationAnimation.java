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
package com.jmathanim.Animations.Strategies.ShowCreation;

import com.jmathanim.mathobjects.AbstractShape;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;

/**
 * An animation that draws a Shape
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SimpleShapeCreationAnimation extends AbstractCreationStrategy {

    private final AbstractShape<?> originShape;
    private final Shape originShapeBase;
    private final AbstractShape<?> intermediateShape;

    public SimpleShapeCreationAnimation(double runtime, AbstractShape<?> originShape) {
        super(runtime);
        this.originShape =originShape;
        this.originShapeBase =new Shape();//Generates a static Shape copy of the AbstractShape we want to show creation
        this.originShapeBase.copyStateFrom(originShape);
        intermediateShape = new Shape();
        intermediateShape.objectLabel=originShape.objectLabel+"_intermediate";
        intermediateShape.getMp().copyFrom(originShape.getMp());
    }

     @Override
    public boolean doInitialization() {
        super.doInitialization();
        
        return true;

    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        double lt = getLT(t);
        intermediateShape.copyStateFrom(this.originShapeBase.getSubShape(0, lt).visible(lt > 0));
    }


    @Override
    public void cleanAnimationAt(double t) {
        double lt = getLT(t);
        if (lt == 0) {//Ended at t=0, nothing remains...
            removeObjectsFromScene(this.originShape, intermediateShape);
            return;
        }
        if (lt == 1) {//Only remains the full line
            removeObjectsFromScene(intermediateShape);
            addObjectsToscene(this.originShape);
            removeObjectsFromScene(removeThisAtTheEnd);
            return;
        }
        //0<t<1, only remains the created segment
        removeObjectsFromScene(this.originShape);
        addObjectsToscene(intermediateShape);
    }

    @Override
    public void prepareForAnim(double t) {
        addObjectsToscene(intermediateShape);
        removeObjectsFromScene(originShape);
    }

    @Override
    public MathObject getIntermediateObject() {
        return intermediateShape;
    }
}
