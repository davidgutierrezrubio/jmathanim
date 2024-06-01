/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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

import com.jmathanim.Utils.ResourceLoader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ShaderLoader {

    private int shaderprogram;

//    public float scaVal;
//    public int unifMiterLimit;
//    public int unifModelMat;
//    public int unifColor;
//    public int unifProject;
//    public int unifScal;
//    public int unifThickness;
//    public int unifViewPort;
//    private static String VERTEX_SHADER_FILE = "#bezier/BezierDraw.vs";
//    private static String GEOMETRY_SHADER_FILE = "#bezier/BezierDrawGenPoints.gs";
//    private static String FRAGMENT_SHADER_FILE = "#bezier/BezierDraw.fs";
//    private static String VERTEX_SHADER_FILE = "#default.vs";
//    private static String GEOMETRY_SHADER_FILE = "#default.gs";
//    private static String FRAGMENT_SHADER_FILE = "#default.fs";
    private int vertexShader;
    private int geometryShader;
    private int fragmentShader;
    private String vertexShaderName;
    private String geomShaderName;
    private String fragmentShaderName;

    public ShaderLoader(String vertexShaderName, String geomShaderName, String fragmentShaderName) {
        this.vertexShaderName = vertexShaderName;
        this.geomShaderName = geomShaderName;
        this.fragmentShaderName = fragmentShaderName;
    }

    public int loadShaders() throws IOException {
        ResourceLoader rl = new ResourceLoader();
        IntBuffer ib = IntBuffer.allocate(1);

        //Vertex Shader
        if (!"".equals(vertexShaderName)) {

            URL urlVS = rl.getResource(vertexShaderName, "shaders");
            String vsrc = loadShaderFile(urlVS);
            vertexShader = createShader(GL_VERTEX_SHADER, vsrc);

        }
        //Geometry Shader
        if (!"".equals(geomShaderName)) {
            URL urlGS = rl.getResource(geomShaderName, "shaders");
            String gsrc = loadShaderFile(urlGS);
            geometryShader = createShader(GL_GEOMETRY_SHADER, gsrc);
        }
        //Fragment Shader
        if (!"".equals(fragmentShaderName)) {
            URL urlFS = rl.getResource(fragmentShaderName, "shaders");
            String fsrc = loadShaderFile(urlFS);
            fragmentShader = createShader(GL_FRAGMENT_SHADER, fsrc);
        }

        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, geometryShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);

        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Failed to link shader program: " + glGetProgramInfoLog(program));
        }

        glDeleteShader(vertexShader);
        glDeleteShader(geometryShader);
        glDeleteShader(fragmentShader);

        return program;
    }

//    /**
//     * Returns a handle for the specified uniform variable
//     *
//     * @param value Name of the uniform variable
//     * @return A handle to be used with jogl
//     */
//    public int getUniformVariable(String value) {
//        return gl.glGetUniformLocation(shaderprogram, value);
//    }

    /**
     * Returns the shader id
     *
     * @return The shader id
     */
    public int getShader() {
        return shaderprogram;
    }

    private String loadShaderFile(URL urlVS) throws IOException, FileNotFoundException {
        BufferedReader brv = new BufferedReader(new FileReader(urlVS.getFile()));
        String vsrc = "";
        String line;
        while ((line = brv.readLine()) != null) {
            vsrc += line + "\n";
        }
        return vsrc;
    }

    private int createShader(int type, String shaderCode) {
        int shader = glCreateShader(type);
        glShaderSource(shader, shaderCode);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Failed to compile shader: " + glGetShaderInfoLog(shader));
        }

        return shader;
    }
}
