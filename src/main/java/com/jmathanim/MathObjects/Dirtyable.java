package com.jmathanim.MathObjects;

import com.jmathanim.jmathanim.JMathAnimScene;

public interface Dirtyable {
    long getVersion();
    boolean isDirty();
    void setDirty();
    void markClean();
    /**
     * Updates object if necessary
     * @param scene Scene where the object belongs
     * @return True if changes were made to the object in the update. False if update was not needed
     */
    boolean update(JMathAnimScene scene);
}
