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
package com.jmathanim.mathobjects;

import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.EmptyRect;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * A null MathObject. This object should be used when the result of an operation
 * gives an undetermined object (like an undefined point)
 *
 * @author David Gutierrez Rubio
 */
public class NullMathObject extends FixedConstructible {

    @Override
    public NullMathObject copy() {
        return new NullMathObject();
    }

    @Override
    public void copyStateFrom(MathObject obj) {
    }

    @Override
    public Rect getBoundingBox() {
        return new EmptyRect();
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
    }

    @Override
    public MathObject getMathObject() {
        return this;
    }

    @Override
    public void rebuildShape() {
    }

    @Override
    public Stylable getMp() {
        return MODrawProperties.makeNullValues();
    }

    @Override
    public int getUpdateLevel() {
        return super.getUpdateLevel(); //To change body of generated methods, choose Tools | Templates.
    }

}