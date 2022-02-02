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
package com.jmathanim.Constructible.Points;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;

/** 
 * Represents an intersection point of lines, rays, or circles
 * @author David
 */
public class ConstrIntersectionPoint extends FixedConstructible{
    private final ConstrPoint point1,point2;
    private final Constructible c2;
    private final Constructible c1;
    private final MathObjectGroup points;
    
    public static ConstrIntersectionPoint make(Constructible c1, Constructible c2) {
        ConstrIntersectionPoint resul=new ConstrIntersectionPoint(c1,c2);
        resul.rebuildShape();
        return resul;
    }
    
    
    
    private ConstrIntersectionPoint(Constructible c1, Constructible c2) {
        this.c1=c1;
        this.c2=c2;
        point1=ConstrPoint.make(Point.at(0,0));
        point2=ConstrPoint.make(Point.at(0,0));
        points=MathObjectGroup.make(point1,point2);
    }
    
    @Override
    public MathObject getMathObject() {
        return points;
    }

    @Override
    public void rebuildShape() {
        //TODO: Implement intersection algorithms for:
        //Line-Line
        //Line-Ray
        //Line-Segment
        //Segment-Ray
        //Segment-Segment
        //Circle
        point1.getMathObject().copyFrom(Point.at(0,.5));//Debug values to show on screen
        point2.getMathObject().copyFrom(Point.at(0,-.5));
    }

    @Override
    public ConstrIntersectionPoint copy() {
        return make(c1.copy(),c2.copy());
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        point1.draw(scene,r);
        point2.draw(scene,r);
    }
    
}
