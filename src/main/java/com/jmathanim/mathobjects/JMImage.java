/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class JMImage extends MathObject {

    public String filename;
    public final Rect bbox;
    public boolean preserveRatio = false;
    public double rotateAngle = 0;
    public double rotateAngleBackup = 0;

    public JMImage(String filename) {
        this.filename = filename;
        this.bbox = JMathAnimConfig.getConfig().getRenderer().createImage(filename);
    }

    @Override
    public Dot getCenter() {
        return bbox.getCenter();
    }

    @Override
    public <T extends MathObject> T shift(Vec shiftVector) {
        bbox.copyFrom(bbox.shifted(shiftVector));
        return (T) this;
    }

    @Override
    public <T extends MathObject> T moveTo(Vec coords) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends MathObject> T copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void prepareForNonLinearAnimation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processAfterNonLinearAnimation() {
    }

    @Override
    public Rect getBoundingBox() {
        return bbox;
    }

    @Override
    public void registerChildrenToBeUpdated(JMathAnimScene scene) {
    }

    @Override
    public void unregisterChildrenToBeUpdated(JMathAnimScene scene) {
    }

    @Override
    public void draw(Renderer r) {
//        System.out.println(bbox);
        r.drawImage(this);
    }

    @Override
    public void update(JMathAnimScene scene) {
    }

    @Override
    public void restoreState() {
        super.restoreState();
        bbox.restoreState();
        this.rotateAngle = this.rotateAngleBackup;
    }

    @Override
    public void saveState() {
        super.saveState();
        bbox.saveState();
        this.rotateAngleBackup = this.rotateAngle;
    }

    @Override
    public <T extends MathObject> T scale(Dot scaleCenter, double sx, double sy, double sz) {
        bbox.copyFrom(Rect.make(bbox.getUL().scale(scaleCenter, sx, sy, sz), bbox.getDR().scale(scaleCenter, sx, sy, sz)));
        return (T) this;
    }


    @Override
    public <T extends MathObject> T rotate(Dot center, double angle) {
        //For now, ignore rotate center
        rotateAngle+=angle;
        return (T) this;
    }

}
