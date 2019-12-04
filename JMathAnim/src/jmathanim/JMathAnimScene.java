/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmathanim;

import Utils.ConfigUtils;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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
    private BufferedImage bufferedImage;
    private Graphics2D g2d;
    private int frameCount;

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
        bufferedImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        g2d = bufferedImage.createGraphics();
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2d.setRenderingHints(rh);

    }

    /**
     *
     */
    public final void runSketch() {
        frames = 0;
        mainLoop();
        makeMovie();
        System.exit(0);

    }

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
        mainLoop();

    }

    public abstract void mainLoop();

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
            obj.draw(g2d);
        }

    }

    public final void advanceFrame() {
        frameCount++;
        saveMPFrame();
    }

    public void saveMPFrame() {
        String fname = "c:\\media\\screen-" + String.format("%05d", frameCount) + ".png";
        frameCount++;
        File file = new File(fname);
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException ex) {
            Logger.getLogger(JMathAnimScene.class.getName()).log(Level.SEVERE, null, ex);
        }

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
