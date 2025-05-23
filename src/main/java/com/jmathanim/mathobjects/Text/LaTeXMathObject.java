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

import com.jmathanim.Cameras.Camera3D;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Anchor;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class LaTeXMathObject extends AbstractLaTeXMathObject implements hasArguments {

    private Point anchor3DA;
    private Point anchor3DC;
    private Point anchor3DD;

    DecimalFormat df;
    private String origText;
    public final HashMap<Integer, Scalar> variables;

    /**
     * Static constructor
     *
     * @param text LaTex text to compile. By default this text is compiled using
     * the compile mode JLaTexMath.
     * @return The LaTexMathObject
     */
    public static LaTeXMathObject make(String text) {
        return make(text, CompileMode.JLaTexMath, Anchor.Type.CENTER);
    }

    /**
     * Static constructor
     *
     * @param text LaTex text to compile. By default this text is compiled using
     * the compile mode JLaTexMath.
     * @param anchor Anchor to align. Default is CENTER. If LEFT, text will be
     * anchored in its left margin to the reference point
     * @return The LaTexMathObject
     */
    public static LaTeXMathObject make(String text, Anchor.Type anchor) {
        return make(text, CompileMode.JLaTexMath, anchor);
    }

    /**
     * Static constructor
     *
     * @param text LaTex text to compile
     * @param compileMode How to generate the shapes from LaTeX string. A value
     * from the enum CompileMode.
     * @param anchor Anchor to align. Default is CENTER. If LEFT, text will be
     * anchored in its left margin to the reference point
     * @return The LaTexMathObject
     */
    public static LaTeXMathObject make(String text, CompileMode compileMode, Anchor.Type anchor) {

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

    /**
     * Creates a new LaTeX generated text
     */
    protected LaTeXMathObject(Anchor.Type anchor) {
        super(anchor);
        df = new DecimalFormat("0.00");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.UK));
        variables = new HashMap<>();
        for (int n = 0; n < 9; n++) {
            variables.put(n, Scalar.make(0));
        }
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
        origText = text;
        text = replaceInnerReferencesInText(text);
        changeInnerLaTeX(text);
        return this;
    }

    protected String replaceInnerReferencesInText(String text) {
        for (Integer index : variables.keySet()) {
            text = text.replace("{#" + index + "}", df.format(variables.get(index).value));
        }
        return text;
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
            for (Map.Entry<Integer,Scalar> pair : copy.variables.entrySet()) {
                variables.get(pair.getKey()).setScalar(pair.getValue().value);
            };
        }
        
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
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

    private void alignTo3DView() {
        if (scene.getCamera() instanceof Camera3D) {
            Camera3D cam = (Camera3D) scene.getCamera();
            Point anchor3DCdest = anchor3DA.copy().shift(cam.up);
            Point anchor3DDdest = anchor3DA.copy().shift(cam.look.to(cam.eye));
            AffineJTransform tr = AffineJTransform.createDirect3DIsomorphic(
                anchor3DA, anchor3DD, anchor3DC,
                anchor3DA.copy(), anchor3DDdest, anchor3DCdest,
                1);
            tr.applyTransform(this);
//            tr.applyTransform(anchor3DA);
            tr.applyTransform(anchor3DC);
            tr.applyTransform(anchor3DD);
        }
    }

    public DecimalFormat getDecimalFormat() {
        return df;
    }


    /**
     * Sets the decimal format for the arguments.
     *
     * @param format A string representing a format for the DecimalFormat class.
     */
    public void setArgumentsFormat(String format) {
        df = new DecimalFormat(format);
    }

    @Override
    public Scalar getArg(int n) {
        Scalar resul = variables.get(n);
        if (resul == null) {
            variables.put(n, Scalar.make(0));
        }
        return variables.get(n);
    }



    @Override
    public String toString() {
        return "LaTeXMathObject{" + "origText=" + origText + '}';
    }
}
