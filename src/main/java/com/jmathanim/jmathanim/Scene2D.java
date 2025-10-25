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

import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.Renderers.Renderer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Scene2D extends JMathAnimScene {

	protected JavaFXRenderer renderer;

	public Scene2D() {
		super();
	}

	@Override
	protected Renderer createRenderer() {
		fps = getConfig().fps;
		dt = 1. / fps;
		timeMillisPerFrame= (long) (1000d/fps);
		try {
			renderer = new JavaFXRenderer(this);
                        renderer.initialize();
		} catch (InterruptedException ex) {
			Logger.getLogger(Scene2D.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(Scene2D.class.getName()).log(Level.SEVERE, null, ex);
		}
		super.renderer = renderer;
        return renderer;
	}
	protected void addFXNode(javafx.scene.Node node) {
		renderer.addJavaFXNode(node);
	}
	protected void removeFXNode(javafx.scene.Node node) {
		renderer.removeJavaFXNode(node);
	}


}
