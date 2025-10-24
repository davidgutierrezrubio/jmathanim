package com.jmathanim.MathObjects.Text.TextUpdaters;

import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Text.LatexMathObject;
import com.jmathanim.MathObjects.Updaters.Updater;
import com.jmathanim.jmathanim.JMathAnimScene;

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
