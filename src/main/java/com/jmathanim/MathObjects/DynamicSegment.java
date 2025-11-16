package com.jmathanim.MathObjects;

import com.jmathanim.MathObjects.Shapes.JMPath;
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Utils.Vec;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

public class DynamicSegment extends AbstractDynamicShape<DynamicSegment>{
    public Vec a;
    public Vec b;
    private final Shape shapeToDraw;


    public DynamicSegment(Coordinates<?> a,Coordinates<?> b) {
        super();
        this.a = a.getVec();
        this.b = b.getVec();
        addDependency(this.a);
        addDependency(this.b);
        shapeToDraw = new Shape();
        objectsToDraw.add(shapeToDraw);
    }

    @Override
    public DrawStyleProperties getMp() {
        return shapeToDraw.getMp();
    }

    @Override
    protected void rebuildShape() {
        JMPath path=shapeToDraw.getPath();
        path.clear();
        path.addPoint(a.copy());
        path.addPoint(b.copy().rotate(a,PI/20));
        path.addPoint(b.copy());
        path.get(0).setSegmentToThisPointVisible(false);
    }

    @Override
    public Shape toShape() {
        return null;
    }

    @Override
    public DynamicSegment copy() {
        return null;
    }
}
