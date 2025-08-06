package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;

public class TransformShape2ShapeStrategy extends TransformStrategy {
    private Shape originShape;
    private Shape destinyShape;
    private Shape intermediateShape;

    public TransformShape2ShapeStrategy(double runTime) {
        super(runTime);
    }

    @Override
    public void setIntermediate(MathObject intermediate) {
        intermediateShape = (Shape) intermediate;
    }

    @Override
    public void setDestiny(MathObject destiny) {
        this.destinyShape = (Shape) destiny;
    }

    @Override
    public void setOrigin(MathObject origin) {
        this.originShape = (Shape) origin;
    }

    @Override
    public Shape getDestinyObject() {
        return destinyShape;
    }

    @Override
    public Shape getOriginObject() {
        return originShape;
    }

    @Override
    public Shape getIntermediateObject() {
        return intermediateShape;
    }
}
