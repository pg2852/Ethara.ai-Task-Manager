package com.example.taskmanager.controller;

import com.example.taskmanager.model.*;
import com.example.taskmanager.repository.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepo;
    private final UserRepository userRepo;
    private final TeamRepository teamRepo;

    public TaskController(TaskRepository taskRepo, UserRepository userRepo, TeamRepository teamRepo) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
        this.teamRepo = teamRepo;
    }

    @GetMapping
    public String listTasks(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        if (user.getRole() == User.Role.ADMIN) {
            model.addAttribute("tasks", taskRepo.findAll());
        } else if (user.getRole() == User.Role.MANAGER) {
            model.addAttribute("tasks", taskRepo.findByTeamIn(teamRepo.findByManager(user)));
        } else {
            model.addAttribute("tasks", taskRepo.findByAssignedTo(user));
        }
        return "tasks";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("teams", teamRepo.findAll());
        model.addAttribute("priorities", Task.Priority.values());
        return "task-form";
    }

    @PostMapping("/create")
    public String createTask(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam Task.Priority priority,
                              @RequestParam Long assignedToId,
                              @RequestParam Long teamId,
                              @RequestParam String dueDate,
                              @AuthenticationPrincipal UserDetails ud) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setAssignedTo(userRepo.findById(assignedToId).orElseThrow());
        task.setTeam(teamRepo.findById(teamId).orElseThrow());
        task.setDueDate(LocalDate.parse(dueDate));
        task.setCreatedBy(userRepo.findByUsername(ud.getUsername()).orElseThrow());
        taskRepo.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Task.Status status) {
        Task task = taskRepo.findById(id).orElseThrow();
        task.setStatus(status);
        taskRepo.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        taskRepo.deleteById(id);
        return "redirect:/tasks";
    }
}