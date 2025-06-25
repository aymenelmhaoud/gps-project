package isima.georganise.app.service.user;


import isima.georganise.app.entity.dao.*;
import isima.georganise.app.entity.dto.GetUserNicknameDTO;
import isima.georganise.app.entity.dto.UserCreationDTO;
import isima.georganise.app.entity.dto.UserLoginDTO;
import isima.georganise.app.entity.dto.UserUpdateDTO;
import isima.georganise.app.exception.*;
import isima.georganise.app.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final @NotNull UsersRepository usersRepository;

    private final @NotNull ImagesRepository imagesRepository;

    private final @NotNull PlacesRepository placesRepository;

    private final @NotNull TokensRepository tokensRepository;

    private final @NotNull TagsRepository tagsRepository;

    private final @NotNull PlacesTagsRepository placesTagsRepository;

    @Autowired
    public UserServiceImpl(@NotNull UsersRepository usersRepository, @NotNull ImagesRepository imagesRepository, @NotNull PlacesRepository placesRepository, @NotNull TokensRepository tokensRepository, @NotNull TagsRepository tagsRepository, @NotNull PlacesTagsRepository placesTagsRepository) {
        Assert.notNull(usersRepository, "Users repository must not be null");
        Assert.notNull(imagesRepository, "Images repository must not be null");
        Assert.notNull(placesRepository, "Places repository must not be null");
        Assert.notNull(tokensRepository, "Tokens repository must not be null");
        Assert.notNull(tagsRepository, "Tags repository must not be null");
        Assert.notNull(placesTagsRepository, "PlacesTags repository must not be null");
        this.usersRepository = usersRepository;
        this.imagesRepository = imagesRepository;
        this.placesRepository = placesRepository;
        this.tokensRepository = tokensRepository;
        this.tagsRepository = tagsRepository;
        this.placesTagsRepository = placesTagsRepository;
    }

    @Override
    public @NotNull List<User> getAllUsers(UUID authToken) {
        usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        return usersRepository.findAll();
    }

    @Override
    public GetUserNicknameDTO getUserById(UUID authToken, @NotNull Long id) {
        usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        GetUserNicknameDTO dto = new GetUserNicknameDTO();
        dto.setNickname(usersRepository.findById(id).orElseThrow(NotFoundException::new).getNickname());

        return dto;
    }

    @Override
    public UUID createUser(@NotNull UserCreationDTO user) {
        if (usersRepository.findByEmail(user.getEmail()).isPresent())
            throw new ConflictException("User with email: " + user.getEmail() + " already exists");
        if (usersRepository.findByNickname(user.getNickname()).isPresent())
            throw new ConflictException("User with nickname: " + user.getNickname() + " already exists");

        return usersRepository.saveAndFlush(new User(user)).getAuthToken();
    }

    @Override
    public void deleteUser(UUID authToken, @NotNull Long id) {
        User user = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        if (!user.getUserId().equals(id))
            throw new UnauthorizedException(user.getNickname(), "delete user with id: " + id);

        List<Image> userImages = imagesRepository.findByUserId(user.getUserId());
        for (Image image : userImages) {
            List<Place> imagePlaces = placesRepository.findByImageId(image.getImageId());
            for (Place place : imagePlaces) {
                place.setImageId(null);
                placesRepository.saveAndFlush(place);
            }
        }
        imagesRepository.deleteAll(userImages);

        tokensRepository.findByCreatorId(user.getUserId()).ifPresent(tokensRepository::deleteAll);
        tokensRepository.findByUserId(user.getUserId()).ifPresent(tokensRepository::deleteAll);

        List<Tag> userTags = tagsRepository.findByUserId(user.getUserId());
        userTags.forEach(tag -> placesTagsRepository.deleteAll(placesTagsRepository.findByTag_TagId(tag.getTagId())));
        tagsRepository.deleteAll(userTags);

        List<Place> userPlaces = placesRepository.findByUserId(user.getUserId());
        userPlaces.forEach(place -> placesTagsRepository.deleteAll(placesTagsRepository.findByPlace_PlaceId(place.getPlaceId())));
        placesRepository.deleteAll(userPlaces);

        usersRepository.delete(usersRepository.findById(id).orElseThrow(UnauthorizedException::new));
    }

    @Override
    public @NotNull User updateUser(UUID authToken, @NotNull Long id, @NotNull UserUpdateDTO user) {
        User loggedUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        if (!loggedUser.getUserId().equals(id))
            throw new UnauthorizedException(loggedUser.getNickname(), "update user with id: " + id);

        User userToUpdate = usersRepository.findById(id).orElseThrow(NotFoundException::new);

        if (user.getNickname() != null)
            userToUpdate.setNickname(user.getNickname());
        if (user.getPassword() != null)
            userToUpdate.setPassword(user.getPassword());
        if (user.getEmail() != null)
            userToUpdate.setEmail(user.getEmail());

        return usersRepository.saveAndFlush(userToUpdate);
    }

    @Override
    public UUID login(@NotNull UserLoginDTO user) {
        System.out.println(user.getEmail() + " is trying to login with: " + user.getPassword());
        User userToLogin = usersRepository.findByEmail(user.getEmail()).orElseThrow(NotFoundException::new);

        if (!userToLogin.getPassword().equals(user.getPassword()))
            throw new WrongPasswordException();

        userToLogin.setAuthToken(UUID.randomUUID());

        System.out.println(user.getEmail() + " has successfully logged in with token: " + userToLogin.getAuthToken());

        return usersRepository.saveAndFlush(userToLogin).getAuthToken();
    }

    @Override
    public void logout(UUID authToken) {
        System.out.println("Logging out user with token: " + authToken);
        User userToLogout = usersRepository.findByAuthToken(authToken).orElseThrow(NotFoundException::new);

        userToLogout.setAuthToken(null);
        System.out.println("User " + userToLogout + " has been successfully logged out");

        usersRepository.saveAndFlush(userToLogout);
    }

    @Override
    public User getMe(UUID authToken) {
        return usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
    }
}

