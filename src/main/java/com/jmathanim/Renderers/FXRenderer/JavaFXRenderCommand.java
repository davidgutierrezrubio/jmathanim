package com.jmathanim.Renderers.FXRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.MathObjects.Drawable;

public class JavaFXRenderCommand {
    public enum COMMAND_TYPE {SHAPE, SHAPE_ABSOLUTE, IMAGE,REMOVE}
    public COMMAND_TYPE type;
    public Drawable object;
    public double shiftVector_x;
    public double shiftVector_y;
    public Camera camera;

    //These variables helps keep if anything changed from previous render
    public double previous_shiftVector_x=0;
    public double previous_shiftVector_y=0;
    public long cameraVersion=-1;
    public long pathVersion=-1;
    public long mpVersion=-1;
    public long imageVersion=-1;
    public boolean showDebugText=false;
    public String debugText="";

}
