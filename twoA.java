// We first calculate the total number of dresses in all machines (sum).
// Then, we calculate the target number of dresses each machine should have (target), which is the integer division of the total dresses by the number of machines.
// If the sum of dresses cannot be evenly divided among the machines, it's not possible to equalize, so we return -1.
// Otherwise, we iterate through the machines, adjusting the number of dresses in each machine and counting the moves required to reach the target number of dresses.
// Finally, we return the total number of moves.

    import java.util.Arrays;

public class twoA {
    public static int minMovesToEqualizeDresses(int[] sewingMachines) {
        int totalDresses = Arrays.stream(sewingMachines).sum();
        int n = sewingMachines.length;
        
        if (totalDresses % n != 0) {
            return -1; // Cannot equalize the number of dresses
        }

        int targetDresses = totalDresses / n;
        int moves = 0;

        for (int i = 0; i < n; i++) {
            int diff = sewingMachines[i] - targetDresses;

            if (diff > 0) {
                sewingMachines[(i + 1) % n] += diff; // Distribute excess dresses to the next machine cyclically
                moves += diff;
            }
        }

        return moves;
    }

    public static void main(String[] args) {
        int[] sewingMachines = {1, 0, 5};
        System.out.println("Minimum number of moves required: " + minMovesToEqualizeDresses(sewingMachines));
    }
}
// Output: 3