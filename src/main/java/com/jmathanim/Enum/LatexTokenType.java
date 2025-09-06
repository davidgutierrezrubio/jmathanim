package com.jmathanim.Enum;

public enum LatexTokenType {
    NONE, //This token will not be assigned never. It is used to always returns false when matching tokens
    NON_MATH_CHAR,//Normal, non-mathematical text
    CHAR,//A char token, mostly a letter
    NUMBER, //0-9 digits, including point if used in the decimal context
    SYMBOL, //A math symbol
    OPERATOR, //A "big" operator like \sum, \int
    BINARY_OPERATOR, //A simpler binary operator like +, -, \cap,\cup...
    RELATION, // A math relation like =, \geq, \leq, etc.
    DELIMITER, //Parenthesis, brackets...of any size
    SQRT, // Square (or nth-) root symbol
    FRACTION_BAR, //That is, the fraction bar :-)
    GREEK_LETTER, //Any greek letter like \pi or \varepsilon
    NAMED_FUNCTION, //A named function like \log or \ln
    ARROW //An arrow
}