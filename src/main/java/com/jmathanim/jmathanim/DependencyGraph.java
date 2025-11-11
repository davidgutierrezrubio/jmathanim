package com.jmathanim.jmathanim;

import com.jmathanim.MathObjects.Updatable;

import java.util.*;

public class DependencyGraph {

    private final Set<Dependable> nodes = new LinkedHashSet<>();
    private final Map<Dependable, List<Dependable>> dependencies = new HashMap<>();
    private final Map<Dependable, List<Dependable>> reverseDeps = new HashMap<>();
    private final List<Dependable> topoOrder = new ArrayList<>();
    private long globalVersionCounter = 0L;

    /**
     * DFS post-order para garantizar que las dependencias aparecen antes que el nodo
     */
    private static void dfsTopo(Dependable node,
                                Map<Dependable, List<Dependable>> deps,
                                Set<Dependable> visited,
                                Set<Dependable> temp,
                                List<Dependable> result) {
        if (visited.contains(node)) return;
        if (temp.contains(node))
            throw new IllegalStateException("Ciclo detectado en dependencias: " + node);

        temp.add(node);
        for (Dependable dep : deps.getOrDefault(node, Collections.emptyList())) {
            dfsTopo(dep, deps, visited, temp, result);
        }
        temp.remove(node);
        visited.add(node);
        result.add(node);
    }

    /**
     * Añade un solo nodo al grafo, insertando también sus dependencias recursivamente.
     */
    public void addNode(Dependable node) {
//        if (node instanceof AbstractMathGroup) {
//            for (Dependable dep : (AbstractMathGroup<?>) node) {
//                addNode(dep);
//            }
//            return;
//        }

        if (nodes.contains(node)) return;
        for (Dependable dep : node.getDependencies()) {
            addNode(dep);
            reverseDeps.computeIfAbsent(dep, k -> new ArrayList<>()).add(node);
        }
        nodes.add(node);
        dependencies.put(node, new ArrayList<>(node.getDependencies()));
        topoOrder.add(node); // inserción incremental simple
    }

    private void addSingleNode(Dependable node) {
//        if (node instanceof AbstractMathGroup) {
//            for (Dependable dep : (AbstractMathGroup<?>) node) {
//                addSingleNode(dep);
//            }
//            return;
//        }
        nodes.add(node);
        dependencies.put(node, new ArrayList<>(node.getDependencies()));
        for (Dependable dep : node.getDependencies()) {
            reverseDeps.computeIfAbsent(dep, k -> new ArrayList<>()).add(node);
        }
    }


    /**
     * Añade varios nodos al grafo de forma eficiente y garantiza que el orden topológico sea correcto.
     */
    public void addNodes(Collection<Dependable> newNodes) {
        // 1️⃣ Añadir nodos y dependencias al grafo sin preocuparse aún del orden
        Set<Dependable> allNew = new LinkedHashSet<>();
        for (Dependable node : newNodes) {
            collectDependenciesRecursive(node, allNew);
        }
        for (Dependable node : allNew) {
            if (!nodes.contains(node)) {
                addSingleNode(node);
            }
        }

        // 2️⃣ Recalcular orden topológico para todos los nodos nuevos
        List<Dependable> newTopo = new ArrayList<>();
        Set<Dependable> visited = new HashSet<>();
        for (Dependable node : allNew) {
            dsfTopHandle(node, visited, newTopo);
        }

        // 3️⃣ Añadir al final del topoOrder sin duplicados
        for (Dependable node : newTopo) {
            if (!topoOrder.contains(node)) {
                topoOrder.add(node);
            }
        }
    }

    private void dsfTopHandle(Dependable node, Set<Dependable> visited, List<Dependable> newTopo) {
//        if (node instanceof AbstractMathGroup) {
//            for (Dependable nodeDep : (AbstractMathGroup<?>) node) {
//                dfsTopo(nodeDep, dependencies, visited, new HashSet<>(), newTopo);
//            }
//            return;
//        }
        dfsTopo(node, dependencies, visited, new HashSet<>(), newTopo);
    }


    /**
     * Recorre recursivamente dependencias de un nodo y las añade a 'result'.
     */
    private void collectDependenciesRecursive(Dependable node, Set<Dependable> result) {
        if (!result.add(node)) return;
        for (Dependable dep : node.getDependencies()) {
            collectDependenciesRecursive(dep, result);
        }
    }

    /**
     * Actualiza todos los nodos en orden topológico
     */
    public void updateAll() {
        for (Dependable dep : topoOrder) {
            if (dep instanceof Updatable) {
                Updatable updatable = (Updatable) dep;
//                long ver=((Dependable)updatable).getVersion();
                updatable.update(JMathAnimConfig.getConfig().getScene());
//                long ver2=((Dependable)updatable).getVersion();
//                if (ver!=ver2)
//                System.out.println("Updating "+ LogUtils.method(updatable.toString())+" version "+LogUtils.number(ver,0)+" to "+LogUtils.number(ver2,0));
            }
        }
    }

    public long getGlobalVersion() {
        return globalVersionCounter;
    }

    public List<Dependable> getTopologicalOrder() {
        return topoOrder;
    }
}
