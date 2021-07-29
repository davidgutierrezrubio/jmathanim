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
package com.jmathanim.Renderers.JOGLRenderer;

/**
 * Represents shader attributes.
 * 
 * @author serhiy
 */
public enum EShaderAttribute {
	POSITION("inPosition"), COLOR("inColor");

	private final String attributeName;

	private EShaderAttribute(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * @return shader attribute name as it is appearing in the shader source
	 *         code.
	 */
	public String getAttributeName() {
		return attributeName;
	}
}