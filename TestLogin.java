import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestLogin {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "super123";
        String adminPassword = "admin123";
        
        // Test superadmin password
        String superadminHash = "$2a$10$meqCgba/zi0x2OFbAwi1N.irGSue.NjBzbY.mwvfIVzubiCpqo5oy";
        boolean superMatches = encoder.matches(password, superadminHash);
        
        // Generate admin password hash
        String adminHash = encoder.encode(adminPassword);
        
        System.out.println("🔐 LOGIN TEST RESULTS:");
        System.out.println("================================");
        System.out.println("Superadmin Test:");
        System.out.println("  Email: superadmin@organization.com");
        System.out.println("  Password: " + password);
        System.out.println("  Status: " + (superMatches ? "✅ VALID" : "❌ INVALID"));
        System.out.println();
        System.out.println("Admin Test:");
        System.out.println("  Email: admin@organization.com");
        System.out.println("  Password: " + adminPassword);
        System.out.println("  New Hash: " + adminHash);
        System.out.println("  Status: ✅ READY TO UPDATE");
    }
}
