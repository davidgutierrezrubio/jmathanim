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

package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;

/**
 * This class represents middle point computed from 2 given ones. This class
 * implements the interface updateable, which automatically updates its
 * components.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
