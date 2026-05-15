package casinoescape.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyGraphTest {
    @Test
    void newGraphIsEmpty() {
        MyGraph<String> graph = new MyGraph<>();

        assertEquals(0, graph.size());
    }

    @Test
    void addNodeAddsOnlyNewNodes() {
        MyGraph<String> graph = new MyGraph<>();

        graph.addNode("one");
        graph.addNode("one");

        assertEquals(1, graph.size());
        assertTrue(graph.containsNode("one"));
    }

    @Test
    void addUndirectedEdgeConnectsBothDirections() {
        MyGraph<String> graph = new MyGraph<>();
        graph.addNode("one");
        graph.addNode("two");

        graph.addUndirectedEdge("one", "two");

        assertTrue(graph.areConnected("one", "two"));
        assertTrue(graph.areConnected("two", "one"));
    }

    @Test
    void duplicateEdgesAreIgnored() {
        MyGraph<String> graph = new MyGraph<>();
        graph.addNode("one");
        graph.addNode("two");

        graph.addUndirectedEdge("one", "two");
        graph.addUndirectedEdge("one", "two");

        assertEquals(1, graph.getNeighbors("one").size());
        assertEquals(1, graph.getNeighbors("two").size());
    }

    @Test
    void getNeighborsReturnsConnectedNodes() {
        MyGraph<String> graph = new MyGraph<>();
        graph.addNode("one");
        graph.addNode("two");
        graph.addNode("three");
        graph.addUndirectedEdge("one", "two");
        graph.addUndirectedEdge("one", "three");

        MyLinkedList<String> neighbors = graph.getNeighbors("one");

        assertEquals(2, neighbors.size());
        assertTrue(neighbors.contains("two"));
        assertTrue(neighbors.contains("three"));
    }

    @Test
    void shortestPathFindsMinimumPath() {
        MyGraph<Integer> graph = casinoGraph();

        MyLinkedList<Integer> path = graph.shortestPath(1, 8);

        assertEquals(5, path.size());
        assertEquals(1, path.get(0));
        assertEquals(2, path.get(1));
        assertEquals(5, path.get(2));
        assertEquals(7, path.get(3));
        assertEquals(8, path.get(4));
    }

    @Test
    void shortestDistanceCountsEdges() {
        MyGraph<Integer> graph = casinoGraph();

        assertEquals(4, graph.shortestDistance(1, 8));
        assertEquals(0, graph.shortestDistance(3, 3));
    }

    @Test
    void disconnectedNodesReturnNoPath() {
        MyGraph<String> graph = new MyGraph<>();
        graph.addNode("one");
        graph.addNode("two");

        MyLinkedList<String> path = graph.shortestPath("one", "two");

        assertTrue(path.isEmpty());
        assertEquals(-1, graph.shortestDistance("one", "two"));
    }

    @Test
    void missingNodeThrowsException() {
        MyGraph<String> graph = new MyGraph<>();
        graph.addNode("one");

        assertThrows(IllegalArgumentException.class, () -> graph.addUndirectedEdge("one", "two"));
        assertThrows(IllegalArgumentException.class, () -> graph.getNeighbors("two"));
        assertThrows(IllegalArgumentException.class, () -> graph.shortestPath("one", "two"));
    }

    @Test
    void nullValuesAreRejected() {
        MyGraph<String> graph = new MyGraph<>();

        assertThrows(IllegalArgumentException.class, () -> graph.addNode(null));
    }

    @Test
    void areConnectedReturnsFalseWhenNoEdgeExists() {
        MyGraph<String> graph = new MyGraph<>();
        graph.addNode("one");
        graph.addNode("two");

        assertFalse(graph.areConnected("one", "two"));
    }

    private MyGraph<Integer> casinoGraph() {
        MyGraph<Integer> graph = new MyGraph<>();
        for (int room = 1; room <= 8; room++) {
            graph.addNode(room);
        }
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(1, 4);
        graph.addUndirectedEdge(2, 3);
        graph.addUndirectedEdge(2, 5);
        graph.addUndirectedEdge(4, 5);
        graph.addUndirectedEdge(4, 6);
        graph.addUndirectedEdge(5, 6);
        graph.addUndirectedEdge(5, 7);
        graph.addUndirectedEdge(7, 8);
        return graph;
    }
}
