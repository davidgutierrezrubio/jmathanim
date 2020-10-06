/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.jmathanim.JMathAnimScene;
import java.util.ArrayList;

/**
 * Stores 2 or more animations and play them in sequential order
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Concatenate extends Animation {

    private final ArrayList<Animation> anims;
    private final ArrayList<Double> cumulativeTimes;
    private final ArrayList<Double> relCumulativeTimes;
    private int currentAnim;

    public Concatenate() {
        this(new ArrayList<Animation>());
    }

    public Concatenate(ArrayList<Animation> anims) {
        super();
        this.anims = anims;
        this.cumulativeTimes = new ArrayList<>();
        this.relCumulativeTimes = new ArrayList<>();
        currentAnim = 0;

    }

    public boolean add(Animation e) {
        return anims.add(e);
    }

    @Override
    public void initialize() {
        //Total runtime
        this.runTime = 0;
        this.cumulativeTimes.add(0d);
        for (Animation anim : anims) {
            this.runTime += anim.runTime;
            this.cumulativeTimes.add(this.runTime);
        }
//        for (int n=0;n<this.cumulativeTimes.size();n++)
//        {
//            this.relCumulativeTimes.add(this.cumulativeTimes.get(n)/this.runTime);
//        }
        //Initialize first animation
        anims.get(currentAnim).initialize();
    }

    @Override
    public void doAnim(double t, double lt) {
        double ct = this.cumulativeTimes.get(currentAnim);
        double l = anims.get(currentAnim).runTime;
        // (x-ct)/l where x=t*totalTime
        double tForThisAnim = (t * this.runTime - ct) / l;
        double ltForThisAnim = lambda(tForThisAnim);

        anims.get(currentAnim).doAnim(tForThisAnim, ltForThisAnim);
        if (tForThisAnim >= 1) {
            anims.get(currentAnim).finishAnimation();
            currentAnim++;
            anims.get(currentAnim).initialize();
        }
    }

    @Override
    public void finishAnimation() {
        anims.get(currentAnim).finishAnimation();
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
    }

}
