import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Create a frame for options
        JFrame frame = new JFrame("Options");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit application on close
        frame.setSize(300, 100); // Set initial size of the frame

        // Create buttons for options
        JButton openJournalBtn = new JButton("Password Manager");
        JButton setupFaceIDBtn = new JButton("Setup Face ID");

        // Add action listeners to buttons
        openJournalBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Check if img2.jpg exists
                String referenceImagePath = "C:/Users/eraph/OneDrive/Desktop/2nd Semester/OOP/Final Project/img2.jpg";
                File referenceImageFile = new File(referenceImagePath);
                if (!referenceImageFile.exists()) {
                    System.out.println("Please set up your Face ID."); // Prompt to set up Face ID if reference image doesn't exist
                } else {
                    // Initiate face recognition
                    FaceRecognition.main(null);
                    frame.dispose(); // Close options window
                }
            }
        });

        setupFaceIDBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Initiate face ID setup
                FaceID.setupFaceID();
                frame.dispose(); // Close options window
            }
        });

        // Check if img2.jpg exists to decide which button to show
        String referenceImagePath = "C:/Users/eraph/OneDrive/Desktop/2nd Semester/OOP/Final Project/img2.jpg";
        File referenceImageFile = new File(referenceImagePath);
        if (referenceImageFile.exists()) {
            frame.getContentPane().add(openJournalBtn, BorderLayout.CENTER); // Show Password Manager button
        } else {
            frame.getContentPane().add(setupFaceIDBtn, BorderLayout.CENTER); // Show Setup Face ID button
        }

        // Display the frame
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }
}
