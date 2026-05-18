package com.example.taskmanager.repository;

import com.example.taskmanager.model.Team;
import com.example.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByManager(User manager);
    List<Team> findByMembersContaining(User user);
}
