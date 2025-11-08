package com.jmathanim.MathObjects;

import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.HashMap;

public abstract class AbstractVersioned implements Dirtyable {
    public static long globalVersion = 0;
    public HashMap<Dirtyable, Long> dependencies = new HashMap<>();
    protected long version = 0;
    protected boolean dirty = true;
    protected long lastCleanedDepsVersionSum = -1;

    public void addDependency(Dirtyable dep) {
        dependencies.put(dep, dep.getVersion());
    }

    public void removeDependency(Dirtyable dep) {
        dependencies.remove(dep);
    }

    @Override
    public boolean isDirty() {
        if (dirty) return true;

        // Comprobar si alguna dependencia ha cambiado
        long depSum = 0;
        for (Dirtyable d : dependencies.keySet()) {
            depSum += d.getVersion();
            if (d.isDirty() || d.getVersion() > dependencies.get(d)) {
                dirty = true;
            }
        }
        if (depSum != lastCleanedDepsVersionSum) dirty = true;

        return dirty;
    }

    @Override
    public void setDirty() {
        this.dirty = true;
    }

    @Override
    public boolean update(JMathAnimScene scene) {
        boolean flag = isDirty();
        if (flag | updateDependents(scene)) {
            performMathObjectUpdateActions(scene);
            flag = true;
        }
        if (flag | applyUpdaters(scene)) {
            performUpdateBoundingBox(scene);
            markClean();
            flag = true;
        }
        return flag;
    }

    protected abstract void performMathObjectUpdateActions(JMathAnimScene scene);

    protected abstract void performUpdateBoundingBox(JMathAnimScene scene);

    protected abstract boolean applyUpdaters(JMathAnimScene scene);

    protected boolean updateDependents(JMathAnimScene scene) {
        boolean updateResult = false;
        for (Dirtyable d : dependencies.keySet()) {
            if (d.isDirty() || d.getVersion() > dependencies.get(d)) {
                updateResult = updateResult | d.update(scene);
                dependencies.put(d, d.getVersion());
            }
        }
        return updateResult;
    }


    public void markClean() {
        dirty = false;
        version = ++globalVersion;
        lastCleanedDepsVersionSum = dependencies.keySet().stream().mapToLong(Dirtyable::getVersion).sum();
    }

    @Override
    public long getVersion() {
        return version;
    }
}
