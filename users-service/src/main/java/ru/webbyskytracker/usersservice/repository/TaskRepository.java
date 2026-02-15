package ru.webbyskytracker.usersservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.webbyskytracker.usersservice.entity.Task;
import ru.webbyskytracker.usersservice.entity.User;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);

    Task getTaskById(Long id);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND DATE(t.time) = :date ORDER BY t.time ASC")
    List<Task> findByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND DATE(t.time) = :date")
    Long countTasksByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);
}
