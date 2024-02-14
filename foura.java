import java.util.*;

public class foura {
    public static void main(String[] args) {
        // Example input grid
        char[][] grid = {
            {'S', 'P', 'q', 'P', 'P'},
            {'W', 'W', 'W', 'P', 'W'},
            {'r', 'P', 'Q', 'P', 'R'}
        };
        
        // Call the method to find the minimum moves required to collect all keys
        int minMoves = minMovesToCollectKeys(grid);
        System.out.println("Minimum moves required to collect all keys: " + minMoves);
    }

    public static int minMovesToCollectKeys(char[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        
        // Variables to track starting point, keys, and collected status
        int[] start = null;
        int keyCount = 0;
        int[][] keys = new int[26][2]; // Assuming maximum of 26 keys (a-z)
        boolean[] collected = new boolean[26];
        
        // Iterate through the grid to find starting point and keys
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                char cell = grid[i][j];
                if (cell == 'S') {
                    start = new int[]{i, j}; // Record starting point
                } else if (cell >= 'a' && cell <= 'z') {
                    int index = cell - 'a';
                    keys[index] = new int[]{i, j}; // Record key position
                    keyCount++;
                }
            }
        }
        
        if (keyCount == 0) return 0; // No keys to collect
        
        // Directions for movement: up, down, left, right
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        // Queue for breadth-first search
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(start); // Start from the starting point
        int steps = 0; // Initialize steps counter
        
        // Breadth-first search
        while (!queue.isEmpty()) {
            int size = queue.size();
            
            // Process all cells in the current level
            for (int i = 0; i < size; i++) {
                int[] curr = queue.poll();
                int x = curr[0]; // Current row
                int y = curr[1]; // Current column
                
                // Explore all four directions
                for (int[] dir : dirs) {
                    int newX = x + dir[0]; // New row after movement
                    int newY = y + dir[1]; // New column after movement
                    
                    // Check if new position is within bounds and not a wall
                    if (newX >= 0 && newX < m && newY >= 0 && newY < n && grid[newX][newY] != 'W') {
                        char cell = grid[newX][newY];
                        
                        // If the cell is a path or the starting point, continue exploring
                        if (cell == 'P' || cell == 'S') {
                            queue.offer(new int[]{newX, newY});
                        } 
                        // If the cell contains a key, collect it and mark the cell as visited
                        else if (cell >= 'a' && cell <= 'z') {
                            int index = cell - 'a'; // Index of the key
                            collected[index] = true; // Mark key as collected
                            keyCount--; // Decrement key count
                            grid[newX][newY] = 'P'; // Mark the cell as visited
                            queue.offer(new int[]{newX, newY});
                        } 
                        // If the cell contains a locked door, check if the corresponding key is collected
                        else if (cell >= 'A' && cell <= 'Z') {
                            int index = cell - 'A'; // Index of the door
                            if (collected[index]) {
                                grid[newX][newY] = 'P'; // Mark the cell as visited
                                queue.offer(new int[]{newX, newY});
                            }
                        }
                    }
                }
            }
            
            steps++; // Increment steps after exploring a level
            
            // If all keys are collected, return the number of steps
            if (keyCount == 0) return steps;
        }
        
        // If not all keys can be collected, return -1
        return -1;
    }
}

// Minimum moves required to collect all keys: 8