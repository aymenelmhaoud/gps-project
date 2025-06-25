package isima.georganise.app.repository;

import isima.georganise.app.entity.dao.PlaceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlacesTagsRepository extends JpaRepository<PlaceTag, Long> {


    Iterable<PlaceTag> findByTag_TagId(Long id);

    Optional<PlaceTag> findByTag_TagIdAndPlace_PlaceId(Long id, Long placeId);

    Iterable<PlaceTag> findByPlace_PlaceId(Long placeId);
}
