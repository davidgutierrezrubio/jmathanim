/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

/**
 * Stores all the data related to global configuration, to be accessed from
 * any object that requires it.
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMathAnimConfig {

    /**
     * Width of media screen. Typically 800 or 1920.
     */
    public int width;
    /**
     * Height of media screen. Typically 600 or 1080.
     */
    public int height;
    
    /**
     * Frames per second to use in the video. Typically 30 or 60.
     */
    public int fps;
    
    /**
     * Set low quality settings (800,600, 30fps). These are the default settings
     */
    public void setLowQuality(){
        width=800;
        height=600;
        fps=30;
        
    }

    /**
     * Set high quality settings (1920,1080, 60fps)
     */
    public void setHighQuality(){
        width=1920;
        height=1080;
        fps=60;
        
    }
}
