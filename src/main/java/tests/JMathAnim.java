/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import ch.qos.logback.classic.Level;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 *
 * @author David
 */
public class JMathAnim {

    

    /**
     * A launcher for the scene
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JMathAnimScene scene = new myScene();
//        JMathAnimScene scene = new DemoScene();
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        scene.execute();
    }

}
