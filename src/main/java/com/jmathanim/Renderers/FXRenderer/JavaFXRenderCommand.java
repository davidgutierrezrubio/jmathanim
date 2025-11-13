package com.jmathanim.Renderers.FXRenderer;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.MathObjects.Drawable;

public class JavaFXRenderCommand {
    public enum COMMAND_TYPE {SHAPE, SHAPE_ABSOLUTE, IMAGE}
    public COMMAND_TYPE type;
    public Drawable object;
    public double shiftVector_x;
    public double shiftVector_y;
    public Camera camera;

}
