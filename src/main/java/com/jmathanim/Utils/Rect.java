/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

/**
 * Encapsulates data about a rectangle
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class Rect {

    public double xmin, ymin, xmax, ymax;

    public Rect(double xmin, double ymin, double xmax, double ymax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    public double[] intersectLine(double x1, double y1, double x2, double y2) {
        Vec v1, v2, v3, v4;
        double sc1, sc2, sc3, sc4;
        Vec vRecta = new Vec(x2 - x1, y2 - y1);
        double lambda1, lambda2;

        double interx1 = xmin;
        double interx2 = xmax;
        double intery1 = ymin;
        double intery2 = ymax;

        v1 = new Vec(xmin - x1, ymin - y1);
        v2 = new Vec(xmin - x1, ymax - y1);
        v3 = new Vec(xmax - x1, ymax - y1);
        v4 = new Vec(xmax - x1, ymin - y1);
        sc1 = vRecta.cross(v1).z;
        sc2 = vRecta.cross(v2).z;
        sc3 = vRecta.cross(v3).z;
        sc4 = vRecta.cross(v4).z;
        if (sc1 < 0) {//Ensure sc1 is always >0
            sc1 = -sc1;
            sc2 = -sc2;
            sc3 = -sc3;
            sc4 = -sc4;
        }

        //Now I test all possible cases:
        //Case 1:
        if (sc2 > 0 && sc3 > 0 && sc4 > 0) {   //There are no interesection points
            return null;
        }
        //Case 2:
        if (sc2 < 0 && sc3 < 0 && sc4 < 0) {   //Line cross at L and D
            //intersect with xmin:
            //x1+lambda*vRecta.x=xmin;
            lambda1 = (xmin - x1) / vRecta.x;
            interx1 = xmin;
            intery1 = y1 + lambda1 * vRecta.y;
            //intersect with ymin:
            //y1+lambda*vRecta.y=ymin;
            lambda2 = (ymin - y1) / vRecta.y;
            interx2 = x1 + lambda2 * vRecta.x;
            intery2 = ymin;
        }
        //Case 3:
        if (sc2 < 0 && sc3 > 0 && sc4 > 0) {   //Line cross at L and U
            //intersect with xmin:
            //x1+lambda*vRecta.x=xmin;
            lambda1 = (xmin - x1) / vRecta.x;
            interx1 = xmin;
            intery1 = y1 + lambda1 * vRecta.y;
            //intersect with ymax:
            //y1+lambda*vRecta.y=ymax;
            lambda2 = (ymax - y1) / vRecta.y;
            interx2 = x1 + lambda2 * vRecta.x;
            intery2 = ymax;
        }
        //Case 4:
        if (sc2 > 0 && sc3 < 0 && sc4 > 0) {   //Line cross at R and U
            //intersect with xmax:
            //x1+lambda*vRecta.x=xmax;
            lambda1 = (xmax - x1) / vRecta.x;
            interx1 = xmax;
            intery1 = y1 + lambda1 * vRecta.y;
            //intersect with ymax:
            //y1+lambda*vRecta.y=ymax;
            lambda2 = (ymax - y1) / vRecta.y;
            interx2 = x1 + lambda2 * vRecta.x;
            intery2 = ymax;
        }

        //Case 5:
        if (sc2 > 0 && sc3 > 0 && sc4 < 0) {   //Line cross at R and D
            //intersect with xmax:
            //x1+lambda*vRecta.x=xmax;
            lambda1 = (xmax - x1) / vRecta.x;
            interx1 = xmax;
            intery1 = y1 + lambda1 * vRecta.y;
            //intersect with ymin:
            //y1+lambda*vRecta.y=ymin;
            lambda2 = (ymin - y1) / vRecta.y;
            interx2 = x1 + lambda2 * vRecta.x;
            intery2 = ymin;
        }
        //Case 6:
        if (sc2 < 0 && sc3 < 0 && sc4 > 0) {   //Line cross at L and R
            //intersect with xmin:
            //x1+lambda*vRecta.x=xmin;
            lambda1 = (xmin - x1) / vRecta.x;
            interx1 = xmin;
            intery1 = y1 + lambda1 * vRecta.y;
            //intersect with xmax:
            //x1+lambda*vRecta.x=xmin;
            lambda2 = (xmax - x1) / vRecta.x;
            interx2 = xmax;
            intery2 = y1 + lambda2 * vRecta.y;
        }
        //Case 7:
        if (sc2 > 0 && sc3 < 0 && sc4 < 0) {   //Line cross at D and U
            //intersect with ymin:
            //y1+lambda*vRecta.y=ymin;
            lambda1 = (ymin - y1) / vRecta.y;
            interx1 = x1 + lambda1 * vRecta.x;
            intery1 = ymin;
            //intersect with ymax:
            //y1+lambda*vRecta.y=ymax;
            lambda2 = (ymax - y1) / vRecta.y;
            interx2 = x1 + lambda2 * vRecta.x;
            intery2 = ymax;
        }

        //Now, determines the correct order of the solution
        double[] resul;
        v1 = new Vec(interx1 - x1, intery1 - y1);
        v2 = new Vec(interx2 - x1, intery2 - y1);
        if (vRecta.dot(v1) > 0) {
            //In this case, interx1,intery2 is closer to x2,y2
            resul = new double[]{interx2, intery2, interx1, intery1};
        } else {
            resul = new double[]{interx1, intery1, interx2, intery2};
        }
        return resul;
    }

}
