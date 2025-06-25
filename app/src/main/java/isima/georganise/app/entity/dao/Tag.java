package isima.georganise.app.entity.dao;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import isima.georganise.app.entity.dto.TagCreationDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "TAGS")
public class Tag implements Serializable {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false, unique = true)
    private Long tagId;

    @Column(name = "TITLE", nullable = false, unique = true)
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "USERID", updatable = false, nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "tag")
    @JsonManagedReference
    private List<PlaceTag> placeTags;

    public Tag(@NotNull TagCreationDTO tag, Long userId) {
        this.title = tag.getTitle();
        this.description = tag.getDescription();
        this.userId = userId;
    }
}
