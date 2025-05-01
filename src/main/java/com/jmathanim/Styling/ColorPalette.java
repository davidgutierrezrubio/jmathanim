package com.jmathanim.Styling;

import java.util.HashMap;

public class ColorPalette {
    private final HashMap<String, Double[]> colorMap;
    public enum ColorBlindnessType {NONE,PROTANOPIA,DEUTERANOPIA,TRITANOPIA};
    private ColorBlindnessType colorBlindness;
    private final double[][] PROTANOPIA_MATRIX = {
            {0.56667d, 0.43333d, 0.0d},
            {0.55833d, 0.44167d, 0.0d},
            {0.0d, 0.24167d, 0.75833d}
    };

    private final double[][] DEUTERANOPIA_MATRIX = {
            {0.625d, 0.375d, 0.0d},
            {0.70d, 0.30d, 0.0d},
            {0.0d, 0.30d, 0.70d}
    };

    private final double[][] TRITANOPIA_MATRIX = {
            {0.95d, 0.05d, 0.0d},
            {0.0d, 0.43333d, 0.56667d},
            {0.0d, 0.475d, 0.525d}
    };

    public ColorPalette() {
        colorMap = new HashMap<>();
        colorBlindness = ColorBlindnessType.NONE;
        generateBasicColors();
    }

    public ColorBlindnessType getColorBlindness() {
        return colorBlindness;
    }

    public void setColorBlindness(ColorBlindnessType colorBlindness) {
        this.colorBlindness = colorBlindness;
    }

    private void generateBasicColors() {
        addColor("white", new JMColor(1, 1, 1, 1));
        addColor("black", new JMColor(0, 0, 0, 1));
        addColor("red", new JMColor(1, 0, 0, 1));
        addColor("green", new JMColor(0, 1, 0, 1));
        addColor("blue", new JMColor(0, 0, 1, 1));
        addColor("transparent", new JMColor(0, 0, 0, 0));
    }

    public Double[] applyColorBlindnessFilter(Double[] rgba, double[][] matrix) {
        double r = rgba[0];
        double g = rgba[1];
        double b = rgba[2];
        double a = rgba[3];

        double r2 = matrix[0][0] * r + matrix[0][1] * g + matrix[0][2] * b;
        double g2 = matrix[1][0] * r + matrix[1][1] * g + matrix[1][2] * b;
        double b2 = matrix[2][0] * r + matrix[2][1] * g + matrix[2][2] * b;

        return new Double[]{clamp(r2), clamp(g2), clamp(b2), a};
    }

    private double clamp(double val) {
        return Math.max(0d, Math.min(1d, val));
    }


    public String addColor(String name, JMColor color) {
        if (name.startsWith("#")) throw new IllegalArgumentException("Color name cannot start with # symbol");
        String nameUppercase = name.toUpperCase().trim();
        colorMap.put(nameUppercase, new Double[]{color.r,color.g,color.b,color.getAlpha()});
        return nameUppercase;
    }

    public JMColor get(String name) {
        String nameUppercase = name.toUpperCase().trim();
        Double[] params= colorMap.getOrDefault(nameUppercase, null);
        if (params==null) return null;
        return buildColor(params);
    }

    private JMColor buildColor(Double[] rgba2) {
        Double[] rgba;
        switch(colorBlindness) {
            case DEUTERANOPIA:
                rgba= applyColorBlindnessFilter(rgba2, DEUTERANOPIA_MATRIX);
                break;
            case PROTANOPIA:
                rgba= applyColorBlindnessFilter(rgba2, PROTANOPIA_MATRIX);
                break;
            case TRITANOPIA:
                rgba= applyColorBlindnessFilter(rgba2, TRITANOPIA_MATRIX);
                break;
            default:
                rgba=rgba2;
        }

        return new JMColor(rgba[0], rgba[1], rgba[2], rgba[3]);
    }


}
