package com.jmathanim.Utils;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Enum.AnchorType;
import com.jmathanim.Enum.ScreenAnchor;
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.MathObject;

public class StackUtils<T extends MathObject<T>> {
    private final T parent;

    private double xGap, yGap;
    private AnchorType anchorOrigin;
    private AnchorType anchorDestiny;
    private boolean useRelativeGaps;

    public StackUtils(T parent) {
        this.parent = parent;
        resetAnchor();
        resetGaps();
    }

    private static AnchorType computeAnchorOriginForAnchorScreenDestiny(ScreenAnchor screenAnchor) {
        switch (screenAnchor) {
            case LEFT:
                return AnchorType.LEFT;
            case RIGHT:
                return AnchorType.RIGHT;
            case LOWER:
                return AnchorType.LOWER;
            case UPPER:
                return AnchorType.UPPER;
            case LOWER_LEFT:
                return AnchorType.DIAG3;
            case UPPER_LEFT:
                return AnchorType.DIAG2;
            case LOWER_RIGHT:
                return AnchorType.DIAG4;
            case UPPER_RIGHT:
                return AnchorType.DIAG1;
            default://Default case CENTER
                return AnchorType.CENTER;
        }
    }

    private static Vec getScreenAnchorPoint(Camera camera, ScreenAnchor screenAnchor) {
        Rect mathView = camera.getMathView();
        switch (screenAnchor) {
            case CENTER:
                return mathView.getCenter();
            case LEFT:
                return mathView.getLeft();
            case RIGHT:
                return mathView.getRight();
            case LOWER:
                return mathView.getLower();
            case UPPER:
                return mathView.getUpper();
            case LOWER_LEFT:
                return mathView.getLowerLeft();
            case UPPER_LEFT:
                return mathView.getUpperLeft();
            case LOWER_RIGHT:
                return mathView.getLowerRight();
            case UPPER_RIGHT:
                return mathView.getUpperRight();
            default://Default case CENTER
                return mathView.getCenter();
        }
    }

    public StackUtils<T> withGaps(double xGap, double yGap) {
        this.xGap = xGap;
        this.yGap = yGap;
        useRelativeGaps = false;
        return this;
    }

    public StackUtils<T> withRelativeGaps(double xGap, double yGap) {
        this.xGap = xGap;
        this.yGap = yGap;
        useRelativeGaps = true;
        return this;
    }

    public StackUtils<T> withGaps(double gap) {
        return withGaps(gap, gap);
    }

    public StackUtils<T> withRelativeGaps(double gap) {
        return withRelativeGaps(gap, gap);
    }


    private void resetGaps() {
        xGap = 0;
        yGap = 0;
        useRelativeGaps = false;
    }

    public StackUtils<T> withDestinyAnchor(AnchorType anchorDestiny) {
        this.anchorDestiny = anchorDestiny;
        return this;
    }

    public StackUtils<T> withOriginAnchor(AnchorType anchorObject) {
        this.anchorOrigin = anchorObject;
        return this;
    }

    private void resetAnchor() {
        anchorDestiny = null;
        anchorOrigin = null;
    }

    private void prepareAnchors() {
        if (anchorDestiny == null) anchorDestiny = AnchorType.CENTER;
        if (anchorOrigin == null) anchorOrigin = Anchor.reverseAnchorPoint(anchorDestiny);
    }

    private Vec computeGaps() {
        if (useRelativeGaps) {
            return Vec.to(parent.getWidth() * xGap, parent.getHeight() * yGap);
        } else
            return Vec.to(xGap, yGap);
    }

    public T toPoint(Coordinates<?> coords) {
        prepareAnchors();
        Vec gaps = computeGaps();
        Vec anchorPoint = Anchor.getAnchorPoint(parent, anchorOrigin, gaps.x, gaps.y, gaps.z);
        parent.shift(anchorPoint.to(coords));
        resetGaps();
        resetAnchor();
        return parent;
    }

    public T toPoint(double x, double y) {
        return toPoint(Vec.to(x, y));
    }


    public T toObject(Boxable boxable) {
        prepareAnchors();
        Vec gaps = computeGaps();
        Vec anchorPoint = Anchor.getAnchorPoint(parent, anchorOrigin, gaps.x, gaps.y, gaps.z);
        Vec coords = Anchor.getAnchorPoint(boxable, anchorDestiny);
        parent.shift(anchorPoint.to(coords));
        resetGaps();
        resetAnchor();
        return parent;
    }

    public T toScreen(ScreenAnchor screenAnchor) {
        if (anchorOrigin == null) {
            withOriginAnchor(computeAnchorOriginForAnchorScreenDestiny(screenAnchor));
        }
        withDestinyAnchor(AnchorType.CENTER);
        toObject(getScreenAnchorPoint(parent.getCamera(), screenAnchor));
        return parent;
    }

}
