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
        LatexToken token=LatexToken.make()
                .setType(type)
                .setSecondaryTypeFlag(secType)
                .setString(name);
        colorizerItem.mustMatchTo(token);
        colorizerItem.setColor(colorName);
        latexStyleItems.add(colorizerItem);
        return this;
    }

    public LatexStyle setColorToChar(String name, String colorName) {
        return setColorToChar(name, JMColor.parse(colorName));
    }

    public LatexStyle setColorToChar(String name, PaintStyle colorName) {
        LatexStyleItem colorizerItem = new LatexStyleItem();
        LatexToken token=LatexToken.make()
                .setType(LatexToken.TokenType.CHAR)
                .setString(name);
        colorizerItem.mustMatchTo(token);
        colorizerItem.setColor(colorName);
        latexStyleItems.add(colorizerItem);
        return this;
    }

    /**
     * Apply this style to the given AbstractLaTeXMathObject object. LaTeX code
     * must be compiled using the JLaTeXMath option to work, as it need its
     * parsing capabilities.
     *
     * @param latex LaTeX expression to parse and colouring
     */
    public void apply(AbstractLaTeXMathObject latex) {
        for (LatexStyleItem colorizerItem : latexStyleItems) {
            colorizerItem.apply(latex);

        }
    }

    /**
     * Add the given style item to the list
     *
     * @param styleItem The style item to add
     * @return This object
     */
    public LatexStyle add(LatexStyleItem styleItem) {
        latexStyleItems.add(styleItem);
        return this;
    }

    /**
     * Returns a copy of this LatexStyle object
     *
     * @return The copy object
     */
    public LatexStyle copy() {
        LatexStyle copy = new LatexStyle();
        for (LatexStyleItem colorizerItem : latexStyleItems) {
            copy.add(colorizerItem.copy());
        }
        return copy;
    }

    /**
     * Returns the nth style item
     *
     * @param index The index of the style item
     * @return The style item
     */
    public LatexStyleItem get(int index) {
        return latexStyleItems.get(index);
    }

    /**
     * Returns the list of Latex Style to apply
     *
     * @return An ArrayList with all the LatexStyleItem objects
     */
    public ArrayList<LatexStyleItem> getLatexStyleItems() {
        return latexStyleItems;
    }

}
