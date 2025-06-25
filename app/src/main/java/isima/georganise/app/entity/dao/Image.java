package isima.georganise.app.entity.dao;

import isima.georganise.app.entity.dto.ImageCreationDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@Table(name = "IMAGES")
public class Image implements Serializable {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false, unique = true)
    private Long imageId;

    @Column(name = "USERID", updatable = false, nullable = false)
    private Long userId;

    @Column(name = "IMAGE", updatable = false, nullable = false)
    private byte[] imageValue;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PUBLIC", nullable = false)
    private boolean isPublic;

    public Image(@NotNull ImageCreationDTO imageCreationDTO, Long userId) {
        this.userId = userId;
        this.imageValue = imageCreationDTO.getImageValue().getBytes();
        this.name = imageCreationDTO.getName();
        this.description = imageCreationDTO.getDescription();
        this.isPublic = imageCreationDTO.getIsPublic();
    }
}
