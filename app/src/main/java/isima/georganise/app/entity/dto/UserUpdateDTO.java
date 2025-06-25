package isima.georganise.app.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateDTO implements Serializable {

    private String nickname;

    private String password;

    private String email;
}
