package isima.georganise.app.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GetPlaceVicinityDTO implements Serializable {

    private BigDecimal latitude;

    private BigDecimal longitude;

    private BigDecimal radius;
}
