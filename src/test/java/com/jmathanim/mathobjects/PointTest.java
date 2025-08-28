package com.jmathanim.mathobjects;

import org.junit.jupiter.api.Test;

class PointTest {

    @Test
    void copyStateFrom() {
        Point A=Point.at(1,2);
        A.update(null);
        Point B=Point.at(1.0000000001,2.03000000001,2);
        TesterMathObjects.assertMathObject(A,B.dotShape,"Point");
    }
}