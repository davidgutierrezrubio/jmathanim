package com.jmathanim.MathObjects.Text.TextUpdaters;

import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Text.AbstractLatexMathObject;
import com.jmathanim.MathObjects.Text.LatexMathObject;
import com.jmathanim.MathObjects.Updaters.Updater;
import com.jmathanim.jmathanim.JMathAnimScene;

public class LengthUpdaterFactory extends TextUpdaterFactory{
    private final Coordinates<?> A;
    private final Coordinates<?> B;



    public LengthUpdaterFactory(AbstractLatexMathObject<?> t, Coordinates<?> a, Coordinates<?> b, String format) {
        super(t,format);
        A = a;
        B = b;
       addUpdater(new Updater() {

           @Override
           public void applyAfter() {

           }

           @Override
           public void applyBefore() {
                double norm = A.to(B).norm();
                t.getArg(0).setValue(norm);
                t.markDirty();
            }
        });
    }
}
