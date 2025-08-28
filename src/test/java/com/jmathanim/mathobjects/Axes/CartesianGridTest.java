package com.jmathanim.mathobjects.Axes;

import com.jmathanim.mathobjects.TesterMathObjects;
import com.jmathanim.mathobjects.TesterStyles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartesianGridTest {

    @Test
    void copyStateFrom() {
//        JMathAnimConfig config=JMathAnimConfig.getConfig();
        CartesianGrid grid1 = CartesianGrid.make(0, 0, 1, 1, 2, 2);
        grid1.getPrimaryGridStyle().thickness(10).drawColor("blue");
        grid1.getSecondaryGridStyle().thickness(4).drawColor("red");
        CartesianGrid grid2 = grid1.copy();
        assertEqualObjects(grid1,grid2," cartesian grid different");
    }

    public static void assertEqualObjects(CartesianGrid grid1,CartesianGrid grid2, String errorMessage) {
        TesterMathObjects.assertVecEquals(grid1.center, grid2.center, errorMessage);
        TesterMathObjects.assertVecEquals(grid1.steps, grid2.steps, errorMessage);
        TesterStyles.assertsMODrawPropertiesEquals(grid1.getPrimaryGridStyle(),grid2.getPrimaryGridStyle(),"primary styles different->"+errorMessage);
        TesterStyles.assertsMODrawPropertiesEquals(grid1.getSecondaryGridStyle(),grid2.getSecondaryGridStyle(),"secondary styles different->"+errorMessage);
        assertEquals(grid1.horizontalPrimaryLines.size(),grid2.horizontalPrimaryLines.size(),"horizontal primary lines different->"+errorMessage);
        assertEquals(grid1.horizontalSecondaryLines.size(),grid2.horizontalSecondaryLines.size(),"horizontal secondary lines different->"+errorMessage);
        assertEquals(grid1.verticalPrimaryLines.size(),grid2.verticalPrimaryLines.size(),"vertical primary lines different->"+errorMessage);
        assertEquals(grid1.verticalSecondaryLines.size(),grid2.verticalSecondaryLines.size(),"vertical secondary lines different->"+errorMessage);

        for (int i = 0; i < grid1.horizontalPrimaryLines.size(); i++) {
            TesterMathObjects.assertMathObject(
                    grid1.horizontalPrimaryLines.get(i),
                    grid2.horizontalPrimaryLines.get(i),
                    "horizontal primary line different at index "+i+"->"+errorMessage);
        }
        for (int i = 0; i < grid1.verticalPrimaryLines.size(); i++) {
            TesterMathObjects.assertMathObject(
                    grid1.verticalPrimaryLines.get(i),
                    grid2.verticalPrimaryLines.get(i),"vertical primary line different at index "+i+"->"+errorMessage);
        }

        for (int i = 0; i < grid1.horizontalSecondaryLines.size(); i++) {
            TesterMathObjects.assertMathObject(
                    grid1.horizontalSecondaryLines.get(i),
                    grid2.horizontalSecondaryLines.get(i),
                    "horizontal secondary line different at index "+i+"->"+errorMessage);
        }
        for (int i = 0; i < grid1.verticalSecondaryLines.size(); i++) {
            TesterMathObjects.assertMathObject(
                    grid1.verticalSecondaryLines.get(i),
                    grid2.verticalSecondaryLines.get(i),"vertical secondary line different at index "+i+"->"+errorMessage);
        }




    }
}