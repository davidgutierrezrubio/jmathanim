/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.mathobjects.Text;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LaTeXMathObject extends AbstractLaTeXMathObject {

    /**
     * Static constructor
     *
     * @param text LaTex text to compile. By default this text is compiled using
     * the compile mode JLaTexMath.
     * @return The LaTexMathObject
     */
    public static LaTeXMathObject make(String text) {
        return make(text, CompileMode.JLaTexMath);
    }

    /**
     * Static constructor
     *
     * @param text LaTex text to compile
     * @param compileMode How to generate the shapes from LaTeX string. A value
     * from the enum CompileMode.
     * @return The LaTexMathObject
     */
    public static LaTeXMathObject make(String text, CompileMode compileMode) {
        LaTeXMathObject resul = new LaTeXMathObject();
        resul.getMp().loadFromStyle("latexdefault");
        resul.getMp().setAbsoluteThickness(true);
//        resul.getMp().setFillColor(resul.getMp().getDrawColor());
        resul.getMp().setThickness(1d);
        resul.mode = compileMode;
        
        if (!"".equals(text)) {
            resul.setLaTeX(text);
        }
        return resul;
    }

    /**
     * Creates a new LaTeX generated text
     */
    protected LaTeXMathObject() {
        super();
    }

    /**
     * Changes the current LaTeX expression, updating the whole object as
     * needed.The JMNumber for example, uses this.The new formula generated will
     * be center-aligned with the replaced one. In case the old formula was
     * empty (no shapes) it will be centered on the screen.
     *
     * @param text The new LaTeX string
     * @return This object
     */
    public LaTeXMathObject setLaTeX(String text) {
        changeInnerLaTeX(text);
        return this;
    }

    @Override
    public LaTeXMathObject copy() {
        LaTeXMathObject resul = new LaTeXMathObject();
        resul.copyStateFrom(this);
        return resul;
    }

//    @Override
//    public void copyStateFrom(MathObject obj) {
//        super.copyStateFrom(obj);
//        if (obj instanceof NewLaTeXMathObject) {
//            NewLaTeXMathObject copy = (NewLaTeXMathObject) obj;
//            super.copyStateFrom(copy);
//            modelMatrix.copyFrom(copy.modelMatrix);
//        }
//    }
}
