package com.jmathanim.MathObjects.Text.TextUpdaters;

import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.Text.AbstractLatexMathObject;
import com.jmathanim.MathObjects.Updaters.Updater;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.ArrayList;

public abstract class TextUpdaterFactory {
    protected final ArrayList<Updater> updaters;
    protected String format;
    protected final AbstractLatexMathObject<?>  t;

    public TextUpdaterFactory(AbstractLatexMathObject<?> t, String format) {
        this.format=format;
        this.t=t;
        this.updaters = new ArrayList<>();
    }
    public ArrayList<Updater> getUpdaters() {
        return updaters;
    }

    public boolean addUpdater(Updater updater) {
        return updaters.add(updater);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void registerUpdaters() {
        for (Updater updater:updaters) {
            t.registerUpdater(updater);
        }
    }
    public void unregisterUpdaters() {
        for (Updater updater:updaters) {
            t.unregisterUpdater(updater);
        }
    }

}
