import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class VerifyPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "super123";
        String hash = "$2a$10$meqCgba/zi0x2OFbAwi1N.irGSue.NjBzbY.mwvfIVzubiCpqo5oy";
        
        boolean matches = encoder.matches(password, hash);
        
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Password matches hash: " + matches);
        
        if (matches) {
            System.out.println("✅ SUCCESS: You can now login with:");
            System.out.println("   Email: superadmin@organization.com");
            System.out.println("   Password: super123");
        } else {
            System.out.println("❌ ERROR: Password verification failed");
        }
    }
}
