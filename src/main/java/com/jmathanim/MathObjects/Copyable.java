package com.jmathanim.MathObjects;

public interface Copyable<T extends Copyable<?>> extends Stateable
{
    T copy();
}
