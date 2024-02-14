
import java.util.*;

// Definition for binary tree node.
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}

public class fourb {
    public static List<Integer> closestValues(TreeNode root, double target, int x) {
        List<Integer> result = new ArrayList<>();
        PriorityQueue<Integer> pq = new PriorityQueue<>(x, (a, b) -> Double.compare(Math.abs(b - target), Math.abs(a - target)));

        // Perform in-order traversal to visit all nodes in the BST
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            pq.offer(current.val);

            // If the size of the priority queue exceeds x, remove the element with the farthest distance
            if (pq.size() > x) {
                pq.poll();
            }

            current = current.right;
        }

        // Add the elements from the priority queue to the result list
        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }

        return result;
    }

    public static void main(String[] args) {
        // Construct the binary search tree
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);

        double target = 3.8;
        int x = 2;

        // Find x closest values to the target
        List<Integer> closest = closestValues(root, target, x);
        System.out.println("Closest values to " + target + ": " + closest);
    }
}
// Closest values to 3.8: [3, 4]