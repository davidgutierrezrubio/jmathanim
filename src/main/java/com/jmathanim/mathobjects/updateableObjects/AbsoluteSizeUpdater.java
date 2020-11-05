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

package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class AbsoluteSizeUpdater implements Updateable{
    public MathObject obj;
    public double ratio;

    public AbsoluteSizeUpdater(MathObject obj, double ratio) {
        this.obj = obj;
        this.ratio = ratio;
    }

    
    @Override
    public int getUpdateLevel() {
        return obj.getUpdateLevel()+1;
    }

    @Override
    public void update(JMathAnimScene scene) {
        Camera camera = scene.getCamera();
        double screenMathWidth=camera.getMathView().getWidth();
        double objectWidth=obj.getBoundingBox().getWidth();
        
        //ObjectWidth should be ratio*screenMathWidth
        //so I scale object by ratio*screenMathWidth/objectWidth
        double scale=ratio*screenMathWidth/objectWidth;
        obj.scale(scale, scale);
    }
    
    
}
