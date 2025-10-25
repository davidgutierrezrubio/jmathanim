/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Renderers.SkijaRenderer.SkijaRenderer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Scene2DSkija extends JMathAnimScene {

	protected SkijaRenderer renderer;
	protected Camera camera;
	protected Camera fixedCamera;

	public Scene2DSkija() {
		super();
	}

	@Override
	protected Renderer createRenderer() {
		fps = getConfig().fps;
		dt = 1. / fps;
		timeMillisPerFrame= (long) (1000d/fps);
		try {
			renderer = new SkijaRenderer(this);
                        renderer.initialize();
		} catch (Exception ex) {
			Logger.getLogger(Scene2DSkija.class.getName()).log(Level.SEVERE, null, ex);
		}
		super.renderer = renderer;
        return renderer;
	}

}
