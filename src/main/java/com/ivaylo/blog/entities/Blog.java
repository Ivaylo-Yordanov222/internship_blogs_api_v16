package com.ivaylo.blog.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(indexes = @Index(name = "title_index", columnList = "slug"))
@JsonIgnoreProperties("user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Blog{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 20)
    private Long id;
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String slug;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id",foreignKey = @ForeignKey(name="FK_blog_user"))
    private User user;

    @OneToMany(mappedBy = "blog", cascade = {CascadeType.REMOVE})
    private List<Article> articles;

    public Blog(String title, String slug, User user){
        this.title = title;
        this.slug = slug;
        this.user = user;
    }
}
