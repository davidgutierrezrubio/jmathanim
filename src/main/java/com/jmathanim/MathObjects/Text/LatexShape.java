package com.jmathanim.MathObjects.Text;

import com.jmathanim.MathObjects.AbstractShape;
import com.jmathanim.MathObjects.Shape;

public class LatexShape extends AbstractShape<LatexShape> {
    public LatexShape() {
    }

    @Override
    public LatexShape copy() {
        LatexShape copy = new LatexShape();
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public Shape toShape() {
        Shape resul = new Shape();
        resul.getPath().getJmPathPoints().addAll(getPath().getJmPathPoints());
        resul.getMp().copyFrom(getMp());
        return resul;
    }
}
