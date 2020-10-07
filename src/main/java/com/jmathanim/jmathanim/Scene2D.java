/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
