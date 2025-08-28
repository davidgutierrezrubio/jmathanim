package com.jmathanim.mathobjects;

import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Styling.JMColor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class TesterStyles {

    public static void assertsJMColorEquals(JMColor color1, JMColor color2, String errorMessage) {
        assertEquals(color1.getBlue(), color2.getBlue(), 1e-4, "different b color->" + errorMessage);
        assertEquals(color1.getRed(), color2.getRed(), 1e-4, "different r color->" + errorMessage);
        assertEquals(color1.getGreen(), color2.getGreen(), 1e-4, "different g color->" + errorMessage);
        assertEquals(color1.getAlpha(), color2.getAlpha(), 1e-4, "different a color->" + errorMessage);
    }

    public static void assertsMODrawPropertiesEquals(DrawStyleProperties mp1, DrawStyleProperties mp2, String errorMessage) {

        //Draw color
        if (mp1.getDrawColor() instanceof JMColor) {
            JMColor drawColor1 = (JMColor) mp1.getDrawColor();
            if (mp2.getDrawColor() instanceof JMColor) {
                JMColor drawColor2 = (JMColor) mp2.getDrawColor();
                assertsJMColorEquals(drawColor1, drawColor2, "drawColor->"+errorMessage);
            } else {
                fail("Different classes. " + mp1.getClass().getName() + " vs " + mp2.getClass().getName()+"->"+errorMessage);
            }
        }


        //Fill color
        if (mp1.getFillColor() instanceof JMColor) {
            JMColor fillColor1 = (JMColor) mp1.getFillColor();
            if (mp2.getDrawColor() instanceof JMColor) {
                JMColor fillColor2 = (JMColor) mp2.getFillColor();
                assertsJMColorEquals(fillColor1, fillColor2, "fillColor->"+errorMessage);
            } else {
                fail("Different classes. " + mp1.getClass().getName() + " vs " + mp2.getClass().getName()+"->"+errorMessage);
            }
        }

        assertEquals(mp1.getDashStyle(), mp2.getDashStyle(), "different dashStyle->"+errorMessage);
        assertEquals(mp1.getDotStyle(), mp2.getDotStyle(), " different dotStyle->"+errorMessage);
        assertEquals(mp1.getDashStyle(), mp2.getDashStyle(), " different dashStyle->"+errorMessage);
        assertEquals(mp1.getLayer(), mp2.getLayer(), " different layer->"+errorMessage);
        assertEquals(mp1.getLineCap(), mp2.getLineCap(), " different line cap->"+errorMessage);
        assertEquals(mp1.getLineJoin(), mp2.getLineJoin(), " different line join->"+errorMessage);
        assertEquals(mp1.getLineJoin(), mp2.getLineJoin(), " different line join->"+errorMessage);
        assertEquals(mp1.isAbsoluteThickness(), mp2.isAbsoluteThickness(), " different absolute thickness flag->"+errorMessage);
        assertEquals(mp1.isVisible(), mp2.isVisible(), " different absolute visible flag->"+errorMessage);
        assertEquals(mp1.isFaceToCamera(), mp2.isFaceToCamera(), " different isFaceToCamera flag->"+errorMessage);

    }
}
