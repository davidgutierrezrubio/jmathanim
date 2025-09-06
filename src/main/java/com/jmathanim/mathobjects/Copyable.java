package com.jmathanim.mathobjects;

public interface Copyable<T extends Copyable<?>> extends Stateable
{
    T copy();
}
