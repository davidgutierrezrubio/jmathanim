package com.jmathanim.MathObjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.EmptyRect;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;

public class NullMathObject extends MathObject<NullMathObject>{
    MODrawProperties mp;

    public NullMathObject() {
        super();
        mp = MODrawProperties.makeNullValues();
        setMp(mp);

    }

    @Override
    public NullMathObject copy() {
        return new NullMathObject();
    }

    @Override
    protected Rect computeBoundingBox() {
        return new EmptyRect();
    }

    @Override
    public void performMathObjectUpdateActions(JMathAnimScene scene) {

    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r, Camera camera) {

    }

    @Override
    public DrawStyleProperties getMp() {
        return null;
    }
}
