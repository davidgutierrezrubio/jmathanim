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
 * A descriptor of a LaTeX element generated by AbstractLaTeXMathObject. Used to
 * identify and colorize properly the elements of math expressions.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LatexToken {

    public LatexToken copy() {
        LatexToken copy = new LatexToken(type, secondaryFlags, string);
        copy.takesStyleFromNext = this.takesStyleFromNext;
        return copy;
    }

    public enum TokenType {
        NONE, //This token will not be assigned never. It is used to always returns false when matching tokens
        CHAR,//A char token, mostly a letter
        NUMBER, //0-9 digits, including point if used in the decimal context
        SYMBOL, //A math symbol
        OPERATOR, //An operator like \sum, \int
        BINARY_OPERATOR, //An simpler binary operator like +, -, \cap,\cup...
        RELATION, // A math relation like =, \geq, \leq, etc.
        DELIMITER, //Parenthesis, brackets...of any size
        SQRT, // Square (or nth-) root symbol
        FRACTION_BAR, //That is, the fraction bar :-)
        GREEK_LETTER, //Any greek letter like \pi or \varepsilon
        NAMED_FUNCTION, //A named function like \log or \ln
        ARROW //An arrow
    }

    public static final int SEC_NONE = 0b00000000;
    public static final int SEC_NORMAL = 0b00000001;
    public static final int SEC_DELIMITER_NORMAL = 0b00000010;
    public static final int SEC_DELIMITER_BIG1 = 0b00000100;
    public static final int SEC_DELIMITER_BIG2 = 0b00001000;
    public static final int SEC_DELIMITER_EXTENSIBLE = 0b00010000;
    public static final int SEC_SUPERSCRIPT = 0b00100000;
    public static final int SEC_SUBSCRIPT = 0b01000000;
    public static final int SEC_FROM_INDEX = 0b10000000;
    public static final int SEC_TO_INDEX = 0b0000000100000000;
    public static final int SEC_NUMERATOR = 0b0000001000000000;
    public static final int SEC_DENOMINATOR = 0b0000010000000000;
    public static final int SEC_DELIMITER_BIG3 = 0b0000100000000000;
    public static final int SEC_DELIMITER_BIG4 = 0b0001000000000000;
    public static final int SEC_LEFT_ARROW = 0b0010000000000000;
    public static final int SEC_RIGHT_ARROW = 0b0100000000000000;
    public static final int SEC_LEFTRIGHT_ARROW = 0b1000000000000000;

    public TokenType type;
    public Integer secondaryFlags;

    public String string;
    public boolean takesStyleFromNext;

    public LatexToken(TokenType type, String name) {
        this(type, null, name);
    }

    public LatexToken(TokenType type, Integer secondaryFlags, String name) {
        this.type = type;
        takesStyleFromNext = false;
        this.secondaryFlags = secondaryFlags;
        this.string = name;
        refineToken();
    }

    public LatexToken setSecondaryTypeFlag(Integer type) {
        this.secondaryFlags |= type;
        return this;
    }

    public LatexToken unsetSecondaryTypeFlag(Integer type) {
        this.secondaryFlags &= ~type;
        return this;
    }

    private void refineToken() {
        if (type == null) {
            return;
        }
        switch (type) {
            case SYMBOL:
                if (greekLetters.contains(string)) {//Check if string is in my list of greek letters
                    type = TokenType.GREEK_LETTER;
                    break;
                }
                if (operators.contains(string)) {
                    type = TokenType.OPERATOR;

                    break;
                }
                if (binaryOperators.contains(string)) {
                    type = TokenType.BINARY_OPERATOR;

                    break;
                }

                if (delimiters.contains(string)) {
                    type = TokenType.DELIMITER;
                    break;
                }
                if (relations.contains(string)) {
                    type = TokenType.RELATION;
                    break;
                }

            case CHAR:
                if (numbers.contains(string)) {
                    type = TokenType.NUMBER;
                    break;
                }
            default:

        }
        this.secondaryFlags = refineSecondaryType(this.secondaryFlags);

    }

    private Integer refineSecondaryType(Integer secType) {
        if (secType == null) {
            return null;
        }
        if (((secType & SEC_SUPERSCRIPT) != 0) || ((secType & SEC_SUPERSCRIPT) != 0)) {
            secType &= ~SEC_NORMAL;//Delete NORMAL bit   
        }
        if (((secType & SEC_NUMERATOR) != 0)) {
            secType &= ~SEC_DENOMINATOR;//Delete DENOMINATOR bit   
        }
        if (((secType & SEC_DENOMINATOR) != 0)) {
            secType &= ~SEC_NUMERATOR;//Delete NUMERATOR bit   
        }

        return secType;
    }

    /**
     * Check if token characteristics match the given ones. A null parameter
     * means that no comparison is done in that parameter.
     *
     * @param type Type to match. Null if no comparison needed.
     *
     * @param name Name to match. Null if no comparison needed.
     * @param secondaryType An integer with secondary bit attributes SEC_XXX
     * @return True if match. False otherwise.
     */
    public boolean match(TokenType type, String name, Integer secondaryType) {
        boolean result = true;
        result = result && ((type == null) || (this.type == null) || (this.type == type));
        result = result && matchSecType(secondaryType);
        result = result && ((name == null) || (this.string == null) || (this.string.equals(name)));
        return result;
    }

    private boolean matchSecType(Integer secondaryFlags) {
        if ((secondaryFlags == null) || (this.secondaryFlags == null)) {
            return true;
        }
        return ((this.secondaryFlags & secondaryFlags) == this.secondaryFlags);
    }

    public boolean match(TokenType type, String name) {
        return match(type, name, null);
    }

    public boolean match(LatexToken tok) {
        if (tok == null) {
            return true;
        }
        return match(tok.type, tok.string, tok.secondaryFlags);
    }

    /**
     * Returns true if ALL of its non-null properties are different from this
     * token.
     *
     * @param tok Token to compare
     * @return True if all non-null properties are different, false otherwise
     */
    public boolean differs(LatexToken tok) {
        if (tok == null) {
            return true;
        }
        boolean result = true;
        result = result && ((tok.type == null) || (tok.type != type));
        result = result && ((tok.secondaryFlags == null) || (this.secondaryFlags == null) || ((~tok.secondaryFlags & secondaryFlags) != 0));
        result = result && ((tok.string == null) || (!tok.string.equals(string)));
        return result;
    }

    @Override
    public String toString() {
        return "LatexToken[" + type + ", " + secondaryFlags + "," + string + "]" + (takesStyleFromNext ? "takes style from next" : "");
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
            "sum", "prod",
            "int", "iint", "iiint", "iiiint",
            "oint"
    );
    private static final List<String> binaryOperators = Arrays.asList(
            "plus", "minus", "slash", "div", "pm", "mp",
            "cap", "cup", "wedge", "vee",
            "cdot", "times", "ast",
            "otimes", "oplus", "ominus", "oslash", "odot",
            "bigcirc", "dagger",
            "star", "circ", "bullet", "diamond",
            "bigtriangleup", "bigtriangledown",
            "triangleleft", "triangleright",
            "unlhd", "unrhd"
    );

    private static final List<String> relations = Arrays.asList(
            "equals", "lt", "leq", "le", "gt", "geq", "ge",
            "triangleq", "thicksim", "doteq", "circeq", "thickapprox",
            "bumpeq", "risingdotseq", "Bumpeq", "approxeq", "asymp",
            "mid", "nmid", "shortmid", "shortparallel", "nshortmid", "nshortparallel",
            "models", "cong", "ncong",
            "neq", "equiv", "cong", "sim", "simeq",
            "approx", "propto", "parallel", "perp", "ne",
            "subset", "supset", "in", "notin",
            "subseteq", "supseteq",
            "nsubseteq", "nsupseteq",
            "parallel", "nparallel",
            "in", "notin", "ni", "mapsto", "rightarrow",
            "Rightarrow", "longrightarrow", "Leftarrow",
            "leftrightarrow", "Leftrightarrow", "implies", "iff",
            "forall", "exists", "mid", "nmid",
            "nless", "ngtr", "leqslant", "geqslant",
            "nleq", "ngeq", "nleqslant", "ngeqslant"
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
