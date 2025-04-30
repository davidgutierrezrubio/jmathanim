package com.jmathanim.Styling;

public abstract class JMGradient extends PaintStyle {
    protected CycleMethod cycleMethod;

    public static enum CycleMethod {NO_CYCLE, REPEAT, REFLECT}
}
