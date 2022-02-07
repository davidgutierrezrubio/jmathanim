/*
 * Copyright (C) 2022 David
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
package com.jmathanim.Animations;

import com.jmathanim.mathobjects.hasScalarParameter;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author David
 */
public class ScalarAnimation extends Animation{

    hasScalarParameter scalarToAnimate;
    private final double b;
    private final double a;

    public ScalarAnimation(double runTime,hasScalarParameter scalarToAnimate,double a, double b) {
        super(runTime);
        this.scalarToAnimate=scalarToAnimate;
        this.a=a;
        this.b=b;
        
    }
    
    
    @Override
    public void doAnim(double t) {
        double lt=lambda.applyAsDouble(t);
        scalarToAnimate.setScalar(a+(b-a)*lt);
    }
    
}
