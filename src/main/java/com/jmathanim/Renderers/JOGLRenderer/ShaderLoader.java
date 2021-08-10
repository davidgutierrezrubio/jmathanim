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

    private GL3ES3 gles3;
    private GL3 gl;
    private int shaderprogram;

    public float scaVal;
    public int unifModelMat;
    public int unifColor;
    public int unifProject;
    public int unifScal;

    public ShaderLoader(GL3ES3 gles, GL3 gl) {
        this.gles3 = gles;
        this.gl = gl;
    }

    public void loadShaders() throws IOException {
        int v = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
        int f = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        int g = gl.glCreateShader(GL3.GL_GEOMETRY_SHADER);

        
        //Vertex Shader
        ResourceLoader rl = new ResourceLoader();
        URL urlVS = rl.getResource("#default.vs", "shaders");
        BufferedReader brv = new BufferedReader(new FileReader(urlVS.getFile()));
        String vsrc = "";
        String line;
        while ((line = brv.readLine()) != null) {
            vsrc += line + "\n";
        }
        gl.glShaderSource(v, 1, new String[]{vsrc}, null);
        gl.glCompileShader(v);
        IntBuffer ib = IntBuffer.allocate(1);
        gl.glGetShaderiv(v, GL2.GL_COMPILE_STATUS, ib);
        if (ib.get(0) == GL2.GL_FALSE) {
            System.out.println("Error compiling Vertex Shader");
        }

         //Geometry Shader
        URL urlGS = rl.getResource("#default.gs", "shaders");
        BufferedReader brg = new BufferedReader(new FileReader(urlGS.getFile()));
        String gsrc = "";
        while ((line = brg.readLine()) != null) {
            gsrc += line + "\n";
        }
        gl.glShaderSource(g, 1, new String[]{gsrc}, null);
        gl.glCompileShader(g);
        ib = IntBuffer.allocate(1);
        gl.glGetShaderiv(g, GL2.GL_COMPILE_STATUS, ib);
        if (ib.get(0) == GL2.GL_FALSE) {
                   System.out.println("Error compiling Geometry Shader");
        }
        
        
        //Fragment Shader
        URL urlFS = rl.getResource("#default.fs", "shaders");
        BufferedReader brf = new BufferedReader(new FileReader(urlFS.getFile()));
        String fsrc = "";
        while ((line = brf.readLine()) != null) {
            fsrc += line + "\n";
        }
        gl.glShaderSource(f, 1, new String[]{fsrc}, null);
        gl.glCompileShader(f);
        ib = IntBuffer.allocate(1);
        gl.glGetShaderiv(f, GL2.GL_COMPILE_STATUS, ib);
        if (ib.get(0) == GL2.GL_FALSE) {
                   System.out.println("Error compiling Fragment Shader");
        }
        
        
        
        
        
        
        
        shaderprogram = gl.glCreateProgram();
        gl.glAttachShader(shaderprogram, v);
        gl.glAttachShader(shaderprogram, g);
        gl.glAttachShader(shaderprogram, f);

        
             gl.glBindAttribLocation(shaderprogram, 0, "at_Posit");
        gl.glBindAttribLocation(shaderprogram, 1, "at_Color");
        
        
        
        
        gl.glLinkProgram(shaderprogram);

        ib = IntBuffer.allocate(1);
        gl.glGetProgramiv(shaderprogram, GL2.GL_LINK_STATUS, ib);
        if (ib.get(0) == GL2.GL_FALSE) {
            System.out.println("ERROR AL LINKEAR");
        }

        gl.glValidateProgram(shaderprogram);
        //Uncomment this for tweaking shaders
        gl.glUseProgram(shaderprogram);
        unifProject = gl.glGetUniformLocation(shaderprogram, "projection");
        unifModelMat = gl.glGetUniformLocation(shaderprogram, "modelMat");
        unifColor = gl.glGetUniformLocation(shaderprogram, "currentColor");

        ib = IntBuffer.allocate(1);
        //Print attributes
        gl.glGetProgramiv(shaderprogram, GL2.GL_ACTIVE_ATTRIBUTES, ib);
        System.out.println("Hay " + ib.get(0) + " atributos");

        for (int n = 0; n < ib.get(0); n++) {
            IntBuffer ib1 = IntBuffer.allocate(16);
            IntBuffer ib2 = IntBuffer.allocate(16);
            IntBuffer ib3 = IntBuffer.allocate(16);
            ByteBuffer bb = ByteBuffer.allocate(16);
            gl.glGetActiveAttrib(shaderprogram, n, 16, ib1, ib2, ib3, bb);
            System.out.println("Attribute " + n + ": type " + ib3.get(0) + ", name: " + StandardCharsets.UTF_8.decode(bb).toString());
        }

    }
}
