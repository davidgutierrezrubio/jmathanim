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
package com.jmathanim.MathObjects.Text;

import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 *
 * @author David
 */
public final class JMNumberLength extends JMNumber {

    private final Vec A;
    private final Vec B;

    private JMNumberLength(Coordinates<?> A, Coordinates<?> B) {
        super(0);
        this.A = A.getVec();
        this.B = B.getVec();
        addDependency(this.A);
        addDependency(this.B);
        update(scene);
    }

    @Override
    public void performMathObjectUpdateActions(JMathAnimScene scene) {
        setValue(A.to(B).norm());
    }
}
