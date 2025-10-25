/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.MathObjects.UpdateableObjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.Arrays;

/**
 * An Updateable subclass that updates a Camera to follow several Mathobjects.
 * The center of the camera is located at the centroid of the centers of the
 * objects.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CameraFollowingObjects implements Updateable {

    /**
     * Creates a new Updateable class to be registereded into a scene with the
     * registerupdateable method. The center of the camera is located at the
     * centroid of the centers of the objects.
     *
     * @param camera Camera to update
     * @param objs MathObjects
     * @return The created object
     */
    public static Updateable make(Camera camera, MathObject<?>... objs) {
        return new CameraFollowingObjects(camera, objs);
    }

    private final MathObject[] objects;
    private final Camera camera;
    private int updateLevel;

    private CameraFollowingObjects(Camera camera, MathObject<?>... objects) {
        this.objects = objects;
        this.camera = camera;
        updateLevel = 0;
    }

    @Override
    public int getUpdateLevel() {
        return updateLevel;
    }

    @Override
    public void setUpdateLevel(int level) {
        updateLevel=level;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        setUpdateLevel(Arrays.stream(objects).mapToInt(MathObject::getUpdateLevel).max().orElse(0) + 1);
    }

    @Override
    public void unregisterUpdateableHook(JMathAnimScene scene) {
    }

    @Override
    public void update(JMathAnimScene scene) {
        Vec centroid = Vec.to(0, 0);
        for (int i = 0; i < objects.length; i++) {
            Vec v = objects[i].getCenter();
            centroid.addInSite(v);
        }
        centroid.multInSite(1d / objects.length);
        Vec vcenter = camera.getMathView().getCenter();
        camera.shift(centroid.minus(vcenter));
        Rect r = camera.getMathView();
        for (Boxable obj : objects) {
            r = Rect.union(r, obj.getBoundingBox());
        }
        camera.adjustToRect(r);
    }

}
