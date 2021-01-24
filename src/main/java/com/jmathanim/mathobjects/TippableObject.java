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
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.JMColor;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.mathobjects.ArrowTip.make;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class TippableObject extends MathObject {

    public static final double DELTA_DERIVATIVE = .0001;

    private double scaleFactorX,scaleFactorY;
    protected Shape shape;
    private MathObject tip, tipCopy;
    private double tLocation;
    private double offsetAngle = 0;
    private Point pointLoc;
    int anchorValue;
    private slopeDirection direction;
    private Vec pointTo;
//    abstract public void updateLocations(JMPathPoint location);

    public double getOffsetAngle() {
        return offsetAngle;
    }

    public <T extends TippableObject> T setOffsetAngle(double angle) {
        this.offsetAngle = angle;
        return (T) this;
    }

    public double getLocation() {
        return tLocation;
    }

    public final void setLocation(double location) {
        this.tLocation = location;
    }

    public MathObject getTip() {
        return tip;
    }

    public void setTip(MathObject tip) {
        this.tip = tip;
    }

    protected MathObject getTipCopy() {
        return tipCopy;
    }

    public MathObject getTippedObject() {
        update(scene);
        return getTipCopy();
    }

    protected void setTipCopy(MathObject tipCopy) {
        this.tipCopy = tipCopy;
    }

    @Override
    public <T extends MathObject> T drawColor(JMColor dc) {
        tip.drawColor(dc);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T fillColor(JMColor fc) {
        tip.fillColor(fc);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T drawAlpha(double alpha) {
        return tip.drawAlpha(alpha);
    }

    @Override
    public <T extends MathObject> T fillAlpha(double alpha) {
        tip.fillAlpha(alpha);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T multFillAlpha(double alphaScale) {
        tip.multFillAlpha(alphaScale);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T multDrawAlpha(double alphaScale) {
        tip.multDrawAlpha(alphaScale);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T thickness(double newThickness) {
        tip.thickness(newThickness);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T style(String name) {
        tip.style(name);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T fillWithDrawColor(boolean fcd) {
        tip.fillWithDrawColor(fcd);
        return (T) this;
    }

    @Override
    public void saveState() {
        super.saveState();
        tip.saveState();
    }

    @Override
    public void restoreState() {
        super.restoreState();
        tip.restoreState();
    }

    @Override
    public void draw(Renderer r) {
        getTipCopy().draw(r);
    }

    public void updateLocations() {

        this.pointLoc = shape.getPath().getPointAt(getLocation()).p;
        Point slopeTo;

        setTipCopy(getTip().copy());
        getTipCopy().setHeight(.1);
        getTipCopy().scale(scaleFactorX, scaleFactorY);
        //Shifting
        Point headPoint = this.getTipCopy().getBoundingBox().getUpper();
        this.getTipCopy().shift(headPoint.to(pointLoc));

        //Rotating
        if (direction == ArrowTip.slopeDirection.NEGATIVE) {
//            this.pointTo = jmp.p.to(jmp.cpEnter);
            slopeTo = shape.getPath().getPointAt(getLocation() - DELTA_DERIVATIVE).p;

        } else {
            slopeTo = shape.getPath().getPointAt(getLocation() + DELTA_DERIVATIVE).p;
        }
        pointTo = pointLoc.to(slopeTo);
        double rotAngle = pointTo.getAngle();
        AffineJTransform tr = AffineJTransform.create2DRotationTransform(pointLoc, -Math.PI / 2 + rotAngle);
        tr.applyTransform(getTipCopy());
        getTipCopy().rotate(getOffsetAngle());
    }

    @Override
    public int getUpdateLevel() {
        return shape.getUpdateLevel() + 1;
    }

    @Override
    public void update(JMathAnimScene scene) {
        updateLocations();
    }

    @Override
    public Rect getBoundingBox() {
        updateLocations();
        return getTipCopy().getBoundingBox();
    }

    @Override
    public <T extends MathObject> T copy() {
        return (T) make(shape, getLocation(), direction, getTip().copy());
    }

    public slopeDirection getDirection() {
        return direction;
    }

    public <T extends MathObject> T setDirection(slopeDirection direction) {
        this.direction = direction;
        return (T) this;
    }

    @Override
    public <T extends MathObject> T scale(double sx, double sy) {
       scaleFactorX*=sx;
       scaleFactorY*=sy;
        return (T) this;
    }

    public TippableObject() {
        scaleFactorX=1;
        scaleFactorY=1;
    }

    @Override
    public <T extends MathObject> T scale(double s) {
       return this.scale(s,s);
    }

    @Override
    public <T extends MathObject> T setHeight(double h) {
        tip.setHeight(h);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T setWidth(double w) {
        tip.setWidth(w);
        return (T) this;
    }

    @Override
    public <T extends MathObject> T rotate(double angle) {
        tip.rotate(angle);
        return (T) this;
    }
    public enum slopeDirection {
        NEGATIVE, POSITIVE
    }

}
