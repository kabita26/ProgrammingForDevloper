// We create an adjacency list representation of the network connections using a HashMap where the key is the device number
// , and the value is a list of connected devices.

// We perform a depth-first search (DFS) starting from the target device. As we traverse the network connections, we add each 
// visited device to the list of impacted devices.

// The DFS continues recursively until all connected devices are visited.

// Finally, we return the list of impacted devices.

// This algorithm efficiently identifies all devices that are directly or indirectly connected to the 
// target device, providing valuable information for notifying affected consumers of a power outage.
import java.util.*;

public class fiveB {
    
    // Method to find impacted devices given network connections and a target device
    public static List<Integer> findImpactedDevices(int[][] edges, int targetDevice) {
        // Create an adjacency list representation of the network connections
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            // Add bidirectional connections
            graph.putIfAbsent(u, new ArrayList<>());
            graph.putIfAbsent(v, new ArrayList<>());
            graph.get(u).add(v);
            graph.get(v).add(u);
        }

        // Perform Depth-First Search (DFS) to find all impacted devices
        Set<Integer> visited = new HashSet<>();
        List<Integer> impactedDevices = new ArrayList<>();
        dfs(graph, targetDevice, visited, impactedDevices);

        // Remove the target device itself from the list of impacted devices
        impactedDevices.remove((Integer) targetDevice);

        return impactedDevices;
    }

    // Helper method for Depth-First Search (DFS)
    private static void dfs(Map<Integer, List<Integer>> graph, int device, Set<Integer> visited, List<Integer> impactedDevices) {
        visited.add(device);
        impactedDevices.add(device); // Add the current device to the list of impacted devices

        // Explore neighbors of the current device
        if (graph.containsKey(device)) {
            for (int neighbor : graph.get(device)) {
                if (!visited.contains(neighbor)) {
                    dfs(graph, neighbor, visited, impactedDevices);
                }
            }
        }
    }

    // Main method to test the implementation
    public static void main(String[] args) {
        // Test data
        int[][] edges = {{0, 1}, {0, 2}, {1, 3}, {1, 6}, {2, 4}, {4, 6}, {4, 5}, {5, 7}};
        int targetDevice = 4;

        // Find impacted devices and print the result
        List<Integer> impactedDevices = findImpactedDevices(edges, targetDevice);
        System.out.println("Impacted Device List: " + impactedDevices);
    }
}

    // Output (Impacted Device List) = {5,7}
    // Impacted Device List: [2, 0, 1, 3, 6, 5, 7]