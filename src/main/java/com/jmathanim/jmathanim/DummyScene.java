package com.jmathanim.jmathanim;

import com.jmathanim.Renderers.DummyRenderer;
import com.jmathanim.Renderers.Renderer;

public abstract class DummyScene extends JMathAnimScene {
    @Override
    protected Renderer createRenderer() {
        return new DummyRenderer(this);
    }

}
