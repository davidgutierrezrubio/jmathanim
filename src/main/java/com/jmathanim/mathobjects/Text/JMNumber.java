/*
 * Copyright (C) 2022 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.mathobjects.Text;

import com.jmathanim.Utils.Anchor;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.hasScalarParameter;

import java.text.DecimalFormat;

/**
 * This class represents a number that can be changed.
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMNumber extends AbstractLaTeXMathObject implements hasScalarParameter {

    private double value;
    private String format;
    private final DecimalFormat formatter;

    public static JMNumber make(double d) {
        JMNumber resul = new JMNumber(d);
        resul.style("latexdefault");
        resul.setScalar(d);
        return resul;
    }

    protected JMNumber(double value) {
        super(Anchor.Type.LEFT);
        formatter = new DecimalFormat();
        format = "";
    }

    @Override
    public double getScalar() {
        return value;
    }

    @Override
    public void setScalar(double scalar) {
        value = scalar;
        String text = formatter.format(value);
        changeInnerLaTeX(text);
    }

    @Override
    public JMNumber copy() {
        JMNumber resul = new JMNumber(getScalar());
        resul.copyStateFrom(this);
        resul.setScalar(value);
        return resul;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
         super.copyStateFrom(obj);
        if (obj instanceof JMNumber) {
            JMNumber copy = (JMNumber) obj;
            this.anchor = copy.anchor;
            this.format = copy.format;
            this.formatter.applyPattern(format);
            super.copyStateFrom(obj);
        }
    }

    /**
     * Returns the current format of the number
     *
     * @return The format, in the convention of DecimalFormat class.
     */
    public String getFormat() {
        return format;
    }

    public JMNumber setFormat(String format) {
        formatter.applyPattern(format);
        this.format = format;
        String text = formatter.format(value);
        changeInnerLaTeX(text);
        return this;
    }

}
