package isima.georganise.app.repository;

import isima.georganise.app.entity.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByAuthToken(UUID authToken);

    Optional<User> findByNickname(String nickname);
}
