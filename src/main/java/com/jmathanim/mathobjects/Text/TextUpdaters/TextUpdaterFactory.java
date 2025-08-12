package com.jmathanim.mathobjects.Text.TextUpdaters;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.updaters.Updater;

public abstract class TextUpdaterFactory {
    protected JMathAnimScene scene;
    protected Updater updater;
    protected String format;

    public TextUpdaterFactory(JMathAnimScene scene, String format) {
        this.scene = scene;
        this.format=format;
    }
    public Updater getUpdater() {
        return updater;
    }
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
