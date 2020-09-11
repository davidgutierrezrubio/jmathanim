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

    protected Java2DRenderer renderer;
    protected Camera2D camera;
    private boolean createMovie=true;
    private boolean showPreviewWindow=false;


    public Scene2D() {
        super();
    }

    @Override
    public void createRenderer(){
        fps = conf.fps;
        dt=1./fps;
        renderer = new Java2DRenderer(this,createMovie,showPreviewWindow);
        camera=renderer.getCamera();
        SCRenderer=renderer;
    }

    public boolean isCreateMovie() {
        return createMovie;
    }

    public void setCreateMovie(boolean createMovie) {
        this.createMovie = createMovie;
    }

    public boolean isShowPreviewWindow() {
        return showPreviewWindow;
    }

    public void setShowPreviewWindow(boolean showPreviewWindow) {
        this.showPreviewWindow = showPreviewWindow;
    }
    
}
