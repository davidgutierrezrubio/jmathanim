package com.jmathanim.jmathanim;
import java.util.Collections;
import java.util.List;

/**
 * Represents an object that participates in a dependency graph.
 *
 * <p>Each {@code Dependable} object can depend on other objects, forming
 * a directed acyclic graph (DAG). This allows an update system to determine
 * the correct order of evaluation or rendering.</p>
 *
 * <p>The interface also provides a versioning mechanism, where each object
 * has a numeric version that increases whenever its state changes.
 * Dependent objects can use these version numbers to determine
 * whether they need to be updated.</p>
 */
public interface Dependable {

    /**
     * Updates version of object. This method should be called when the object changes.
     */
    public void changeVersionAndMarkDirty();
    /**
     * Returns an immutable list of direct dependencies of this object.
     *
     * <p>For example:</p>
     * <ul>
     *   <li>A {@code Vec} that represents a single vector may have no dependencies,
     *       and should return {@link #EMPTY_DEPENDENCIES}.</li>
     *   <li>A {@code Segment} that depends on two {@code Vec} objects
     *       should return a list containing both.</li>
     * </ul>
     *
     * @return a list of {@code Dependable} objects that this object depends on.
     *         Must not be {@code null}. If there are no dependencies,
     *         return {@link #EMPTY_DEPENDENCIES}.
     */
    List<Dependable> getDependencies();

    void addDependency(Dependable dep);

    /**
     * Returns the current version number of this object.
     *
     * <p>Each time the object changes its internal state, it should increment
     * a global version counter and assign that new value to its own version.
     * This allows dependent objects to detect changes efficiently.</p>
     *
     * @return the current version of the object.
     */
    long getVersion();
    /**
     * Returns a human-readable name for this object, useful for debugging
     * or graph visualization purposes.
     *
     * <p>By default, this returns the simple class name.</p>
     *
     * @return the name of this object.
     */
    default String getClassName() {
        return getClass().getSimpleName();
    }

    /**
     * Shared immutable empty list that can be returned by objects with
     * no dependencies (e.g., basic vector or scalar types).
     *
     * <p>This constant avoids unnecessary list allocations for lightweight
     * objects that have no dependencies.</p>
     */
    List<Dependable> EMPTY_DEPENDENCIES = Collections.emptyList();
}
