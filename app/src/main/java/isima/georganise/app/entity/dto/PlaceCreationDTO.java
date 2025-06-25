package isima.georganise.app.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PlaceCreationDTO implements Serializable {

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String name;

    private String description;

    private List<Long> tagIds;

    private Long imageId;
}
