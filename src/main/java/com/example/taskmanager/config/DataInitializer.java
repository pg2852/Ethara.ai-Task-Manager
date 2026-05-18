package com.example.taskmanager.config;

import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final TeamRepository teamRepo;
    private final TaskRepository taskRepo;
    private final PasswordEncoder encoder;

    // Constructor injection (no Lombok needed)
    public DataInitializer(UserRepository userRepo, TeamRepository teamRepo,
                           TaskRepository taskRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.teamRepo = teamRepo;
        this.taskRepo = taskRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {

        // --- Create Users ---
        User admin = new User("admin", encoder.encode("admin123"), "Admin User", User.Role.ADMIN);
        User manager = new User("manager", encoder.encode("manager123"), "John Manager", User.Role.MANAGER);
        User alice = new User("alice", encoder.encode("alice123"), "Alice Smith", User.Role.MEMBER);
        User bob = new User("bob", encoder.encode("bob123"), "Bob Jones", User.Role.MEMBER);

        userRepo.save(admin);
        userRepo.save(manager);
        userRepo.save(alice);
        userRepo.save(bob);

        // --- Create Team ---
        Team team = new Team();
        team.setName("Dev Team");
        team.setDescription("Main development team");
        team.setManager(manager);
        team.getMembers().add(alice);
        team.getMembers().add(bob);
        teamRepo.save(team);

        // --- Create Tasks ---
        Task t1 = new Task();
        t1.setTitle("Setup project");
        t1.setDescription("Initialize repo and configure CI");
        t1.setStatus(Task.Status.DONE);
        t1.setPriority(Task.Priority.HIGH);
        t1.setAssignedTo(alice);
        t1.setTeam(team);
        t1.setCreatedBy(manager);
        t1.setDueDate(LocalDate.now().plusDays(3));
        taskRepo.save(t1);

        Task t2 = new Task();
        t2.setTitle("Write unit tests");
        t2.setDescription("Cover all service layer methods");
        t2.setStatus(Task.Status.IN_PROGRESS);
        t2.setPriority(Task.Priority.MEDIUM);
        t2.setAssignedTo(bob);
        t2.setTeam(team);
        t2.setCreatedBy(manager);
        t2.setDueDate(LocalDate.now().plusDays(7));
        taskRepo.save(t2);

        Task t3 = new Task();
        t3.setTitle("Deploy to staging");
        t3.setDescription("Deploy latest build to staging server");
        t3.setStatus(Task.Status.TODO);
        t3.setPriority(Task.Priority.LOW);
        t3.setAssignedTo(alice);
        t3.setTeam(team);
        t3.setCreatedBy(manager);
        t3.setDueDate(LocalDate.now().plusDays(14));
        taskRepo.save(t3);
    }
}