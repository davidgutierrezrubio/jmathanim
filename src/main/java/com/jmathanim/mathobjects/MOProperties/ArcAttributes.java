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

package com.jmathanim.mathobjects.MOProperties;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Dot;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class ArcAttributes extends MathObjectAttributes {

    public Dot center;
    public double radius, angle;
    public Shape arc;
    private double radiusState, angleState;

    public ArcAttributes(Dot center, double radius, double angle, Shape arc) {
        super(arc);
        this.center = center;
        this.radius = radius;
        this.angle = angle;
        this.arc = arc;
    }

    @Override
    public void applyTransform(AffineJTransform tr) {
        tr.applyTransform(center);
        double sum = 0;
        for (Dot p : arc.getPath().getPoints()) {
            sum += center.to(p).norm();
        }
        radius = sum / arc.getPath().size();
    }

    @Override
    public void saveState() {
        center.saveState();
        radiusState = radius;
        angleState = angle;

    }

    @Override
    public void restoreState() {
        center.restoreState();
        radius = radiusState;
        angle = angleState;
    }

    @Override
    public MathObjectAttributes copy() {
        return new ArcAttributes(center.copy(), radius, angle, null);
    }

    @Override
    public void setParent(MathObject parent) {
        arc = (Shape) parent;
    }
}
