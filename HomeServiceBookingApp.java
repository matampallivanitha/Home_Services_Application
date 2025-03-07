import java.util.Scanner;

public class HomeServiceBookingApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Home Service Booking System");
            System.out.println("1. User Registration");
            System.out.println("2. User Login");
            System.out.println("3. Service Provider Registration");
            System.out.println("4. Service Provider Login");
            System.out.println("5. Admin Login");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    UserModule.registerUser();
                    break;
                case 2:
                    UserModule.loginUser();
                    break;
                case 3:
                    ServiceProviderModule.registerServiceProvider();
                    break;
                case 4:
                    ServiceProviderModule.loginServiceProvider();
                    break;
                case 5:
                    System.out.println("Enter Admin Username: ");
                    String username = scanner.next();
                    System.out.println("Enter Admin Password: ");
                    String password = scanner.next();

                    AdminModule admin = new AdminModule();
                    if (admin.login(username, password)) {
                        System.out.println("Admin logged in successfully.");
                        admin.adminMenu(); // Redirects to Admin Dashboard
                    } else {
                        System.out.println("Invalid credentials. Returning to main menu...");
                    }
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}