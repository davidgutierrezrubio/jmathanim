/*
 * Copyright (C) 2022 David
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
package com.jmathanim.Constructible;

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.mathobjects.MathObject;

/**
 * A Constructible object that cannot be transformed
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class FixedConstructible extends Constructible {

    @Override
    public FixedConstructible applyAffineTransform(AffineJTransform transform) {
        return this; //Do nothing
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        //This object has no state, only its drawing attributes
        this.getMp().copyFrom(obj.getMp());
    }
}
