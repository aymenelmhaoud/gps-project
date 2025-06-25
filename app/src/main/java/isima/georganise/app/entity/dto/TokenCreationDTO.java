package isima.georganise.app.entity.dto;

import isima.georganise.app.entity.util.Right;
import lombok.Data;

import java.io.Serializable;

@Data
public class TokenCreationDTO implements Serializable {

    private Right accessRight;

    private Long userId;

    private Long tagId;
}
