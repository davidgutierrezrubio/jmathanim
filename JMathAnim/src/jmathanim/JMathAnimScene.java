/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmathanim;

import Renderers.Java2DRenderer;
import Renderers.Renderer;
import Utils.ConfigUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import mathobjects.MathObject;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class JMathAnimScene {

    String[] DEFAULT_CONFIG = {
        "WIDTH", "800",
        "HEIGHT", "600"
    };
    int contador = 0;
    int x;
    private final Properties cnf;
    ArrayList<MathObject> objects;
    private int frames;
    private Renderer renderer;
    private int frameCount;

    public JMathAnimScene() {
        this(null);
    }

    public JMathAnimScene(Properties configParam) {
        cnf = new Properties();
        objects = new ArrayList<>();
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG, configParam);
        renderer=new Java2DRenderer(cnf);
        settings();
    }

    public final void settings() {
        

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
