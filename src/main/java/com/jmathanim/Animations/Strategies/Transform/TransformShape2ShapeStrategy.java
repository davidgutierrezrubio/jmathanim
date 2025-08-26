package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.mathobjects.AbstractShape;

public class TransformShape2ShapeStrategy extends TransformStrategy<AbstractShape<?>> {
    private AbstractShape<?> originShape;
    private AbstractShape<?> destinyShape;
    private AbstractShape<?> intermediateShape;

    public TransformShape2ShapeStrategy(double runTime) {
        super(runTime);
    }

    @Override
    public void setIntermediate(AbstractShape<?> intermediate) {
        intermediateShape =  intermediate;
    }


    @Override
    public void setDestiny(AbstractShape<?> destiny) {
        this.destinyShape =  destiny;
    }

    @Override
    public void setOrigin(AbstractShape<?> origin) {
        this.originShape =  origin;
    }

    @Override
    public AbstractShape<?> getDestinyObject() {
        return destinyShape;
    }

    @Override
    public AbstractShape<?> getOriginObject() {
        return originShape;
    }

    @Override
    public AbstractShape<?> getIntermediateObject() {
        return intermediateShape;
    }
}
