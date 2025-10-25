/*
 * Copyright (C) 2021 David
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
package com.jmathanim.MathObjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.DrawStyleProperties;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import javafx.scene.image.Image;

/**
 *
 * @author David
 */
public abstract class AbstractJMImage<T extends AbstractJMImage<T>>  extends MathObject<T>  {

    protected Rect bbox;
    public boolean preserveRatio = false;
    private boolean cached = false;
    protected AffineJTransform currentViewTransform;
    private final MODrawProperties mp;


    public AbstractJMImage() {
        this.currentViewTransform = new AffineJTransform();
        this.mp = JMathAnimConfig.getConfig().getDefaultMP();
    }
    @Override
    public DrawStyleProperties getMp() {
        return mp;
    }
    
    
    @Override
    protected Rect computeBoundingBox() {
        return bbox.getTransformedRect(currentViewTransform);
    }

    @Override
    public T applyAffineTransform(AffineJTransform affineJTransform) {
        currentViewTransform = currentViewTransform.compose(affineJTransform);
        return (T) this;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r,Camera cam) {
        if (isVisible()) {
            r.drawImage(this,cam);
        }
        scene.markAsAlreadydrawn(this);
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    abstract public String getId();

    abstract public Image getImage();

    public AffineJTransform getCurrentViewTransform() {
        return currentViewTransform;
    }
    
}
