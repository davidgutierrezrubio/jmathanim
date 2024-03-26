/*
 * Copyright (C) 2024 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Utils;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;
import com.jmathanim.mathobjects.hasArguments;
import java.util.HashMap;

/**
 * A link between 2 Linkable objects
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Link {

    public enum LinkType {
        X, Y, XY, VALUE, ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, WIDTH, HEIGHT, XMIN, XMAX, YMIN, YMAX
    };
    public Linkable origin;
    public LinkType originLinkType;
    public LinkType destinyLinkType;
    public Linkable destiny;

    public Link(Linkable origin, LinkType originLinkType, Linkable destiny, LinkType destinyLinkType) {
        this.origin = origin;
        this.originLinkType = originLinkType;
        this.destinyLinkType = destinyLinkType;
        this.destiny = destiny;
    }

    /**
     * Apply current link
     *
     * @return True if link has been applied succesfully. False otherwise.
     */
    public boolean apply() {
        try {
            //Get origin data
            Object data = getLinkData();

            //Apply data
            //*********************************************************
            if (destiny instanceof MathObject) {
                MathObject mathObject = (MathObject) destiny;
                Vec vshift = Vec.to(0, 0);
                Point center = mathObject.getCenter();

                switch (destinyLinkType) {
                    case X:
                        vshift.x = getX(data) - center.v.x;
                        mathObject.shift(vshift);
                        break;
                    case Y:
                        vshift.y = getY(data) - center.v.y;
                        mathObject.shift(vshift);
                        break;
                    case XY:
                        vshift.copyFrom(getXY(data).add(center.v.mult(-1)));
                        mathObject.shift(vshift);
                        break;
                    case XMIN:
                        vshift.x = getValue(data) - mathObject.getBoundingBox().xmin;
                        mathObject.shift(vshift);
                        break;
                    case XMAX:
                        vshift.x = getValue(data) - mathObject.getBoundingBox().xmax;
                        mathObject.shift(vshift);
                        break;
                    case YMIN:
                        vshift.y = getValue(data) - mathObject.getBoundingBox().ymin;
                        mathObject.shift(vshift);
                        break;
                    case YMAX:
                        vshift.y = getValue(data) - mathObject.getBoundingBox().ymax;
                        mathObject.shift(vshift);
                        break;

                    case WIDTH:
                        Double w = getValue(data);
                        double w0 = mathObject.getWidth();
                        if (w0 > 0) {
                            mathObject.scale(w / w0, 1);
                        }
                        break;
                    case HEIGHT:
                        Double h = getValue(data);
                        double h0 = mathObject.getHeight();
                        if (h0 > 0) {
                            mathObject.scale(1, h / h0);
                        }
                        break;

                }

                if (destiny instanceof Scalar) {
                    Scalar scalar = (Scalar) destiny;
                    switch (destinyLinkType) {
                        case VALUE:
                            scalar.setScalar(getValue(data));
                            break;
                    }
                }

            }

            //*********************************************************
            if (destiny instanceof hasArguments) {
                LaTeXMathObject lat = (LaTeXMathObject) destiny;
                double value = getValue(data);
                switch (destinyLinkType) {
                    case ARG0:
                        lat.getArg(0).setScalar(value);
                        break;
                    case ARG1:
                        lat.getArg(1).setScalar(value);
                        break;
                    case ARG3:
                        lat.getArg(3).setScalar(value);
                        break;
                    case ARG4:
                        lat.getArg(4).setScalar(value);
                        break;
                    case ARG5:
                        lat.getArg(4).setScalar(value);
                        break;
                }
            }
        } catch (JLinkException e) {
            JMathAnimScene.logger.warn("Link could not be done");
        }
        return true;
    }

    private Object getLinkData() throws JLinkException {
        switch (originLinkType) {
            case X:
                return getX(origin);
            case Y:
                return getY(origin);
            case XY:
                return getXY(origin);
            case ARG0:
                return getArg(origin, 0);
            case ARG1:
                return getArg(origin, 1);
            case ARG2:
                return getArg(origin, 2);
            case ARG3:
                return getArg(origin, 3);
            case ARG4:
                return getArg(origin, 4);
            case ARG5:
                return getArg(origin, 5);
            case WIDTH:
                return getWidth(origin);
            case HEIGHT:
                return getHeight(origin);
            case XMIN:
                return getBoundaryCoordinate(origin, 3);
            case XMAX:
                return getBoundaryCoordinate(origin, 1);
            case YMAX:
                return getBoundaryCoordinate(origin, 0);
            case YMIN:
                return getBoundaryCoordinate(origin, 2);
        }

        return null;
    }

    private Double getBoundaryCoordinate(Object obj, int num) throws JLinkException {
        if (obj instanceof Boxable) {
            Boxable boxable = (Boxable) obj;
            Rect bb = boxable.getBoundingBox();
            switch (num) {
                case 0:
                    return bb.ymax;
                case 1:
                    return bb.xmax;
                case 2:
                    return bb.ymin;
                case 3:
                    return bb.xmin;
            }

        }
        throw new JLinkException();
    }

    private Double getWidth(Object obj) throws JLinkException {
        if (obj instanceof Boxable) {
            Boxable boxable = (Boxable) obj;
            return boxable.getBoundingBox().getWidth();
        }
        throw new JLinkException();
    }

    private Double getHeight(Object obj) throws JLinkException {
        if (obj instanceof Boxable) {
            Boxable boxable = (Boxable) obj;
            return boxable.getBoundingBox().getHeight();
        }
        throw new JLinkException();
    }

    private Double getX(Object obj) throws JLinkException {

        if (obj instanceof Point) {
            Point point = (Point) obj;
            return point.v.x;
        }
        if (obj instanceof Vec) {
            Vec vec = (Vec) obj;
            return vec.x;
        }
        if (obj instanceof Double) {
            return (Double) obj;
        }
        if (obj instanceof Boxable) {
            Boxable boxable = (Boxable) obj;
            return boxable.getBoundingBox().getCenter().v.x;
        }
        throw new JLinkException();
    }

    private Double getY(Object obj) throws JLinkException {

        if (obj instanceof Point) {
            Point point = (Point) obj;
            return point.v.y;
        }
        if (obj instanceof Vec) {
            Vec vec = (Vec) obj;
            return vec.y;
        }
        if (obj instanceof Double) {
            return (Double) obj;
        }
        if (obj instanceof Boxable) {
            Boxable boxable = (Boxable) obj;
            return boxable.getBoundingBox().getCenter().v.y;
        }
        throw new JLinkException();
    }

    private Vec getXY(Object obj) throws JLinkException {

        if (obj instanceof Point) {
            Point point = (Point) obj;
            return point.v;
        }
        if (obj instanceof Vec) {
            return (Vec) obj;
        }
        if (obj instanceof Boxable) {
            Boxable boxable = (Boxable) obj;
            return boxable.getBoundingBox().getCenter().v;
        }
        throw new JLinkException();
    }

    private Double getValue(Object obj) throws JLinkException {
        if (obj instanceof Double) {
            return (Double) obj;
        }
        throw new JLinkException();
    }

    private Double getArg(Object obj, int n) throws JLinkException {
        if (obj instanceof hasArguments) {
            hasArguments hasArgs = (hasArguments) obj;
            return hasArgs.getArg(n).value;
        }
        throw new JLinkException();
    }

}
