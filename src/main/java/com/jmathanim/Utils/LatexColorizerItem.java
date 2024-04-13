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
import com.jmathanim.Styling.Stylable;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.Text.AbstractLaTeXMathObject;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LatexColorizerItem {

    public LatexToken tokenEq = null;
    public LatexToken tokenDif = null;
    public LatexToken tokenEqPrev = null;
    public LatexToken tokenDifPrev = null;
    public LatexToken tokenEqAfter = null;
    public LatexToken tokenDifAfter = null;
    public MODrawProperties style;

    public static LatexColorizerItem equalsChar(String equal, String color) {
        return equalsChar(null, equal, null, color);
    }

    public static LatexColorizerItem equalsChar(String before, String equal, String after, String color) {
        LatexColorizerItem resul = new LatexColorizerItem();
        resul.tokenEqPrev = new LatexToken(null, before);
        resul.tokenEq = new LatexToken(null, equal);
        resul.tokenEqAfter = new LatexToken(null, after);
        JMColor col = JMColor.parse(color);

        resul.style.setDrawColor(col);
        resul.style.setFillColor(col);

        return resul;
    }

    public LatexColorizerItem() {
//       this.tokenDif=new LatexToken(null, null, null);
//       this.tokenEq=new LatexToken(null, null, null);
//       this.tokenEqPrev=new LatexToken(null, null, null);
//       this.tokenEqAfter=new LatexToken(null, null, null);
//       this.tokenDif=new LatexToken(null, null, null);
//       this.tokenDifAfter=new LatexToken(null, null, null);
//       this.tokenDifPrev=new LatexToken(null, null, null);
        this.style = JMathAnimConfig.getConfig().getStyles().get("LATEXDEFAULT").copy();
    }

    public void setColor(String colorName) {
        JMColor col = JMColor.parse(colorName);
        style.setDrawColor(col);
        style.setFillColor(col);
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
        if (latexParser==null) {
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
            LatexToken tokPrev = (i > 0 ? tokens.get(i - 1) : new LatexToken(LatexToken.TokenType.NONE, LatexToken.SecondaryType.NONE, ""));
            LatexToken token = tokens.get(i);
            LatexToken tokAfter = (i < tokens.size() - 1 ? tokens.get(i + 1) : new LatexToken(LatexToken.TokenType.NONE, LatexToken.SecondaryType.NONE, ""));

            if (match(tokPrev, token, tokAfter)) {
                latexShape.getMp().copyFrom(style);
            }

        }
    }
    

}
