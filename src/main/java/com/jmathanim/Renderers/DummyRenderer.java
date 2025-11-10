package com.jmathanim.Renderers;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.MathObjects.AbstractJMImage;
import com.jmathanim.MathObjects.AbstractShape;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.Renderers.MovieEncoders.SoundItem;
import com.jmathanim.Styling.RendererEffects;
import com.jmathanim.Utils.EmptyRect;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public class DummyRenderer extends Renderer{
    private final Camera camera;
    private final Camera fixedCamera;
    private final BufferedImage bufferedImage = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);


    public DummyRenderer(JMathAnimScene parentScene) {
        super(parentScene);
        this.camera =new Camera(parentScene,1280,720);
        this.fixedCamera =new Camera(parentScene,1280,720);
    }

    @Override
    public RendererEffects buildRendererEffects() {
        return new RendererEffects();
    }

    @Override
    public void initialize() {
        JMathAnimScene.logger.debug("Initiatilizing dummy renderer");
    }

    @Override
    public <T extends Camera> T getCamera() {
        return (T) camera;
    }

    @Override
    public <T extends Camera> T getFixedCamera() {
        return (T) fixedCamera;
    }

    @Override
    public void saveFrame(int frameCount) {
        JMathAnimScene.logger.debug("Saving frame "+frameCount+" in dummy renderer");
    }

    @Override
    public void finish(int frameCount) {
        JMathAnimScene.logger.debug("Finishing "+frameCount+" in dummy renderer");
    }

    @Override
    protected BufferedImage getRenderedImage(int frameCount) {
        return bufferedImage;
    }

    @Override
    protected void drawPath(AbstractShape<?> mobj) {

    }

    @Override
    public void drawPath(AbstractShape<?> mobj, Vec shiftVector, Camera camera) {

    }

    @Override
    public void drawAbsoluteCopy(AbstractShape<?> sh, Vec anchor) {

    }

    @Override
    public Rect createImage(InputStream stream) {
        return new EmptyRect();
    }

    @Override
    public void drawImage(AbstractJMImage<?> obj, Camera cam) {

    }

    @Override
    public void debugText(String text, Vec loc) {

    }

    @Override
    public double MathWidthToThickness(double w) {
        return 0;
    }

    @Override
    public double ThicknessToMathWidth(double th) {
        return 0;
    }

    @Override
    public double ThicknessToMathWidth(MathObject<?> obj) {
        return 0;
    }

    @Override
    public void addSound(SoundItem soundItem) {

    }
}
