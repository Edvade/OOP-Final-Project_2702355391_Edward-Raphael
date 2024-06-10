import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PasswordManager {

    // Define the directory where passwords will be stored
    private static final String PASSWORD_DIRECTORY = "C:/Users/eraph/OneDrive/Desktop/2nd Semester/OOP/Final Project/Passwords";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Options:");
            System.out.println("1. Set a Password");
            System.out.println("2. Open Passwords");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    setPassword(scanner);
                    break;
                case 2:
                    openPasswords();
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void setPassword(Scanner scanner) {
        System.out.print("Enter login: ");
        String login = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter file name (without extension): ");
        String fileName = scanner.nextLine();

        // Ensure the directory exists, if not create it
        File directory = new File(PASSWORD_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directories if they don't exist
        }

        File file = new File(PASSWORD_DIRECTORY, fileName + ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Login: " + login + "\nPassword: " + password);
            System.out.println("Password saved successfully.");
        } catch (IOException e) {
            System.err.println("Failed to save password: " + e.getMessage());
        }
    }

    private static void openPasswords() {
        try {
            File directory = new File(PASSWORD_DIRECTORY);
            Desktop.getDesktop().open(directory); // Open password directory
        } catch (IOException e) {
            System.err.println("Failed to open password directory: " + e.getMessage());
        }
    }
}
