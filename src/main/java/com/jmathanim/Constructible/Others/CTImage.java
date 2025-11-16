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
package com.jmathanim.Constructible.Others;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.JMImage;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.Utils.Vec;

/**
 *
 * @author David
 */
public class CTImage extends Constructible<CTImage> {

    private final JMImage image;
    private final Vec A;
    private final Vec B;

    public static CTImage make(Coordinates<?> A, Coordinates<?> B, JMImage image) {
        return new CTImage(A, B, image);
    }

    private CTImage(Coordinates<?> A, Coordinates<?> B, JMImage image) {
        this.image = image;
        this.A = A.getVec();
        this.B = B.getVec();
        addDependency(this.A);
        addDependency(this.B);
    }

    @Override
    public MathObject getMathObject() {
        return image;
    }

    @Override
    public void rebuildShape() {
        image.adjustTo(A.getVec(), B.getVec());
    }

    @Override
    public CTImage copy() {
        CTImage copy = make(A.copy(), B.copy(), image.copy());
        copy.copyStateFrom(this);
        return this;
    }
}
