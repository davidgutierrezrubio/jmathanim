/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmathanim;

/**
 *
 * @author David
 */
public class MathProc {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JMathAnimScene scene=new PointSimple();
        scene.setupSketch();
        scene.execute();
    }

}
