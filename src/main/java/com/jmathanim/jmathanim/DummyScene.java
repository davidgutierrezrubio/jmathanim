package com.jmathanim.jmathanim;

import com.jmathanim.Renderers.DummyRenderer;
import com.jmathanim.Renderers.Renderer;

public abstract class DummyScene extends JMathAnimScene {

    public DummyScene() {
       fps=25;
    }

    @Override
    protected Renderer createRenderer() {
        //Creates a new dummy renderer
        return new DummyRenderer(this);
    }

}
