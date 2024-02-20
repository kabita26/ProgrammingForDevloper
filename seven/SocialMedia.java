import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SocialMedia extends JFrame {
    private String loggedInUsername;
    private List<String> suggestedFriends;
    private List<String> friendRequests;
    private List<String> allUsers;
    private List<String> uploadedImages;
    private Connection connection;
    private JLabel imageLabel;
    private Timer autoSaveTimer;

    public SocialMedia() {
        setTitle("Social Media Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        allUsers = new ArrayList<>();
        friendRequests = new ArrayList<>();

        // Create the login panel and add it to the frame
        JPanel loginPanel = createLoginPanel();
        add(loginPanel);

        // Connect to the database and start the auto-save timer
        connectToDatabase();
        startAutoSaveTimer();
    }

    // Method to create the login panel with username input field and buttons
    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JButton loginButton = new JButton("Login");
        JLabel createAccountLabel = new JLabel("Don't have an account?");
        JButton createAccountButton = new JButton("Create Account");

        // Add components to the login panel
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel());
        loginPanel.add(loginButton);
        loginPanel.add(createAccountLabel);
        loginPanel.add(createAccountButton);

        // Add action listeners to the buttons
        loginButton.addActionListener(e -> login(usernameField.getText()));
        createAccountButton.addActionListener(e -> createAccount());

        return loginPanel;
    }

    // Method to handle login functionality
    private void login(String username) {
        if (username != null && !username.isEmpty()) {
            loggedInUsername = username;
            showHomePage(); // Once logged in, show the home page
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a username.");
        }
    }

    // Method to create a new user account
    private void createAccount() {
        String username = JOptionPane.showInputDialog("Enter username:");
        if (username != null && !username.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Account created successfully for " + username);
            allUsers.add(username); // Add the new user to the list of all users
        }
    }

    // Method to display the home page after successful login
    private void showHomePage() {
        // Remove all existing components from the frame
        getContentPane().removeAll();
        repaint();

        // Create the home panel with various buttons and labels
        JPanel homePanel = new JPanel(new BorderLayout());
        JButton logoutButton = new JButton("Logout");
        JLabel welcomeLabel = new JLabel("Welcome to the Home Page, " + loggedInUsername);
        JButton suggestFriendsButton = new JButton("Suggest Friends");
        JButton sendFriendRequestButton = new JButton("Send Friend Request");
        JButton showRequestsButton = new JButton("Show Friend Requests");
        JButton recommendContentButton = new JButton("Recommend Content");
        JButton uploadImageButton = new JButton("Upload Image");
        imageLabel = new JLabel();

        // Create top panel for logout button and welcome message
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(logoutButton);
        topPanel.add(welcomeLabel);

        // Create button panel for other functionality
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(uploadImageButton);
        buttonPanel.add(sendFriendRequestButton);
        buttonPanel.add(suggestFriendsButton);
        buttonPanel.add(recommendContentButton);
        buttonPanel.add(showRequestsButton);

        // Add components to the home panel
        homePanel.add(topPanel, BorderLayout.NORTH);
        homePanel.add(buttonPanel, BorderLayout.CENTER);
        homePanel.add(imageLabel, BorderLayout.SOUTH);

        // Add the home panel to the frame
        getContentPane().add(homePanel);
        revalidate();

        // Add action listeners to the buttons
        logoutButton.addActionListener(e -> logout());
        suggestFriendsButton.addActionListener(e -> suggestFriends());
        sendFriendRequestButton.addActionListener(e -> sendFriendRequest());
        showRequestsButton.addActionListener(e -> showFriendRequests());
        recommendContentButton.addActionListener(e -> recommendContent());
        uploadImageButton.addActionListener(e -> uploadImage());
    }

    // Method to handle logout functionality
    private void logout() {
        friendRequests.clear();
        suggestedFriends = null;
        loggedInUsername = null;
        getContentPane().removeAll();
        repaint();
        JPanel loginPanel = createLoginPanel(); // Show the login panel again
        getContentPane().add(loginPanel);
        revalidate();
    }

    // Method to suggest friends to the logged-in user
    private void suggestFriends() {
        suggestedFriends = new ArrayList<>();
        for (String user : allUsers) {
            if (!user.equals(loggedInUsername) && !friendRequests.contains(user)) {
                suggestedFriends.add(user);
            }
        }
        JOptionPane.showMessageDialog(null, "Suggested Friends: " + suggestedFriends);
    }

    // Method to send a friend request
    private void sendFriendRequest() {
        String recipient = selectRecipient();
        if (recipient != null) {
            if (allUsers.contains(recipient)) {
                if (!friendRequests.contains(recipient)) {
                    friendRequests.add(recipient);
                    JOptionPane.showMessageDialog(null, "Friend request sent to " + recipient);
                } else {
                    JOptionPane.showMessageDialog(null, "A friend request has already been sent to " + recipient);
                }
            } else {
                JOptionPane.showMessageDialog(null, "User does not exist or has not created an account.");
            }
        }
    }

    // Method to select a recipient for the friend request
    private String selectRecipient() {
        if (suggestedFriends != null && !suggestedFriends.isEmpty()) {
            String[] options = suggestedFriends.toArray(new String[0]);
            return (String) JOptionPane.showInputDialog(null, "Select a friend to send request to:", "Send Friend Request",
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        } else {
            return JOptionPane.showInputDialog("Enter the username of the friend you want to send a request to:");
        }
    }

    // Method to show friend requests received by the logged-in user
    private void showFriendRequests() {
        if (friendRequests.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No friend requests.");
        } else {
            StringBuilder requestList = new StringBuilder("Friend Requests:\n");
            for (String request : friendRequests) {
                requestList.append(request).append("\n");
            }
            JOptionPane.showMessageDialog(null, requestList.toString(), "Friend Requests", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Method to recommend content to the logged-in user
    private void recommendContent() {
        List<String> recommendedContent = new ArrayList<>();
        recommendedContent.add("Article");
        recommendedContent.add("Video");
        recommendedContent.add("Podcast");
        showRecommendedContent(recommendedContent);
    }

    // Method to show recommended content to     // the user
    private void showRecommendedContent(List<String> recommendedContent) {
        if (recommendedContent.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No recommended content available.");
        } else {
            StringBuilder contentBuilder = new StringBuilder("Recommended Content:\n");
            for (String content : recommendedContent) {
                contentBuilder.append(content).append("\n");
            }
            JOptionPane.showMessageDialog(null, contentBuilder.toString(), "Recommended Content", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Method to handle image upload functionality
    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String imagePath = file.getPath();

            ImageIcon icon = new ImageIcon(imagePath);
            Image image = icon.getImage().getScaledInstance(400, 300, Image.SCALE_DEFAULT);
            ImageIcon scaledIcon = new ImageIcon(image);
            imageLabel.setIcon(scaledIcon);

            // Select friends to share the image with
            selectFriendsToShareImage();

            repaint();
        }
    }
    
    // Method to select friends to share the image with
    private void selectFriendsToShareImage() {
        if (allUsers.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No friends available to share the image with.");
            return;
        }

        String[] options = allUsers.toArray(new String[0]); // Convert to String array
        String[] selectedFriends = (String[]) JOptionPane.showInputDialog(
                this, // Parent component
                "Select friends to share the image with:",
                "Share Image",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                null
        );

        if (selectedFriends != null) {
            // Process selected friends
            StringBuilder selectedFriendsMessage = new StringBuilder("Selected friends to share the image with:\n");
            for (String friend : selectedFriends) {
                selectedFriendsMessage.append(friend).append("\n");
                // You can implement the logic to share the image with selected friends here
            }
            JOptionPane.showMessageDialog(null, selectedFriendsMessage.toString());
        } else {
            JOptionPane.showMessageDialog(null, "No friends selected to share the image with.");
        }
    }

    // Method to connect to the database
    private void connectToDatabase() {
        try {
            String DB_URL = "jdbc:postgresql://localhost:5432/DSA";
            String DB_USER = "postgres";
            String DB_PASSWORD = "123456";
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to database successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to start the auto-save timer for user data
    private void startAutoSaveTimer() {
        autoSaveTimer = new Timer();
        autoSaveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                saveUserDataToDatabase();
            }
        }, 0, 60000); // Auto-save every minute
    }

    // Method to save user data to the database
    private void saveUserDataToDatabase() {
        try {
            for (String request : friendRequests) {
                String query = "INSERT INTO friend_requests (sender_username, recipient_username) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, loggedInUsername);
                preparedStatement.setString(2, request);
                preparedStatement.executeUpdate();
            }

            for (String imagePath : uploadedImages) {
                String query = "INSERT INTO images (username, image_path) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, loggedInUsername);
                preparedStatement.setString(2, imagePath);
                preparedStatement.executeUpdate();
            }

            System.out.println("User data auto-saved to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            new SocialMedia().setVisible(true);
        });
    }
}

