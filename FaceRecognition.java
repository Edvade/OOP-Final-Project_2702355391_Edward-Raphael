import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import org.opencv.core.*;
import org.opencv.dnn.*;
import org.opencv.imgcodecs.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class FaceRecognition extends AuthenticationManager {

    private static final String MODEL_PATH = "C:/Users/eraph/OneDrive/Desktop/2nd Semester/OOP/Final Project/pyannote-data-master/openface.nn4.small2.v1.t7";
    private Net net; // Neural network for face recognition
    private VideoCapture capture; // Video capture object for webcam
    private JFrame frame;
    private JPanel videoPanel;

    public static void main(String[] args) {
        FaceRecognition fr = new FaceRecognition();
        fr.run();
    }

    public void run() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Load OpenCV native library

        try {
            net = Dnn.readNetFromTorch(MODEL_PATH); // Load pre-trained face recognition model
        } catch (CvException e) {
            System.err.println("Failed to load model from " + MODEL_PATH);
            e.printStackTrace();
            return;
        }

        capture = new VideoCapture(0); // Initialize video capture from webcam
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 1920); // Set capture frame width
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 1080); // Set capture frame height

        createAndShowGUI(); // Create and show the GUI
    }

    // Create and show GUI
    private void createAndShowGUI() {
        frame = new JFrame("Face Recognition");
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

        JButton captureButton = new JButton("Capture");
        captureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the frame after capture
                authenticate(); // Perform authentication
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
    private void startPreview() {
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

    // Capture and recognize face
    private boolean captureAndRecognize() {
        Mat capturedImage = new Mat();
        if (capture.isOpened()) {
            capture.read(capturedImage); // Capture image from webcam

            // Path to save the captured image
            String capturedImagePath = "C:/Users/eraph/OneDrive/Desktop/2nd Semester/OOP/Final Project/image1.jpg";
            boolean isSaved = Imgcodecs.imwrite(capturedImagePath, capturedImage); // Save captured image
            if (!isSaved) {
                System.err.println("Failed to save captured image to " + capturedImagePath);
                return false;
            }

            // Process captured image and reference image for face recognition
            Mat feature1 = process(capturedImage);
            String referenceImagePath = "C:/Users/eraph/OneDrive/Desktop/2nd Semester/OOP/Final Project/img2.jpg";
            Mat referenceImage = Imgcodecs.imread(referenceImagePath);

            if (!referenceImage.empty()) {
                Mat feature2 = process(referenceImage);

                double dist = Core.norm(feature1, feature2); // Calculate distance between features
                System.out.println("Distance: " + dist);

                if (dist < 0.6) {
                    System.out.println("Face recognized successfully. Login successful.");
                    openPasswordManager(); // Open password manager
                    return true;
                } else {
                    System.out.println("Face recognition failed. Login unsuccessful.");
                }
            } else {
                System.err.println("Failed to load reference image from " + referenceImagePath);
            }

            capture.release();
        } else {
            JOptionPane.showMessageDialog(frame, "Failed to open webcam.");
        }
        return false;
    }

    // Open password manager
    private void openPasswordManager() {
        try {
            PasswordManager.main(new String[0]);
        } catch (Exception e) {
            System.err.println("Failed to open PasswordManager: " + e.getMessage());
        }
    }

    // Process image for face recognition
    private Mat process(Mat img) {
        Mat inputBlob = Dnn.blobFromImage(img, 1./255, new Size(96, 96), new Scalar(0, 0, 0), true, false);
        net.setInput(inputBlob);
        return net.forward().clone(); // Forward pass through the neural network
    }

    // Convert OpenCV Mat to BufferedImage
    private BufferedImage matToBufferedImage(Mat mat) {
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

    @Override
    public void authenticate() {
        boolean isAuthenticated = captureAndRecognize();
        if (isAuthenticated) {
            System.out.println("Face recognized successfully. Login successful.");
            // Add logic to redirect to PasswordManager or perform other actions
        } else {
            System.out.println("Face recognition failed. Login unsuccessful.");
        }
    }
}
