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
package com.jmathanim.Constructible;

import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;
import com.jmathanim.mathobjects.updateableObjects.Updateable;

/**
 *
 * @author David
 */
public class CTLaTeX extends Constructible {

    private final LaTeXMathObject tex;
    private final Anchor.Type anchorType;

    /**
     * Creates a LaTeX text anchored to a given point
     *
     * @param text Text to show, in math LaTeX format
     * @param anchor Anchor point
     * @param anchorType Anchor type. For example a LEFT anchor will put the
     * text so that its LEFT side is aligned with anchor point.
     * @return The created object
     */
    public static CTLaTeX make(String text, CTPoint anchor, Anchor.Type anchorType) {
        CTLaTeX resul = new CTLaTeX(text, anchor, anchorType);
        return resul;
    }
    private final CTPoint anchor;

    private CTLaTeX(String text, CTPoint anchor, Anchor.Type anchorType) {
        tex = LaTeXMathObject.make(text);
        this.anchorType = anchorType;
        this.anchor = anchor;
    }

    @Override
    public MathObject getMathObject() {
        return tex;
    }

    @Override
    public void rebuildShape() {
        tex.stackTo(anchorType, anchor, Anchor.Type.CENTER, 0);
    }

    @Override
    public CTLaTeX copy() {
        CTLaTeX copy = make(tex.getText(), anchor, this.anchorType);
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        tex.draw(scene, r);
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(this.anchor);
    }
}
