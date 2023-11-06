/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmathanim.Utils;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class SeqUtils {

    /**
     * Generates a integer sequence. For example seq(7,10,1) will generate 7,8,9
     *
     * @param start Starting number
     * @param end Ending number (this number will not be in the seq)
     * @param step Step
     * @return An int array with numbers
     */
    public static int[] seq(int start, int end, int step) {
        if (step == 0) {
            throw new IllegalArgumentException("Step cannot be zero");
        }

        int length = (int) Math.ceil((double) (end - start) / step);
        int[] result = new int[length];

        for (int i = 0, value = start; i < length; i++, value += step) {
            result[i] = value;
        }

        return result;
    }

    /**
     * Generates a sequence from start to start+numSteps-1. For example
     * steps(2,3) generates 2,3,4
     *
     * @param start Starting number
     * @param numSteps Number of steps
     * @return An int array with the numbers
     */
    public static int[] steps(int start, int numSteps) {
        int[] resul = new int[numSteps];
        int n = start;
        for (int i = 0; i < numSteps; i++) {
            resul[i] = n;
            n++;
        }
        return resul;
    }

}
