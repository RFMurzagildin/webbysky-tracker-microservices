package ru.webbyskytracker.usersservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role")
    private UserRole role = UserRole.USER_ROLE;

    @Column(name = "mail_verified")
    private Boolean emailVerified;

    public User(String username, String email, String password, Boolean emailVerified){
        this.username = username;
        this.email = email;
        this.password = password;
        this.emailVerified = emailVerified;
    }
}
