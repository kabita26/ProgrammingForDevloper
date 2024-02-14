
//  We need to adjust the code to ensure that each 
// individual is added to the list of recipients only 
// once. 
// Output[0,1,2,3,4]
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
 
public class twob {
 
    // Method to find individuals who know the secret based on intervals and the first person
    public static List<Integer> findIndividuals(int n, int[][] intervals, int firstPerson) {
        // Array to track whether each person knows the secret
        boolean[] knowsSecret = new boolean[n];
        
        // The first person knows the secret initially
        knowsSecret[firstPerson] = true;
 
        // Iterate through each interval
        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];
 
            // Check if anyone in the current interval knows the secret
            for (int i = start; i <= end; i++) {
                if (knowsSecret[i]) {
                    // Share the secret with everyone in the interval
                    for (int j = start; j <= end; j++) {
                        knowsSecret[j] = true;
                    }
                    break;
                }
            }
        }
 
        // List to store individuals who know the secret
        List<Integer> result = new ArrayList<>();
        
        // Populate the result list with individuals who know the secret
        for (int i = 0; i < n; i++) {
            if (knowsSecret[i]) {
                result.add(i);
            }
        }
 
        return result;
    }
 
    // Main method for testing the findIndividuals method
    public static void main(String[] args) {
        // Example parameters
        int n = 5;
        int[][] intervals = {{0, 2}, {1, 3}, {2, 4}};
        int firstPerson = 0;
 
        // Find and print individuals who know the secret
        List<Integer> result = findIndividuals(n, intervals, firstPerson);
 
        System.out.println("Individuals who know the secret: " + Arrays.toString(result.toArray()));
    }
}
 
// Output: [0, 1, 2, 3, 4]
