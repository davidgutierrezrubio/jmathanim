package com.jmathanim.MathObjects.UpdateableObjects;

import com.jmathanim.Cameras.Camera3D;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.SceneJOGL;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

public class Camera3DAlwaysRotating implements  Updateable{
    double angle;
    Camera3D camera=null;
    double spr;

    public Camera3DAlwaysRotating(double secondsForEachRevolution) {
        this.spr=secondsForEachRevolution;
    }

    @Override
    public int getUpdateLevel() {
        return 0;
    }

    @Override
    public void setUpdateLevel(int level) {

    }

    @Override
    public void update(JMathAnimScene scene) {
        if (camera==null) return;
        camera.eye.rotate(camera.look,angle);
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        if (scene instanceof SceneJOGL) {
            SceneJOGL sceneJOGL = (SceneJOGL) scene;
            camera=sceneJOGL.getRenderer().getCamera();
            angle=2*PI/(sceneJOGL.getConfig().fps*spr);
        }

    }

    @Override
    public void unregisterUpdateableHook(JMathAnimScene scene) {

    }
}
