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

import com.jmathanim.Enum.LatexTokenType;
import com.jmathanim.MathObjects.Text.AbstractLatexMathObject;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.PaintStyle;

import java.util.ArrayList;

/**
 * This class assign colors to elements of LaTeXMathObject, according to their
 * identifying tokens
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LatexStyle {

    private final ArrayList<LatexStyleItem> latexStyleItems;

    public LatexStyle() {
        this.latexStyleItems = new ArrayList<>();
    }

    public static LatexStyle make() {
        return new LatexStyle();
    }

    public LatexStyle setColorTo(LatexTokenType type, Integer secType, String name, String colorName) {
        LatexStyleItem colorizerItem = new LatexStyleItem();
        LatexToken token = LatexToken.make().setType(type).setSecondaryTypeFlag(secType).setString(name);
        colorizerItem.mustMatchTo(token);
        colorizerItem.setColor(colorName);
        latexStyleItems.add(colorizerItem);
        return this;
    }

    /**
     * Adds a match to a specific char in the token list, applying the given color to matching tokens.
     * Bear in mind that this char is for math mode only. Non-math-char are ignored
     *
     * @param charStr Character to match
     * @param colorName String representing color.
     * @return This LatexStyle object
     */
    public LatexStyle setColorToChar(String charStr, String colorName) {
        return setColorToChar(charStr, JMColor.parse(colorName));
    }
    /**
     * Adds a match to a specific char in the token list, applying given style to matching tokens.
     * Bear in mind that this char is for math mode only. Non-math-char are ignored
     *
     * @param charStr Character to match
     * @param paintStyle PaintStyle to apply to matching tokens
     * @return This LatexStyle object
     */
    public LatexStyle setColorToChar(String charStr, PaintStyle paintStyle) {
        LatexStyleItem latexStyleItem = new LatexStyleItem();
        LatexToken token = LatexToken.make().setType(LatexTokenType.CHAR).setString(charStr);
        latexStyleItem.mustMatchTo(token);
        latexStyleItem.setColor(paintStyle);
        latexStyleItems.add(latexStyleItem);
        return this;
    }

    /**
     * Apply this style to the given AbstractLaTeXMathObject object. LaTeX code
     * must be compiled using the JLaTeXMath option to work, as it need its
     * parsing capabilities.
     *
     * @param latex LaTeX expression to parse and colouring
     */
    public void apply(AbstractLatexMathObject latex) {
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
