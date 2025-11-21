package com.jmathanim.MathObjects.Text.TextUpdaters;

import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.Updaters.Updater;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.ArrayList;

public abstract class TextUpdaterFactory {
    protected final ArrayList<Updater> updaters;
    protected String format;

    public TextUpdaterFactory(  String format) {
        this.format=format;
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

    public void registerUpdaters(MathObject<?> object) {
        for (Updater updater:updaters) {
            object.registerUpdater(updater);
        }
    }
    public void unregisterUpdaters(MathObject<?> object) {
        for (Updater updater:updaters) {
            object.unregisterUpdater(updater);
        }
    }

}
