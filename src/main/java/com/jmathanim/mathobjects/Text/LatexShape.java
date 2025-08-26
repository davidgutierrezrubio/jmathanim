package com.jmathanim.mathobjects.Text;

import com.jmathanim.mathobjects.AbstractShape;
import com.jmathanim.mathobjects.Shape;

public class LatexShape extends AbstractShape<LatexShape> {
    public LatexShape() {
    }

    public Shape toShape() {
        Shape resul = new Shape();
        resul.getPath().getJmPathPoints().addAll(getPath().getJmPathPoints());
        resul.getMp().copyFrom(getMp());
        return resul;
    }
}
