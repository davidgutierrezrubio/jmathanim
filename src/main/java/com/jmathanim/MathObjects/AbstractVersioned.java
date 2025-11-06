package com.jmathanim.MathObjects;

import com.jmathanim.MathObjects.UpdateableObjects.Updateable;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.*;

public abstract class AbstractVersioned implements Dirtyable  {
    protected long version = 0;
    protected boolean dirty = true;


    protected List<Dirtyable> dependencies = new ArrayList<>();

    @Override
    public void setDirty(boolean dirty) {
        this.dirty=dirty;
    }

    public static long globalVersion = 0;

    protected long lastCleanedDepsVersionSum = -1; // ayuda para evitar recálculos innecesarios

    public void addDependency(Dirtyable dep) {
        dependencies.add(dep);
    }

    @Override
    public boolean isDirty() {
        if (dirty) return true;

        // Comprobar si alguna dependencia ha cambiado
        long depSum = 0;
        for (Dirtyable d : dependencies) {
            depSum += d.getVersion();
            if (d.isDirty() || d.getVersion() > version) {
                dirty = true;
            }
        }
        if (depSum != lastCleanedDepsVersionSum) dirty = true;

        return dirty;
    }

    @Override
    public void update(JMathAnimScene scene) {
        for (Dirtyable d : dependencies) {
            if (d.isDirty() || d.getVersion() > version) {
               d.update(scene);
            }
        }
    }




    protected void markClean() {
        dirty = false;
        version = ++globalVersion;
        lastCleanedDepsVersionSum = dependencies.stream().mapToLong(Dirtyable::getVersion).sum();
    }

    @Override
    public long getVersion() {
        return version;
    }
}
