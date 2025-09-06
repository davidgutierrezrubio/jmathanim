package com.jmathanim.Utils;

import org.junit.jupiter.api.Test;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AffineJTransformTest {

    @Test
    public void testEquals() {
        AffineJTransform tr1 = AffineJTransform.create2DRotationTransform(Vec.to(0, 0),PI/4);
        AffineJTransform tr2 = AffineJTransform.create2DRotationTransform(Vec.to(1, 0),PI/4);
        assertAffineJTransformEquals(tr1, tr2," different matrices");

    }


    public static void assertAffineJTransformEquals(AffineJTransform tr1, AffineJTransform tr2, String errorMessage) {
        assertEquals(tr1.matrix, tr2.matrix,errorMessage);
    }

}