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
import com.jmathanim.MathObjects.Text.LatexShape;
import com.jmathanim.Styling.JMColor;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LatexStyleItem {

    private LatexToken tokenEq = null;
    private LatexToken tokenDif = null;
    private LatexToken tokenEqPrev = null;
    private LatexToken tokenDifPrev = null;
    private LatexToken tokenEqAfter = null;
    private LatexToken tokenDifAfter = null;
    private MODrawProperties style;

    /**
     * Returns a LatexStyleItem that matches the given glyph name, assigning the color specified
     * @param name Name glyph to match
     * @param color Color to assign, a String
     * @return The LatexStyleItem created
     */
    public static LatexStyleItem equalsChar(String name, JMColor color) {
          LatexStyleItem resul = new LatexStyleItem();
        resul.tokenEqPrev = LatexToken.make().setString(name);
        resul.setColor(color);
        return resul;
    }

    public static LatexStyleItem equalsChar(String name, String color) {
        return equalsChar(name, JMColor.parse(color));
    }

    public static LatexStyleItem make(JMColor color) {
        if (color != null) {
            return new LatexStyleItem(color);
        } else {
            return new LatexStyleItem();
        }
    }

    public static LatexStyleItem make(String color) {
        return make(JMColor.parse(color));
    }

    public LatexStyleItem() {
        this(null);
    }

    public LatexStyleItem(PaintStyle color) {
        this.style = JMathAnimConfig.getConfig().getStyles().get("LATEXDEFAULT").copy();
        if (color != null) {
            this.style.setDrawColor(color);
            this.style.setFillColor(color);
        }
    }

    public LatexStyleItem copy() {
        LatexStyleItem copyObject = new LatexStyleItem();
        copyObject.tokenEq = this.tokenEq.copy();
        copyObject.tokenEqAfter = this.tokenEqAfter.copy();
        copyObject.tokenEqPrev = this.tokenEqPrev.copy();
        copyObject.tokenDif = this.tokenDif.copy();
        copyObject.tokenDifAfter = this.tokenDifAfter.copy();
        copyObject.tokenDifPrev = this.tokenDifPrev.copy();
        copyObject.style = copyObject.style.copy();
        return copyObject;
    }

    public LatexStyleItem setColor(String colorName) {
        JMColor col = JMColor.parse(colorName);
        style.setDrawColor(col);
        style.setFillColor(col);
        return this;
    }

    public LatexStyleItem setColor(PaintStyle paintStyle) {
        style.setDrawColor(paintStyle);
        style.setFillColor(paintStyle);
        return this;
    }

    public boolean match(LatexToken tokPrev, LatexToken tok, LatexToken tokAfter) {
        boolean result = true;
        if (tokPrev != null) {
            if ((tokenEqPrev != null) && (!tokenEqPrev.match(tokPrev))) {
                return false;
            }
            if ((tokenDifPrev != null) && (!tokenDifPrev.differs(tokPrev))) {
                return false;
            }
        }
        if (tok != null) {
            if ((tokenEq != null) && (!tokenEq.match(tok))) {
                return false;
            }
            if ((tokenDif != null) && (!tokenDif.differs(tok))) {
                return false;
            }
        }
        if (tokAfter != null) {
            if ((tokenEqAfter != null) && (!tokenEqAfter.match(tokAfter))) {
                return false;
            }
            return (tokenDifAfter == null) || (tokenDifAfter.differs(tokAfter));
        }
        return true;//All Ok!
    }

    public LatexStyleItem mustMatchTo(LatexToken token) {
        tokenEq = token;
        return this;
    }

    public LatexStyleItem mustDifferFrom(LatexToken token) {
        tokenDif = token;
        return this;
    }

    public LatexStyleItem previousTokenMustMatchTo(LatexToken token) {
        tokenEqPrev = token;
        return this;
    }

    public LatexStyleItem previousTokenMustDifferFrom(LatexToken token) {
        tokenDifPrev = token;
        return this;
    }

    public LatexStyleItem nextTokenMustMatchTo(LatexToken token) {
        tokenEqAfter = token;
        return this;
    }

    public LatexStyleItem nextTokenMustDifferFrom(LatexToken token) {
        tokenDifAfter = token;
        return this;
    }

    /**
     * Apply this conditional style to all shapes of a LaTeX object
     *
     * @param latex An AbstractLaTeXMathObject representing a formula
     */
    public void apply(AbstractLatexMathObject<?> latex) {
        LatexParser latexParser = latex.getLatexParser();
        if (latexParser == null) {
            JMathAnimScene.logger.warn("This LaTeXMathObject has no parser. It cannot be coloured, sorry!");
            return;
        }
        ArrayList<LatexToken> tokens = latexParser.getTokensList();
        if (tokens == null) {
            JMathAnimScene.logger.warn("Cannot apply style to a LaTeX without assigned tokens");
            return;
        }
        if (tokens.size() != latex.size()) {
            JMathAnimScene.logger.warn("Number of LaTeX shapes and tokens differ. Cannot continue. Please don't blame the poor programmer!");
            return;
        }

        for (int i = 0; i < latex.size(); i++) {
            LatexShape latexShape = latex.get(i);
            LatexToken tokPrev = (i > 0 ? tokens.get(i - 1) : LatexToken.make(LatexTokenType.NONE, LatexToken.SEC_NONE, ""));
            LatexToken token = tokens.get(i);
            LatexToken tokAfter = (i < tokens.size() - 1 ? tokens.get(i + 1) : LatexToken.make(LatexTokenType.NONE, LatexToken.SEC_NONE, ""));

            if (match(tokPrev, token, tokAfter)) {
                latexShape.getMp().copyFrom(style);

            }
            for (int k = 1; k <= i; k++) {
                if (tokens.get(i - k).isTakesStyleFromNext()) {
                    latex.get(i - k).getMp().copyFrom(latexShape.getMp());
                } else {
                    break;
                }
            }

        }
    }

    public MODrawProperties getStyle() {
        return style;
    }

    public void setStyle(MODrawProperties style) {
        this.style = style;
    }

}
