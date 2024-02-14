// code implements Kruskal's algorithm for finding 
// the minimum spanning tree (MST) of a graph. It 
// uses a priority queue to sort the edges based on 
// their weights, and a disjoint set data structure
//  to efficiently check for cycles while adding edges 
// to the MST.
import java.util.*;

class Edge implements Comparable<Edge> {
    int source, destination, weight;

    public Edge(int source, int destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }
}

class DisjointSet {
    int[] parent, rank;

    public DisjointSet(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

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

public class threeb {

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
