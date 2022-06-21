package com.ivaylo.blog.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "user_email_unique", columnNames = "email"),
        @UniqueConstraint(name = "user_name_unique", columnNames = "username")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 20)
    private Long id;

    private String sessionId;
    private boolean isLogin = false;
    @Column(nullable = false,length = 100)
    private String username;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false, length = 72)
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Blog> blogs;

    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
