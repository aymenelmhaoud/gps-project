package isima.georganise.app.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ImageCreationDTO implements Serializable {

    private String imageValue;

    private String name;

    private String description;

    private Boolean isPublic;
}
