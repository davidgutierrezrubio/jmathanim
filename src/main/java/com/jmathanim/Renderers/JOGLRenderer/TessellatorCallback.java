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

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLUtessellatorCallback;
import com.jogamp.opengl.glu.gl2.GLUgl2;

class TessellatorCallback implements GLUtessellatorCallback {

    private final GL2 gl;
    private final GLUgl2 glu;

    public TessellatorCallback(GLUgl2 glu, GL2 gl) {
        this.glu = glu;
        this.gl = gl;
    }

    @Override
    public void begin(int type) {
        gl.glBegin(type);

    }

    @Override
    public void beginData(int type, Object polygonData) {
    }

    @Override
    public void edgeFlag(boolean boundaryEdge) {
    }

    @Override
    public void edgeFlagData(boolean boundaryEdge, Object polygonData) {
    }

    @Override
    public void vertex(Object vertexData) {
        double[] data = (double[]) vertexData;
        gl.glVertex3d(data[0], data[1], data[2]);
    }

    @Override
    public void vertexData(Object vertexData, Object polygonData) {
    }

    @Override
    public void end() {
        gl.glEnd();
    }

    @Override
    public void endData(Object polygonData) {
        System.out.println("EndData " + polygonData);
    }

    @Override
    public void combine(double[] doubles, Object[] os, float[] weight, Object[] outData) {
//        double[] p0=(double[])os[0];
//        double[] p1=(double[])os[1];
//        double[] p2=(double[])os[2];
//        double[] p3=(double[])os[3];

//        double x=weight[0]*p0[0]+weight[1]*p1[0]+weight[2]*p2[0]+weight3*p3[0];
        outData[0] = new double[]{doubles[0], doubles[1], doubles[2]};
    }

    @Override
    public void combineData(double[] coords, Object[] data, float[] weight, Object[] outData, Object polygonData) {
    }

    @Override
    public void error(int errnum) {
    }

    @Override
    public void errorData(int errnum, Object polygonData) {
    }

}
