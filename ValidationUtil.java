class ValidationUtil {
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return false;
        }
        if (name.length() > 20) {
            System.out.println("Name cannot exceed 20 characters.");
            return false;
        }
        if (!name.matches("[A-Za-z ]+")) {
            System.out.println("Invalid name. Numbers and special characters are not allowed.");
            return false;
        }
        return true;
    }


    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            System.out.println("Phone number cannot be empty.");
            return false;
        }
        if (!phone.matches("^[6789]\\d{9}$")) {
            System.out.println("Phone number must be exactly 10 digits and start with 6, 7, 8, or 9.");
            return false;
        }
        return true;
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Password cannot be empty.");
            return false;
        }
        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters long.");
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            System.out.println("Password must contain at least one uppercase letter.");
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            System.out.println("Password must contain at least one number.");
            return false;
        }
        if (!password.matches(".*[!@#$%^&*()-+=<>?/{}|~].*")) {
            System.out.println("Password must contain at least one special character.");
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Email cannot be empty.");
            return false;
        }

        if (email.length() > 30) {
            System.out.println("Email cannot exceed 30 characters.");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            System.out.println("Invalid email format.");
            return false;
        }
        return true;
    }

    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            System.out.println("Address cannot be empty.");
            return false;
        }
        if (address.length() > 30) {
            System.out.println("Email cannot exceed 30 characters.");
            return false;
        }
        return true;
    }
    public static boolean isValidServiceType(String serviceType) {
        if (serviceType == null || serviceType.trim().isEmpty()) {
            System.out.println("Service type should not be empty.");
            return false;
        }
        if (serviceType.length() > 20) {
            System.out.println("Name cannot exceed 20 characters.");
            return false;
        }
        if (!serviceType.matches("[a-zA-Z ]+")) {
            System.out.println("Service type should not contain numbers or special characters.");
            return false;
        }

        return true; // Service type is valid
    }


    public static boolean isValidPrice(String priceStr) {
        // Check if the input is empty
        if (priceStr == null || priceStr.trim().isEmpty()) {
            System.out.println("Price should not be empty.");
            return false;
        }

        // Try to parse the string to a double and check if it's a valid number
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                System.out.println("Price should be a positive number.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Price should be a valid number.");
            return false;
        }

        return true; // Price is valid
    }


    // Validate Rating (should be an integer between 1 and 5)
    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }

    // Validate Review (optional, max 500 characters, trims spaces)
    public static boolean isValidReview(String review) {
        return review == null || review.trim().length() <= 500;
    }

}
