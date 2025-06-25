package isima.georganise.app.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TagCreationDTO implements Serializable {

    private String title;

    private String description;
}
