package isima.georganise.app.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetUserNicknameDTO implements Serializable {

    private String nickname;
}
