package com.example.taskmanager.controller;

import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class DashboardController {

    private final UserRepository userRepo;
    private final TeamRepository teamRepo;
    private final TaskRepository taskRepo;

    public DashboardController(UserRepository userRepo, TeamRepository teamRepo, TaskRepository taskRepo) {
        this.userRepo = userRepo;
        this.teamRepo = teamRepo;
        this.taskRepo = taskRepo;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userRepo.findByUsername(userDetails.getUsername()).orElseThrow();
        model.addAttribute("user", currentUser);

        if (currentUser.getRole() == User.Role.ADMIN) {
            model.addAttribute("tasks", taskRepo.findAll());
            model.addAttribute("teams", teamRepo.findAll());
            model.addAttribute("users", userRepo.findAll());
        } else if (currentUser.getRole() == User.Role.MANAGER) {
            List<Team> myTeams = teamRepo.findByManager(currentUser);
            model.addAttribute("teams", myTeams);
            model.addAttribute("tasks", taskRepo.findByTeamIn(myTeams));
        } else {
            model.addAttribute("tasks", taskRepo.findByAssignedTo(currentUser));
            model.addAttribute("teams", teamRepo.findByMembersContaining(currentUser));
        }

        return "dashboard";
    }
}