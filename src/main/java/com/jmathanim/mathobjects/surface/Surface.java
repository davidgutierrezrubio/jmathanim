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
package com.jmathanim.mathobjects.surface;

import com.jmathanim.Renderers.JOGLRenderer.JOGLRenderer;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import java.util.ArrayList;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Surface extends MathObject {

    public final ArrayList<Face> faces;

    public Surface() {
        faces = new ArrayList<>();
    }

    @Override
    public Surface copy() {
        Surface copy = new Surface();
        for (Face f : this.faces) {
            copy.faces.add(f.copy());
        }
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public Rect getBoundingBox() {
        return null;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        if (r instanceof JOGLRenderer) {
            ((JOGLRenderer) r).drawSurface(this);//Only for JOGL renderers
        }
    }

}
