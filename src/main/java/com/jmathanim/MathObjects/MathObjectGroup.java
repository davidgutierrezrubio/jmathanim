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
package com.jmathanim.MathObjects;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class that manages sets of MathObjects. The objects are not added to the scene when you add this object to the
 * scene. It acts as a container to easily perform bulk-operations
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MathObjectGroup extends AbstractMathGroup<MathObjectGroup> {

    protected MathObjectGroup() {
        super();
    }

    protected MathObjectGroup(MathObject<?>... objects) {
        this(new ArrayList<>(Arrays.asList(objects)));
    }

    protected MathObjectGroup(ArrayList<MathObject<?>> objects) {
        super(objects);

    }

    @Override
    protected MathObjectGroup makeNewInstance() {
        return MathObjectGroup.make();
    }

    public static MathObjectGroup make(MathObject<?>... objects) {
        return new MathObjectGroup(objects);
    }

    public static MathObjectGroup make(ArrayList<MathObject<?>> objects) {
        return new MathObjectGroup(objects);
    }

    /**
     * Creates a MathObjectGroup with a give number of copies of a MathObject
     * @param object MathObjet to copy
     * @param numberOfCopies Number of copies
     * @return The MathObjectGroup created
     */
    public static MathObjectGroup makeCopies(MathObject<?> object, int numberOfCopies) {
        MathObjectGroup resul = MathObjectGroup.make();
        for (int i = 0; i < numberOfCopies; i++) {
            resul.add(object.copy());
        }
        return resul;
    }

    /**
     * Orientation (for distribution method)
     */
    public enum Orientation {
        VERTICAL,
        HORIZONTAL
    }
}
