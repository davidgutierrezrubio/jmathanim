package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.MathObjects.AbstractShape;

public class TransformShape2ShapeStrategy<T extends AbstractShape<?>> extends TransformStrategy<T> {
    private T originShape;
    private T destinyShape;
    private T intermediateShape;

    public TransformShape2ShapeStrategy(double runTime) {
        super(runTime);
    }

    @Override
    public void setIntermediate(T intermediate) {
        intermediateShape =  intermediate;
    }


    @Override
    public void setDestiny(T destiny) {
        this.destinyShape =  destiny;
    }

    @Override
    public void setOrigin(T origin) {
        this.originShape =  origin;
    }

    @Override
    public T getDestinyObject() {
        return destinyShape;
    }

    @Override
    public T getOriginObject() {
        return originShape;
    }

    @Override
    public T getIntermediateObject() {
        return intermediateShape;
    }
}
