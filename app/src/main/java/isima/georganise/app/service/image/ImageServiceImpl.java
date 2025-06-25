package isima.georganise.app.service.image;

import isima.georganise.app.entity.dao.Image;
import isima.georganise.app.entity.dao.User;
import isima.georganise.app.entity.dto.ImageCreationDTO;
import isima.georganise.app.entity.dto.ImageDTO;
import isima.georganise.app.entity.dto.ImageUpdateDTO;
import isima.georganise.app.exception.NotFoundException;
import isima.georganise.app.exception.NotLoggedException;
import isima.georganise.app.repository.ImagesRepository;
import isima.georganise.app.repository.UsersRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private final @NotNull ImagesRepository imagesRepository;

    private final @NotNull UsersRepository usersRepository;

    @Autowired
    public ImageServiceImpl(@NotNull ImagesRepository imagesRepository, @NotNull UsersRepository usersRepository) {
        Assert.notNull(imagesRepository, "ImagesRepository cannot be null");
        Assert.notNull(usersRepository, "UsersRepository cannot be null");
        this.imagesRepository = imagesRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public Iterable<ImageDTO> getAllImages(UUID authToken) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Iterable<Image> images = imagesRepository.findAllPublic(currentUser.getUserId());

        return ImageDTO.fromImages(images);
    }

    @Override
    public ImageDTO getImageById(UUID authToken, Long id) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Image image = imagesRepository.findByImageIdAndUserId(id, userCurrent.getUserId()).orElseThrow(NotFoundException::new);

        return new ImageDTO(image);
    }

    @Override
    public Iterable<ImageDTO> getImageByKeyword(UUID authToken, String keyword) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Iterable<Image> images = imagesRepository.findByKeywordAndUserId(keyword, userCurrent.getUserId()).orElseThrow(NotFoundException::new);

        return ImageDTO.fromImages(images);
    }

    @Override
    public @NotNull ImageDTO createImage(UUID authToken, @NotNull ImageCreationDTO imageCreation) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        Image image;
        try {
            image = imagesRepository.saveAndFlush(new Image(imageCreation, userCurrent.getUserId()));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        return new ImageDTO(image);
    }

    @Override
    public void deleteImage(UUID authToken, Long id) {
        Image image = checkImageAccessRights(authToken, id);

        imagesRepository.delete(image);
    }

    @Override
    public @NotNull ImageDTO updateImage(UUID authToken, Long id, @NotNull ImageUpdateDTO imageUpdateDTO) {
        Image image = checkImageAccessRights(authToken, id);

        if (imageUpdateDTO.getName() != null)
            image.setName(imageUpdateDTO.getName());
        if (imageUpdateDTO.getDescription() != null)
            image.setDescription(imageUpdateDTO.getDescription());
        if (imageUpdateDTO.getIsPublic() != null)
            image.setPublic(imageUpdateDTO.getIsPublic());

        return new ImageDTO(imagesRepository.saveAndFlush(image));
    }

    @NotNull
    private Image checkImageAccessRights(UUID authToken, Long id) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Image image = imagesRepository.findByImageIdAndUserId(id, userCurrent.getUserId()).orElseThrow(NotFoundException::new);

        if (!image.getUserId().equals(userCurrent.getUserId()))
            throw new NotFoundException();
        return image;
    }
}

