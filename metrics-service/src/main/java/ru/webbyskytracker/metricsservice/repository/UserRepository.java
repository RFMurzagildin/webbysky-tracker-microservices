package ru.webbyskytracker.metricsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.webbyskytracker.metricsservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
