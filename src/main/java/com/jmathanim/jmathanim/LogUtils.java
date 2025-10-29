package com.jmathanim.jmathanim;

// Constantes con códigos ANSI
public class LogUtils {
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String BOLD = "\u001B[1m";

    public static void printProgressBar(double t) {
        int width = 50;
        float percentage = ((float) t) * 100;
        String bar = String.format(
                "\r[%-" + width + "s] %.1f%%",
                "=".repeat((int) (width * (percentage / 100))),
                percentage
        );
        System.out.print(bar);  // Usa print (no println) para evitar saltos de línea
        if (t == 1) System.out.println();  // Salto final
    }

    public static String number(double number, int numDecimals) {
        return String.format(String.format("%s%s%%.%df%s", BOLD, GREEN, numDecimals, RESET), number);
    }

    public static String method(String method) {
        return String.format("%s%s%s%s", BOLD, CYAN, method,RESET);
    }

    public static String fileName(String fileName) {
        return String.format("%s%s%s%s", BOLD, YELLOW, fileName,RESET);
    }


}