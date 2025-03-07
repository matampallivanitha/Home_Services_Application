import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

class AdminModule {
    private Scanner scanner = new Scanner(System.in);

    public boolean login(String username, String password) {
        try (Connection conn = DBUtil.getConnection()) { // Use DBUtil connection
            String sql = "SELECT * FROM Admins WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void viewBookings() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT b.booking_id, u.name AS user, sp.name AS provider, sp.service_type, b.booking_date, b.price, b.status " +
                    "FROM Bookings b " +
                    "JOIN Users u ON b.user_id = u.user_id " +
                    "JOIN ServiceProviders sp ON b.provider_id = sp.provider_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (!rs.isBeforeFirst()) {
                System.out.println("No bookings available.");
                return;
            }

            System.out.println("+------------+----------------+--------------------+----------------+---------------------+--------+-----------+");
            System.out.println("| Booking ID | User           | Service Provider  | Service         | Booking Date        | Price  | Status    |");
            System.out.println("+------------+----------------+--------------------+----------------+---------------------+--------+-----------+");

            while (rs.next()) {
                System.out.printf("| %-10d | %-14s | %-18s | %-16s | %-22s | %-6.2f | %-9s |%n",
                        rs.getInt("booking_id"),
                        rs.getString("user"),
                        rs.getString("provider"),
                        rs.getString("service_type"),
                        rs.getTimestamp("booking_date"),
                        rs.getDouble("price"),
                        rs.getString("status"));
            }

            System.out.println("+------------+----------------+--------------------+----------------+---------------------+--------+-----------+");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteServiceProvider() {
        System.out.print("Enter Service Provider ID to delete: ");
        int providerId;

        try {
            providerId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter a valid number.");
            scanner.nextLine(); // Clear invalid input
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Step 1: Delete bookings related to the service provider
            String deleteBookingsSQL = "DELETE FROM Bookings WHERE provider_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteBookingsSQL)) {
                stmt.setInt(1, providerId);
                stmt.executeUpdate();
            }

            // Step 2: Delete service provider after bookings are removed
            String deleteProviderSQL = "DELETE FROM ServiceProviders WHERE provider_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteProviderSQL)) {
                stmt.setInt(1, providerId);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit(); // Commit transaction
                    System.out.println("Service provider deleted successfully.");
                } else {
                    conn.rollback(); // Rollback changes
                    System.out.println("Service provider not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteUser() {
        System.out.print("Enter User ID to delete: ");
        int userId = scanner.nextInt();
        scanner.nextLine();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "DELETE FROM Users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void adminMenu() {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. View Booking History");
            System.out.println("2. Delete Service Provider");
            System.out.println("3. Delete User");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewBookings();
                    break;
                case 2:
                    deleteServiceProvider();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
}



