/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Cameras.Camera2D;
import com.jmathanim.Renderers.Java2DRenderer;
import com.jmathanim.Utils.ConfigUtils;
import java.util.Properties;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class Scene2D extends JMathAnimScene {

    String[] DEFAULT_CONFIG_2D = {
        "WIDTH", "800",
        "HEIGHT", "600",
        "FPS", "25"
    };
    protected Java2DRenderer renderer;
    protected Camera2D camera;

    public Scene2D() {
        this(null);
    }

    public Scene2D(Properties configParam) {
        super(configParam);
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG_2D, configParam);
        fps = Double.parseDouble((String) cnf.get("FPS"));
        renderer = new Java2DRenderer(cnf);
        camera=renderer.getCamera();
        SCRenderer=renderer;
        SCCamera=camera;

    }

}
