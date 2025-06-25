package isima.georganise.app.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PlaceUpdateDTO {

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String name;

    private String description;

    private Long imageId;

    private List<Long> tagIds;

}
