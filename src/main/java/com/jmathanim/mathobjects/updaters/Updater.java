package com.jmathanim.mathobjects.updaters;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

public abstract class Updater {
    private MathObject mathObject;
    protected int updateLevel;

    public Updater() {
    }

    protected final void setUpdateLevel(int level) {
        this.updateLevel=level;
    }

    public MathObject getMathObject() {
        return mathObject;
    }

    public void setMathObject(MathObject mathObject) {
        this.mathObject = mathObject;
        computeUpdateLevel();
    }
    public abstract  void computeUpdateLevel();

    public abstract void update(JMathAnimScene scene);

    ;

    public final int getUpdateLevel() {
        return updateLevel;
    }
}
