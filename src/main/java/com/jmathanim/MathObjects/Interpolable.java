package com.jmathanim.MathObjects;

public interface Interpolable<T extends Coordinates<?>> {
    T interpolate(Coordinates<?> coords, double alpha);
}
