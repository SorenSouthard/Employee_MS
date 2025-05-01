import com.sdproject.DatabaseConnection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {
    @Test
    void testDatabaseConnection() {
        try {
            DatabaseConnection.getConnection();
        } catch (Exception e) {
            System.out.println("Failed to establish database connection");
            fail("Database connection failed: " + e.getMessage());
        }
    }
    
    @Test
    void testValidateUser() {
        try {
            // Test with valid username and matching password
            String isValid = DatabaseConnection.validateUser("admin", "admin123");
            boolean isValidBoolean = (isValid != null);
            assertTrue(isValidBoolean, "User validation failed for valid credentials");
    
            // Test with valid username but incorrect password
            String isInvalidPassword = DatabaseConnection.validateUser("admin", "wrongPass");
            assertFalse(isInvalidPassword != null && !isInvalidPassword.isEmpty(), "User validation succeeded for incorrect password");
    
            // Test with invalid username
            String isInvalidUser = DatabaseConnection.validateUser("invalidUser", "testPass");
            assertFalse(isInvalidUser != null && !isInvalidUser.isEmpty(), "User validation succeeded for non-existent username");
        } catch (Exception e) {
            fail("Error during user validation test: " + e.getMessage());
        }
    }
}