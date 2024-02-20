// code implements Kruskal's algorithm for finding 
// the minimum spanning tree (MST) of a graph. It 
// uses a priority queue to sort the edges based on 
// their weights, and a disjoint set data structure
//  to efficiently check for cycles while adding edges 
// to the MST.
import java.util.*;

/**
 * Represents an edge in a graph.
 */
class Edge implements Comparable<Edge> {
    int source, destination, weight;

    /**
     * Constructs an Edge object.
     *
     * @param source      The source vertex of the edge.
     * @param destination The destination vertex of the edge.
     * @param weight      The weight of the edge.
     */
    public Edge(int source, int destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    /**
     * Compares this edge with another edge based on their weights.
     *
     * @param other The other edge to compare with.
     * @return A negative integer, zero, or a positive integer if this edge is less than, equal to, or greater than the other edge.
     */
    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }
}

/**
 * Represents a disjoint set data structure.
 */
class DisjointSet {
    int[] parent, rank;

    /**
     * Constructs a DisjointSet object.
     *
     * @param size The size of the disjoint set.
     */
    public DisjointSet(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    /**
     * Finds the root of the set containing the specified element.
     *
     * @param x The element to find.
     * @return The root of the set containing the element.
     */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    /**
     * Unions two sets by rank.
     *
     * @param x The representative of the first set.
     * @param y The representative of the second set.
     */
    public void union(int x, int y) {
        int xRoot = find(x);
        int yRoot = find(y);
        if (xRoot == yRoot) return;

        if (rank[xRoot] < rank[yRoot]) {
            parent[xRoot] = yRoot;
        } else if (rank[xRoot] > rank[yRoot]) {
            parent[yRoot] = xRoot;
        } else {
            parent[yRoot] = xRoot;
            rank[xRoot]++;
        }
    }
}

/**
 * Implements Kruskal's algorithm for finding the minimum spanning tree (MST) of a graph.
 */
public class threeb {

    /**
     * Finds the minimum spanning tree (MST) of a graph using Kruskal's algorithm.
     *
     * @param edges    The list of edges in the graph.
     * @param vertices The number of vertices in the graph.
     * @return The list of edges in the minimum spanning tree.
     */
    public static List<Edge> kruskalMST(List<Edge> edges, int vertices) {
        List<Edge> minimumSpanningTree = new ArrayList<>();
        DisjointSet disjointSet = new DisjointSet(vertices);

        Collections.sort(edges); // Sort edges by weight

        for (Edge edge : edges) {
            int sourceRoot = disjointSet.find(edge.source);
            int destinationRoot = disjointSet.find(edge.destination);

            if (sourceRoot != destinationRoot) {
                minimumSpanningTree.add(edge);
                disjointSet.union(sourceRoot, destinationRoot);
            }
        }

        return minimumSpanningTree;
    }

    /**
     * Main method to demonstrate Kruskal's algorithm.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        int vertices = 4;
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, 10));
        edges.add(new Edge(0, 2, 6));
        edges.add(new Edge(0, 3, 5));
        edges.add(new Edge(1, 3, 15));
        edges.add(new Edge(2, 3, 4));

        List<Edge> minimumSpanningTree = kruskalMST(edges, vertices);

        System.out.println("Minimum Spanning Tree Edges:");
        for (Edge edge : minimumSpanningTree) {
            System.out.println(edge.source + " - " + edge.destination + " : " + edge.weight);
        }
    }
}

