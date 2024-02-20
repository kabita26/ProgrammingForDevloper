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

        allUsers = new ArrayList<>(); // Initialize the list
        friendRequests = new ArrayList<>(); // Initialize friendRequests list

        JPanel loginPanel = createLoginPanel();
        add(loginPanel);

        connectToDatabase(); // Connect to the database
        startAutoSaveTimer(); // Start auto-save timer
    }

    private JPanel createLoginPanel() {
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

        loginButton.addActionListener(e -> login(usernameField.getText())); // Action listener for login button
        createAccountButton.addActionListener(e -> createAccount()); // Action listener for create account button

        return loginPanel;
    }

    private void login(String username) {
        if (username != null && !username.isEmpty()) {
            loggedInUsername = username;
            showHomePage();
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a username.");
        }
    }

    private void createAccount() {
        String username = JOptionPane.showInputDialog("Enter username:");
        if (username != null && !username.isEmpty()) {
            // Show message for successful account creation
            JOptionPane.showMessageDialog(null, "Account created successfully for " + username);
            // Add the new user to the list of all users
            allUsers.add(username);
        }
    }

    private void showHomePage() {
        getContentPane().removeAll();
        repaint();

        JPanel homePanel = new JPanel(new BorderLayout());
        JButton logoutButton = new JButton("Logout");
        JLabel welcomeLabel = new JLabel("Welcome to the Home Page, " + loggedInUsername);
        JButton suggestFriendsButton = new JButton("Suggest Friends");
        JButton sendFriendRequestButton = new JButton("Send Friend Request");
        JButton showRequestsButton = new JButton("Show Friend Requests");
        JButton recommendContentButton = new JButton("Recommend Content");
        JButton uploadImageButton = new JButton("Upload Image");
        imageLabel = new JLabel();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(logoutButton);
        topPanel.add(welcomeLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(uploadImageButton);
        buttonPanel.add(sendFriendRequestButton);
        buttonPanel.add(suggestFriendsButton);
        buttonPanel.add(recommendContentButton);
        buttonPanel.add(showRequestsButton);

        homePanel.add(topPanel, BorderLayout.NORTH);
        homePanel.add(buttonPanel, BorderLayout.CENTER);
        homePanel.add(imageLabel, BorderLayout.SOUTH);

        getContentPane().add(homePanel);
        revalidate();   
        

        logoutButton.addActionListener(e -> {
            friendRequests.clear();
            suggestedFriends = null;
            loggedInUsername = null;
            getContentPane().removeAll();
            repaint();
            JPanel loginPanel = createLoginPanel();
            getContentPane().add(loginPanel);
            revalidate();
        });

        suggestFriendsButton.addActionListener(e -> {
            suggestedFriends = suggestFriends(loggedInUsername);
            JOptionPane.showMessageDialog(null, "Suggested Friends: " + suggestedFriends);
        });

        sendFriendRequestButton.addActionListener(e -> sendFriendRequest());

        showRequestsButton.addActionListener(e -> showFriendRequestsDialog());

        recommendContentButton.addActionListener(e -> {
            List<String> recommendedContent = recommendContent();

            // Create a list model to hold the recommended content
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (String content : recommendedContent) {
                listModel.addElement(content);
            }

            // Create a JList with the list model
            JList<String> contentList = new JList<>(listModel);
            contentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // Add the JList to a JScrollPane for scrolling if needed
            JScrollPane scrollPane = new JScrollPane(contentList);

            // Create a new JFrame to display the recommended content
            JFrame recommendationFrame = new JFrame("Recommended Content");
            recommendationFrame.getContentPane().add(scrollPane);
            recommendationFrame.setSize(300, 200);
            recommendationFrame.setLocationRelativeTo(null);
            recommendationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            recommendationFrame.setVisible(true);

            // Add a list selection listener to handle the click action
            contentList.addListSelectionListener(event -> {
                if (!event.getValueIsAdjusting()) {
                    String selectedItem = contentList.getSelectedValue();
                    if (selectedItem != null) {
                        // Handle the selection, e.g., open a new page
                        JOptionPane.showMessageDialog(recommendationFrame, "Selected item: " + selectedItem);
                        // Here you can navigate to a new page based on the selected item
                        // For simplicity, let's just print the selected item for now
                        System.out.println("Selected item: " + selectedItem);
                    }
                }
            });
        });

        uploadImageButton.addActionListener(e -> uploadImage());
    }

    private List<String> suggestFriends(String username) {
        List<String> suggestedFriends = new ArrayList<>();
        for (String user : allUsers) {
            if (!user.equals(username) && !friendRequests.contains(user)) {
                suggestedFriends.add(user);
            }
        }
        return suggestedFriends;
    }

    private void sendFriendRequest() {
        if (suggestedFriends != null && !suggestedFriends.isEmpty()) {
            String suggestedFriend = suggestedFriends.get(0);
            if (allUsers.contains(suggestedFriend) && !friendRequests.contains(suggestedFriend)) {
                int choice = JOptionPane.showConfirmDialog(null, "Would you like to send a friend request to " + suggestedFriend + "?", "Send Friend Request", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    friendRequests.add(suggestedFriend);
                    JOptionPane.showMessageDialog(null,"Friend request sent to " + suggestedFriend);
                    suggestedFriends.remove(0);
                }
            } else {
                JOptionPane.showMessageDialog(null, "User does not exist, has not created an account, or already has a pending friend request.");
            }
        } else {
            String recipient = JOptionPane.showInputDialog("Enter the username of the friend you want to send a request to:");
            if (recipient != null && !recipient.isEmpty()) {
                if (allUsers.contains(recipient) && !friendRequests.contains(recipient)) {
                    friendRequests.add(recipient);
                    JOptionPane.showMessageDialog(null, "Friend request sent to " + recipient);
                } else {
                    JOptionPane.showMessageDialog(null, "User does not exist, has not created an account, or already has a pending friend request.");
                }
            }
        }
    }
    
    private void showFriendRequestsDialog() {
        if (friendRequests.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No friend requests.");
            return;
        }
        StringBuilder requestList = new StringBuilder("Friend Requests:\n");
        for (String request : friendRequests) {
            requestList.append(request).append("\n");
        }
        JOptionPane.showMessageDialog(null, requestList.toString(), "Friend Requests", JOptionPane.INFORMATION_MESSAGE);
    }

    private List<String> recommendContent() {
        List<String> recommendedContent = new ArrayList<>();
        recommendedContent.add("Article");
        recommendedContent.add("Video");
        recommendedContent.add("Podcast");
        return recommendedContent;
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String imagePath = file.getPath();
    
            // Load the image and set it to the imageLabel
            ImageIcon icon = new ImageIcon(imagePath);
            Image image = icon.getImage().getScaledInstance(400, 300, Image.SCALE_DEFAULT);
            ImageIcon scaledIcon = new ImageIcon(image);
            imageLabel.setIcon(scaledIcon);
    
            // Ensure the frame is repainted
            repaint();
    
            // Save image to database
            // saveImageToDatabase(imagePath);
        }
    }
    
    
    
    


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

    private void startAutoSaveTimer() {
        autoSaveTimer = new Timer();
        autoSaveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                saveUserDataToDatabase();
            }
        }, 0, 60000);
    }

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

