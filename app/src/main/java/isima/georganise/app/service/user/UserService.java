package isima.georganise.app.service.user;

import isima.georganise.app.entity.dao.User;
import isima.georganise.app.entity.dto.GetUserNicknameDTO;
import isima.georganise.app.entity.dto.UserCreationDTO;
import isima.georganise.app.entity.dto.UserLoginDTO;
import isima.georganise.app.entity.dto.UserUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {

    List<User> getAllUsers(UUID authToken);

    GetUserNicknameDTO getUserById(UUID authToken, Long id);

    UUID createUser(UserCreationDTO user);

    void deleteUser(UUID authToken, Long id);

    User updateUser(UUID authToken, Long id, UserUpdateDTO user);

    UUID login(UserLoginDTO user);

    void logout(UUID authToken);

    User getMe(UUID authToken);
}
