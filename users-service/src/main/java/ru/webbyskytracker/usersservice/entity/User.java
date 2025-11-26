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

    @Column(name = "mail", nullable = false, unique = true)
    private String mail;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role")
    private UserRole role = UserRole.USER_ROLE;

    public User(String username, String mail, String password){
        this.username = username;
        this.mail = mail;
        this.password = password;
    }
}
