/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import Cameras.Camera;
import com.jmathanim.Renderers.Java2DRenderer;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.ConfigUtils;
import com.jmathanim.mathobjects.MathObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class JMathAnimScene {

    String[] DEFAULT_CONFIG = {
        "WIDTH", "1920",
        "HEIGHT", "1280"
    };
    int contador = 0;
    int x;
    private final Properties cnf;
    ArrayList<MathObject> objects;
    private int frames;
    private Renderer renderer;
    private int frameCount;
    private Camera camera;

    public JMathAnimScene() {
        this(null);
    }

    public JMathAnimScene(Properties configParam) {
        cnf = new Properties();
        objects = new ArrayList<>();
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG, configParam);
        settings();
    }

    public final void settings() {
        camera=new Camera();
        renderer=new Java2DRenderer(cnf);
        renderer.setCamera(camera);

    }

//    /**
//     *
//     */
//    public final void runSketch() {
//        frames = 0;
//        mainLoop();
//        makeMovie();
//        System.exit(0);
//
//    }

    /**
     * Preparation code for the animation should go here
     */
    public abstract void setupSketch();

    /**
     *
     */
    public final void execute() {
        String nombre = this.getClass().getName();
        System.out.println("Run sketch: " + nombre);
        setupSketch();
        runSketch();
        renderer.finish();//Finish rendering jobs

    }

    public abstract void runSketch();

    public final MathObject add(MathObject obj) {
        objects.add(obj);
        return obj;
    }

    public final MathObject remove(MathObject obj) {
        objects.remove(obj);
        return obj;
    }

    public final void doDraws() {
        for (MathObject obj : objects) {
            obj.draw(renderer);
        }

    }

    public final void advanceFrame() {
        frameCount++;
        saveMPFrame();
        renderer.clear();
    }

    public void saveMPFrame() {
        
        renderer.saveFrame(frameCount);
        frameCount++;
        

    }

    public void makeMovie() {
        String cmd = "ffmpeg -r 13 -f image2 -i screen-%05d.tif" + " prueba.mp4";
        try {
            Process process = Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            Logger.getLogger(JMathAnimScene.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
