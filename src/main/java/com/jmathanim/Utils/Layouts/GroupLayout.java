/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Utils.Layouts;

import com.jmathanim.Utils.Boxable;
import com.jmathanim.Utils.Rect;
import com.jmathanim.mathobjects.MathObjectGroup;

/**
 * A basic abstract class to implement any layout can be applied to a
 * MathObjectGroup
 *
 * @author David Gutiérrez Rubio
 */
public abstract class GroupLayout {

    public abstract void applyLayout(MathObjectGroup group);

    public Rect getBoundingBox(MathObjectGroup group) {
        if (group.isEmpty()) {//Nothing to show
            return null;
        }
        group.saveState();
        applyLayout(group);
        Rect bbox = group.getBoundingBox();
        group.restoreState();
        return bbox;
    }

}
