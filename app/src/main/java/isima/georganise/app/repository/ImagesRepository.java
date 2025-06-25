package isima.georganise.app.repository;

import isima.georganise.app.entity.dao.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagesRepository extends JpaRepository<Image, Long> {

    @Query("SELECT i FROM Image i WHERE (i.name LIKE %:keyword% OR i.description LIKE %:keyword%) AND (i.userId = :userId OR i.isPublic)")
    Optional<Iterable<Image>> findByKeywordAndUserId(String keyword, Long userId);

    @Query("SELECT i FROM Image i WHERE i.imageId = :imageId AND (i.userId = :userId OR i.isPublic)")
    Optional<Image> findByImageIdAndUserId(Long imageId, Long userId);

    @Query("SELECT i FROM Image i WHERE i.isPublic OR i.userId = :userId")
    Iterable<Image> findAllPublic(long userId);

    List<Image> findByUserId(Long userId);
}
