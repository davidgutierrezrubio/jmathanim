package com.jmathanim.mathobjects;

import com.jmathanim.Utils.AffineJTransformTest;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Tippable.AbstractTippableObject;

import static com.jmathanim.mathobjects.TesterStyles.assertsMODrawPropertiesEquals;
import static com.jmathanim.mathobjects.Tippable.AbstractTippableObjectTest.assertTippableObjects;
import static org.junit.jupiter.api.Assertions.*;

public class TesterMathObjects {


    public static void assertVecEquals(Vec v1, Vec v2, String errorMessage) {
        assertAll("Vec equals",
                () -> assertEquals(v1.x, v2.x, 1e-4, "different x->" + errorMessage),
                () -> assertEquals(v1.y, v2.y, 1e-4, "different y->" + errorMessage)
        );
    }

    public static void assertJMpathPointEquals(JMPathPoint jmp1, JMPathPoint jmp2, String errorMessage) {
        assertAll("JMPathPoint equals",
                () -> assertVecEquals(jmp1.getV(), jmp2.getV(), " in path V->" + errorMessage),
                () -> assertVecEquals(jmp1.getvEnter(), jmp2.getvEnter(), "in path vEnter->" + errorMessage),
                () -> assertVecEquals(jmp1.getvExit(), jmp2.getvExit(), " in path vExit->" + errorMessage)
        );

    }

    public static void assertJMPathEquals(JMPath path1, JMPath path2, String errorMessage) {
        assertEquals(path1.size(), path2.size(), "Different path sizes->"+errorMessage);
        for (int i = 0; i < path1.size(); i++) {
            assertJMpathPointEquals(path1.get(i), path2.get(i), " in point " + i + " ->" + errorMessage);
        }
    }

    public static void assertShapeEquals(AbstractShape<?> sh1, AbstractShape<?> sh2, String errorMessage) {
        assertJMPathEquals(sh1.getPath(), sh2.getPath(), errorMessage);
        assertsMODrawPropertiesEquals(sh1.getMp(), sh2.getMp(), errorMessage);
    }

    public static void assertPointEquals(Point p1, Point p2, String errorMessage) {
        assertShapeEquals(p1.dotShape, p2.dotShape, "shapes different->"+errorMessage);
        assertVecEquals(p1.getVec(), p2.getVec(), "vectors different->"+errorMessage);
        assertsMODrawPropertiesEquals(p1.getMp(), p2.getMp(), "styles different->"+errorMessage);
    }

    public static void assertRigidBoxEquals(RigidBox rb1, RigidBox rb2, String errorMessage) {
        AffineJTransformTest.assertAffineJTransformEquals(rb1.modelMatrix,rb2.modelMatrix, "matrix different->"+errorMessage);
    }


    public static void assertMathObject(MathObject<?> ob1, MathObject<?> ob2,String errorMessage) {
        if (ob1.getClass()!=ob2.getClass()) {
            fail("Different objects "+ob1.getClass().getName()+" vs "+ob2.getClass().getName()+"->"+errorMessage);
        }
        
        //Point
        if (ob1 instanceof Point) {
            Point p1 = (Point) ob1;
            Point p2 = (Point) ob2;
            assertPointEquals(p1, p2, "points different->"+errorMessage);
            return;
        }
        if (ob1 instanceof Line) {
            Line l1 = (Line) ob1;
            Line l2 = (Line) ob2;
            assertVecEquals(l1.p1,l2.p1,"points different->"+errorMessage);
            assertVecEquals(l1.p2,l2.p2,"points different->"+errorMessage);
            assertsMODrawPropertiesEquals(l1.getMp(),l2.getMp(),"styles different->"+errorMessage);
        }
        
        //Shape
        if (ob1 instanceof Shape) {
            Shape sh1 = (Shape) ob1;
            Shape sh2 = (Shape) ob2;
            assertShapeEquals(sh1,sh2,"shapes different->"+errorMessage);
            return;
        }
        
        
        //Multishape
        if (ob1 instanceof AbstractMultiShapeObject) {
            AbstractMultiShapeObject <?,?> msh1= (AbstractMultiShapeObject<?,?>) ob1;
            AbstractMultiShapeObject <?,?> msh2= (AbstractMultiShapeObject<?,?>) ob2;

            assertEquals(msh1.size(),msh2.size(),"different sizes "+msh1.size()+" vs "+msh2.size()+" ->"+errorMessage);
            for (int i = 0; i < msh1.size(); i++) {
                assertShapeEquals(msh1.get(i),msh2.get(i),"shapes different at index "+i+" ->"+errorMessage);
            }
            return;
        }

        if (ob1 instanceof RigidBox) {
            RigidBox rb1 = (RigidBox) ob1;
            RigidBox rb2 = (RigidBox) ob2;
            assertRigidBoxEquals(rb1,rb2,"rigid boxes different->"+errorMessage);
            return;
        }
        
        

        if (ob1 instanceof AbstractTippableObject) {
            AbstractTippableObject<?> abstractTippableObject = (AbstractTippableObject<?>) ob1;
            AbstractTippableObject<?> abstractTippableObject2 = (AbstractTippableObject<?>) ob2;
            assertTippableObjects(abstractTippableObject,abstractTippableObject2,"tippable objects different->"+errorMessage);
            return;
        }


    }



}
