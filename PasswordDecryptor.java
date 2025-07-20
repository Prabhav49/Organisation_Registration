import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordDecryptor {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String targetHash = "$2a$10$WYqMtcQCBf.J27XC76W68.tnvIMNaRXvg..6A3sZQZvivcZCdoQ.u";
        
        // Common passwords to try
        String[] commonPasswords = {
            "admin",
            "password", 
            "123456",
            "admin123",
            "superadmin",
            "secret",
            "password123",
            "admin@123",
            "root",
            "test",
            "qwerty",
            "letmein",
            "welcome",
            "administrator",
            "Admin@123",
            "Password@123",
            "SuperAdmin@123",
            "organization",
            "Organization@123",
            "temp123",
            "default",
            "changeme",
            "P@ssw0rd",
            "admin@organization",
            "superadmin@organization"
        };
        
        System.out.println("Attempting to find password for hash: " + targetHash);
        System.out.println("Trying common passwords...\n");
        
        for (String password : commonPasswords) {
            if (encoder.matches(password, targetHash)) {
                System.out.println("🎉 PASSWORD FOUND!");
                System.out.println("Password: " + password);
                System.out.println("Hash: " + targetHash);
                return;
            } else {
                System.out.println("❌ " + password + " - No match");
            }
        }
        
        System.out.println("\n❌ Password not found in common passwords list.");
        System.out.println("The password might be a custom one not in the common list.");
        System.out.println("\nTo reset the password, you can:");
        System.out.println("1. Update the database directly with a new hash");
        System.out.println("2. Use the password reset functionality in the application");
    }
}
