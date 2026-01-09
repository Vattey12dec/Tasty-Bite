public class SessionManager {
    private static SessionManager instance;
    private String currentUsername;
    private String currentRole;
    private int currentUserId;
    private String currentFullName;

    private SessionManager() {}

    // Thread-safe singleton with double-checked locking
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    /**
     * Login with user credentials
     */
    public void login(String username, String role, int userId) {
        this.currentUsername = username;
        this.currentRole = role;
        this.currentUserId = userId;
    }

    /**
     * Login with full name included
     */
    public void login(String username, String role, int userId, String fullName) {
        this.currentUsername = username;
        this.currentRole = role;
        this.currentUserId = userId;
        this.currentFullName = fullName;
    }

    /**
     * Clear all session data
     */
    public void logout() {
        this.currentUsername = null;
        this.currentRole = null;
        this.currentUserId = -1;
        this.currentFullName = null;
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUsername != null;
    }

    // Getters
    public String getCurrentUsername() {
        return currentUsername;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public String getCurrentFullName() {
        return currentFullName;
    }

    /**
     * Enhanced permission system with granular controls
     */
    public boolean hasPermission(String permission) {
        if (!isLoggedIn()) {
            return false;
        }

        if (currentRole == null) {
            return false;
        }

        String role = currentRole.toLowerCase();

        switch (role) {
            case "admin":
                return true; // Full access to everything

            case "manager":
                return true; // Full access (same as admin)

            case "waiter":
                // Waiters can: manage orders, tables, view customers
                return permission.equals("orders") ||
                        permission.equals("orders.view") ||
                        permission.equals("orders.create") ||
                        permission.equals("orders.update") ||
                        permission.equals("tables") ||
                        permission.equals("tables.view") ||
                        permission.equals("tables.update") ||
                        permission.equals("customers.view");

            case "chef":
                // Chefs can: view menu, view orders, update order status
                return permission.equals("menu") ||
                        permission.equals("menu.view") ||
                        permission.equals("orders") ||  // ADDED THIS
                        permission.equals("orders.view") ||
                        permission.equals("orders.create") ||  // ADDED THIS
                        permission.equals("orders.update");

            case "cashier":
                // Cashiers can: view orders, manage payments, view reports
                return permission.equals("orders") ||  // ADDED THIS
                        permission.equals("orders.view") ||
                        permission.equals("orders.update") ||
                        permission.equals("reports.view");

            case "staff":
                // Basic staff: limited view access
                return permission.equals("menu.view") ||
                        permission.equals("orders.view");

            default:
                return false;
        }
    }

    /**
     * Check if user can access a specific module
     */
    public boolean canAccessModule(String module) {
        if (!isLoggedIn()) {
            return false;
        }

        switch (module.toLowerCase()) {
            case "dashboard":
                return true; // Everyone can access dashboard

            case "customers":
                return hasPermission("customers.view") ||
                        isAdminOrManager();

            case "menu":
                return hasPermission("menu") ||
                        hasPermission("menu.view") ||  // ADDED THIS
                        isAdminOrManager();

            case "orders":
                return hasPermission("orders") ||
                        hasPermission("orders.view") ||  // ADDED THIS
                        isAdminOrManager();

            case "staff":
                return isAdminOrManager();

            case "tables":
                return hasPermission("tables") ||
                        hasPermission("tables.view") ||  // ADDED THIS
                        isAdminOrManager();

            case "reports":
                return isAdminOrManager() ||
                        hasPermission("reports.view");

            default:
                return isAdminOrManager();
        }
    }

    /**
     * Check if current user is Admin or Manager
     */
    public boolean isAdminOrManager() {
        if (currentRole == null) {
            return false;
        }
        String role = currentRole.toLowerCase();
        return role.equals("admin") || role.equals("manager");
    }

    /**
     * Check if current user is Admin
     */
    public boolean isAdmin() {
        return currentRole != null && currentRole.equalsIgnoreCase("admin");
    }

    /**
     * Get user info string for display
     */
    public String getUserInfo() {
        if (!isLoggedIn()) {
            return "Not logged in";
        }
        return String.format("%s (%s)",
                currentFullName != null ? currentFullName : currentUsername,
                currentRole);
    }
}