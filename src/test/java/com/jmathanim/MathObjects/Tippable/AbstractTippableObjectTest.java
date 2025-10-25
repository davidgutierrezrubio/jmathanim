package com.jmathanim.MathObjects.Tippable;

import org.junit.jupiter.api.Test;

public class AbstractTippableObjectTest {

    public static void assertTippableObjects(AbstractTippableObject<?> obj1, AbstractTippableObject<?> obj2, String errorMessage) {
//        assertAll(
//                () -> assertMathObject(obj1.tipObjectRigidBox.getReferenceMathObject(), obj2.tipObjectRigidBox.getReferenceMathObject(), "different tips->"+errorMessage),
//                ()-> assertsMODrawPropertiesEquals(obj1.getMp(),obj2.getMp(),"Styles different->"+errorMessage),
//                () -> assertSame(obj1.shape, obj2.shape,"not in the same shape->"+errorMessage),
//                () -> assertEquals(obj1.distanceToShape, obj2.distanceToShape, "different distanceToShape->"+errorMessage),
//                () -> assertVecEquals(obj1.getAbsoluteAnchor(), obj2.getAbsoluteAnchor(), "different absolute anchor->"+errorMessage),
////        assertVecEquals(obj1.getAbsoluteAnchorVec(), obj2.getAbsoluteAnchorVec(), "different absolute anchor");
//                () -> assertEquals(obj1.getRotationType(), obj2.getRotationType(), "different rotation type->"+errorMessage),
//                () -> assertVecEquals(obj1.getMarkLabelLocation(), obj2.getMarkLabelLocation(), "different rotation type->"+errorMessage),
//                () -> assertRigidBoxEquals(obj1.tipObjectRigidBox, obj2.tipObjectRigidBox, "different rigid box->"+errorMessage)
//
//        );
    }

    @Test
    public void testExample() {
//        Shape s = Shape.circle();
//        LabelTip a = LabelTip.makeLabelTip(s, .4, "A", true);
//        LabelTip b = LabelTip.makeLabelTip(s, .4, "A", true);
//        b.drawColor("red");
////        b.copyStateFrom(a);
//        assertTippableObjects(a, b, "tippable objects are different");
    }

}