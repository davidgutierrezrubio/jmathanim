/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class AbsoluteSizeUpdater implements Updateable{
    public MathObject obj;
    public double ratio;

    public AbsoluteSizeUpdater(MathObject obj, double ratio) {
        this.obj = obj;
        this.ratio = ratio;
    }

    
    @Override
    public int getUpdateLevel() {
        return obj.getUpdateLevel()+1;
    }

    @Override
    public void update() {
        Camera camera = JMathAnimConfig.getConfig().getCamera();
        double screenMathWidth=camera.getMathView().getWidth();
        double objectWidth=obj.getBoundingBox().getWidth();
        
        //ObjectWidth should be ratio*screenMathWidth
        //so I scale object by ratio*screenMathWidth/objectWidth
        double scale=ratio*screenMathWidth/objectWidth;
        obj.scale(scale, scale);
    }
    
    
}
