package com.jmathanim.MathObjects;

import com.jmathanim.jmathanim.Dependable;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractVersioned implements Dependable, Updatable {

    public ArrayList<Dependable> dependencies = new ArrayList<>();
    protected long version = 0;
    protected boolean dirty = true;
    protected long lastCleanedDepsVersionSum = -1;
    protected long newLastMaxDependencyVersion = -2;

    @Override
    public void addDependency(Dependable dep) {
        dependencies.add(dep);
    }

    public void removeDependency(Dependable dep) {
        dependencies.remove(dep);
    }

    @Override
    public boolean needsUpdate() {
        newLastMaxDependencyVersion = dependencies.stream()
                .mapToLong(Dependable::getVersion)
                .max()
                .orElse(0);
        return newLastMaxDependencyVersion != lastCleanedDepsVersionSum;
    }


    @Override
    public boolean update(JMathAnimScene scene) {
        boolean flag = false;
        if (needsUpdate()) {
            performMathObjectUpdateActions(scene);
            flag = true;
        }
        flag = flag | applyUpdaters(scene);
        if (flag) {
            performUpdateBoundingBox(scene);
            changeVersion();
        }
        markClean();
        return true;
    }

    public void markClean() {
        lastCleanedDepsVersionSum = newLastMaxDependencyVersion;
    }

    @Override
    public void changeVersion() {
        version = ++JMathAnimScene.globalVersion;
    }

    @Override
    public List<Dependable> getDependencies() {
        return dependencies;
    }

    protected abstract void performMathObjectUpdateActions(JMathAnimScene scene);

    protected abstract void performUpdateBoundingBox(JMathAnimScene scene);

    protected abstract boolean applyUpdaters(JMathAnimScene scene);


    @Override
    public long getVersion() {
        return version;
    }
}
