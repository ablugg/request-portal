package com.github.ablugg.requestportal.config;

import com.github.ablugg.requestportal.staff.StaffUser;
import com.github.ablugg.requestportal.staff.StaffUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final StaffUserRepository repository;
    private final PasswordEncoder encoder;

    public DataSeeder(StaffUserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (repository.findByUsername("admin").isEmpty()) {
            repository.save(new StaffUser("admin", encoder.encode("admin"), "STAFF"));
        }
    }
}
