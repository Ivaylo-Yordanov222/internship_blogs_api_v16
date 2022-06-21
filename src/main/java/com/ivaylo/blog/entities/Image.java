package com.ivaylo.blog.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "image" ,uniqueConstraints = {
        @UniqueConstraint(name = "image_article_id_unique", columnNames = "article_id")
})
@JsonIgnoreProperties("article")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(length = 20)
    private Long id;
    @Column(nullable = false)
    private String imageName;
    @Column(nullable = false, length = 300)
    private String url;
    @OneToOne(optional = false)
    @JoinColumn(name = "article_id", referencedColumnName = "id",foreignKey = @ForeignKey(name="FK_image_article"))
    private Article article;

    public Image(String imageName,String url){
        this.url = url;
        this.imageName = imageName;
    }
}
