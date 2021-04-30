/*
 * Copyright (C) 2021 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.mathobjects;

import java.util.function.Function;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMNumber extends LaTeXMathObject {

	private double number;
	Function<Double, String> lambdaText = t -> {
		return "$" + t + "$";
	};

	/**
	 * Creates a new LaTeXMathObject that shows a formatted number. The number can
	 * be changed easily with the method setNumber. A lambda function is used to
	 * format the number into a string
	 *
	 * @param number Number to show
	 * @return The new JMNumber object
	 */
	public static JMNumber make(double number) {
		JMNumber resul = new JMNumber(0);
		resul.setNumber(number);

		return resul;
	}

	private double refHeight;
	private String unitString;

	protected JMNumber(double number) {
		super();
		this.number = number;
		this.unitString = "";
		this.refHeight = 0;
	}

	private void updateContents() {
		double h = this.getHeight();
		setLaTeX(lambdaText.apply(this.number) + unitString);
		this.refHeight = this.getHeight();

		if (h > 0) {
			scale(h / this.refHeight);
		}
	}

	/**
	 * Returns the number showed
	 *
	 * @return
	 */
	public double getNumber() {
		return number;
	}

	/**
	 * Sets the new number,updating the LaTeX content
	 *
	 * @param number New number
	 */
	public final void setNumber(double number) {
		this.number = number;
		updateContents();
	}

	/**
	 * Returns the lambda function used to convert the number into a string
	 *
	 * @return The lambda function
	 */
	public Function<Double, String> getLambdaText() {
		return lambdaText;
	}

	/**
	 * Sets the lambda formatting function, that converts a double number into a
	 * string. LaTeX content is automatically updated.
	 *
	 * @param lambdaText The new lambda function
	 */
	public void setLambdaText(Function<Double, String> lambdaText) {
		this.lambdaText = lambdaText;
		updateContents();
	}

	/**
	 * Sets the lambda formatting function to a integer format.
	 *
	 * @param <T> This subclass
	 * @return This object
	 */
	public <T extends JMNumber> T setIntegerFormat() {
		this.lambdaText = t -> {
			return String.format("$%32.0f$", t);
		};
		updateContents();
		return (T) this;
	}

	/**
	 * Sets the lambda formatting function to a decimal format, with a fixed number
	 * of decimals.
	 *
	 * @param <T>         This subclass
	 * @param numDecimals Number of decimals to show
	 * @return This object
	 */
	public <T extends JMNumber> T setDecimalFormat(int numDecimals) {
		this.lambdaText = t -> {
			return String.format("$%32." + numDecimals + "f$", t);
		};
		updateContents();
		return (T) this;
	}

	/**
	 * Sets the unit of the number, if any. For example, setUnit("cm") will add the
	 * LaTeX string "cm" to the number
	 *
	 * @param <T>        This subclass
	 * @param unitString String with the unit "cm", "gr", etc.
	 * @return This object
	 */
	public <T extends JMNumber> T setUnit(String unitString) {
		this.unitString = unitString;
		updateContents();
		return (T) this;
	}

}
