import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageDownloaderApp extends JFrame {

    private JTextField urlField;
    private JButton downloadButton;
    private JTextArea logArea;
    private ExecutorService executor;

    public ImageDownloaderApp() {
        setTitle("Multithreaded Image Downloader");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize components
        urlField = new JTextField();
        downloadButton = new JButton("Download");
        logArea = new JTextArea();
        logArea.setEditable(false);

        // Add components to the frame
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Enter URLs (separated by space): "), BorderLayout.WEST);
        inputPanel.add(urlField, BorderLayout.CENTER);
        inputPanel.add(downloadButton, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(logArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize thread pool with fixed number of threads
        executor = Executors.newFixedThreadPool(5);

        // Add action listener to the download button
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String urlsText = urlField.getText().trim();
                if (!urlsText.isEmpty()) {
                    String[] urls = urlsText.split("\\s+");
                    for (String url : urls) {
                        downloadImage(url.trim());
                    }
                } else {
                    log("Please enter one or more URLs.");
                }
            }
        });
    }

    private void downloadImage(String imageUrl) {
        executor.submit(new ImageDownloaderTask(imageUrl));
    }

    private class ImageDownloaderTask implements Runnable {

        private String imageUrl;

        public ImageDownloaderTask(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();

                    // Extract filename from URL
                    String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                    File outputFile = new File(fileName);
                    FileOutputStream outputStream = new FileOutputStream(outputFile);

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    inputStream.close();
                    outputStream.close();
                    log("Downloaded: " + fileName);
                } else {
                    log("Error downloading image from " + imageUrl + ": HTTP error code " + responseCode);
                }
            } catch (IOException e) {
                log("Error downloading image from " + imageUrl + ": " + e.getMessage());
            }
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ImageDownloaderApp().setVisible(true);
        });
    }
}
