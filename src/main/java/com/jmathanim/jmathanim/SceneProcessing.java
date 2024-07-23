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
package com.jmathanim.jmathanim;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Cameras.Camera3D;
import com.jmathanim.Renderers.ProcessingRenderer.ProcessingRenderer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class SceneProcessing extends JMathAnimScene {

    protected Camera3D camera;
    protected Camera3D fixedCamera;

    public SceneProcessing() {
        super();
    }

    @Override
    protected void createRenderer() {
        fps = getConfig().fps;
        dt = 1. / fps;
        try {
            renderer = new ProcessingRenderer(this);
            renderer.initialize();
        } catch (Exception ex) {
            Logger.getLogger(Scene2D.class.getName()).log(Level.SEVERE, null, ex);
        }
        camera = renderer.getCamera();
        fixedCamera = renderer.getFixedCamera();
        super.renderer = renderer;
    }

}
