/*
 * Copyright (C) 2021 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Cameras;

import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Camera3D extends Camera {

    public final Point eye, look;
    public final Vec up;
    public float fov;

    public Camera3D(JMathAnimScene scene, int screenWidth, int screenHeight) {
        super(scene, screenWidth, screenHeight);
        perspective = true;//Default mode: perspective
        fov = 45.0f;
        eye = Point.at(0, 0, 1.125d / Math.tan(1d * fov * PI / 360)).visible(false);
        look = Point.at(0, 0, 0).visible(false);
        up = Vec.to(0, 0, 0);
    }

    @Override
    public Camera setMathXY(double xmin, double xmax, double ycenter) {
        super.setMathXY(xmin, xmax, ycenter);
        adjustLookAtToMathView();
        return this;
    }
    private void adjustLookAtToMathView() {
        Rect bb=getMathView();
        double x=bb.getCenter().v.x;
        double y=bb.getCenter().v.y;
        eye.copyFrom(Point.at(x, y, getProperEyeHeight(bb)));
        look.copyFrom(Point.at(x,y,0));
    }

    public double getProperEyeHeight(Rect bb) {
        return .5*bb.getHeight()/ Math.tan(1d * fov * PI / 360);
    }
    
    public double getMathViewHeight3D(double zDepth) {
        return 2* Math.tan(1d * fov * PI / 360)*zDepth;//TODO: store Tan to optimize
        
    }
    
    
    
  public void lookAt(Point eye, Point look) {
        lookAt(eye, look, Vec.to(0, 0, 0));
    }

    public void lookAt(Point eye, Point look, Vec up) {
        this.eye.copyFrom(eye);
        this.look.copyFrom(look);
        this.up.copyFrom(up);
    }

    public Vec getUpVector() {
        Vec l = eye.to(look);
        if ((l.x != 0) || (l.y != 0)) {
            return Vec.to(0, 0, 1);
        } else {
            return Vec.to(0, 1, 0);
        }
    }
}
