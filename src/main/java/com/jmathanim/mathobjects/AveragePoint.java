/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.mathobjects.updateableObjects.Updateable;
import com.jmathanim.Utils.Vec;
import java.util.ArrayList;

/**
 * This class represents middle point computed from 2 given ones. This class
 * implements the interface updateable, which automatically updates its
 * components.
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class AveragePoint extends Point implements Updateable {

    private final JMPath path;

    public AveragePoint(JMPath path) {
        super();
        this.path = path;
    }

    @Override
    public void update() {
        Vec resul = new Vec(0, 0);
        for (int n = 0; n < path.size(); n++) {
            resul.addInSite(path.getJMPoint(n).p.v);
        }
        resul.multInSite(1.0d / path.size());
        this.v = resul;
    }

    @Override
    public int getUpdateLevel() {
        int level = -1;
        for (JMPathPoint p : path.jmPathPoints) {
            level = Math.max(level, p.getUpdateLevel());
        }
        return level;
    }

}
