package com.jmathanim.mathobjects.updaters;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

public abstract class Updater {
    protected MathObject mathObject;

    public Updater() {
    }

    public void setMathObject(MathObject mathObject) {
        this.mathObject = mathObject;
    }

    public abstract void update(JMathAnimScene scene);
    public abstract int getUpdateLevel();
}
