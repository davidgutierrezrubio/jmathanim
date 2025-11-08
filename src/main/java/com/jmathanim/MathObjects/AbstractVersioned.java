package com.jmathanim.MathObjects;

import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.HashMap;

public abstract class AbstractVersioned implements Dirtyable {
    public static long globalVersion = 0;
    protected long version = 0;
    protected boolean dirty = true;
    public HashMap<Dirtyable, Long> dependencies = new HashMap<>();
    protected long lastCleanedDepsVersionSum = -1; // ayuda para evitar recálculos innecesarios

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
            long version1 = d.getVersion();
            Long version2 = dependencies.get(d);
            if (d.isDirty() || version1 > version2) {
                dirty = true;
            }
        }
        if (depSum != lastCleanedDepsVersionSum) dirty = true;

        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public boolean update(JMathAnimScene scene) {
        boolean flag = false;
        if (updateDependents(scene)) {
            performMathObjectUpdateActions(scene);
            flag = true;
        }
        flag = flag || applyUpdaters(scene);
        if (flag) markClean();
        return flag;
    }

    protected abstract void performMathObjectUpdateActions(JMathAnimScene scene);

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


    protected void markClean() {
        dirty = false;
        version = ++globalVersion;
        lastCleanedDepsVersionSum = dependencies.keySet().stream().mapToLong(Dirtyable::getVersion).sum();
    }

    @Override
    public long getVersion() {
        return version;
    }
}
