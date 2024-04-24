/*
 * Copyright (C) 2024 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Utils;

import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.mathobjects.Text.AbstractLaTeXMathObject;
import java.util.ArrayList;

/**
 * This class assign colors to elements of LaTeXMathObject, according to their
 * identifying tokens
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LatexStyle {

    private final ArrayList<LatexStyleItem> latexStyleItems;

    public static LatexStyle make() {
        return new LatexStyle();
    }

    public LatexStyle() {
        this.latexStyleItems = new ArrayList<>();
    }

    public LatexStyle setColorTo(LatexToken.TokenType type, Integer secType, String name, String colorName) {
        LatexStyleItem colorizerItem = new LatexStyleItem();
        colorizerItem.tokenEq = new LatexToken(type, secType, name);
        colorizerItem.setColor(colorName);
        latexStyleItems.add(colorizerItem);
        return this;
    }

    public LatexStyle setColorToChar(String name, String colorName) {
        return setColorToChar(name, JMColor.parse(colorName));
    }

    public LatexStyle setColorToChar(String name, PaintStyle colorName) {
        LatexStyleItem colorizerItem = new LatexStyleItem();
        colorizerItem.tokenEq = new LatexToken(LatexToken.TokenType.CHAR, name);
        colorizerItem.setColor(colorName);
        latexStyleItems.add(colorizerItem);
        return this;
    }

    public void apply(AbstractLaTeXMathObject latex) {
        for (LatexStyleItem colorizerItem : latexStyleItems) {
            colorizerItem.apply(latex);

        }
    }

    public boolean add(LatexStyleItem e) {
        return latexStyleItems.add(e);
    }

    public LatexStyle copy() {
        LatexStyle copy = new LatexStyle();
        for (LatexStyleItem colorizerItem : latexStyleItems) {
            copy.add(colorizerItem.copy());
        }
        return copy;
    }

    public LatexStyleItem get(int index) {
        return latexStyleItems.get(index);
    }

    public ArrayList<LatexStyleItem> getLatexStyleItems() {
        return latexStyleItems;
    }

}
