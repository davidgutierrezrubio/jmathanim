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

import com.jmathanim.Enum.AnchorType;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Scalar;
import com.jmathanim.mathobjects.hasArguments;

import java.util.Map;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LaTeXMathObject extends AbstractLaTeXMathObject<LaTeXMathObject> implements hasArguments {

    /**
     * Creates a new LaTeX generated text
     */
    protected LaTeXMathObject(AnchorType anchor) {
        super(anchor);
    }


    /**
     * Static constructor
     *
     * @param text   LaTex text to compile. By default this text is compiled using the compile mode JLaTexMath.
     * @param anchor Anchor to align. Default is CENTER. If LEFT, text will be anchored in its left margin to the
     *               reference point
     * @return The LaTexMathObject
     */
    public static LaTeXMathObject make(String text, AnchorType anchor) {
        return make(text, CompileMode.JLaTexMath, anchor);
    }

    /**
     * Static constructor
     *
     * @param text LaTex text to compile. By default this text is compiled using the compile mode JLaTexMath.
     * @return The LaTexMathObject
     */
    public static LaTeXMathObject make(String text) {
        return make(text, CompileMode.JLaTexMath, AnchorType.CENTER);
    }

    public static LaTeXMathObject make(String text, CompileMode compileMode, AnchorType anchor) {

        LaTeXMathObject resul = new LaTeXMathObject(anchor);
        resul.getMp().loadFromStyle("latexdefault");
        resul.getMp().setAbsoluteThickness(true);
//        resul.getMp().setFillColor(resul.getMp().getDrawColor());
//        resul.getMp().setThickness(1d);
        resul.mode = compileMode;

        if (!"".equals(text)) {
            resul.setLaTeX(text);
        }
        return resul;
    }




    @Override
    public LaTeXMathObject copy() {
        LaTeXMathObject resul = new LaTeXMathObject(this.anchor);
        resul.copyStateFrom(this);
        return resul;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        super.copyStateFrom(obj);
        if (obj instanceof LaTeXMathObject) {
            LaTeXMathObject copy = (LaTeXMathObject) obj;
            this.origText = copy.origText;
            this.anchor = copy.anchor;
            //copy all variable values
            for (Map.Entry<Integer, Scalar> pair : copy.variables.entrySet()) {
                variables.get(pair.getKey()).setScalar(pair.getValue().value);
            }
        }

    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        if (isHasBeenUpdated()) return;
        if (origText == null) {
            origText = getText();
        }
        //Actualizo numeros
        String newText = origText;
        for (Integer index : variables.keySet()) {
            newText = newText.replace("{#" + index + "}", df.format(variables.get(index).value));
        }
        if (!newText.equals(origText)) {//No need to update if text has not changed
            changeInnerLaTeX(newText);
        }
//        anchor3DA = getBoundingBox().getLower();
//        anchor3DC = anchor3DA.copy().shift(0, 1, 0);
//        anchor3DD = anchor3DA.copy().shift(0, 0, 1);
//        alignTo3DView();
    }


    @Override
    public String toString() {
        return "LaTeXMathObject{" + "origText=" + origText + '}';
    }
}
