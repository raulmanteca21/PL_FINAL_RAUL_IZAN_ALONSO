package casinoescape.structures;

public class MyGraph<T> {
    private final MyLinkedList<GraphNode<T>> nodes = new MyLinkedList<>();

    public void addNode(T value) {
        requireValue(value);
        if (!containsNode(value)) {
            nodes.add(new GraphNode<>(value));
        }
    }

    public void addUndirectedEdge(T from, T to) {
        requireValue(from);
        requireValue(to);
        GraphNode<T> fromNode = findNodeOrFail(from);
        GraphNode<T> toNode = findNodeOrFail(to);
        addNeighborIfMissing(fromNode, toNode.value);
        addNeighborIfMissing(toNode, fromNode.value);
    }

    public boolean containsNode(T value) {
        requireValue(value);
        return findNode(value) != null;
    }

    public boolean areConnected(T from, T to) {
        requireValue(from);
        requireValue(to);
        GraphNode<T> fromNode = findNodeOrFail(from);
        findNodeOrFail(to);
        return fromNode.neighbors.contains(to);
    }

    public MyLinkedList<T> getNeighbors(T value) {
        GraphNode<T> node = findNodeOrFail(value);
        MyLinkedList<T> copy = new MyLinkedList<>();
        for (int i = 0; i < node.neighbors.size(); i++) {
            copy.add(node.neighbors.get(i));
        }
        return copy;
    }

    public int size() {
        return nodes.size();
    }

    public MyLinkedList<T> shortestPath(T start, T goal) {
        requireValue(start);
        requireValue(goal);
        findNodeOrFail(start);
        findNodeOrFail(goal);

        MyQueue<PathNode<T>> pending = new MyQueue<>();
        MyLinkedList<T> visited = new MyLinkedList<>();
        pending.enqueue(new PathNode<>(start, null));
        visited.add(start);

        while (!pending.isEmpty()) {
            PathNode<T> current = pending.dequeue();
            if (current.value.equals(goal)) {
                return buildPath(current);
            }

            MyLinkedList<T> neighbors = getNeighbors(current.value);
            for (int i = 0; i < neighbors.size(); i++) {
                T neighbor = neighbors.get(i);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    pending.enqueue(new PathNode<>(neighbor, current));
                }
            }
        }

        return new MyLinkedList<>();
    }

    public int shortestDistance(T start, T goal) {
        MyLinkedList<T> path = shortestPath(start, goal);
        if (path.isEmpty()) {
            return -1;
        }
        return path.size() - 1;
    }

    private MyLinkedList<T> buildPath(PathNode<T> end) {
        MyStack<T> reversed = new MyStack<>();
        PathNode<T> current = end;
        while (current != null) {
            reversed.push(current.value);
            current = current.previous;
        }

        MyLinkedList<T> path = new MyLinkedList<>();
        while (!reversed.isEmpty()) {
            path.add(reversed.pop());
        }
        return path;
    }

    private void addNeighborIfMissing(GraphNode<T> node, T value) {
        if (!node.neighbors.contains(value)) {
            node.neighbors.add(value);
        }
    }

    private GraphNode<T> findNodeOrFail(T value) {
        GraphNode<T> node = findNode(value);
        if (node == null) {
            throw new IllegalArgumentException("Graph node does not exist: " + value);
        }
        return node;
    }

    private GraphNode<T> findNode(T value) {
        for (int i = 0; i < nodes.size(); i++) {
            GraphNode<T> node = nodes.get(i);
            if (node.value.equals(value)) {
                return node;
            }
        }
        return null;
    }

    private void requireValue(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Null values are not supported");
        }
    }

    private static class GraphNode<T> {
        private final T value;
        private final MyLinkedList<T> neighbors = new MyLinkedList<>();

        private GraphNode(T value) {
            this.value = value;
        }
    }

    private static class PathNode<T> {
        private final T value;
        private final PathNode<T> previous;

        private PathNode(T value, PathNode<T> previous) {
            this.value = value;
            this.previous = previous;
        }
    }
}
