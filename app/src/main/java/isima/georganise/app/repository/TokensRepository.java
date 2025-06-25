package isima.georganise.app.repository;

import isima.georganise.app.entity.dao.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokensRepository extends JpaRepository<Token, Long> {

    Optional<Iterable<Token>> findByUserId(Long id);

    Optional<Iterable<Token>> findByCreatorId(Long id);

    List<Token> findByUserIdAndTagId(Long userId, Long tagId);

    List<Token> findByTagId(Long tagId);
}
