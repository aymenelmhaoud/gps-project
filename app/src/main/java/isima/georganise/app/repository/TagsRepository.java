package isima.georganise.app.repository;

import isima.georganise.app.entity.dao.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagsRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT ta FROM Tag ta WHERE (UPPER(ta.title) LIKE UPPER(CONCAT('%', :keyword, '%')) OR UPPER(ta.description) LIKE UPPER(CONCAT('%', :keyword, '%')))")
    List<Tag> findByKeyword(String keyword);

    Optional<Tag> findByTitle(String title);

    List<Tag> findByUserId(Long userId);

}
