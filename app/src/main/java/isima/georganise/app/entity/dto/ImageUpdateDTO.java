package isima.georganise.app.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ImageUpdateDTO implements Serializable {

    private String name;

    private String description;

    private Boolean isPublic;
}
