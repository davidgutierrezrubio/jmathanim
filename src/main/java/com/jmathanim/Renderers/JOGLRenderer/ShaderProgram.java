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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.GL2;

/**
 * Manages the shader program.
 * 
 * @author serhiy
 */
public class ShaderProgram {
	private int programId;
	private int vertexShaderId;
	private int fragmentShaderId;
	private Map<EShaderAttribute, Integer> shaderAttributeLocations = new HashMap<>();
	private boolean initialized = false;

	/**
	 * Initializes the shader program.
	 * 
	 * @param gl2 context.
	 * @param vertexShader file.
	 * @param fragmentShader file.
	 * @return true if initialization was successful, false otherwise.
	 */
	public boolean init(GL2 gl2, File vertexShader, File fragmentShader) {
		if (initialized) {
			throw new IllegalStateException(
					"Unable to initialize the shader program! (it was already initialized)");
		}

		try {
			String vertexShaderCode = ShaderUtils.loadResource(vertexShader
					.getPath());
			String fragmentShaderCode = ShaderUtils.loadResource(fragmentShader
					.getPath());

			programId = gl2.glCreateProgram();
			vertexShaderId = ShaderUtils.createShader(gl2, programId,
					vertexShaderCode, GL2.GL_VERTEX_SHADER);
			fragmentShaderId = ShaderUtils.createShader(gl2, programId,
					fragmentShaderCode, GL2.GL_FRAGMENT_SHADER);

			ShaderUtils.link(gl2, programId);

			shaderAttributeLocations.put(EShaderAttribute.POSITION,
					gl2.glGetAttribLocation(programId, "inPosition"));
			shaderAttributeLocations.put(EShaderAttribute.COLOR,
					gl2.glGetAttribLocation(programId, "inColor"));

			initialized = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return initialized;

	}

	/**
	 * Destroys the shader program.
	 * 
	 * @param gl2 context.
	 */
	public void dispose(GL2 gl2) {
		initialized = false;
		gl2.glDetachShader(programId, vertexShaderId);
		gl2.glDetachShader(programId, fragmentShaderId);
		gl2.glDeleteProgram(programId);
	}

	/**
	 * @return shader program id.
	 */
	public int getProgramId() {
		if (!initialized) {
			throw new IllegalStateException(
					"Unable to get the program id! The shader program was not initialized!");
		}
		return programId;
	}

	/**
	 * @param shaderAttribute to retrieve its location.
	 * @return location of the shader attribute.
	 */
	public int getShaderAttributeLocation(EShaderAttribute shaderAttribute) {
		if (!initialized) {
			throw new IllegalStateException(
					"Unable to get the attribute location! The shader program was not initialized!");
		}
		return shaderAttributeLocations.get(shaderAttribute);
	}

	public boolean isInitialized() {
		return initialized;
	}
}
