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

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LatexToken {

    public boolean math;

    public enum TokenType {
        CHAR, NUMBER, SYMBOL, OPERATOR, RELATION, DELIMITER, SQRT, FRACTIONBAR, GREEKLETTER, NAMED_FUNCTION
    }
    public TokenType type;

    public String name;

    public LatexToken(TokenType type, String name, boolean math) {
        this.type = type;
        this.name = name;
        this.math = math;
        refineToken();
    }

    private void refineToken() {
        switch (type) {
            case SYMBOL:
                if (greekLetters.contains(name)) {
                    type = TokenType.GREEKLETTER;
                    break;
                }
                if (operators.contains(name)) {
                    type = TokenType.OPERATOR;
                    break;
                }
                if (delimiters.contains(name)) {
                    type = TokenType.DELIMITER;
                    break;
                }
                if (relations.contains(name)) {
                    type = TokenType.RELATION;
                    break;
                }
            case CHAR:
                if (numbers.contains(name)) {
                    type = TokenType.NUMBER;
                    break;
                }
            default:

        }

    }

    @Override
    public String toString() {
        if (math) {
            return "LatexTokenMath[" + type + ", " + name + "]";
        } else {
            return "LatexToken[" + type + ", " + name + "]";
        }
    }
    List<String> greekLetters = Arrays.asList(
            "Alpha", "alpha",
            "Beta", "beta",
            "Gamma", "gamma",
            "Delta", "delta",
            "Epsilon", "epsilon", "varepsilon",
            "Zeta", "zeta",
            "Eta", "eta",
            "Theta", "theta",
            "Iota", "iota",
            "Kappa", "kappa",
            "Lambda", "lambda",
            "Mu", "mu",
            "Nu", "nu",
            "Xi", "xi",
            "Omicron", "omicron",
            "Pi", "pi",
            "Rho", "rho",
            "Sigma", "sigma",
            "Tau", "tau",
            "Upsilon", "upsilon",
            "Phi", "phi", "varphi",
            "Chi", "chi",
            "Psi", "psi",
            "Omega", "omega"
    );

    List<String> numbers = Arrays.asList(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    );

    List<String> operators = Arrays.asList(
            "plus", "minus", "slash", "div",
            "cap", "cup", "wedge", "vee","sum","prod"
    );
    List<String> relations = Arrays.asList(
            "equals", "lt", "leq", "le", "gt", "geq", "ge",
            "neq", "equiv", "cong", "sim", "simeq",
            "approx", "propto", "parallel", "perp",
            "subset", "subseteq", "supset", "supseteq",
            "in", "notin", "ni", "mapsto", "rightarrow",
            "Rightarrow", "longrightarrow", "Leftarrow",
            "leftrightarrow", "Leftrightarrow", "implies", "iff",
            "forall", "exists", "mid", "nmid"
    );

    List<String> delimiters = Arrays.asList(
            "lbrack", "rbrack",
            "lsqbrack", "rsqbrack",
            "lbrace", "rbrace"
    );
}
