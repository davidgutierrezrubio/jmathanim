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
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL3ES3;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ShaderLoader {

    private GL3 gl;
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
    private final String vertexShader;
    private final String geomShader;
    private final String fragmentShader;

    public ShaderLoader(GL3 gl, String vertexShader, String geomShader, String fragmentShader) {
        this.vertexShader = vertexShader;
        this.geomShader = geomShader;
        this.fragmentShader = fragmentShader;
        this.gl = gl;
    }

    public void loadShaders() throws IOException {
        int v = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
        int f = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        int g = gl.glCreateShader(GL3.GL_GEOMETRY_SHADER);
        ResourceLoader rl = new ResourceLoader();
        IntBuffer ib = IntBuffer.allocate(1);

        //Vertex Shader
        if (!"".equals(vertexShader)) {

            URL urlVS = rl.getResource(vertexShader, "shaders");
            String vsrc = loadShaderFile(urlVS);
            gl.glShaderSource(v, 1, new String[]{vsrc}, null);
            gl.glCompileShader(v);

            gl.glGetShaderiv(v, GL2.GL_COMPILE_STATUS, ib);
            if (ib.get(0) == GL2.GL_FALSE) {
                System.out.println("Error compiling Vertex Shader");
            } else {
                System.out.println("Sucesfully compiled Vertex Shader");
            }
        }
        //Geometry Shader
        if (!"".equals(geomShader)) {
            URL urlGS = rl.getResource(geomShader, "shaders");
            BufferedReader brg = new BufferedReader(new FileReader(urlGS.getFile()));
            String gsrc = loadShaderFile(urlGS);
            gl.glShaderSource(g, 1, new String[]{gsrc}, null);
            gl.glCompileShader(g);
            ib = IntBuffer.allocate(1);
            gl.glGetShaderiv(g, GL2.GL_COMPILE_STATUS, ib);
            if (ib.get(0) == GL2.GL_FALSE) {
                System.out.println("Error compiling Geometry Shader");
            } else {
                System.out.println("Sucesfully compiled Geometry Shader");
            }
        }
        //Fragment Shader
        if (!"".equals(fragmentShader)) {
            URL urlFS = rl.getResource(fragmentShader, "shaders");
            BufferedReader brf = new BufferedReader(new FileReader(urlFS.getFile()));
            String fsrc = loadShaderFile(urlFS);
            gl.glShaderSource(f, 1, new String[]{fsrc}, null);
            gl.glCompileShader(f);
            ib = IntBuffer.allocate(1);
            gl.glGetShaderiv(f, GL2.GL_COMPILE_STATUS, ib);
            if (ib.get(0) == GL2.GL_FALSE) {
                System.out.println("Error compiling Fragment Shader");
            } else {
                System.out.println("Sucesfully compiled Fragment Shader");
            }
        }

        shaderprogram = gl.glCreateProgram();
        if (!"".equals(vertexShader)) {
            gl.glAttachShader(shaderprogram, v);
        }
        if (!"".equals(geomShader)) {
            gl.glAttachShader(shaderprogram, g);
        }
        if (!"".equals(fragmentShader)) {
            gl.glAttachShader(shaderprogram, f);
        }
        gl.glLinkProgram(shaderprogram);

        ib = IntBuffer.allocate(1);
        gl.glGetProgramiv(shaderprogram, GL2.GL_LINK_STATUS, ib);
        if (ib.get(0) == GL2.GL_FALSE) {
            System.out.println("ERROR AL LINKEAR");
        }

        gl.glValidateProgram(shaderprogram);
//        gl.glUseProgram(shaderprogram);
//        unifProject = gl.glGetUniformLocation(shaderprogram, "projection");
//        unifModelMat = gl.glGetUniformLocation(shaderprogram, "modelMat");
//        unifThickness = gl.glGetUniformLocation(shaderprogram, "Thickness");
//        unifViewPort = gl.glGetUniformLocation(shaderprogram, "Viewport");
//        unifMiterLimit = gl.glGetUniformLocation(shaderprogram, "MiterLimit");
//        unifColor = gl.glGetUniformLocation(shaderprogram, "unifColor");

//        ib = IntBuffer.allocate(1);
//        //Print attributes
//        gl.glGetProgramiv(shaderprogram, GL2.GL_ACTIVE_ATTRIBUTES, ib);
//        System.out.println("Hay " + ib.get(0) + " atributos");
//
//        for (int n = 0; n < ib.get(0); n++) {
//            IntBuffer ib1 = IntBuffer.allocate(16);
//            IntBuffer ib2 = IntBuffer.allocate(16);
//            IntBuffer ib3 = IntBuffer.allocate(16);
//            ByteBuffer bb = ByteBuffer.allocate(16);
//            gl.glGetActiveAttrib(shaderprogram, n, 16, ib1, ib2, ib3, bb);
//            System.out.println("Attribute " + n + ": type " + ib3.get(0) + ", name: " + StandardCharsets.UTF_8.decode(bb).toString());
//        }
    }

    /**
     * Returns a handle for the specified uniform variable
     *
     * @param value Name of the uniform variable
     * @return A handle to be used with jogl
     */
    public int getUniformVariable(String value) {
        return gl.glGetUniformLocation(shaderprogram, value);
    }

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
}
