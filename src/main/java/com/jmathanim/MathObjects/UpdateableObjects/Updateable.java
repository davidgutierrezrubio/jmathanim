/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.MathObjects.UpdateableObjects;

import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * Anything that can be updated prior to be drawn on screen. Note that is not
 * necessary for an object to be added to the scene, only register it with the
 * JMathAnimScene.registerUpdateable method
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public interface Updateable {

    /**
     * Gets the update level. The update level will determine the order when
     * updating all updateables. 0 level corresponds to first objects to be
     * updated. If an updateable A depends on another updatable B to be
     * computed, the update level of A should be greater than of B.
     *
     * @return The update level.
     */
    int getUpdateLevel();

    /**
     * Sets the update level for this object
     * @param level Update level. An integer from 0 to MAX_VALUE.
     */
    void setUpdateLevel(int level);

    /**
     * Performs the update. This method is called prior to draw all objects of
     * the scene.
     *
     * @param scene Scene where the object is being updated.
     */
    void update(JMathAnimScene scene);

    /**
     * This method is called when this object is registered in a scene. The
     * update level should be set here, using the setUpdate(value)
     * method. Otherwise it is set to 0.
     *
     * @param scene Scene where object is registered to update
     */
    void registerUpdateableHook(JMathAnimScene scene);

    /**
     * This method is called when this object is unregistered from a scene
     *
     * @param scene Scene where object is unregistered
     */
    void unregisterUpdateableHook(JMathAnimScene scene);
}
