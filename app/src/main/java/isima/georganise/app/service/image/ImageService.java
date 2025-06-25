package isima.georganise.app.service.image;

import isima.georganise.app.entity.dao.Image;
import isima.georganise.app.entity.dto.ImageCreationDTO;
import isima.georganise.app.entity.dto.ImageDTO;
import isima.georganise.app.entity.dto.ImageUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ImageService {

    Iterable<ImageDTO> getAllImages(UUID authToken);

    ImageDTO getImageById(UUID authToken, Long id);

    Iterable<ImageDTO> getImageByKeyword(UUID authToken, String keyword);

    ImageDTO createImage(UUID authToken, ImageCreationDTO image);

    void deleteImage(UUID authToken, Long id);

    ImageDTO updateImage(UUID authToken, Long id, ImageUpdateDTO imageUpdateDTO);

}
