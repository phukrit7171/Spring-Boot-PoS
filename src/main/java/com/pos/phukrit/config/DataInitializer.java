package com.pos.phukrit.config;

import com.pos.phukrit.models.UserModel;
import com.pos.phukrit.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create a default ADMIN user if one doesn't exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserModel admin = new UserModel();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("password")); // Hash the password
            admin.setRole(UserModel.Role.ADMIN);
            admin.setName("Default Admin");
            admin.setEmail("admin@example.com");
            userRepository.save(admin);
            System.out.println(">>> Created default ADMIN user");
        }

        // Create a default STAFF user if one doesn't exist
        if (userRepository.findByUsername("staff").isEmpty()) {
            UserModel staff = new UserModel();
            staff.setUsername("staff");
            staff.setPassword(passwordEncoder.encode("password")); // Hash the password
            staff.setRole(UserModel.Role.STAFF);
            staff.setName("Default Staff");
            staff.setEmail("staff@example.com");
            userRepository.save(staff);
            System.out.println(">>> Created default STAFF user");
        }
    }
}