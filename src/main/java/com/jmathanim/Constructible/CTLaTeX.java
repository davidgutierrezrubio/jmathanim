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
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Text.LatexMathObject;

/**
 * A constructible LaTeX expression, anchored to a CTPoint
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTLaTeX extends Constructible<CTLaTeX> {

    private final LatexMathObject tex;
    private final AnchorType anchorType;
    private final double gap;

    /**
     * Creates a LaTeX text anchored to a given point
     *
     * @param text Text to show, in math LaTeX format
     * @param anchor Anchor point
     * @param anchorType Anchor type. For example a LEFT anchor will put the
     * text so that its LEFT side is aligned with anchor point.
     * @param gap Gap between anchor and text
     * @return The created object
     */
    public static CTLaTeX make(String text, CTPoint anchor, AnchorType anchorType, double gap) {
        CTLaTeX resul = new CTLaTeX(text, anchor, anchorType, gap);
        return resul;
    }
    private final CTPoint anchor;
    private boolean visible;

    private CTLaTeX(String text, CTPoint anchor, AnchorType anchorType, double gap) {
        tex = LatexMathObject.make(text);
        this.gap = gap;
        this.anchorType = anchorType;
        this.anchor = anchor;
        this.visible = true;
    }

    @Override
    public LatexMathObject getMathObject() {
        return tex;
    }

    @Override
    public void rebuildShape() {
        if (!isFreeMathObject()) {
            if (anchor.isNaN()) {
                //If anchor is NaN point, to prevent the shape to be completely NaN
                //we made it invisible
                visible = tex.isVisible();
                tex.visible(false);
            } else {
                tex.visible(true);
                tex.stackTo(anchorType, anchor, AnchorType.CENTER, this.gap);
            }
        }
    }

    @Override
    public CTLaTeX copy() {
        CTLaTeX copy = make(tex.getText(), anchor, this.anchorType, this.gap);
        copy.tex.copyStateFrom(tex);
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, this.anchor);
    }
}
