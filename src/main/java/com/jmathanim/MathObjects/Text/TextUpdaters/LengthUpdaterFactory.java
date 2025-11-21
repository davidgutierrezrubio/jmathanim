package com.jmathanim.MathObjects.Text.TextUpdaters;

import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Text.LatexMathObject;
import com.jmathanim.MathObjects.Updaters.Updater;
import com.jmathanim.jmathanim.JMathAnimScene;

public class LengthUpdaterFactory extends TextUpdaterFactory{
    private final Coordinates<?> A;
    private final Coordinates<?> B;
    private final LatexMathObject t;


    public LengthUpdaterFactory( LatexMathObject t, Coordinates<?> a, Coordinates<?> b, String format) {
        super(format);
        A = a;
        B = b;
        this.t=t;
       addUpdater(new Updater() {
            @Override
            public void update() {
                double norm = A.to(B).norm();
                t.getArg(0).setValue(norm);
            }
        });
    }
}
