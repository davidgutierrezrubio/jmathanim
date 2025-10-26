package com.jmathanim.jmathanim.Groovy;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;
import com.jmathanim.jmathanim.Scene2DSkija;
import javafx.application.Platform;

import java.io.File;

import static com.jmathanim.jmathanim.LogUtils.RESET;

public class GroovyExecutorSkija extends Scene2DSkija {


    private final GroovyUtils groovyUtils;

    /**
     * Creates a new Scene2D to execute the given Groovy script file
     *
     * @param groovyScriptFileName Filename of Groovy Script filename
     */
    public GroovyExecutorSkija(String groovyScriptFileName) {
        super();
        groovyUtils = new GroovyUtils(groovyScriptFileName, this);

    }

    /**
     * Main method of GroovyExecutor
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            //print fancy help menu!
            System.out.println("No groovy script files given");
            System.exit(0);
        }
        boolean isJavaFXRunning = false;
        for (String arg : args) {
            File file = new File(arg);
            String absPath = file.getAbsolutePath();
            logger.info("Running Groovy Script " + LogUtils.BLUE + absPath + RESET);
            JMathAnimScene tr = new GroovyExecutorSkija(absPath);
            tr.getConfig().setScriptMode(true);
            tr.execute();
            if (tr.getConfig().isJavaFXRunning()) {
                isJavaFXRunning = true;
            }
        }
        // This is tricky, as I cannot shutdown javafx and initialize it again in the same execution
        //If I want to execute several consecutive scripts I must delegate this to the main class
        if (isJavaFXRunning)
            Platform.exit(); // Close JavaFX Toolkit
        System.exit(0);
    }


    @Override
    public void setupSketch() {
        groovyUtils.runSetup();
    }

    @Override
    public void runSketch() throws Exception {
        groovyUtils.runSketch();
    }
}
