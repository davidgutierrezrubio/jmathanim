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
package com.jmathanim.mathobjects.Text;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David
 */
public final class JMNumberLength extends JMNumber {

    private final Point A;
    private final Point B;

    private JMNumberLength(Point A, Point B) {
        super(0);
        this.A = A;
        this.B = B;
        update(scene);
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        setScalar(A.to(B).norm());
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(A,B);
        setUpdateLevel(Math.max(A.getUpdateLevel(), B.getUpdateLevel()) + 1);
    }
}
