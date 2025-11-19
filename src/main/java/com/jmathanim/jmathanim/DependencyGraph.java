package com.jmathanim.jmathanim;

import com.jmathanim.MathObjects.Updatable;

import java.util.*;

/**
 * Represents a directed acyclic dependency graph for {@link Dependable} objects.
 * It maintains the correct topological order for update propagation.
 */
public class DependencyGraph {

    private final Set<Dependable> nodes = new LinkedHashSet<>();
    private final Map<Dependable, List<Dependable>> dependencies = new HashMap<>();
    private final Map<Dependable, List<Dependable>> reverseDeps = new HashMap<>();
    private final List<Dependable> topoOrder = new ArrayList<>();
    private boolean dirty;

    public DependencyGraph() {
        this.dirty = false;
    }

    /**
     * Depth-first search for topological sorting (post-order).
     */
    private static void dfsTopo(Dependable node,
                                Map<Dependable, List<Dependable>> deps,
                                Set<Dependable> visited,
                                Set<Dependable> temp,
                                List<Dependable> result) {
        if (visited.contains(node)) return;
        if (temp.contains(node))
            throw new IllegalStateException("Cycle detected in dependencies: " + node);

        temp.add(node);
        for (Dependable dep : deps.getOrDefault(node, Collections.emptyList())) {
            dfsTopo(dep, deps, visited, temp, result);
        }
        temp.remove(node);
        visited.add(node);
        result.add(node);
    }

    /**
     * Adds a node and all its dependencies recursively.
     * Marks the graph as dirty, requiring resorting.
     *
     * @param node the node to add
     */
    public void addNode(Dependable node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
            for (Dependable dep : node.getDependencies()) {
                addNode(dep);
                dependencies.computeIfAbsent(node, k -> new ArrayList<>()).add(dep);
                reverseDeps.computeIfAbsent(dep, k -> new ArrayList<>()).add(node);
            }
            dirty = true;
        }
    }

    /**
     * Adds multiple nodes to the graph without sorting immediately.
     *
     * @param newNodes collection of nodes to add
     */
    public void addNodes(Collection<? extends Dependable> newNodes) {
        for (Dependable node : newNodes) {
            addNode(node);
        }
        dirty = true;
    }

    /**
     * Removes a node and all edges referencing it.
     *
     * @param node the node to remove
     */
    public void removeNode(Dependable node) {
        nodes.remove(node);
        dependencies.remove(node);
        reverseDeps.remove(node);
        dependencies.values().forEach(list -> list.remove(node));
        reverseDeps.values().forEach(list -> list.remove(node));
        dirty = true;
    }

    /**
     * Performs a full topological sort if the graph is dirty.
     * This ensures dependencies are processed before dependents.
     */
    public void sort() {
        if (!dirty) return;
        topoOrder.clear();
        topoOrder.addAll(topologicalSort());
        dirty = false;
    }

    /**
     * Computes a full topological ordering of the graph nodes.
     *
     * @return a list of nodes in topological order
     */
    private List<Dependable> topologicalSort() {
        List<Dependable> result = new ArrayList<>();
        Set<Dependable> visited = new HashSet<>();
        Set<Dependable> temp = new HashSet<>();
        for (Dependable node : nodes) {
            dfsTopo(node, dependencies, visited, temp, result);
        }
        return result;
    }

    /**
     * Updates all updatable nodes in topological order.
     * Each node's dependencies are guaranteed to be updated first.
     */
    public void updateAll() {
        if (dirty) sort();
        for (Dependable dep : topoOrder) {
            if (dep instanceof Updatable) {
                Updatable dep1 = (Updatable) dep;
                dep1.update();

            }
        }
    }

    /**
     * Returns the list of nodes in current topological order.
     * The graph is automatically sorted if marked dirty.
     *
     * @return ordered list of nodes
     */
    public List<Dependable> getTopologicalOrder() {
        if (dirty) sort();
        return Collections.unmodifiableList(topoOrder);
    }
}
