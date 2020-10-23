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

package tests;

import com.jmathanim.jmathanim.JMathAnimScene;

/**
 *
 * @author David
 */
public class JMathAnim {

    

    /**
     * A launcher for the scene
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        JMathAnimScene demoScene = new myScene();
//        demoScene.execute();
//        JMathAnimScene tsc = new TesterShowCreation();
//        tsc.execute();
         JMathAnimScene tsf = new TesterTransform();
        tsf.execute();
    }

}
