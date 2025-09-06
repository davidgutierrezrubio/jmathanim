package com.jmathanim.Enum;

public enum JumpType {
    /**
     * A semicircular jump. This jump has a fixed height depending on the
     * diameter of the semicircle. The jumpheight parameter only determines
     * the direction by its sign
     */
    SEMICIRCLE,
    /**
     * A parabolical jump path
     */
    PARABOLICAL,
    /**
     * A elliptical jump path, with the jumpHeight given
     */
    ELLIPTICAL,
    /**
     * A path resembling a triangular roof
     */
    TRIANGULAR,
    /**
     * A path with the shape of Descartes Folium
     */
    FOLIUM,
    /**
     * A path with a sin(t) form from 0 to PI
     */
    SINUSOIDAL,
    /**
     * A path with a sin(t) form from 0 to 2PI
     */
    SINUSOIDAL2,
    /**
     * A path resembling a crane taking an object, following a rectangular
     * path
     */
    CRANE,
    /**
     * A parabolical path with a single bounce
     */
    BOUNCE1,
    /**
     * A parabolical path with a double bounce
     */
    BOUNCE2
}