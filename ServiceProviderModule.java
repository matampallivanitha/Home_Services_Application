import java.sql.*;
import java.util.Scanner;

public class ServiceProviderModule {

    public static void registerServiceProvider() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Service Provider Registration");

        // Name Validation
        System.out.print("Name: ");
        String name = scanner.nextLine();
        while (!ValidationUtil.isValidName(name)) {
            System.out.print("Name: ");
            name = scanner.nextLine();
        }

        // Email Validation + Uniqueness Check
        System.out.print("Email: ");
        String email = scanner.nextLine();
        while (!ValidationUtil.isValidEmail(email) || !isEmailUnique(email)) {
            System.out.println("Invalid or already registered email. Please enter a different email.");
            System.out.print("Email: ");
            email = scanner.nextLine();
        }

        // Password Validation + Uniqueness Check
        System.out.print("Password: ");
        String password = scanner.nextLine();
        while (!ValidationUtil.isValidPassword(password) || !isPasswordUnique(password)) {
            System.out.println("Invalid or already used password. Please enter a unique password.");
            System.out.print("Password: ");
            password = scanner.nextLine();
        }

        // Validate phone number
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        while (!ValidationUtil.isValidPhone(phone) || !isPhoneUnique(phone)) {
            System.out.println("Invalid or  registered phone number. Please enter a different phone number.");
            System.out.print("Phone: ");
            phone = scanner.nextLine();
        }

        /** Service Type Validation
        System.out.print("Service Type: ");
        String serviceType = scanner.nextLine();
        while (!ValidationUtil.isValidServiceType(serviceType)) {
            System.out.print("Service Type: ");
            serviceType = scanner.nextLine();
        }

         **/


        // Predefined Service Types
        String[] predefinedServices = {"Cleaning", "Plumbing", "Electrical", "Painting", "Carpentry"};

        System.out.println("\nAvailable Services:");
        for (int i = 0; i < predefinedServices.length; i++) {
            System.out.println((i + 1) + ". " + predefinedServices[i]);
        }
        System.out.println((predefinedServices.length + 1) + ". Other (Type your own)");

        // Choose or enter a new service
        System.out.print("Enter the number of your service or type a new service name: ");
        String serviceType;
        int choice;

        if (scanner.hasNextInt()) {
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (choice > 0 && choice <= predefinedServices.length) {
                serviceType = predefinedServices[choice - 1]; // Selected predefined service
            } else {
                System.out.print("Enter your custom service type: ");
                serviceType = scanner.nextLine(); // Custom service
            }
        } else {
            scanner.nextLine(); // Consume invalid input
            System.out.print("Enter your custom service type: ");
            serviceType = scanner.nextLine();
        }
        // Price Validation
        System.out.print("Price: ");
        String priceStr = scanner.nextLine();
        while (!ValidationUtil.isValidPrice(priceStr)) {
            System.out.print("Price: ");
            priceStr = scanner.nextLine();
        }
        double price = Double.parseDouble(priceStr);

        // Register service provider
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            String sql = "INSERT INTO ServiceProviders (name, email, password, phone, service_type, price) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, phone);
            stmt.setString(5, serviceType);
            stmt.setDouble(6, price);

            int result = stmt.executeUpdate();
            System.out.println(result > 0 ? "Registration successful!" : "Registration failed. Please try again.");
        } catch (SQLException e) {
            System.out.println("Database error occurred: " + e.getMessage());
        }
    }

    public static boolean isEmailUnique(String email) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            String sql = "SELECT COUNT(*) FROM ServiceProviders WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPasswordUnique(String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            String sql = "SELECT COUNT(*) FROM ServiceProviders WHERE password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPhoneUnique(String phone) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            String sql = "SELECT COUNT(*) FROM ServiceProviders WHERE phone = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) == 0; // If count is 0, phone is unique
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void loginServiceProvider() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Service Provider Email: ");
        String email = scanner.nextLine();
        while (!ValidationUtil.isValidEmail(email)) {
            System.out.print("Service Provider Email: ");
            email = scanner.nextLine();
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();
        while (!ValidationUtil.isValidPassword(password)) {
            System.out.print("Password: ");
            password = scanner.nextLine();
        }

        // Authenticate service provider
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            String sql = "SELECT provider_id, name FROM ServiceProviders WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int providerId = rs.getInt("provider_id");
                System.out.println("Login successful! Welcome, " + rs.getString("name") + ".");
                serviceProviderDashboard(providerId);
            } else {
                System.out.println("Invalid email or password.");
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred: " + e.getMessage());
        }
    }


    public static void serviceProviderDashboard(int providerId) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nService Provider Dashboard");
            System.out.println("1. View Bookings");
            System.out.println("2. Update Booking Status");
            System.out.println("3. View Ratings");
            System.out.println("4. Update Availability");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1 -> viewBookings(providerId);
                case 2 -> updateBookingStatus();
                case 3 -> viewRatings(providerId);
                case 4 -> {
                    System.out.print("Set availability (true/false): ");
                    boolean isAvailable = scanner.nextBoolean();
                    scanner.nextLine(); // Consume newline
                    updateAvailability(providerId, isAvailable);
                }
                case 5 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }


    public static void viewBookings(int providerId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            String sql = """
                    SELECT b.booking_id, b.user_id, b.status, sp.service_type, b.booking_date, b.price, b.description
                    FROM Bookings b
                    JOIN ServiceProviders sp ON b.provider_id = sp.provider_id
                    WHERE b.provider_id = ?""";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, providerId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nYour Bookings:");
            while (rs.next()) {
                System.out.println(
                        "Booking ID: " + rs.getInt("booking_id") +
                                ", User ID: " + rs.getInt("user_id") +
                                ", Service Type: " + rs.getString("service_type") +
                                ", Status: " + rs.getString("status") +
                                ", Booking Date: " + rs.getTimestamp("booking_date") +
                                ", Price: $" + rs.getDouble("price") +
                                ", Description: " + rs.getString("description")
                );
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred: " + e.getMessage());
        }
    }

    public static boolean isBookingAvailable(int providerId, Timestamp bookingDateTime) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            String sql = "SELECT COUNT(*) FROM Bookings WHERE provider_id = ? AND booking_date = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, providerId);
            stmt.setTimestamp(2, bookingDateTime);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updateBookingStatus() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Booking ID: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter New Status (Pending/Confirmed/Completed/Cancelled): ");
        String status = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            String sql = "UPDATE Bookings SET status = ? WHERE booking_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);

            int result = stmt.executeUpdate();
            System.out.println(result > 0 ? "Booking status updated!" : "Failed to update booking status.");
        } catch (SQLException e) {
            System.out.println("Database error occurred: " + e.getMessage());
        }
    }

    public static void updateAvailability(int providerId, boolean isAvailable) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            String sql = "UPDATE ServiceProviders SET availability = ? WHERE provider_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, providerId);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("Availability updated successfully!");
            } else {
                System.out.println("Failed to update availability.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewRatings(int providerId) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT rating, review FROM Ratings WHERE provider_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, providerId);
            ResultSet rs = stmt.executeQuery();

            // Check if there are ratings for the provider
            if (!rs.isBeforeFirst()) {
                // If no ratings, show "No ratings for you"
                System.out.println("No ratings for you.");
            } else {
                // If there are ratings, display them
                System.out.println("Ratings for provider  ID: " + providerId);
                while (rs.next()) {
                    System.out.println("Rating: " + rs.getInt("rating") + ", Review: " + rs.getString("review"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
