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

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import com.jogamp.opengl.GL2;

/**
 * Shader program utilities.
 * 
 * @author serhiy
 */
public class ShaderUtils {
	
	private ShaderUtils() {
		/* Prevent initialization, only static methods below. */
	}
	
	/**
	 * Loads the resource.
	 * 
	 * @param fileName of the resource to load.
	 * @return content of the resource converted to UTF-8 text.
	 * @throws Exception when an error occurs loading resource.
	 */
	public static String loadResource(String fileName) throws Exception {
		try (InputStream in = ShaderUtils.class.getClassLoader().getResourceAsStream(fileName)) {
			return new Scanner(in, "UTF-8").useDelimiter("\\A").next();
		}
	}
	
	/**
	 * Creates and compile the shader in the shader program.
	 * 
	 * @param gl2 context.
	 * @param programId to create its shaders.
	 * @param shaderCode to compile.
	 * @param shaderType of the shader to be compiled.
	 * @return the id of the created and compiled shader.
	 * @throws Exception when an error occurs creating the shader program.
	 */
	public static int createShader(GL2 gl2, int programId, String shaderCode, int shaderType) throws Exception {
		int shaderId = gl2.glCreateShader(shaderType);
		if (shaderId == 0) {
			throw new Exception("Error creating shader. Shader id is zero.");
		}
		
		gl2.glShaderSource(shaderId, 1, new String[] { shaderCode }, null);
		gl2.glCompileShader(shaderId);
		
		IntBuffer intBuffer = IntBuffer.allocate(1);
		gl2.glGetShaderiv(shaderId, GL2.GL_COMPILE_STATUS, intBuffer);

		if (intBuffer.get(0) != 1) {
			gl2.glGetShaderiv(shaderId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
			int size = intBuffer.get(0);
			if (size > 0) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(size);
				gl2.glGetShaderInfoLog(shaderId, size, intBuffer, byteBuffer);
				System.out.println(new String(byteBuffer.array()));
			}
			throw new Exception("Error compiling shader!");
		}

		gl2.glAttachShader(programId, shaderId);

		return shaderId;
	}

	/**
	 * Links the shaders within created shader program.
	 * 
	 * @param gl2 context.
	 * @param programId to link its shaders.
	 * @throws Exception when an error occurs linking the shaders.
	 */
	public static void link(GL2 gl2, int programId) throws Exception {
		gl2.glLinkProgram(programId);

		IntBuffer intBuffer = IntBuffer.allocate(1);
		gl2.glGetProgramiv(programId, GL2.GL_LINK_STATUS, intBuffer);

		if (intBuffer.get(0) != 1) {
			gl2.glGetProgramiv(programId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
			int size = intBuffer.get(0);
			if (size > 0) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(size);
				gl2.glGetProgramInfoLog(programId, size, intBuffer, byteBuffer);
				System.out.println(new String(byteBuffer.array()));
			}
			throw new Exception("Error linking shader program!");
		}

		gl2.glValidateProgram(programId);

		intBuffer = IntBuffer.allocate(1);
		gl2.glGetProgramiv(programId, GL2.GL_VALIDATE_STATUS, intBuffer);

		if (intBuffer.get(0) != 1) {
			gl2.glGetProgramiv(programId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
			int size = intBuffer.get(0);
			if (size > 0) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(size);
				gl2.glGetProgramInfoLog(programId, size, intBuffer, byteBuffer);
				System.out.println(new String(byteBuffer.array()));
			}
			throw new Exception("Error validating shader program!");
		}
	}
}
