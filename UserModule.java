import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;


public class UserModule {
    public static void registerUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("User Registration");
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
            System.out.println("Invalid or already registered phone number. Please enter a different phone number.");
            System.out.print("Phone: ");
            phone = scanner.nextLine();
        }

        // Validate address
        System.out.print("Address: ");
        String address = scanner.nextLine();
        while (!ValidationUtil.isValidAddress(address)) {
            System.out.print("Address: ");
            address = scanner.nextLine();
        }


        // Register user
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            String sql = "INSERT INTO Users (name, email, password, phone, address, wallet_balance) VALUES (?, ?, ?, ?, ?, 0.00)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("Registration successful!");
            } else {
                System.out.println("Registration failed. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static boolean isEmailUnique(String email) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            String sql = "SELECT COUNT(*) FROM Users WHERE email = ?";
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
            String sql = "SELECT COUNT(*) FROM Users WHERE password = ?";
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
        if (!ValidationUtil.isValidPhone(phone)) { // Call validation method first
            return false; // If invalid, no need to check in the database
        }

        String url = "jdbc:mysql://localhost:3306/homeservice";
        String user = "root";
        String password = "Vanitha@4829";
        String sql = "SELECT COUNT(*) FROM Users WHERE phone = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();

            return rs.next() && rs.getInt(1) == 0; // Return true if phone is unique, otherwise false

        } catch (SQLException e) {
            return false; // Return false in case of SQL exceptions
        }
    }


    public static void loginUser() {
        Scanner scanner = new Scanner(System.in);

        // Validate email
        System.out.print("User Email: ");
        String email = scanner.nextLine();
        while (!ValidationUtil.isValidEmail(email)) {
            System.out.print("User Email: ");
            email = scanner.nextLine();
        }

        // Validate password
        System.out.print("Password: ");
        String password = scanner.nextLine();
        while (!ValidationUtil.isValidPassword(password)) {
            System.out.print("Password: ");
            password = scanner.nextLine();
        }

        // Authenticate user
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            String sql = "SELECT * FROM Users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                System.out.println("Login successful! Welcome, " + rs.getString("name") + ".");
                userDashboard(userId); // Redirect to user dashboard
            } else {
                System.out.println("Invalid email or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void userDashboard(int userId) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("User Dashboard");
            System.out.println("1. Book a Service");
            System.out.println("2. View Bookings");
            System.out.println("3. Rate a Service");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Make a Payment");
            System.out.println("6. Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    bookService(userId);
                    break;
                case 2:
                    viewBookings(userId);
                    break;
                case 3:
                    rateService(userId);
                    break;
                case 4:
                    System.out.print("Enter Booking ID to cancel: ");
                    int bookingId = scanner.nextInt();
                    cancelBooking(bookingId);
                    break;
                case 5:
                    System.out.print("Enter Booking ID to make payment: ");
                    int paymentBookingId = scanner.nextInt();
                    makePayment(userId, paymentBookingId);
                    break;
                case 6:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void bookService(int userId) {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            // Step 1: Fetch unique services from available providers
            String sql = "SELECT DISTINCT service_type FROM ServiceProviders WHERE availability = TRUE";
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("Available Services:");
            int serviceCount = 0;
            while (rs.next()) {
                serviceCount++;
                System.out.println(serviceCount + ": " + rs.getString("service_type"));
            }

            if (serviceCount == 0) {
                System.out.println("No services available.");
                return;
            }

            // Step 2: Select service
            System.out.print("Select Service (Enter number): ");
            int serviceNumber = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Get selected service
            rs = stmt.executeQuery("SELECT DISTINCT service_type FROM ServiceProviders WHERE availability = TRUE");
            String selectedService = null;
            int count = 0;
            while (rs.next()) {
                count++;
                if (count == serviceNumber) {
                    selectedService = rs.getString("service_type");
                    break;
                }
            }

            if (selectedService == null) {
                System.out.println("Invalid service selection.");
                return;
            }

            // Step 3: Get booking date and time
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date bookingDateTime = null;

            while (true) {
                System.out.print("Enter booking date and time (yyyy-MM-dd HH:mm): ");
                String dateTimeInput = scanner.nextLine();

                try {
                    bookingDateTime = dateTimeFormat.parse(dateTimeInput);
                    if (bookingDateTime.before(new Date())) {
                        System.out.println("Booking time cannot be in the past. Try again.");
                    } else {
                        break;
                    }
                } catch (ParseException e) {
                    System.out.println("Invalid format. Use yyyy-MM-dd HH:mm.");
                }
            }

            // Step 4: Get available providers for the selected service
            sql = "SELECT provider_id, name, phone, price FROM ServiceProviders " +
                    "WHERE service_type = ? AND availability = TRUE AND provider_id NOT IN " +
                    "(SELECT provider_id FROM Bookings WHERE booking_date = ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, selectedService);
            pstmt.setTimestamp(2, new Timestamp(bookingDateTime.getTime()));
            ResultSet providersRs = pstmt.executeQuery();

            System.out.println("Available Providers:");
            int providerCount = 0;
            while (providersRs.next()) {
                providerCount++;
                System.out.println(
                        "Provider ID: " + providersRs.getInt("provider_id") +
                                ", Name: " + providersRs.getString("name") +
                                ", Phone: " + providersRs.getString("phone") +
                                ", Price: $" + providersRs.getDouble("price")
                );
            }

            if (providerCount == 0) {
                System.out.println("No available providers for the selected time. Try a different time.");
                return;
            }

            // Step 5: Select provider
            System.out.print("Select Provider ID: ");
            int providerId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            sql = "SELECT price FROM ServiceProviders WHERE provider_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, providerId);
            ResultSet priceRs = pstmt.executeQuery();

            double price = 0.0;
            if (priceRs.next()) {
                price = priceRs.getDouble("price");
            } else {
                System.out.println("Invalid provider ID.");
                return;
            }

            // Step 6: Enter description
            System.out.print("Enter description: ");
            String description = scanner.nextLine();
            if (description.trim().isEmpty()) {
                System.out.println("Description cannot be empty.");
                return;
            }

            // Step 7: Insert booking
            sql = "INSERT INTO Bookings (user_id, provider_id, booking_date, price, status, description) " +
                    "VALUES (?, ?, ?, ?, 'Pending', ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, providerId);
            pstmt.setTimestamp(3, new Timestamp(bookingDateTime.getTime()));
            pstmt.setDouble(4, price);
            pstmt.setString(5, description);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("Booking successful!");
            } else {
                System.out.println("Booking failed. Try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void cancelBooking(int bookingId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            // Check if the booking exists and belongs to the user
            String checkSql = "SELECT status FROM Bookings WHERE booking_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, bookingId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Booking not found.");
                return;
            }

            String currentStatus = rs.getString("status");
            if (currentStatus.equalsIgnoreCase("Cancelled")) {
                System.out.println("This booking is already cancelled.");
                return;
            }

            // Update status to "Cancelled"
            String sql = "UPDATE Bookings SET status = 'Cancelled' WHERE booking_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookingId);

            int result = stmt.executeUpdate();
            if (result > 0) {
                System.out.println("Booking cancelled successfully!");
            } else {
                System.out.println("Booking not found or can't be cancelled.");
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred: " + e.getMessage());
        }
    }

    public static void rateService(int userId) {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            // Fetch completed bookings for the user
            String sql = "SELECT b.booking_id, b.provider_id, sp.name AS provider_name, sp.service_type " +
                    "FROM Bookings b " +
                    "JOIN ServiceProviders sp ON b.provider_id = sp.provider_id " +
                    "WHERE b.user_id = ? AND b.status = 'Completed'";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<Integer> bookingIds = new ArrayList<>();
            Map<Integer, Integer> providerMap = new HashMap<>();
            int index = 1;

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                int providerId = rs.getInt("provider_id");
                bookingIds.add(bookingId);
                providerMap.put(bookingId, providerId);
                System.out.println(index++ + ". " + rs.getString("provider_name") + " - " + rs.getString("service_type"));
            }

            if (bookingIds.isEmpty()) {
                System.out.println("No completed bookings to rate.");
                return;
            }

            // Select a booking to rate
            System.out.print("Select a booking to rate (Enter number): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice < 1 || choice > bookingIds.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            int bookingId = bookingIds.get(choice - 1);
            int providerId = providerMap.get(bookingId);

            // Check if the user has already rated this booking
            String checkRatingSql = "SELECT COUNT(*) FROM Ratings WHERE user_id = ? AND booking_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkRatingSql);
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, bookingId);
            ResultSet checkRs = checkStmt.executeQuery();
            checkRs.next();

            if (checkRs.getInt(1) > 0) {
                System.out.println("You have already rated this booking.");
                return;
            }

            // Ensure only integer rating (1-5) is accepted
            int rating;
            while (true) {
                System.out.print("Enter rating (1-5): ");
                if (scanner.hasNextInt()) {
                    rating = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    if (rating >= 1 && rating <= 5) {
                        break; // Valid input
                    }
                } else {
                    scanner.next(); // Clear invalid input
                }
                System.out.println("Invalid input. Please enter an **integer** between 1 and 5.");
            }

            // Review is optional
            System.out.print("Enter review (optional): ");
            String review = scanner.nextLine().trim(); // Remove extra spaces
            if (review.isEmpty()) {
                review = null; // Store NULL in database
            }

            // Insert rating into the database
            String insertSql = "INSERT INTO Ratings (user_id, provider_id, booking_id, rating, review) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, providerId);
            insertStmt.setInt(3, bookingId);
            insertStmt.setInt(4, rating);
            insertStmt.setObject(5, review, Types.VARCHAR); // Use setObject to safely insert NULL

            if (insertStmt.executeUpdate() > 0) {
                System.out.println("Thank you! Your rating has been submitted.");
            } else {
                System.out.println("Failed to submit rating.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewBookings(int userId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            // Fetch bookings for the user, including provider and service details
            String sql = "SELECT b.booking_id, b.provider_id, b.status, sp.name AS provider_name, sp.service_type " +
                    "FROM Bookings b " +
                    "JOIN ServiceProviders sp ON b.provider_id = sp.provider_id " +
                    "WHERE b.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Your Bookings:");
            while (rs.next()) {
                System.out.println(
                        "Booking ID: " + rs.getInt("booking_id") +
                                ", Service: " + rs.getString("service_type") +
                                ", Provider: " + rs.getString("provider_name") +
                                ", Status: " + rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void makePayment(int userId, int bookingId) {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/homeservice", "root", "Vanitha@4829")) {
            // Fetch booking details
            String bookingSql = "SELECT price, status FROM Bookings WHERE booking_id = ? AND user_id = ?";
            PreparedStatement bookingStmt = conn.prepareStatement(bookingSql);
            bookingStmt.setInt(1, bookingId);
            bookingStmt.setInt(2, userId);
            ResultSet bookingRs = bookingStmt.executeQuery();

            if (!bookingRs.next()) {
                System.out.println("Booking not found or does not belong to you.");
                return;
            }

            double price = bookingRs.getDouble("price");
            String status = bookingRs.getString("status");

            if ("Paid".equalsIgnoreCase(status)) {
                System.out.println("This booking is already paid.");
                return;
            }

            // Fetch user's wallet balance
            String userSql = "SELECT wallet_balance FROM Users WHERE user_id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setInt(1, userId);
            ResultSet userRs = userStmt.executeQuery();

            if (!userRs.next()) {
                System.out.println("User not found.");
                return;
            }

            double walletBalance = userRs.getDouble("wallet_balance");

            // Check if the user is eligible for a milestone reward
            double rewardAmount = getMilestoneReward(userId, conn);
            if (rewardAmount > 0) {
                walletBalance += rewardAmount;
                System.out.println("Congratulations! You received a ₹" + rewardAmount + " wallet reward.");
            }

            System.out.println("\nTotal amount to pay: ₹" + price);
            System.out.println("Your wallet balance: ₹" + walletBalance);
            System.out.println("Select payment method: 1. Wallet  2. Credit Card");
            int paymentMethod = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            boolean paymentSuccess = false;

            if (paymentMethod == 1) {
                if (walletBalance >= price) {
                    updateWalletBalance(conn, userId, -price);
                    paymentSuccess = true;
                    System.out.println(" ₹" + price + " deducted from wallet. Payment successful!");
                } else {
                    double remainingAmount = price - walletBalance;
                    updateWalletBalance(conn, userId, -walletBalance);
                    System.out.println(" ₹" + walletBalance + " paid from wallet. ₹" + remainingAmount + " needs to be paid via Credit Card.");

                    if (processCreditCardPayment(scanner, remainingAmount)) {
                        paymentSuccess = true;
                    }
                }
            } else if (paymentMethod == 2) {
                if (processCreditCardPayment(scanner, price)) {
                    paymentSuccess = true;
                }
            } else {
                System.out.println("Invalid payment method.");
            }

            if (paymentSuccess) {
                String updateBookingSql = "UPDATE Bookings SET status = 'Paid' WHERE booking_id = ?";
                PreparedStatement updateBookingStmt = conn.prepareStatement(updateBookingSql);
                updateBookingStmt.setInt(1, bookingId);
                updateBookingStmt.executeUpdate();
                System.out.println("Booking status updated to 'Paid'.");
            } else {
                System.out.println("Payment failed. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // **Process Credit Card Payment**
    private static boolean processCreditCardPayment(Scanner scanner, double amount) {
        System.out.println("\n Enter Credit Card Details:");

        // Validate card number (16 digits)
        String cardNumber;
        while (true) {
            System.out.print("➡ Card Number (16 digits): ");
            cardNumber = scanner.nextLine().trim();
            if (!Pattern.matches("\\d{16}", cardNumber)) {
                System.out.println("Invalid! Must be exactly 16 digits (numbers only). Try again.");
            } else {
                break;
            }
        }

        // Validate CVV (3 digits)
        String cvv;
        while (true) {
            System.out.print("➡ CVV (3 digits): ");
            cvv = scanner.nextLine().trim();
            if (!Pattern.matches("\\d{3}", cvv)) {
                System.out.println("Invalid! CVV must be exactly 3 digits.");
            } else {
                break;
            }
        }

        // Validate Expiry Date (MM/YY, must be a future date)
        String expiryDate;
        while (true) {
            System.out.print("➡ Expiry Date (MM/YY): ");
            expiryDate = scanner.nextLine().trim();
            if (!Pattern.matches("(0[1-9]|1[0-2])/\\d{2}", expiryDate)) {
                System.out.println("Invalid format! Use MM/YY.");
            } else if (!isFutureExpiry(expiryDate)) {
                System.out.println("Expiry date has passed! Enter a future date.");
            } else {
                break;
            }
        }

        System.out.println("\nProcessing payment of ₹" + amount + "... ");
        System.out.println("Payment successful! Thank you for using your credit card.");
        return true;
    }

    private static boolean isFutureExpiry(String expiryDate) {
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt("20" + parts[1]); // Convert YY to YYYY
        YearMonth expiry = YearMonth.of(year, month);
        return expiry.isAfter(YearMonth.now());
    }

    private static double getMilestoneReward(int userId, Connection conn) throws SQLException {
        double reward = 0;
        String countSql = "SELECT COUNT(*) FROM Bookings WHERE user_id = ? AND status = 'Paid'";
        PreparedStatement countStmt = conn.prepareStatement(countSql);
        countStmt.setInt(1, userId);
        ResultSet countRs = countStmt.executeQuery();
        int paidBookingCount = countRs.next() ? countRs.getInt(1) : 0;

        if (paidBookingCount == 5) {
            reward = 50;
        } else if (paidBookingCount == 10) {
            reward = 60;
        }

        if (reward > 0) {
            updateWalletBalance(conn, userId, reward);
        }
        return reward;
    }

    private static void updateWalletBalance(Connection conn, int userId, double amount) throws SQLException {
        String sql = "UPDATE Users SET wallet_balance = wallet_balance + ? WHERE user_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDouble(1, amount);
        stmt.setInt(2, userId);
        stmt.executeUpdate();
    }



}