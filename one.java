public class one {

    public static int minCost(int[][] costs) {
        if (costs == null || costs.length == 0 || costs[0].length == 0) {
            return 0;
        }

        int n = costs.length;
        int k = costs[0].length;

        // dp[i][j] represents the minimum cost of decorating venues up to index i with venue i decorated with theme j
        int[][] dp = new int[n][k];

        // Initialize the first row of dp with costs of decorating the first venue
        for (int j = 0; j < k; j++) {
            dp[0][j] = costs[0][j];
        }

        // Iterate through venues starting from the second one
        for (int i = 1; i < n; i++) {
            // Iterate through themes for the current venue
            for (int j = 0; j < k; j++) {
                // Initialize minCost with the maximum possible value
                int minCost = Integer.MAX_VALUE;
                // Iterate through themes for the previous venue
                for (int prevTheme = 0; prevTheme < k; prevTheme++) {
                    // If the previous venue was not decorated with the same theme as the current one
                    if (prevTheme != j) {
                        // Update minCost with the minimum between the current minCost and the cost of decorating
                        // the previous venue with the previous theme plus the cost of decorating the current venue
                        // with the current theme
                        minCost = Math.min(minCost, dp[i - 1][prevTheme] + costs[i][j]);
                    }
                }
                // Assign the minimum cost to dp[i][j]
                dp[i][j] = minCost;
            }
        }

        // Find the minimum cost from the last row of dp
        int minCost = Integer.MAX_VALUE;
        for (int j = 0; j < k; j++) {
            minCost = Math.min(minCost, dp[n - 1][j]);
        }

        return minCost;
    }

    public static void main(String[] args) {
        int[][] costs = {{1, 3, 2}, {4, 6, 8}, {3, 1, 5}};
        System.out.println("Minimum cost: " + minCost(costs)); // Output: 7
    }
}
