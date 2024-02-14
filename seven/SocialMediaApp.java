package seven;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.List;

public class SocialMediaApp extends JFrame {
    private String loggedInUsername;
    private java.util.List<String> suggestedFriends;
    private java.util.List<String> friendRequests;
    private java.util.List<String> allUsers;

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/your_database";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "123456";
    private Connection connection;
    private JLabel imageLabel;

    public SocialMediaApp() {
        setTitle("Social Media Application");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        allUsers = new ArrayList<>(); // Initialize the list
        friendRequests = new ArrayList<>(); // Initialize friendRequests list

        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JButton loginButton = new JButton("Login");
        JLabel createAccountLabel = new JLabel("Don't have an account?");
        JButton createAccountButton = new JButton("Create Account");

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel()); // Empty cell
        loginPanel.add(loginButton);
        loginPanel.add(createAccountLabel);
        loginPanel.add(createAccountButton);

        add(loginPanel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle login button click event
                String username = usernameField.getText();
                if (username != null && !username.isEmpty()) {
                    loggedInUsername = username;
                    showHomePage();
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a username.");
                }
            }
        });

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle create account button click event
                String username = JOptionPane.showInputDialog("Enter username:");
                if (username != null && !username.isEmpty()) {
                    // Show message for successful account creation
                    JOptionPane.showMessageDialog(null, "Account created successfully for " + username);

                    // Add the new user to the list of all users
                    allUsers.add(username);

                    // Add user to the database
                    addUserToDatabase(username);

                    // Navigate to home page
                    loggedInUsername = username;
                    showHomePage();
                }
            }
        });
    }

    private void showHomePage() {
        // Create and display the home page
        JFrame homePage = new JFrame("Home Page");
        homePage.setSize(600, 400);
        homePage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add components to the home page
        JPanel homePanel = new JPanel(new BorderLayout());
        JButton logoutButton = new JButton("Logout");
        JLabel welcomeLabel = new JLabel("Welcome to the Home Page, " + loggedInUsername);
        JButton suggestFriendsButton = new JButton("Suggest Friends");
        JButton sendFriendRequestButton = new JButton("Send Friend Request");
        JButton showRequestsButton = new JButton("Show Friend Requests"); // New button to show friend requests
        JButton recommendContentButton = new JButton("Recommend Content"); // Button to recommend content
        JButton uploadImageButton = new JButton("Upload Image"); // Button to upload image
        imageLabel = new JLabel(); // Label to display uploaded image

        homePanel.add(logoutButton, BorderLayout.NORTH);
        homePanel.add(welcomeLabel, BorderLayout.CENTER);
        homePanel.add(uploadImageButton, BorderLayout.WEST); // Add the button to upload image
        homePanel.add(imageLabel, BorderLayout.CENTER); // Add the label to display image
        homePanel.add(suggestFriendsButton, BorderLayout.EAST);
        homePanel.add(sendFriendRequestButton, BorderLayout.WEST);
        homePanel.add(showRequestsButton, BorderLayout.SOUTH); // Add the button to the bottom of the panel
        homePanel.add(recommendContentButton, BorderLayout.SOUTH); // Add the button to recommend content

        homePage.add(homePanel);
        homePage.setVisible(true);

        // Hide the current login window
        setVisible(false);

        // Add action listener for logout button
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                friendRequests.clear();
                suggestedFriends = null;
                // Show login window again
                setVisible(true);
                homePage.dispose(); // Close the home page
            }
        });

        // Add action listener for suggest friends button
        suggestFriendsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Suggest friends to the user
                suggestedFriends = suggestFriends(loggedInUsername);
                JOptionPane.showMessageDialog(null, "Suggested Friends: " + suggestedFriends);
            }
        });

        // Add action listener for send friend request button
        sendFriendRequestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suggestedFriends != null && !suggestedFriends.isEmpty()) {
                    String suggestedFriend = suggestedFriends.get(0);
                    int choice = JOptionPane.showConfirmDialog(null, "Would you like to send a friend request to " + suggestedFriend + "?", "Send Friend Request", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        friendRequests.add(suggestedFriend);
                        JOptionPane.showMessageDialog(null, "Friend request sent to " + suggestedFriend);
                        suggestedFriends.remove(0); // Remove the suggested friend from the list
                    }
                } else {
                    String recipient = JOptionPane.showInputDialog("Enter the username of the friend you want to send a request to:");
                    if (recipient != null && !recipient.isEmpty()) {
                        friendRequests.add(recipient);
                        JOptionPane.showMessageDialog(null, "Friend request sent to " + recipient);
                    }
                }
            }
        });

        // Add action listener for show friend requests button
        showRequestsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show friend requests dialog
                showFriendRequestsDialog();
            }
        });

        // Add action listener for recommend content button
        recommendContentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Recommend content to the user
                List<String> recommendedContent = recommendContent(loggedInUsername);
                JOptionPane.showMessageDialog(null, "Recommended Content: " + recommendedContent);
            }
        });

        // Add action listener for upload image button
        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call the method to handle image upload
                uploadImage();
            }
        });
    }

    // Method to suggest friends based on mutual friends using a graph-based approach
    private List<String> suggest;
    // Method to suggest friends based on mutual friends using a graph-based approach
    private List<String> suggestFriends(String username) {
        List<String> suggestedFriends = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        // Perform depth-first search (DFS) to traverse the graph
        dfs(username, visited);

        // Retrieve suggested friends based on the traversal
        for (String user : allUsers) {
            if (!user.equals(username) && !visited.contains(user)) {
                suggestedFriends.add(user);
            }
        }

        return suggestedFriends;
    }

    // Depth-first search (DFS) to traverse the graph starting from the given user
    private void dfs(String user, Set<String> visited) {
        visited.add(user);

        // Get friends of the current user from the graph
        List<String> friends = getFriends(user);

        // Recursively traverse the friends of the current user
        for (String friend : friends) {
            if (!visited.contains(friend)) {
                dfs(friend, visited);
            }
        }
    }

    // Method to recommend content to the user
    private List<String> recommendContent(String username) {
        // Perform recommendation algorithm based on user interests, preferences, and network connections
        // This could involve analyzing user interactions, content popularity, and network influence
        // Implement your recommendation algorithm here
        return new ArrayList<>(); // Placeholder, replace with actual recommendations
    }

    // Method to get friends of a user (dummy implementation)
    private List<String> getFriends(String username) {
        // Return dummy list of friends for demonstration
        return new ArrayList<>(Arrays.asList("Friend1", "Friend2", "Friend3"));
    }

    // Method to show friend requests dialog
    private void showFriendRequestsDialog() {
        StringBuilder requestList = new StringBuilder("Friend Requests:\n");
        for (String request : friendRequests) {
            requestList.append(request).append("\n");
        }
        JOptionPane.showMessageDialog(null, requestList.toString(), "Friend Requests", JOptionPane.INFORMATION_MESSAGE);
    }

    // Method to add user to the database (dummy implementation)
    private void addUserToDatabase(String username) {
        // Dummy implementation for adding user to the database
        System.out.println("User added to database: " + username);
    }

    // Method to handle image upload
    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(file.getPath());
            imageLabel.setIcon(icon);
        }
    }

    public static void main(String[] args) {
        // Load PostgreSQL JDBC driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SocialMediaApp().setVisible(true);
            }
        });
    }
}

