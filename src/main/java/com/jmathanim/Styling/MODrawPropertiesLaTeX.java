package com.jmathanim.Styling;

import com.jmathanim.Utils.LatexStyle;

/**
 * Subclass implementation of MODrawProperties with additional config options for
 * LaTeX objects
 */
public class MODrawPropertiesLaTeX extends MODrawProperties {
    LatexStyle latexStyle = null;

    public MODrawPropertiesLaTeX() {
        super();
    }

    /**
     * Returns the LaTeXStyle of the associated AbstractLaTeXMathObject
     *
     * @return A LaTeXStyle instance, or null if there is no style defined
     */
    public LatexStyle getLatexStyle() {
        return latexStyle;
    }

    /**
     * Sets the currente LaTeXStyle for the associated AbstractLaTeXMathObject
     * The LaTeXStyle class manages automatic coloring of LaTeX tokens
     *
     * @param latexStyle LaTeXStyle to set
     */
    public void setLatexStyle(LatexStyle latexStyle) {
        this.latexStyle = latexStyle;
    }

    @Override
    public void copyFrom(Stylable prop) {
        super.copyFrom(prop);
        if (prop instanceof MODrawPropertiesLaTeX) {
            MODrawPropertiesLaTeX moDrawPropertiesLaTeX = (MODrawPropertiesLaTeX) prop;
            if (moDrawPropertiesLaTeX.latexStyle != null)
                this.latexStyle = moDrawPropertiesLaTeX.latexStyle;
        }
    }

    @Override
    public MODrawPropertiesLaTeX copy() {
        MODrawPropertiesLaTeX resul = new MODrawPropertiesLaTeX();
        resul.copyFrom(this);
        return resul;
    }
}
