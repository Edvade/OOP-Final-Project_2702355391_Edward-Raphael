import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class FaceID {

    private static VideoCapture capture;
    private static JFrame frame;
    private static JPanel videoPanel;

    public static void setupFaceID() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Load OpenCV native library

        // Initialize video capture for webcam
        capture = new VideoCapture(0);
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 1920); // Set capture frame width
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 1080); // Set capture frame height

        // Create and show GUI
        createAndShowGUI();
    }

    // Create and show GUI
    private static void createAndShowGUI() {
        frame = new JFrame("Face ID Setup");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        videoPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (capture.isOpened()) {
                    Mat previewFrame = new Mat();
                    capture.read(previewFrame); // Read frame from webcam
                    BufferedImage previewImage = matToBufferedImage(previewFrame);
                    if (previewImage != null) {
                        g.drawImage(previewImage, 0, 0, getWidth(), getHeight(), null); // Display frame in the panel
                    }
                }
                repaint(); // Schedule next repaint
            }
        };
        videoPanel.setPreferredSize(new Dimension(640, 480));
        frame.add(videoPanel, BorderLayout.NORTH);

        // Capture button
        JButton captureButton = new JButton("Capture");
        captureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                captureImage(); // Capture the image
            }
        });
        frame.add(captureButton, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);

        startPreview(); // Start preview
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    // Start preview thread
    private static void startPreview() {
        Thread previewThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    frame.repaint(); // Update the preview panel
                    try {
                        Thread.sleep(50); // Adjust frame rate
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        previewThread.start();
    }

    // Capture image from webcam
    private static void captureImage() {
        Mat capturedImage = new Mat();
        if (capture.isOpened()) {
            capture.read(capturedImage); // Capture image from webcam

            // Get the directory of the saved image
            File saveDirectory = new File("C:/Users/eraph/OneDrive/Desktop/2nd Semester/OOP/Final Project");
            String capturedImagePath = saveDirectory + "/img2.jpg";
            Imgcodecs.imwrite(capturedImagePath, capturedImage); // Save captured image as img2.jpg

            // Close the Face ID setup
            frame.dispose();

            System.out.println("Face ID Setup Successful");

            // Rerun the Main class
            SwingUtilities.invokeLater(() -> {
                Main.main(new String[]{});
            });

            capture.release(); // Release the webcam
        } else {
            JOptionPane.showMessageDialog(frame, "Failed to open webcam.");
        }
    }

    // Convert OpenCV Mat to BufferedImage
    private static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_3BYTE_BGR; // Set image type to color (BGR)
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        byte[] data = new byte[mat.channels() * mat.cols() * mat.rows()];
        mat.get(0, 0, data);

        // For BGR format, OpenCV stores pixels in Blue-Green-Red order, so we need to swap channels
        for (int i = 0; i < mat.cols() * mat.rows(); i++) {
            byte temp = data[i * 3];
            data[i * 3] = data[i * 3 + 2]; // Swap red and blue channels
            data[i * 3 + 2] = temp;
        }

        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        return image;
    }
}
