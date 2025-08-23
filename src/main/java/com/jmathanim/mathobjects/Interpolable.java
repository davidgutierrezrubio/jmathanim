package com.jmathanim.mathobjects;

import com.jmathanim.mathobjects.updaters.Coordinates;

public interface Interpolable<T extends Coordinates> {
    T interpolate(Coordinates coords2, double alpha);
}
