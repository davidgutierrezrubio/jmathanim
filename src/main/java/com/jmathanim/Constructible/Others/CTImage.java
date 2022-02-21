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

import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMImage;
import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David
 */
public class CTImage extends FixedConstructible {

    private JMImage image;
    private CTPoint A;
    private CTPoint B;

    public static CTImage make(CTPoint A, CTPoint B, JMImage image) {
        return new CTImage(A, B, image);
    }

    private CTImage(CTPoint A, CTPoint B, JMImage image) {
        this.image = image;
        this.A = A;
        this.B = B;
    }

    @Override
    public MathObject getMathObject() {
        return image;
    }

    @Override
    public void rebuildShape() {
        image.adjustTo(A.getMathObject(), B.getMathObject());
    }

    @Override
    public CTImage copy() {
        return make(A.copy(), B.copy(), image.copy());
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        image.draw(scene, r);
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(this.A, this.B);
        setUpdateLevel(Math.max(this.A.getUpdateLevel(), this.B.getUpdateLevel()) + 1);
    }
}