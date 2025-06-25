package isima.georganise.app.entity.dto;

import isima.georganise.app.entity.dao.Image;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Data
@NoArgsConstructor
public class ImageDTO implements Serializable {

    private Long imageId;

    private Long userId;

    private String imageValue;

    private String name;

    private String description;

    private boolean isPublic;

    public ImageDTO(Image image) {
        this.imageId = image.getImageId();
        this.userId = image.getUserId();
        this.imageValue = Base64.getEncoder().encodeToString(Base64.getDecoder().decode(image.getImageValue()));
        this.name = image.getName();
        this.description = image.getDescription();
        this.isPublic = image.isPublic();
    }

    public static List<ImageDTO> fromImages(Iterable<Image> images) {
        List<ImageDTO> imageDTOS = new ArrayList<>();
        for (Image image : images) {
            imageDTOS.add(new ImageDTO(image));
        }
        return imageDTOS;
    }
}
