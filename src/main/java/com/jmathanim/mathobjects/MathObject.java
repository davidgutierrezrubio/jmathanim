/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.ConfigUtils;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.util.HashSet;
import java.util.Properties;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class MathObject implements Drawable {

    String[] DEFAULT_CONFIG_MATHOBJECT = {
        "VISIBLE", "TRUE",
        "ALPHA", "1",
        "COLOR", "255"
    };
     /**
     * Scenes where this object belongs.
     * 
     */
    private HashSet<JMathAnimScene> scenes;
    Properties cnf;
    public boolean visible;
    /**
     * This parameter specifies the amount of object to be drawn 0=none,
     * 1/2=draw half
     */
    protected double drawParam;

    /**
     * Transparency of object, 0=transparent, 1=opaque
     */
    protected double alpha;

    /**
     * Mathobjects dependent of this. These should be updated4
     * when this object changes
     */
    public final HashSet<MathObject> descendent;
    
    /**
     * Mathobjects which this is dependent from. This object should be updated4
     * when any of this list changes.
     */
    public final HashSet<MathObject> ascendent;
    public final HashSet<MathObject> cousins;
    public MathObject() {
        this(null);
    }

    public MathObject(Properties configParam) {
        cnf = new Properties();
        visible=true;
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG_MATHOBJECT, configParam);
        alpha = Float.parseFloat(cnf.getProperty("ALPHA"));
        drawParam = 1;
        ascendent=new HashSet<>();
        descendent=new HashSet<>();
        cousins=new HashSet<>();
        scenes=new HashSet<>();
    }

    /**
     * Return center of object. Implementation depends on type of MathObject
     *
     * @return Vec object with center
     */
    public abstract Vec getCenter();

    /**
     * Move object so that center is the given coords
     *
     * @param coords Vec with coordinates of new center
     */
    public abstract void moveTo(Vec coords);

    /**
     * Shift object with the given vector
     *
     * @param shiftVector
     */
    public abstract void shift(Vec shiftVector);

    /**
     * Scale from center of object (2D version)
     *
     * @param sx
     * @param sy
     */
    public void scale(double sx, double sy) {
        scale(getCenter(), sx, sy, 1);
    }

    /**
     * Scale from center of object (3D version)
     *
     * @param sx
     * @param sy
     * @param sz
     */
    public void scale(double sx, double sy, double sz) {
        scale(getCenter(), sx, sy, sz);
    }

    public abstract void scale(Vec scaleCenter, double sx, double sy, double sz);

    /**
     * Return draw Parameter. This is used in animations like draw() 0=no drawn,
     * 1=totally drawn
     *
     * @return
     */
    public final double getDrawParam() {
        return drawParam;
    }

    /**
     * Set draw Parameter
     *
     * @param drawParam
     */
    public final void setDrawParam(double drawParam) {
        this.drawParam = drawParam;
    }

    /**
     * Get Alpha parameter of transparency 0=full transparency, 1=opaque
     *
     * @return
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Set alpha parameter
     *
     * @param alpha
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * Returns a copy of the object
     *
     * @return copy of object, with identical properties
     */
    abstract public MathObject copy();
    
    /**
     * Update all necessary componentes of this object to display properly
     * This should be called when any of its subobjects (sides, vertices...)
     * changes
     */
    abstract public void update();
    
    public void dependsOn(MathObject mob)
    {
        ascendent.add(mob);
        mob.descendent.add(this);
    }
    
    public void removeDependsOn(MathObject mob)
        {
        ascendent.remove(mob);
        mob.descendent.remove(this);
    }    
    
    public void updateDependents()
    {
        HashSet<MathObject> desC = (HashSet<MathObject>) descendent.clone();
        for (MathObject mob:desC)
        {
            mob.update();
        }
    }

    public void addScene(JMathAnimScene scen) {
        scenes.add(scen);
        for (MathObject mob:cousins)
        {
            scen.add(mob);
        }
    }

    public void removeScene(JMathAnimScene scen) {
        scenes.remove(scen); 
         for (MathObject mob:descendent)
        {
            mob.removeScene(scen);
        }
    }
    public void addObjectToScene(MathObject mob)
    {
        for (JMathAnimScene sce:scenes)
        {
            mob.addScene(sce);
        }
    }
    
}
