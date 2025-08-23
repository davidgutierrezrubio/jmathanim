package com.jmathanim.mathobjects;

import com.jmathanim.Utils.AffineJTransform;

public interface AffineTransformable <T extends AffineTransformable<T>> {
    T applyAffineTransform(AffineJTransform tr);
}
