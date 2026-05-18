package com.example.taskmanager.controller;

import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teams")
public class TeamController {

    private final TeamRepository teamRepo;
    private final UserRepository userRepo;

    public TeamController(TeamRepository teamRepo, UserRepository userRepo) {
        this.teamRepo = teamRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public String listTeams(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        if (user.getRole() == User.Role.ADMIN) {
            model.addAttribute("teams", teamRepo.findAll());
        } else if (user.getRole() == User.Role.MANAGER) {
            model.addAttribute("teams", teamRepo.findByManager(user));
        } else {
            model.addAttribute("teams", teamRepo.findByMembersContaining(user));
        }
        return "teams";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("users", userRepo.findAll());
        return "team-form";
    }

    @PostMapping("/create")
    public String createTeam(@RequestParam String name,
                              @RequestParam String description,
                              @RequestParam Long managerId,
                              @RequestParam(required = false) Long[] memberIds) {
        Team team = new Team();
        team.setName(name);
        team.setDescription(description);
        team.setManager(userRepo.findById(managerId).orElseThrow());
        if (memberIds != null) {
            for (Long id : memberIds) {
                userRepo.findById(id).ifPresent(team.getMembers()::add);
            }
        }
        teamRepo.save(team);
        return "redirect:/teams";
    }

    @GetMapping("/{id}/delete")
    public String deleteTeam(@PathVariable Long id) {
        teamRepo.deleteById(id);
        return "redirect:/teams";
    }
}