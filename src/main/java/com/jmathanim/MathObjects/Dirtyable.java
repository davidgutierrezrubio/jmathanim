package com.jmathanim.MathObjects;

import com.jmathanim.MathObjects.UpdateableObjects.Updateable;

public interface Dirtyable extends Updateable {
    long getVersion();
    boolean isDirty();
    void setDirty(boolean dirty);
}
