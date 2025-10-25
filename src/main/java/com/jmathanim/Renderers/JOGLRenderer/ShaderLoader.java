///*
// * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
// *
// * This program is free software; you can redistribute it and/or
// * modify it under the terms of the GNU General Public License
// * as published by the Free Software Foundation; either version 2
// * of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
// */
//package com.jmathanim.Renderers.JOGLRenderer;
//
//import com.jmathanim.Utils.ResourceLoader;
//import com.jmathanim.jmathanim.JMathAnimScene;
//import com.jogamp.opengl.GL2;
//import com.jogamp.opengl.GL3;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.net.URL;
//import java.nio.IntBuffer;
//
///**
// *
// * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
// */
//public class ShaderLoader {
//
//    private final GL3 gl;
//    private int shaderprogram;
//
////    public float scaVal;
////    public int unifMiterLimit;
////    public int unifModelMat;
////    public int unifColor;
////    public int unifProject;
////    public int unifScal;
////    public int unifThickness;
////    public int unifViewPort;
////    private static String VERTEX_SHADER_FILE = "#bezier/BezierDraw.vs";
////    private static String GEOMETRY_SHADER_FILE = "#bezier/BezierDrawGenPoints.gs";
////    private static String FRAGMENT_SHADER_FILE = "#bezier/BezierDraw.fs";
////    private static String VERTEX_SHADER_FILE = "#default.vs";
////    private static String GEOMETRY_SHADER_FILE = "#default.gs";
////    private static String FRAGMENT_SHADER_FILE = "#default.fs";
//    private final String vertexShader;
//    private final String geomShader;
//    private final String fragmentShader;
//
//    public ShaderLoader(GL3 gl, String vertexShader, String geomShader, String fragmentShader) {
//        this.vertexShader = vertexShader;
//        this.geomShader = geomShader;
//        this.fragmentShader = fragmentShader;
//        this.gl = gl;
//    }
//
//    public void loadShaders() throws IOException {
//        int v = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
//        int f = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
//        int g = gl.glCreateShader(GL3.GL_GEOMETRY_SHADER);
//        ResourceLoader rl = new ResourceLoader();
//        IntBuffer ib = IntBuffer.allocate(1);
//
//        //Vertex Shader
//        if (!"".equals(vertexShader)) {
//
//            URL urlVS = rl.getResource(vertexShader, "shaders");
//            String vsrc = loadShaderFile(urlVS);
//            gl.glShaderSource(v, 1, new String[]{vsrc}, null);
//            gl.glCompileShader(v);
//
//            gl.glGetShaderiv(v, GL2.GL_COMPILE_STATUS, ib);
//            if (ib.get(0) == GL2.GL_FALSE) {
//                JMathAnimScene.logger.error("Error compiling Vertex Shader " + vertexShader);
//                int[] infoLogLength = new int[1];
//                gl.glGetShaderiv(v, GL2.GL_INFO_LOG_LENGTH, infoLogLength, 0);
//                byte[] infoLog = new byte[infoLogLength[0]];
//                gl.glGetShaderInfoLog(v, infoLogLength[0], null, 0, infoLog, 0);
//
//                JMathAnimScene.logger.error(new String(infoLog));
//                System.exit(0);
//            } else {
//                JMathAnimScene.logger.debug("Sucesfully compiled Vertex Shader " + vertexShader);
//            }
//        }
//        //Geometry Shader
//        if (!"".equals(geomShader)) {
//            URL urlGS = rl.getResource(geomShader, "shaders");
//            String gsrc = loadShaderFile(urlGS);
//            gl.glShaderSource(g, 1, new String[]{gsrc}, null);
//            gl.glCompileShader(g);
//            ib = IntBuffer.allocate(1);
//            gl.glGetShaderiv(g, GL2.GL_COMPILE_STATUS, ib);
//            if (ib.get(0) == GL2.GL_FALSE) {
//                JMathAnimScene.logger.error("Error compiling Geometry Shader " + geomShader);
//                int[] infoLogLength = new int[1];
//                gl.glGetShaderiv(g, GL2.GL_INFO_LOG_LENGTH, infoLogLength, 0);
//                byte[] infoLog = new byte[infoLogLength[0]];
//                gl.glGetShaderInfoLog(g, infoLogLength[0], null, 0, infoLog, 0);
//
//                JMathAnimScene.logger.error(new String(infoLog));
//                System.exit(0);
//            } else {
//                JMathAnimScene.logger.debug("Sucesfully compiled Geometry Shader " + geomShader);
//            }
//        }
//        //Fragment Shader
//        if (!"".equals(fragmentShader)) {
//            URL urlFS = rl.getResource(fragmentShader, "shaders");
//            String fsrc = loadShaderFile(urlFS);
//            gl.glShaderSource(f, 1, new String[]{fsrc}, null);
//            gl.glCompileShader(f);
//            ib = IntBuffer.allocate(1);
//            gl.glGetShaderiv(f, GL2.GL_COMPILE_STATUS, ib);
//            if (ib.get(0) == GL2.GL_FALSE) {
//                JMathAnimScene.logger.error("Error compiling Fragment Shader " + fragmentShader);
//                int[] infoLogLength = new int[1];
//                gl.glGetShaderiv(f, GL2.GL_INFO_LOG_LENGTH, infoLogLength, 0);
//                byte[] infoLog = new byte[infoLogLength[0]];
//                gl.glGetShaderInfoLog(f, infoLogLength[0], null, 0, infoLog, 0);
//
//                JMathAnimScene.logger.error(new String(infoLog));
//                System.exit(0);
//
//
//            } else {
//                JMathAnimScene.logger.debug("Sucesfully compiled Fragment Shader " + fragmentShader);
//            }
//        }
//
//        shaderprogram = gl.glCreateProgram();
//        if (!"".equals(vertexShader)) {
//            gl.glAttachShader(shaderprogram, v);
//        }
//        if (!"".equals(geomShader)) {
//            gl.glAttachShader(shaderprogram, g);
//        }
//        if (!"".equals(fragmentShader)) {
//            gl.glAttachShader(shaderprogram, f);
//        }
//        gl.glLinkProgram(shaderprogram);
//
//        ib = IntBuffer.allocate(1);
//        gl.glGetProgramiv(shaderprogram, GL2.GL_LINK_STATUS, ib);
//        if (ib.get(0) == GL2.GL_FALSE) {
//            JMathAnimScene.logger.error("An error ocurred linking shaders!");
//        }
//
//        gl.glValidateProgram(shaderprogram);
//    }
//
//    /**
//     * Returns a handle for the specified uniform variable
//     *
//     * @param value Name of the uniform variable
//     * @return A handle to be used with jogl
//     */
//    public int getUniformVariable(String value) {
//        return gl.glGetUniformLocation(shaderprogram, value);
//    }
//
//    /**
//     * Returns the shader id
//     *
//     * @return The shader id
//     */
//    public int getShader() {
//        return shaderprogram;
//    }
//
//    private String loadShaderFile(URL urlVS) throws IOException {
//        BufferedReader brv = new BufferedReader(new FileReader(urlVS.getFile()));
//        String vsrc = "";
//        String line;
//        while ((line = brv.readLine()) != null) {
//            vsrc += line + "\n";
//        }
//        return vsrc;
//    }
//}
