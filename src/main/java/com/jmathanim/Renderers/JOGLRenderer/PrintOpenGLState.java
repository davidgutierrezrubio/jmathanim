/*
 * Copyright (C) 2024 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Renderers.JOGLRenderer;

import com.jogamp.opengl.GL3;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class PrintOpenGLState {

    public static void printOpenGLState(GL3 gl3) {
        // Buffer to store state values
        int[] intBuffer = new int[1];
        float[] floatBuffer = new float[1];
        byte[] booleanBuffer = new byte[1];

        // Depth Test State
        gl3.glGetIntegerv(GL3.GL_DEPTH_TEST, intBuffer, 0);
        System.out.println("GL_DEPTH_TEST: " + (gl3.glIsEnabled(GL3.GL_DEPTH_TEST) ? "Enabled" : "Disabled"));

        // Depth Mask State
        gl3.glGetBooleanv(GL3.GL_DEPTH_WRITEMASK, booleanBuffer, 0);
        System.out.println("GL_DEPTH_WRITEMASK: " + (booleanBuffer[0] == 1 ? "True" : "False"));

        // Depth Function
        gl3.glGetIntegerv(GL3.GL_DEPTH_FUNC, intBuffer, 0);
        System.out.println("GL_DEPTH_FUNC: " + getDepthFunctionName(intBuffer[0]));

        // Stencil Test State
        System.out.println("GL_STENCIL_TEST: " + (gl3.glIsEnabled(GL3.GL_STENCIL_TEST) ? "Enabled" : "Disabled"));

        // Blend State
        System.out.println("GL_BLEND: " + (gl3.glIsEnabled(GL3.GL_BLEND) ? "Enabled" : "Disabled"));

        // Cull Face State
        System.out.println("GL_CULL_FACE: " + (gl3.glIsEnabled(GL3.GL_CULL_FACE) ? "Enabled" : "Disabled"));

        // Current Blend Function
        gl3.glGetIntegerv(GL3.GL_BLEND_SRC_RGB, intBuffer, 0);
        System.out.println("GL_BLEND_SRC_RGB: " + getBlendFactorName(intBuffer[0]));
        gl3.glGetIntegerv(GL3.GL_BLEND_DST_RGB, intBuffer, 0);
        System.out.println("GL_BLEND_DST_RGB: " + getBlendFactorName(intBuffer[0]));

        // Current Viewport
        int[] viewport = new int[4];
        gl3.glGetIntegerv(GL3.GL_VIEWPORT, viewport, 0);
        System.out.println("GL_VIEWPORT: x=" + viewport[0] + " y=" + viewport[1]
                + " width=" + viewport[2] + " height=" + viewport[3]);

        // Current Scissor Box
        int[] scissorBox = new int[4];
        gl3.glGetIntegerv(GL3.GL_SCISSOR_BOX, scissorBox, 0);
        System.out.println("GL_SCISSOR_BOX: x=" + scissorBox[0] + " y=" + scissorBox[1]
                + " width=" + scissorBox[2] + " height=" + scissorBox[3]);

        // Current Clear Color
        float[] clearColor = new float[4];
        gl3.glGetFloatv(GL3.GL_COLOR_CLEAR_VALUE, clearColor, 0);
        System.out.println("GL_COLOR_CLEAR_VALUE: R=" + clearColor[0] + " G=" + clearColor[1]
                + " B=" + clearColor[2] + " A=" + clearColor[3]);
        System.out.println("-------------------------------------------");
    }

// Helper method to get the depth function name
    public static String getDepthFunctionName(int func) {
        switch (func) {
            case GL3.GL_NEVER:
                return "GL_NEVER";
            case GL3.GL_LESS:
                return "GL_LESS";
            case GL3.GL_EQUAL:
                return "GL_EQUAL";
            case GL3.GL_LEQUAL:
                return "GL_LEQUAL";
            case GL3.GL_GREATER:
                return "GL_GREATER";
            case GL3.GL_NOTEQUAL:
                return "GL_NOTEQUAL";
            case GL3.GL_GEQUAL:
                return "GL_GEQUAL";
            case GL3.GL_ALWAYS:
                return "GL_ALWAYS";
            default:
                return "UNKNOWN";
        }
    }

// Helper method to get the blend factor name
    public static String getBlendFactorName(int factor) {
        switch (factor) {
            case GL3.GL_ZERO:
                return "GL_ZERO";
            case GL3.GL_ONE:
                return "GL_ONE";
            case GL3.GL_SRC_COLOR:
                return "GL_SRC_COLOR";
            case GL3.GL_ONE_MINUS_SRC_COLOR:
                return "GL_ONE_MINUS_SRC_COLOR";
            case GL3.GL_DST_COLOR:
                return "GL_DST_COLOR";
            case GL3.GL_ONE_MINUS_DST_COLOR:
                return "GL_ONE_MINUS_DST_COLOR";
            case GL3.GL_SRC_ALPHA:
                return "GL_SRC_ALPHA";
            case GL3.GL_ONE_MINUS_SRC_ALPHA:
                return "GL_ONE_MINUS_SRC_ALPHA";
            case GL3.GL_DST_ALPHA:
                return "GL_DST_ALPHA";
            case GL3.GL_ONE_MINUS_DST_ALPHA:
                return "GL_ONE_MINUS_DST_ALPHA";
            case GL3.GL_CONSTANT_COLOR:
                return "GL_CONSTANT_COLOR";
            case GL3.GL_ONE_MINUS_CONSTANT_COLOR:
                return "GL_ONE_MINUS_CONSTANT_COLOR";
            case GL3.GL_CONSTANT_ALPHA:
                return "GL_CONSTANT_ALPHA";
            case GL3.GL_ONE_MINUS_CONSTANT_ALPHA:
                return "GL_ONE_MINUS_CONSTANT_ALPHA";
            default:
                return "UNKNOWN";
        }
    }
}
