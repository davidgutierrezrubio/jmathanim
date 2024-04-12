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


    public enum TokenType {
        NONE,CHAR, NUMBER, SYMBOL, OPERATOR, RELATION, DELIMITER, SQRT, FRACTIONBAR, GREEKLETTER, NAMED_FUNCTION
    }

    public enum SecondaryType {
        NONE,
        NORMAL,
        DELIMITER_NORMAL, //A normal sized delimiter
        DELIMITER_BIG1, //A delimiter with command \big, like \big(
        DELIMITER_BIG2, //A delimiter with command \Big, like \Big(
        DELIMITER_EXTENSIBLE, //An extensible delimiter, like \left(
        SUPERSCRIPT, //Superscript style, like power exponents
        SUBSCRIPT, //Subscript style
        FROM_INDEX, //In sums, products, integrals...the "from" part
        TO_INDEX//In sums, products, integrals...the "to" part
    }
    public TokenType type;
    public SecondaryType secondaryType;

    public String name;

    public LatexToken(TokenType type, String name) {
        this(type, null, name);
    }

    public LatexToken(TokenType type, SecondaryType delimiterType, String name) {
        this.type = type;
        this.secondaryType = delimiterType;
        this.name = name;
        refineToken();
    }

    public LatexToken setSecondaryType(SecondaryType type) {
        this.secondaryType = type;
        return this;
    }

    private void refineToken() {
        if (type == null) {
            return;
        }
        switch (type) {
            case SYMBOL:
                if (greekLetters.contains(name)) {//Check if name is in my list of greek letters
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

    /**
     * Check if token characteristics match the given ones. A null parameter
     * means that no comparison is done in that parameter.
     *
     * @param type Type to match. Null if no comparison needed.
     * @param secondaryType Secondary type. Null if no comparison needed
     * @param name Name to match. Null if no comparison needed.
     * @return True if match. False otherwise.
     */
    public boolean match(TokenType type, SecondaryType secondaryType, String name) {
        boolean result = true;
        result = result && ((type == null) || (this.type == null) || (this.type == type));
        result = result && ((secondaryType == null) || (this.secondaryType == null) || (this.secondaryType == secondaryType));
        result = result && ((name == null) || (this.name == null) || (this.name.equals(name)));
        return result;
    }

    public boolean match(TokenType type, String name) {
        return match(type, null, name);
    }

    public boolean match(LatexToken tok) {
        if (tok == null) {
            return true;
        }
        return match(tok.type, tok.secondaryType, tok.name);
    }

    /**
     * Returns true if all its non-null properties are different from this token
     *
     * @param tok Token to compare
     * @return True if all non-null properties are different, false otherwise
     */
    public boolean differs(LatexToken tok) {
        if (tok == null) {
            return true;
        }
        boolean result = true;
        result = result && ((tok.type == null) || (this.type != type));
        result = result && ((tok.secondaryType == null) || (this.secondaryType != secondaryType));
        result = result && ((tok.name == null) || (!this.name.equals(name)));
        return result;
    }

    @Override
    public String toString() {
            return "LatexToken[" + type + ", " + secondaryType + "," + name + "]";
    }
    private static final List<String> greekLetters = Arrays.asList(
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

    private static final List<String> numbers = Arrays.asList(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    );

    private static final List<String> operators = Arrays.asList(
            "plus", "minus", "slash", "div",
            "cap", "cup", "wedge", "vee", "sum", "prod"
    );
    private static final List<String> relations = Arrays.asList(
            "equals", "lt", "leq", "le", "gt", "geq", "ge",
            "neq", "equiv", "cong", "sim", "simeq",
            "approx", "propto", "parallel", "perp",
            "subset", "subseteq", "supset", "supseteq",
            "in", "notin", "ni", "mapsto", "rightarrow",
            "Rightarrow", "longrightarrow", "Leftarrow",
            "leftrightarrow", "Leftrightarrow", "implies", "iff",
            "forall", "exists", "mid", "nmid"
    );

    private static final List<String> delimiters = Arrays.asList(
            "lbrack", "rbrack",
            "lsqbrack", "rsqbrack",
            "lbrace", "rbrace",
            "lfloor", "rfloor",
            "lceil", "rceil",
            "langle", "rangle"
    );
}
