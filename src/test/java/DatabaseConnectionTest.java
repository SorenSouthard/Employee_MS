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
            boolean isValid = DatabaseConnection.validateUser("admin", "admin123");
            assertTrue(isValid, "User validation failed for valid credentials");
    
            // Test with valid username but incorrect password
            boolean isInvalidPassword = DatabaseConnection.validateUser("admin", "wrongPass");
            assertFalse(isInvalidPassword, "User validation succeeded for incorrect password");
    
            // Test with invalid username
            boolean isInvalidUser = DatabaseConnection.validateUser("invalidUser", "testPass");
            assertFalse(isInvalidUser, "User validation succeeded for non-existent username");
        } catch (Exception e) {
            fail("Error during user validation test: " + e.getMessage());
        }
    }
}

