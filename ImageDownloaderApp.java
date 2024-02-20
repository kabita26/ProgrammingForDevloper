import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.image.BufferedImage;

/**
 * This class represents a multithreaded image downloader application that allows users to download images
 * from specified URLs. It provides functionality to download, pause, and resume downloads of images.
 */
public class ImageDownloaderApp extends JFrame {

    private JTextField urlField1;
    private JTextField urlField2;
    private JButton downloadButton;
    private JButton pauseButton;
    private JButton resumeButton;
    private JButton deleteButton; // Added delete button
    private JTextArea logArea;
    private JPanel imagePanel; // Panel to hold the downloaded images
    private ExecutorService executor;
    private boolean isPaused;

    /**
     * Constructs the ImageDownloaderApp GUI with its components and initializes necessary fields.
     */
    public ImageDownloaderApp() {
        setTitle("Multithreaded Image Downloader");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize components
        urlField1 = new JTextField();
        urlField2 = new JTextField();
        downloadButton = new JButton("Download");
        pauseButton = new JButton("Pause");
        resumeButton = new JButton("Resume");
        deleteButton = new JButton("Delete"); // Added delete button
        logArea = new JTextArea();
        logArea.setEditable(false);

        // Add components to the frame
        JPanel inputPanel = new JPanel(new GridLayout(4, 1)); // Adjusted GridLayout
        inputPanel.add(new JLabel("Enter URL 1: "));
        inputPanel.add(urlField1);
        inputPanel.add(new JLabel("Enter URL 2: "));
        inputPanel.add(urlField2);

        JPanel buttonPanel = new JPanel(new FlowLayout()); // Button panel with FlowLayout
        buttonPanel.add(downloadButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);
        buttonPanel.add(deleteButton); // Added delete button

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER); // Using JScrollPane for logArea
        add(buttonPanel, BorderLayout.SOUTH);

        // Panel to hold the downloaded images
        imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(new JScrollPane(imagePanel), BorderLayout.WEST); // Using JScrollPane for imagePanel

        // Initialize thread pool with fixed number of threads
        executor = Executors.newFixedThreadPool(5);

        // Add action listeners to buttons
        downloadButton.addActionListener(e -> startDownload());
        pauseButton.addActionListener(e -> pauseDownload());
        resumeButton.addActionListener(e -> resumeDownload());
        deleteButton.addActionListener(e -> deleteImages()); // Added action listener for delete button
    }

    /**
     * Initiates the download process for the entered URLs.
     */
    private void startDownload() {
        String url1 = urlField1.getText().trim();
        String url2 = urlField2.getText().trim();
        if (!url1.isEmpty() && !url2.isEmpty()) {
            downloadImage(url1);
            downloadImage(url2);
        } else {
            log("Please enter URLs for both images.");
        }
    }

    /**
     * Downloads an image from the specified URL.
     *
     * @param imageUrl The URL of the image to download.
     */
    private void downloadImage(String imageUrl) {
        executor.submit(() -> {
            try {
                log("Downloading image from: " + imageUrl);
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int responseCode = connection.getResponseCode();
                log("Response code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedImage originalImage = ImageIO.read(inputStream);
                    inputStream.close();
                    if (originalImage != null && !isPaused) { // Check if not paused before displaying image
                        displayImage(originalImage);
                        log("Downloaded: " + imageUrl);
                    } else {
                        log("Download paused: " + imageUrl);
                    }
                } else {
                    log("Error downloading image from " + imageUrl + ": HTTP error code " + responseCode);
                }
            } catch (IOException e) {
                log("Error downloading image from " + imageUrl + ": " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Displays the downloaded image on the GUI.
     *
     * @param image The BufferedImage to display.
     */
    private void displayImage(BufferedImage image) {
        ImageIcon imageIcon = new ImageIcon(image);
        JLabel imageLabel = new JLabel(imageIcon);
        imagePanel.add(imageLabel);
        validate(); // Revalidate the layout
        repaint(); // Repaint the panel
    }

    /**
     * Pauses the download process.
     */
    private void pauseDownload() {
        isPaused = true;
        log("Downloads Paused");
    }

    /**
     * Resumes the download process.
     */
    private void resumeDownload() {
        isPaused = false;
        log("Downloads Resumed");
    }

    /**
     * Deletes all downloaded images from the GUI.
     */
    private void deleteImages() {
        imagePanel.removeAll(); // Remove all images from the panel
        validate(); // Revalidate the layout
        repaint(); // Repaint the panel
        log("All downloaded images deleted");
    }

    /**
     * Logs messages to the text area on the GUI.
     *
     * @param message The message to log.
     */
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
        });
    }

    /**
     * Main method to start the application.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ImageDownloaderApp().setVisible(true);
        });
    }
}
