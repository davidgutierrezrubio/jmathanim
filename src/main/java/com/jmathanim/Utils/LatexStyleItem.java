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
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Styling.PaintStyle;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.Text.AbstractLaTeXMathObject;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LatexStyleItem {

    public LatexToken tokenEq = null;
    public LatexToken tokenDif = null;
    public LatexToken tokenEqPrev = null;
    public LatexToken tokenDifPrev = null;
    public LatexToken tokenEqAfter = null;
    public LatexToken tokenDifAfter = null;
    public MODrawProperties style;

    public static LatexStyleItem equalsChar(String equal, String color) {
        return equalsChar(null, equal, null, color);
    }

    public static LatexStyleItem equalsChar(String before, String equal, String after, String color) {
        LatexStyleItem resul = new LatexStyleItem();
        resul.tokenEqPrev = new LatexToken(null, before);
        resul.tokenEq = new LatexToken(null, equal);
        resul.tokenEqAfter = new LatexToken(null, after);
        JMColor col = JMColor.parse(color);

        resul.style.setDrawColor(col);
        resul.style.setFillColor(col);

        return resul;
    }

    public static LatexStyleItem make(String color) {
        if (color != null) {
            return new LatexStyleItem(JMColor.parse(color));
        } else {
            return new LatexStyleItem();
        }
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
            if ((tokenDifAfter != null) && (!tokenDifAfter.differs(tokAfter))) {
                return false;
            }
        }
        return true;//All Ok!
    }
//  public boolean match(LatexToken tokPrev, LatexToken tok, LatexToken tokAfter) {
//        boolean result = true;
//        if (tokPrev != null) {
//            if ((tokenEqPrev != null) && (!tokPrev.match(tokenEqPrev))) {
//                return false;
//            }
//            if ((tokenDifPrev != null) && (!tokPrev.differs(tokenDifPrev))) {
//                return false;
//            }
//        }
//        if (tok != null) {
//            if ((tokenEq != null) && (!tok.match(tokenEq))) {
//                return false;
//            }
//            if ((tokenDif != null) && (!tok.differs(tokenDif))) {
//                return false;
//            }
//        }
//        if (tokAfter != null) {
//            if ((tokenEqAfter != null) && (!tokAfter.match(tokenEqAfter))) {
//                return false;
//            }
//            if ((tokenDifAfter != null) && (!tokAfter.differs(tokenDifAfter))) {
//                return false;
//            }
//        }
//        return true;//All Ok!
//    }

    public boolean match(LatexToken tok) {
        return match(null, tok, null);
    }

    /**
     * Apply this conditional style to all shapes of a LaTeX object
     *
     * @param latex An AbstractLaTeXMathObject representing a formula
     */
    public void apply(AbstractLaTeXMathObject latex) {
        LatexParser latexParser = latex.getLatexParser();
        if (latexParser == null) {
            JMathAnimScene.logger.warn("This LaTeXMathObject has no parser, cannot colorize, sorry!");
            return;
        }
        ArrayList<LatexToken> tokens = latexParser.assignedTokens;
        if (tokens == null) {
            JMathAnimScene.logger.warn("Cannot apply style to a LaTeX without assigned tokens");
            return;
        }
        if (tokens.size() != latex.size()) {
            JMathAnimScene.logger.warn("Number of LaTeX shapes and tokens differ. Cannot continue. Please don't blame the poor programmer!");
            return;
        }

        for (int i = 0; i < latex.size(); i++) {
            Shape latexShape = latex.get(i);
            LatexToken tokPrev = (i > 0 ? tokens.get(i - 1) : new LatexToken(LatexToken.TokenType.NONE, LatexToken.SEC_NONE, ""));
            LatexToken token = tokens.get(i);
            LatexToken tokAfter = (i < tokens.size() - 1 ? tokens.get(i + 1) : new LatexToken(LatexToken.TokenType.NONE, LatexToken.SEC_NONE, ""));

            if (match(tokPrev, token, tokAfter)) {
                latexShape.getMp().copyFrom(style);

            }
            for (int k = 1; k <= i; k++) {
                if (tokens.get(i - k).takesStyleFromNext) {
                    latex.get(i - k).getMp().copyFrom(latexShape.getMp());
                } else {
                    break;
                }
            }


        }
    }

}
