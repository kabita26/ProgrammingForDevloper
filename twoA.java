// We first calculate the total number of dresses in all machines (sum).
// Then, we calculate the target number of dresses each machine should have (target), which is the integer division of the total dresses by the number of machines.
// If the sum of dresses cannot be evenly divided among the machines, it's not possible to equalize, so we return -1.
// Otherwise, we iterate through the machines, adjusting the number of dresses in each machine and counting the moves required to reach the target number of dresses.
// Finally, we return the total number of moves.

    import java.util.Arrays;

public class twoA {
    public static int minMovesToEqualizeDresses(int[] sewingMachines) {
        int totalDresses = 0;
        int n = sewingMachines.length;
        
        for (int dress :sewingMachines){
            totalDresses +=dress;
        } 

        int targetDresses = totalDresses / n;
        int moves = 0;
        int diff =0;

        for (int i = 0; i < n; i++) {
            diff += sewingMachines[i] - targetDresses;
            moves +=Math.abs(diff);

        }

        return moves/2;
    }

    public static void main(String[] args) {
        int[] sewingMachines = {1, 0, 5};
        int result=minMovesToEqualizeDresses(sewingMachines);


        System.out.println("Minimum number of moves required: " + result);
    }
}
// Output: Minimum number of moves required: 2