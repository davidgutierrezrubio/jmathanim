package com.jmathanim.mathobjects.Text.TextUpdaters;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.Text.LatexMathObject;
import com.jmathanim.mathobjects.updaters.Updater;

public class LengthUpdaterFactory extends TextUpdaterFactory{
    private final Coordinates<?> A;
    private final Coordinates<?> B;
    private final LatexMathObject t;


    public LengthUpdaterFactory(JMathAnimScene scene, LatexMathObject t, Coordinates<?> a, Coordinates<?> b, String format) {
        super(scene,format);
        A = a;
        B = b;
        this.t=t;
        updater= new Updater() {
            @Override
            public void update(JMathAnimScene scene) {
                double norm = A.to(B).norm();
                t.getArg(0).setValue(norm);
            }
        };
    }
}
