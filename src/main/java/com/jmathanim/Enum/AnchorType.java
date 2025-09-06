package com.jmathanim.Enum;


public enum AnchorType {
    /**
     * Anchor is specified by a given point
     */
    BY_POINT,
    /**
     * Anchor determined by the center of the object
     */
    CENTER,
    /**
     * Right anchor point. Vertically centered.
     */
    RIGHT,
    /**
     * Upper anchor point. Horizontally centered
     */
    UPPER,
    /**
     * Left anchor point. Vertically centered.
     */
    LEFT,
    /**
     * Lower anchor point. Horizontally centered
     */
    LOWER,
    /**
     * Down-Right anchor point
     */
    RIGHT_AND_ALIGNED_LOWER,

    /**
     * Up-Right anchor point
     */
    RIGHT_AND_ALIGNED_UPPER,
    /**
     * Up-Left anchor point
     */
    LEFT_AND_ALIGNED_UPPER,
    /**
     * Down-Left anchor point
     */
    LEFT_AND_ALIGNED_LOWER,
    LOWER_AND_ALIGNED_RIGHT,
    UPPER_AND_ALIGNED_RIGHT,
    LOWER_AND_ALIGNED_LEFT,
    UPPER_AND_ALIGNED_LEFT,

    /**
     * Diagonal first quadrant (45 degrees)
     */
    DIAG1,
    /**
     * Diagonal second quadrant (135 degrees)
     */
    DIAG2,

    /**
     * Diagonal third quadrant (225 degrees)
     */
    DIAG3,

    /**
     * Diagonal third quadrant (315 degrees)
     */
    DIAG4,
    ZTOP,
    ZBOTTOM
}