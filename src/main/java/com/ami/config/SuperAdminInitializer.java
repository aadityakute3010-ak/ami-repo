package com.ami.config;

import com.ami.entity.User;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.StatusType;
import com.ami.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SuperAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public SuperAdminInitializer(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    } 

    @Override
    public void run(String... args) {

        String email = "superadmin@gmail.com";

        if (userRepository.findByEmail(email).isPresent()) {
//            System.out.println("Super Admin Already Exists");
            return;
        } 

        User superAdmin = new User();

        superAdmin.setFirstName("Super");
        superAdmin.setLastName("Admin");

        superAdmin.setEmail(email);

        superAdmin.setPassword(passwordEncoder.encode("12345678")); 

        superAdmin.setRole(RoleType.SUPER_ADMIN);

        superAdmin.setStatus(StatusType.ACTIVE);

        superAdmin.setAssignedSources(Set.of(SourceType.values()));  

        userRepository.save(superAdmin);

        System.out.println("Super Admin Created Successfully");
    }
}