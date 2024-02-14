// Output: 4
import java.util.*;

public class oneb {
    public static int minTimeToBuildEngines(int[] engines, int splitCost) {
        Arrays.sort(engines);
        int totalEngines = engines.length;
        int totalCost = 0;

        while (totalEngines > 1) {
            // Split one engineer into two
            totalCost += splitCost;

            // Remove the two engines with the highest build times
            int firstEngineTime = engines[totalEngines - 1];
            int secondEngineTime = engines[totalEngines - 2];
            totalEngines -= 2;

            // Add the new engine build time to the list
            engines[totalEngines] = Math.max(firstEngineTime, secondEngineTime) + splitCost; // Both engineers work in parallel
        }

        return totalCost + engines[0]; // Add the time for the last engine to be built
    }

    public static void main(String[] args) {
        int[] engines = {1, 2, 3};
        int splitCost = 1;
        System.out.println("Minimum time needed to build all engines: " + minTimeToBuildEngines(engines, splitCost));
    }
}
// Minimum time needed to build all engines:  2