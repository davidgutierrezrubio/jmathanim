/*
 * Copyright (C) 2022 David Gutierrez Rubio
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
package com.jmathanim.Constructible.Conics;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * Creates the only ellipse with 2 given focus that pass through a third point
 *
 * @author David Gutierrez Rubio
 */
public class CTEllipse extends Constructible {
    
    CTPoint focus1, focus2, A;
    private final Shape originalShape;
    private final Shape ellipseToDraw;

    /**
     * Creates a constructible ellipse with given focus and a point of the
     * ellipse
     *
     * @param focus1 First focus
     * @param focus2 Second focus
     * @param A A point from the ellipse
     * @return The created constructible ellipse
     */
    public static CTEllipse make(CTPoint focus1, CTPoint focus2, CTPoint A) {
        CTEllipse resul = new CTEllipse(focus1, focus2, A);
        resul.rebuildShape();
        return resul;
    }

    /**
     * Overloaded method. Creates a constructible ellipse with given focus and a
     * point of the ellipse
     *
     * @param focus1 First focus
     * @param focus2 Second focus
     * @param A A point from the ellipse
     * @return The created constructible ellipse
     */
    public static CTEllipse make(Point focus1, Point focus2, Point A) {
        CTEllipse resul = new CTEllipse(CTPoint.make(focus1), CTPoint.make(focus2), CTPoint.make(A));
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a constructible ellipse with given focus and a point of the
     * ellipse
     *
     * @param focus1 First focus
     * @param focus2 Second focus
     * @param A A point from the ellipse
     * @return The created constructible ellipse
     */
    private CTEllipse(CTPoint focus1, CTPoint focus2, CTPoint A) {
        this.focus1 = focus1;
        this.focus2 = focus2;
        this.A = A;
        originalShape = Shape.circle();
        ellipseToDraw = Shape.circle();
    }
    
    @Override
    public Shape getMathObject() {
        return ellipseToDraw;
    }
    
    @Override
    public void rebuildShape() {
        double centerToFocus = focus1.to(focus2).norm() / 2;
        double d = focus1.to(A).norm() + focus2.to(A).norm();
        double minAxis = Math.sqrt(.25 * d * d - centerToFocus * centerToFocus);
        double maxAxis = d / 2;
        
        Point centerEllipse = focus1.getMathObject().interpolate(focus2.getMathObject(), .5);
        Vec centerToRightPoint = centerEllipse.to(focus2.getMathObject()).normalize();
        Point rightPoint = centerEllipse.add(centerToRightPoint.mult(maxAxis));
        Vec centerToUpperPoint = Vec.to(-centerToRightPoint.y, centerToRightPoint.x);//Rotated 90 degrees
        Point upperPoint = centerEllipse.add(centerToUpperPoint.mult(minAxis));

        //Now we "reset" the shape to draw to a unit circle and apply a linear transformation
        for (int i = 0; i < ellipseToDraw.size(); i++) {
            JMPathPoint get = ellipseToDraw.get(i);
            get.copyFrom(originalShape.get(i));
        }

        //Create the affine transformation by 3 points: center, right and upper
        AffineJTransform tr = AffineJTransform.createAffineTransformation(Point.origin(), Point.at(1, 0), Point.at(0, 1), centerEllipse, rightPoint, upperPoint, 1);
        tr.applyTransform(ellipseToDraw);
    }
    
    @Override
    public CTEllipse copy() {
        CTEllipse copy = CTEllipse.make(focus1.copy(), focus2.copy(), A.copy());
        copy.copyStateFrom(this);
        return copy;
    }
    
    @Override
    public void copyStateFrom(MathObject obj) {
        this.getMp().copyFrom(obj.getMp());
        super.copyStateFrom(obj);
    }
    
    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, this.focus1, this.focus2, this.A);
    }
}
