/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David
 */
public class JMathAnim {

    public final static Logger logger = LoggerFactory.getLogger("com.jmathanim.jmathanim.JMathAnim");

    /**
     * A launcher for the scene
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JMathAnimScene scene = new PointSimple();
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.WARN);
        logger.debug("Loading scene " + scene.getClass().getName());

        scene.execute();
    }

}
