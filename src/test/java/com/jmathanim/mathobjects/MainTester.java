package com.jmathanim.mathobjects;

import org.junit.jupiter.api.Test;

class MainTester {

    @Test
    void simpleCopyTest() {
        Shape s1, s2;
        s1 = Shape.regularPolygon(5);
        s2 = s1.copy();
        testerMathObjects.assertShapeEquals(s1, s2,  "Regular polygon 5");

        s1 = Shape.arc(Math.PI / 4);
        s2 = s1.copy();
        testerMathObjects.assertShapeEquals(s1, s1,  "Arc PI/4");

        s1 = Shape.annulus(.5, 1);
        s2 = s1.copy();
        testerMathObjects.assertShapeEquals(s1, s1,  "Annulus");
    }
    @Test
    void copyFromTest() {
        //Shapes
        Shape s1, s2;
        s1 = Shape.regularPolygon(5);
        s2=new Shape();
        s2.copyStateFrom(s1);
        testerMathObjects.assertMathObject(s1, s2,  "Regular polygon 5");

        //Arrows



    }
}