package com.jmathanim.Renderers.FXRenderer;

import com.jmathanim.MathObjects.Drawable;

import java.util.ArrayList;
import java.util.HashMap;

public class DataFrame {
    public final ArrayList<JavaFXRenderCommand> renderCommands;
    public final HashMap<Drawable, JavaFXRenderCommand> drawableToRenderCommand;
    public int frameCount;

    public DataFrame(int frameCount) {
        renderCommands = new ArrayList<>();
        this.drawableToRenderCommand = new HashMap<>();
        this.frameCount = frameCount;
    }

    public void add(JavaFXRenderCommand rc) {
        renderCommands.add(rc);
        drawableToRenderCommand.put(rc.object, rc);
    }
}
