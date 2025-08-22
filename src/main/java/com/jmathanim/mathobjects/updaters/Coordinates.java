package com.jmathanim.mathobjects.updaters;

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.Vec;

public interface Coordinates<T extends Coordinates<T>> extends Boxable {
    Vec getVec();

    T interpolate(Coordinates coords2, double alpha);
    T applyAffineTransform(AffineJTransform tr);
}
