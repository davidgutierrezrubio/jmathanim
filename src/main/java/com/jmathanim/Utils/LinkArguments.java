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

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Constructible.Lines.CTSegment;
import com.jmathanim.Constructible.Others.CTAngleMark;
import com.jmathanim.Enum.LinkType;
import com.jmathanim.MathObjects.*;
import com.jmathanim.MathObjects.Shapes.MultiShapeObject;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.AbstractCollection;
import java.util.function.DoubleUnaryOperator;

/**
 * A link between 2 Linkable objects
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public final class LinkArguments extends Link {



    public Object origin;
    public LinkType originLinkType;
    public LinkType destinyLinkType;
    public Linkable destiny;
    public DoubleUnaryOperator lambda;

    public static LinkArguments make(Object origin, LinkType originLinkType, Linkable destiny, LinkType destinyLinkType) {
        return new LinkArguments(origin, originLinkType, destiny, destinyLinkType, t -> t);
    }

    public static LinkArguments make(Object origin, LinkType originLinkType, Linkable destiny, LinkType destinyLinkType, DoubleUnaryOperator function) {
        return new LinkArguments(origin, originLinkType, destiny, destinyLinkType, function);
    }

    private LinkArguments(Object origin, LinkType originLinkType, Linkable destiny, LinkType destinyLinkType, DoubleUnaryOperator function) {
        this.origin = origin;
        this.originLinkType = originLinkType;
        this.destinyLinkType = destinyLinkType;
        this.destiny = destiny;
        this.lambda = function;
    }

  
    @Override
    public boolean apply() {
        try {
            //Get origin data
            double data = getLinkData();
            data = lambda.applyAsDouble(data);

            //Apply data
            //*********************************************************
            if (destiny instanceof MathObject) {
                MathObject mathObject = (MathObject) destiny;
                Vec vshift = Vec.to(0, 0);
                Vec center = mathObject.getCenter();

                switch (destinyLinkType) {
                    case X:
                        vshift.x = getValue(data) - center.x;
                        mathObject.shift(vshift);
                        break;
                    case Y:
                        vshift.y = getValue(data) - center.y;
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
                            scalar.setValue(getValue(data));
                            break;
                    }
                }

            }

            //*********************************************************
            if (destiny instanceof hasArguments) {
                hasArguments lat = (hasArguments) destiny;
                double value = getValue(data);
                switch (destinyLinkType) {
                    case ARG0:
                        lat.getArg(0).setValue(value);
                        break;
                    case ARG1:
                        lat.getArg(1).setValue(value);
                        break;
                    case ARG3:
                        lat.getArg(3).setValue(value);
                        break;
                    case ARG4:
                        lat.getArg(4).setValue(value);
                        break;
                    case ARG5:
                        lat.getArg(5).setValue(value);
                        break;
                    case ARG6:
                        lat.getArg(6).setValue(value);
                        break;
                    case ARG7:
                        lat.getArg(7).setValue(value);
                        break;
                    case ARG8:
                        lat.getArg(8).setValue(value);
                        break;
                    case ARG9:
                        lat.getArg(9).setValue(value);
                        break;

                }
            }
        } catch (JLinkException e) {
            JMathAnimScene.logger.warn("Link could not be done");
        }
        return true;
    }

    private double getLinkData() throws JLinkException {
        switch (originLinkType) {
            case X:
                return getX(origin);
            case Y:
                return getY(origin);
            case VALUE:
                return getValue(origin);
            case COUNT:
                return getCount(origin);
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

        throw new JLinkException();
    }

    private int getCount(Object obj) throws JLinkException {
        if (obj instanceof Shape) {
            Shape shape = (Shape) obj;
            return shape.size();
        }
        if (obj instanceof MathObjectGroup) {
            MathObjectGroup mg = (MathObjectGroup) obj;
            return mg.size();
        }
           if (obj instanceof MultiShapeObject) {
            MultiShapeObject msh = (MultiShapeObject) obj;
            return msh.size();
        }
           if (obj instanceof AnimationGroup) {
               int count=0;
               AnimationGroup anim = (AnimationGroup) obj;
               for (Animation animation : anim.getAnimations()) {
                   count += (animation.getTotalLambda().applyAsDouble(animation.getT()) ==1 ? 1 : 0);
               }
                return count;
           }
        if (obj instanceof AbstractCollection) {
            AbstractCollection o = (AbstractCollection) obj;
            return o.size();

        }


        throw new JLinkException();
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

        if (obj instanceof Coordinates) {
            Coordinates point = (Coordinates) obj;
            return point.getVec().x;
        }
        if (obj instanceof Double) {
            return (Double) obj;
        }
        if (obj instanceof Boxable) {
            Boxable boxable = (Boxable) obj;
            return boxable.getBoundingBox().getCenter().x;
        }
        throw new JLinkException();
    }

    private Double getY(Object obj) throws JLinkException {

        if (obj instanceof Coordinates) {
            Coordinates point = (Coordinates) obj;
            return point.getVec().y;
        }
        if (obj instanceof Double) {
            return (Double) obj;
        }
        if (obj instanceof Boxable) {
            Boxable boxable = (Boxable) obj;
            return boxable.getBoundingBox().getCenter().y;
        }
        throw new JLinkException();
    }

    private Double getValue(Object obj) throws JLinkException {
        if (obj instanceof Scalar) {
            Scalar scalar = (Scalar) obj;
            return scalar.getValue();
        }
        if (obj instanceof Double) {
            return (Double) obj;
        }
        if (obj instanceof Vec) {
            Vec vec = (Vec) obj;
            return vec.norm();
        }
        if (obj instanceof CTSegment) {
            CTSegment cts=(CTSegment) obj;
            return cts.getP1().to(cts.getP2()).norm();
        }

        if (obj instanceof CTAngleMark) {
            return ((CTAngleMark)obj).getAngle();
        }
        if (obj instanceof Boxable) {
            Boxable boxable = (Boxable) obj;
            return boxable.getBoundingBox().getCenter().norm();
        }
        throw new JLinkException();
    }

    private Double getArg(Object obj, int n) throws JLinkException {
        if (obj instanceof hasArguments) {
            hasArguments hasArgs = (hasArguments) obj;
            return hasArgs.getArg(n).getValue();
        }
        throw new JLinkException();
    }

    public DoubleUnaryOperator getLambda() {
        return lambda;
    }

    public LinkArguments setLambda(DoubleUnaryOperator lambda) {
        this.lambda = lambda;
        return this;
    }
}
