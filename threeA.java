
// // Output: 88.9 (average of 90.1 and 85.5)
// / Output: 86.95 (average of 88.7 and 85.5)

import java.util.PriorityQueue;

public class threeA {
    private PriorityQueue<Double> minHeap; // for the second half of scores
    private PriorityQueue<Double> maxHeap; // for the first half of scores

    public threeA() {
        minHeap = new PriorityQueue<>();
        maxHeap = new PriorityQueue<>((a, b) -> Double.compare(b, a)); // max heap comparator
    }

    public void addScore(double score) {
        maxHeap.offer(score); // Add to max heap first

        // Balance the heaps
        minHeap.offer(maxHeap.poll());
        if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    public double getMedianScore() {
        if (maxHeap.isEmpty()) {
            throw new IllegalStateException("No scores available.");
        }

        if (maxHeap.size() == minHeap.size()) {
            // If the number of scores is even, return the average of the two middle scores
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        } else {
            // If the number of scores is odd, return the middle score
            return maxHeap.peek();
        }
    }

    public static void main(String[] args) {
        threeA scoreTracker = new threeA();
        scoreTracker.addScore(85.5);
        scoreTracker.addScore(92.3);
        scoreTracker.addScore(77.8);
        scoreTracker.addScore(90.1);
        double median1 = scoreTracker.getMedianScore();
        System.out.println("Median 1: " + median1); // Output: 88.9

        scoreTracker.addScore(81.2);
        scoreTracker.addScore(88.7);
        double median2 = scoreTracker.getMedianScore();
        System.out.println("Median 2: " + median2); // Output: 86.95
    }
}
// Median 1: 87.8
// Median 2: 87.1