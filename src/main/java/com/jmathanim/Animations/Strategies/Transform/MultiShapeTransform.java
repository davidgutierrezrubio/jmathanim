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
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.Transform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MultiShapeObject;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class MultiShapeTransform extends AnimationGroup {

    private MultiShapeObject dst;
    private MultiShapeObject tr;
    private MultiShapeObject mobjTransformed;
    private MultiShapeObject mobjDestiny;

    public MultiShapeTransform(double runtime, MultiShapeObject mobjTransformed, MultiShapeObject mobjDestiny) {
        super();
        this.mobjDestiny=mobjDestiny;
        this.mobjTransformed=mobjTransformed;
        tr = new MultiShapeObject();
        dst = new MultiShapeObject();
        int sizeTr = mobjTransformed.shapes.size();
        int sizeDst = mobjDestiny.shapes.size();
        int numAnims = Math.max(sizeTr, sizeDst);

        if (sizeDst < sizeTr) {
            for (int i = 0; i < sizeTr; i++) {
                dst.add(mobjDestiny.get(i * sizeDst / sizeTr).copy());
            }
            tr = mobjTransformed.copy();
        }
        if (sizeTr < sizeDst) {
            for (int i = 0; i < sizeDst; i++) {
                tr.add(mobjTransformed.get(i * sizeTr / sizeDst).copy());
            }
            dst = mobjDestiny.copy();
        }
        if (sizeDst == sizeTr) {
            dst = mobjDestiny.copy();
            tr = mobjTransformed.copy();
        }

        for (int n = 0; n < numAnims; n++) {
            add(new Transform(runtime, tr.get(n), dst.get(n)));
        }
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        scene.remove(mobjTransformed);
    }
    

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        scene.remove(tr);
        scene.remove(dst);
        scene.add(mobjDestiny);
    }

}
