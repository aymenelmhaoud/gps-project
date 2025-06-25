package isima.georganise.app.repository;

import isima.georganise.app.entity.dao.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlacesRepository extends JpaRepository<Place, Long> {

    List<Place> findByUserId(Long id);

    Place findByPlaceIdAndUserId(Long placeId, Long userId);

    @Query("SELECT p FROM Place p JOIN PlaceTag pt ON pt.place.placeId = p.placeId WHERE pt.tag.tagId = :id")
    List<Place> findByTagId(Long id);

    @Query("SELECT p FROM Place p JOIN PlaceTag pt ON pt.place.placeId = p.placeId WHERE UPPER(p.name) LIKE UPPER(CONCAT('%', :keyword, '%')) OR UPPER(p.description) LIKE UPPER(CONCAT('%', :keyword, '%')) OR UPPER(pt.tag.title) LIKE UPPER(CONCAT('%', :keyword, '%')) OR UPPER(pt.tag.description) LIKE UPPER(CONCAT('%', :keyword, '%'))")
    List<Place> findByKeyword(String keyword);

    @Query("SELECT p FROM Place p JOIN PlaceTag pt ON pt.place.placeId = p.placeId JOIN Token t ON pt.tag.tagId = t.tagId WHERE p.latitude BETWEEN :minLatitude AND :maxLatitude AND p.longitude BETWEEN :minLongitude AND :maxLongitude AND (t.userId = :userId OR p.userId = :userId)")
    Optional<List<Place>> findByVicinityAndUserId(BigDecimal minLongitude, BigDecimal maxLongitude, BigDecimal minLatitude, BigDecimal maxLatitude, Long userId);

    List<Place> findByImageId(Long imageId);
}
