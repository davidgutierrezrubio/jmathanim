package com.jmathanim.mathobjects.Text.TextUpdaters;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;
import com.jmathanim.mathobjects.updaters.Updater;

public class LengthUpdaterFactory extends TextUpdaterFactory{
    private final Point A;
    private final Point B;
    private final LaTeXMathObject t;


    public LengthUpdaterFactory(JMathAnimScene scene, LaTeXMathObject t,Point a, Point b,String format) {
        super(scene,format);
        A = a;
        B = b;
        this.t=t;
        updater= new Updater() {
            @Override
            public void update(JMathAnimScene scene) {
                double norm = A.to(B).norm();
                t.getArg(0).setScalar(norm);

            }
        };
    }
}
