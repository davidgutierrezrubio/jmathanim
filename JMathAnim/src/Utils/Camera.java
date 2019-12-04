/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

/**
 * This class represents a camera, covering a region of the plane, and
 * fitting into the screen
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Camera {

    public Camera() {
    }


    public Vec mathToPixel(float x,float y, float z)
    {
    return new Vec(x,y,z);//Right now, camera is useless
    }
    
}
