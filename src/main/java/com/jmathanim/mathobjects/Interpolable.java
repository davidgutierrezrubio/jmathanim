package com.jmathanim.mathobjects;

public interface Interpolable<T extends Coordinates<?>> {
    T interpolate(Coordinates<?> coords, double alpha);
}
