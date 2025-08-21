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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * A descriptor of a LaTeX element created by abstractLaTeXMathObject. It is
 * used to identify and correctly colour the elements of mathematical
 * expressions.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LatexToken {



    /**
     * Secondary style flags
     */
    public static final int SEC_NONE = 0b00000000;//None. A value of 0 should never be matched
    public static final int SEC_NORMAL = 0b00000001;//This token is normal style, nothing special about it
    public static final int SEC_DELIMITER_NORMAL = 0b00000010;//This token is a delimiter, normal size
    public static final int SEC_DELIMITER_BIG1 = 0b00000100;//This token is a delimiter, \big size
    public static final int SEC_DELIMITER_BIG2 = 0b00001000;//This token is a delimiter, \Big size
    public static final int SEC_DELIMITER_BIG3 = 0b0000100000000000;//This token is a delimiter, \bigg size
    public static final int SEC_DELIMITER_BIG4 = 0b0001000000000000;//This token is a delimiter, \Bigg size
    public static final int SEC_DELIMITER_EXTENSIBLE = 0b00010000;//This token is a delimiter, extensible size
    public static final int SEC_SUPERSCRIPT = 0b00100000;//This token is in a superscript
    public static final int SEC_SUBSCRIPT = 0b01000000;//This token is in a subscript
    public static final int SEC_FROM_INDEX = 0b10000000;//This token is in the "from" part of an \int, \sum...
    public static final int SEC_TO_INDEX = 0b0000000100000000;//This token is in the "to" part of an \int, \sum...
    public static final int SEC_NUMERATOR = 0b0000001000000000;//This token is in the numerator part of a fraction
    public static final int SEC_DENOMINATOR = 0b0000010000000000;//This token is in the denominator part of a fraction
    public static final int SEC_LEFT_ARROW = 0b0010000000000000; //This token is a left arrow
    public static final int SEC_RIGHT_ARROW = 0b0100000000000000;//This token is a right arrow
    public static final int SEC_LEFTRIGHT_ARROW = 0b1000000000000000;//This token is a leftright arrow
    public static final int SEC_BOLD_FONT = 0b10000000000000000;//This token is in bold math

    private LatexTokenType type;
    private Integer secondaryFlags;

    private String string;
    public boolean takesStyleFromNext;
    private Integer delimiterDepth;

    /**
     * Returns a new LatexToken with all its attributes set to null
     *
     * @return The created LatexToken
     */
    public static LatexToken make() {
        return new LatexToken();
    }

    public static LatexToken make(LatexTokenType type, Integer secFlags, String name) {
        return LatexToken.make()
                .setType(type)
                .setSecondaryTypeFlag(secFlags)
                .setString(name);
    }

    protected LatexToken() {
        this.type = null;
        this.secondaryFlags = null;
        this.string = null;
        this.delimiterDepth = null;

    }

    /**
     * Sets the given secondary flag
     *
     * @param secFlag The secondary flag to activate (a public static variable
     * starting with SEC_)
     * @return This object
     */
    public LatexToken setSecondaryTypeFlag(Integer secFlag) {
        if (this.secondaryFlags == null) {
            this.secondaryFlags = secFlag;
        } else {
            this.secondaryFlags |= secFlag;
        }
        refineToken();
        return this;
    }

    /**
     * Unsets the given secondary flag
     *
     * @param secFlag The secondary flag to deactivate (a public static variable
     * starting with SEC_)
     * @return This object
     */
    public LatexToken unsetSecondaryTypeFlag(Integer secFlag) {
        this.secondaryFlags &= ~secFlag;
        return this;
    }

    /**
     * Sets the token type
     *
     * @param type Token type, a value of enum TokenType
     * @return This object
     */
    public LatexToken setType(LatexTokenType type) {
        this.type = type;
        refineToken();
        return this;
    }

    /**
     * Sets the string name of the token. Usually the character or LaTeX command
     * without backslashes
     *
     * @param string The name
     * @return This object
     */
    public LatexToken setString(String string) {
        if (string != null) {
            switch (string) {//Some special cases to make it easier
                case ",":
                    string = "comma";
                    break;
                case ".":
                    string = "normaldot";
                    break;
                case ";":
                    string = "semicolon";
                    break;
                case ":":
                    string = "colon";
                    break;
                case "|":
                    string = "vert";
                    break;
                case "(":
                    string = "lbrack";
                    break;
                case ")":
                    string = "rbrack";
                    break;
                case "{":
                    string = "lbrace";
                    break;
                case "}":
                    string = "rbrace";
                    break;
                case "[":
                    string = "lsqbrack";
                    break;
                case "]":
                    string = "rsqbrack";
                    break;
            }
        }

        this.string = string;
        refineToken();
        return this;
    }

    /**
     * Returns the delimiter depth of this glyph, that is, how many delimiters
     * the glyph is "buried" in.
     *
     * @return The delimiter depth. 0 if the token is not inside of any
     * delimiter.
     */
    public Integer getDelimiterDepth() {
        return delimiterDepth;
    }

    /**
     * Sets the delimiter depth
     *
     * @param delimiterDepth The new delimiter depth.
     * @return This object
     */
    public LatexToken setDelimiterDepth(Integer delimiterDepth) {
        this.delimiterDepth = delimiterDepth;
        refineToken();
        return this;
    }

    /**
     * Refine the classification of the token.
     */
    private void refineToken() {
        if (type == null) {
            return;
        }
        switch (type) {
            case SYMBOL:
                if (greekLetters.contains(string)) {//Check if string is in my list of greek letters
                    type = LatexTokenType.GREEK_LETTER;
                    break;
                }
                if (operators.contains(string)) {//Check if string is in my list of operators
                    type = LatexTokenType.OPERATOR;

                    break;
                }
                if (binaryOperators.contains(string)) {
                    type = LatexTokenType.BINARY_OPERATOR;

                    break;
                }

                if (delimiters.contains(string)) {
                    type = LatexTokenType.DELIMITER;
                    break;
                }
                if (relations.contains(string)) {
                    type = LatexTokenType.RELATION;
                    break;
                }

                if (arrows.contains(string)) {
                    type = LatexTokenType.ARROW;
                    String strl = string.toLowerCase();
                    if (strl.contains("leftright")) {
                        activateSecondaryFlag(SEC_LEFTRIGHT_ARROW);

                    } else {
                        if (strl.contains("left")) {
                            activateSecondaryFlag(SEC_LEFT_ARROW);
                        }
                        if (strl.contains("right")) {
                            activateSecondaryFlag(SEC_RIGHT_ARROW);
                        }
                    }
                    break;
                }

            case CHAR:
                if (numbers.contains(string)) {
                    type = LatexTokenType.NUMBER;
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
        if (((secType & SEC_SUBSCRIPT) != 0) || ((secType & SEC_SUPERSCRIPT) != 0)) {
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
     * Activates a flag given by the static variables SEC_
     *
     * @param flag The flag, an static LatexToken variable starting with SEC_
     * @return This object
     */
    public LatexToken activateSecondaryFlag(int flag) {
        if (this.secondaryFlags == null) {
            this.secondaryFlags = 0;
        }
        this.secondaryFlags |= flag;
        return this;
    }

    /**
     * Dectivates a flag given by the static variables SEC_
     *
     * @param flag The flag, an static LatexToken variable starting with SEC_
     * @return This object
     */
    public LatexToken deactivateSecondaryFlag(int flag) {
        if (this.secondaryFlags == null) {
            return this;
        }
        this.secondaryFlags &= ~flag;
        return this;
    }

    public Integer getSecondaryFlags() {
        return secondaryFlags;
    }

    public LatexTokenType getType() {
        return type;
    }

    public String getString() {
        return string;
    }

//    /**
//     * Check if token characteristics match the given ones. A null passed as a
//     * parameter means that no comparison is done in that parameter.
//     *
//     * @param type Type to match. Null if no comparison needed.
//     * @param name Name to match. Null if no comparison needed.
//     * @param secondaryType An integer with secondary bit attributes SEC_XXX.
//     * Null if no comparison needed.
//     * @param delimiterDepth
//     * @return True if matched. False otherwise.
//     */
//    public boolean match(TokenType type, String name, Integer secondaryType, Integer delimiterDepth) {
//        boolean result = true;
//        result = result && ((type == null) || (this.type == null) || (this.type == type));
//        result = result && matchSecType(secondaryType);
//        result = result && ((name == null) || (this.string == null) || (this.string.equals(name)));
//          result = result && ((delimiterDepth == null) || (this.delimiterDepth == null) || (this.delimiterDepth == delimiterDepth));
//        return result;
//    }
    private boolean matchSecType(Integer secondaryFlags) {
        if ((secondaryFlags == null) || (this.secondaryFlags == null)) {
            return true;
        }
        return ((this.secondaryFlags & secondaryFlags) == this.secondaryFlags);
    }

    /**
     * Check if token characteristics match with another given token. If a given
     * characteristic is null (for example string name), this comparison will
     * not be done.
     *
     * @param token Token to compare
     * @return True if match, false otherwise
     */
    public boolean match(LatexToken token) {
        if (token == null) {
            return true;
        }
        boolean result = true;
        result = result && ((token.type == null) || (this.type == null) || (this.type == token.type));
        result = result && matchSecType(token.secondaryFlags);
        result = result && ((token.string == null) || (this.string == null) || (this.string.equals(token.string)));
        result = result && ((token.delimiterDepth == null) || (this.delimiterDepth == null) || (this.delimiterDepth.equals(token.delimiterDepth)));
        return result;
    }

    /**
     * Returns true if ALL of its non-null properties are different from this
     * token. Token attributes set to null are not compared.
     *
     * @param token Token to compare
     * @return True if all non-null properties are different, false otherwise
     */
    public boolean differs(LatexToken token) {
        if (token == null) {
            return true;
        }
        boolean result = true;
        result = result && ((token.type == null) || (this.type == null) || (token.type != this.type));
        result = result && ((token.secondaryFlags == null) || (this.secondaryFlags == null) || ((~token.secondaryFlags & secondaryFlags) != 0));
        result = result && ((token.string == null) || (!token.string.equals(string)));
        result = result && ((token.delimiterDepth == null) || (this.delimiterDepth == null) || (!token.delimiterDepth.equals(this.delimiterDepth)));
        return result;
    }

    public LatexToken copy() {
        LatexToken copy = LatexToken.make()
                .setType(this.type)
                .setSecondaryTypeFlag(this.secondaryFlags)
                .setString(this.string)
                .setDelimiterDepth(this.delimiterDepth);
        copy.takesStyleFromNext = this.takesStyleFromNext;
        return copy;
    }

    @Override
    public String toString() {
        String delDepth = (delimiterDepth == null ? "null" : delimiterDepth + "");
        return "LatexToken[" + type + ", \"" + string + "\"]" + (takesStyleFromNext ? " <takes style from next> " : " ") + "[delimDepth=" + delDepth + "] "
                + secFlagsToString(secondaryFlags);
    }

    private String secFlagsToString(Integer secFlag) {
        if (secFlag == null) {
            return "null";
        }
        StringBuilder activatedFlags = new StringBuilder();
//
        try {
            // Obtener todas las variables de la clase ActivadorDeBits
            Field[] fields = LatexToken.class.getDeclaredFields();
            for (Field field : fields) {
                // Verificar si el nombre del campo comienza por "SEC_"
                if (field.getName().startsWith("SEC_")) {
                    // Obtener el valor del campo
                    int valorCampo = field.getInt(null);
                    // Verificar si el bit correspondiente está activado en N
                    if ((secFlag & valorCampo) != 0) {
                        // Agregar el nombre del campo a la lista de variables activadas
                        activatedFlags.append(field.getName()).append(" ");
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return activatedFlags.toString().trim();
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
            "in", "notin", "ni",
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

    private static final List<String> arrows = Arrays.asList(
            "rightarrow",
            "leftarrow",
            "Rightarrow",
            "Leftarrow",
            "leftrightarrow",
            "longleftrightarrow",
            "Leftrightarrow",
            "mapsto",
            "longrightarrow",
            "longleftarrow",
            "Longrightarrow",
            "Longleftarrow",
            "longleftrightarrow",
            "longmapsto",
            "hookrightarrow",
            "hookleftarrow",
            "uparrow",
            "downarrow",
            "Uparrow",
            "Downarrow",
            "nearrow",
            "searrow",
            "swarrow",
            "nwarrow",
            "implies", "iff"
    );
}
