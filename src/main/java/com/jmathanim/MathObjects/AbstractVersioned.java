package com.jmathanim.MathObjects;

import com.jmathanim.Utils.DependableUtils;
import com.jmathanim.jmathanim.Dependable;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractVersioned implements Dependable, Updatable {

    public ArrayList<Dependable> dependencies = new ArrayList<>();
    protected long version = 0;
    protected boolean dirty = true;
    protected long lastCleanedDepsVersionSum = -2;
    protected long newLastMaxDependencyVersion = -1;
    private boolean updating = false;

    @Override
    public void addDependency(Dependable dep) {
        dependencies.add(dep);
    }

    public void removeDependency(Dependable dep) {
        dependencies.remove(dep);
    }

    @Override
    public boolean needsUpdate() {
//        List<Dependable> deps = getDependencies();
//        long maxDep = 0;
//        for (int i = 0, n = deps.size(); i < n; i++) {
//            long v = deps.get(i).getVersion();
//            if (v > maxDep) maxDep = v;
//        }
        newLastMaxDependencyVersion = DependableUtils.maxVersion(getDependencies());
        if (dirty) return true;
        return newLastMaxDependencyVersion != lastCleanedDepsVersionSum;
    }


    @Override
    public boolean update(JMathAnimScene scene) {
        if (updating) return false;
        updating=true;
        boolean flag = false;
        if (dirty||needsUpdate()) {
            performMathObjectUpdateActions(scene);
            flag = true;
        }
        flag = flag | applyUpdaters(scene);
        if (flag) {
            performUpdateBoundingBox(scene);
            changeVersion();
            markClean();
        }
        updating=false;
        return flag;
    }

    public void markClean() {
        lastCleanedDepsVersionSum = newLastMaxDependencyVersion;
        dirty=false;
    }
    public void markDirty() {
        dirty=true;
    }


    @Override
    public void changeVersion() {
        version = ++JMathAnimScene.globalVersion;
        markDirty();
    }

    @Override
    public List<Dependable> getDependencies() {
        return dependencies;
    }

    public abstract void performMathObjectUpdateActions(JMathAnimScene scene);

    public abstract void performUpdateBoundingBox(JMathAnimScene scene);

    protected abstract boolean applyUpdaters(JMathAnimScene scene);


    @Override
    public long getVersion() {
        return version;
    }
}
