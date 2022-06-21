package com.ivaylo.blog.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
@Entity
@Table(indexes = @Index(name = "art_title_index", columnList = "title"))
@JsonIgnoreProperties("blog")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 20)
    private Long id;
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String slug;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @ManyToOne
    @JoinColumn(name = "blog_id", referencedColumnName = "id",foreignKey = @ForeignKey(name="FK_article_blog"))
    private Blog blog;
    @OneToOne(mappedBy = "article",cascade = {CascadeType.REMOVE})
    private Image image;

    public Article(String title,String content, String slug, Blog blog){
        this.title = title;
        this.content = content;
        this.slug = slug;
        this.blog = blog;
    }
}
