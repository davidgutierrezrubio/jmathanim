/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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

package com.jmathanim.jmathanim;

import com.jmathanim.Cameras.Camera2D;
import com.jmathanim.Renderers.Java2DRenderer;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Scene2D extends JMathAnimScene {

    protected Java2DRenderer renderer2d;
    protected Camera2D camera;


    public Scene2D() {
        super();
    }

    @Override
    public void createRenderer(){
        fps = conf.fps;
        dt=1./fps;
        renderer2d = new Java2DRenderer(this);
        camera=renderer2d.getCamera();
        super.renderer=renderer2d;
    }

   
    
}
