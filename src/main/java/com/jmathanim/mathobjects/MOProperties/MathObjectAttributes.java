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

package com.jmathanim.mathobjects.MOProperties;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Stateable;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class MathObjectAttributes implements Stateable {

    private MathObject parent;

    public MathObjectAttributes(MathObject parent) {
        this.parent = parent;
    }

    public abstract void applyTransform(AffineJTransform tr);

    public abstract MathObjectAttributes copy();

    public MathObject getParent() {
        return parent;
    }

    abstract public void setParent(MathObject parent);
    

}
