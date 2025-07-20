import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ResetPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String newPassword = "super123";
        String hashedPassword = encoder.encode(newPassword);
        
        System.out.println("Original Password: " + newPassword);
        System.out.println("BCrypt Hash: " + hashedPassword);
        
        // Verify the hash works
        boolean matches = encoder.matches(newPassword, hashedPassword);
        System.out.println("Verification successful: " + matches);
    }
}
