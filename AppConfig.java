import java.util.Scanner;

public class AppConfig {
    private static ThreadingMode threadingMode = ThreadingMode.YIELD;  // Default
    private static boolean debugLogsEnabled = false;  // Flag to enable/disable new debug logs

    public static ThreadingMode getThreadingMode() {
        return threadingMode;
    }

    public static void setThreadingMode(ThreadingMode mode) {
        threadingMode = mode;
        System.out.println("Threading mode changed to: " + mode);
    }

    public static boolean isDebugLogsEnabled() {
        return debugLogsEnabled;
    }

    public static void setDebugLogsEnabled(boolean enabled) {
        debugLogsEnabled = enabled;
        System.out.println("[CONFIG] Debug logs " + (enabled ? "ENABLED" : "DISABLED"));
    }

    /**
     * Display interactive menu for user to choose threading mode
     */
    public static void displayThreadingModeMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean validInput = false;
        
        while (!validInput) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("SELECT THREADING MODE");
            System.out.println("=".repeat(50));
            System.out.println("\n1. BUSY_WAIT");
          
            System.out.println("2. YIELD");
         
            System.out.print("Enter your choice (1-2): ");
            
            try {
                int choice = scanner.nextInt();
                
                switch(choice) {
                    case 1:
                        setThreadingMode(ThreadingMode.BUSY_WAIT);
                        validInput = true;
                        break;
                    case 2:
                        setThreadingMode(ThreadingMode.YIELD);
                        validInput = true;
                        break;
                    default:
                        System.out.println("\n✗ Invalid choice. Please enter 1, or 2.");
                }
            } catch (Exception e) {
                System.out.println("\n✗ Invalid input. Please enter a number (1-2).");
                scanner.nextLine();  // Clear invalid input
            }
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Initializing system with " + threadingMode + " mode...");
        System.out.println("=".repeat(50) + "\n");
    }
}