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
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import javafx.scene.image.Image;

/**
 *
 * @author David
 */
public abstract class AbstractJMImage extends MathObject {

    protected Rect bbox;
    public boolean preserveRatio = false;
    private boolean cached = false;
    protected AffineJTransform currentViewTransform;

    public AbstractJMImage() {
        this.currentViewTransform = new AffineJTransform();
    }

    
    
    @Override
    public Rect computeBoundingBox() {
        return bbox.getTransformedRect(currentViewTransform);
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
        currentViewTransform = currentViewTransform.compose(tr);
        return (T) this;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        if (isVisible()) {
            r.drawImage(this);
        }
        scene.markAsAlreadyDrawed(this);
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
