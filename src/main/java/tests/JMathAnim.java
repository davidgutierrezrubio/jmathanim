/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import com.jmathanim.jmathanim.JMathAnimScene;

/**
 *
 * @author David
 */
public class JMathAnim {

    

    /**
     * A launcher for the scene
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JMathAnimScene scene = new myScene();
        int resul = scene.execute();
        System.out.println("Hemos terminado con un "+resul);
        JMathAnimScene scene2 = new myScene();
        scene2.execute();
    }

}
