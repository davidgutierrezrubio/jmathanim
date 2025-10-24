package com.jmathanim.MathObjects.Updaters;

import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.jmathanim.JMathAnimScene;

public abstract class Updater {
    private MathObject mathObject;
    protected int updateLevel;

    public Updater() {
    }

    protected final void setUpdateLevel(int level) {
        this.updateLevel=level;
    }

    public MathObject<?> getMathObject() {
        return mathObject;
    }

    public void setMathObject(MathObject<?> mathObject) {
        this.mathObject = mathObject;
//        setUpdateLevel(computeUpdateLevel());
    }
//    public abstract  int computeUpdateLevel();

    public abstract void update(JMathAnimScene scene);

    ;

    public final int getUpdateLevel() {
        return updateLevel;
    }
}
