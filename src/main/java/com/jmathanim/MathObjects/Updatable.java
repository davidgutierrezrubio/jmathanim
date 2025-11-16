package com.jmathanim.MathObjects;

import com.jmathanim.jmathanim.Dependable;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * Represents an object whose state can be updated based on its dependencies.
 *
 * <p>This interface is typically implemented alongside {@link Dependable},
 * so that the object can check whether its dependencies have changed
 * and recompute its internal state if necessary.</p>
 */
public interface Updatable {

    /**
     * Checks whether the object needs to be updated, usually by comparing
     * the version numbers of its dependencies with those stored internally.
     *
     * @return true if the object is out of date and requires recomputation.
     */
    boolean needsUpdate();

    /**
     * Performs the actual update, recomputing any internal data that depends
     * on other objects.
     * @return True if object needed to be updated and some changes were made
     */
    boolean update(JMathAnimScene scene);
}
