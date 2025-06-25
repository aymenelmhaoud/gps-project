package isima.georganise.app.service.place;

import isima.georganise.app.entity.dao.Place;
import isima.georganise.app.entity.dto.GetPlaceVicinityDTO;
import isima.georganise.app.entity.dto.PlaceCreationDTO;
import isima.georganise.app.entity.dto.PlaceUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface PlaceService {

    List<Place> getAllPlaces(UUID authToken);

    Place getPlaceById(UUID authToken, Long id);

    List<Place> getPlacesByUser(UUID authToken, Long id);

    List<Place> getPlacesByTag(UUID authToken, Long id);

    List<Place> getPlacesByKeyword(UUID authToken, String keyword);

    List<Place> getPlacesByVicinity(UUID authToken, GetPlaceVicinityDTO dto);

    Place createPlace(UUID authToken, PlaceCreationDTO place);

    void deletePlace(UUID authToken, Long id);

    Place updatePlace(UUID authToken, Long id, PlaceUpdateDTO place);

    Place getPlacesByPlaceTag(UUID authToken, Long id);
}
