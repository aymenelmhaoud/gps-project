package isima.georganise.app.service.place;


import isima.georganise.app.entity.dao.*;
import isima.georganise.app.entity.dto.GetPlaceVicinityDTO;
import isima.georganise.app.entity.dto.PlaceCreationDTO;
import isima.georganise.app.entity.dto.PlaceUpdateDTO;
import isima.georganise.app.exception.NotFoundException;
import isima.georganise.app.exception.NotLoggedException;
import isima.georganise.app.exception.UnauthorizedException;
import isima.georganise.app.repository.*;
import isima.georganise.app.service.util.GpsFormatConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PlaceServiceImpl implements PlaceService {

    private final @NotNull PlacesRepository placesRepository;

    private final @NotNull TokensRepository tokensRepository;

    private final @NotNull UsersRepository usersRepository;

    private final @NotNull TagsRepository tagsRepository;

    private final @NotNull PlacesTagsRepository placesTagsRepository;

    private final @NotNull GpsFormatConverter gpsFormatConverter;

    @Autowired
    public PlaceServiceImpl(@NotNull TagsRepository tagsRepository, @NotNull PlacesRepository placesRepository, @NotNull TokensRepository tokensRepository, @NotNull UsersRepository usersRepository, @NotNull PlacesTagsRepository placesTagsRepository, @NotNull GpsFormatConverter gpsFormatConverter) {
        Assert.notNull(tagsRepository, "TagsRepository cannot be null");
        Assert.notNull(placesRepository, "PlacesRepository cannot be null");
        Assert.notNull(tokensRepository, "TokensRepository cannot be null");
        Assert.notNull(usersRepository, "UsersRepository cannot be null");
        Assert.notNull(placesTagsRepository, "PlacesTagsRepository cannot be null");
        Assert.notNull(gpsFormatConverter, "GpsFormatConverter cannot be null");
        this.tagsRepository = tagsRepository;
        this.placesRepository = placesRepository;
        this.tokensRepository = tokensRepository;
        this.usersRepository = usersRepository;
        this.placesTagsRepository = placesTagsRepository;
        this.gpsFormatConverter = gpsFormatConverter;
    }

    @Override
    public @NotNull List<Place> getAllPlaces(UUID authToken) {
        usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        return placesRepository.findAll();
    }

    @Override
    public @NotNull Place getPlaceById(UUID authToken, @NotNull Long id) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Place place = placesRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!userCurrent.getUserId().equals(place.getUserId())) {
            List<Tag> tags = place.getPlaceTags().stream().map(PlaceTag::getTag).toList();
            if (tags.isEmpty())
                throw new NotFoundException("User " + userCurrent.getUserId() + " has no access to this place.");
            tags.forEach(tag -> {
                if (tokensRepository.findByUserIdAndTagId(userCurrent.getUserId(), tag.getTagId()).isEmpty())
                    throw new NotFoundException("User " + userCurrent.getUserId() + " has no access token to this place.");
            });
        }

        return place;
    }


    @Override
    public @NotNull List<Place> getPlacesByUser(UUID authToken, Long id) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        if (!userCurrent.getUserId().equals(id))
            throw new NotFoundException("User " + userCurrent.getUserId() + " has no access to this user's places.");

        List<Place> places = placesRepository.findAll();

        List<Place> placesToReturn = removeUnautorizedPlace(places, userCurrent);
        placesToReturn.sort(Comparator.comparing(Place::getPlaceId));

        return placesToReturn;
    }

    @NotNull
    private List<Place> removeUnautorizedPlace(List<Place> places, User userCurrent) {
        List<Place> placesToReturn = new ArrayList<>();
        for (Place place : places) {
            if (place.getUserId().equals(userCurrent.getUserId())) {
                placesToReturn.add(place);
                continue;
            }

            List<Tag> tags = place.getPlaceTags().stream().map(PlaceTag::getTag).toList();
            for (Tag tag : tags) {
                if (tag.getUserId().equals(userCurrent.getUserId()) || !tokensRepository.findByUserIdAndTagId(userCurrent.getUserId(), tag.getTagId()).isEmpty()){
                    placesToReturn.add(place);
                    break;
                }
            }
        }
        return placesToReturn;
    }

    @Override
    public @NotNull List<Place> getPlacesByTag(UUID authToken, @NotNull Long id) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Tag tag = tagsRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!tag.getUserId().equals(userCurrent.getUserId()) && (tokensRepository.findByUserIdAndTagId(userCurrent.getUserId(), tag.getTagId()).isEmpty()))
            throw new NotFoundException("User " + userCurrent.getUserId() + " has no access token to this tag.");

        List<Place> places = placesRepository.findByTagId(id);

        if (places.isEmpty())
            throw new NotFoundException("User " + userCurrent.getUserId() + " has no places with tag " + id + ".");

        return places;
    }

    @Override
    public @NotNull List<Place> getPlacesByKeyword(UUID authToken, String keyword) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        List<Place> places = placesRepository.findByKeyword(keyword);

        if (places.isEmpty()) throw new NotFoundException("User " + userCurrent.getUserId() + " has no places with keyword " + keyword + ".");

        List<Place> placesToReturn = removeUnautorizedPlace(places, userCurrent);
        placesToReturn.sort(Comparator.comparing(Place::getPlaceId));

        return placesToReturn;
    }

    @Override
    public @NotNull List<Place> getPlacesByVicinity(UUID authToken, @NotNull GetPlaceVicinityDTO dto) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        BigDecimal minLongitude = dto.getLongitude().subtract(dto.getRadius());
        BigDecimal maxLongitude = dto.getLongitude().add(dto.getRadius());
        BigDecimal minLatitude = dto.getLatitude().subtract(dto.getRadius());
        BigDecimal maxLatitude = dto.getLatitude().add(dto.getRadius());

        List<Place> places = placesRepository.findByVicinityAndUserId(minLongitude, maxLongitude, minLatitude, maxLatitude, userCurrent.getUserId()).orElse(new ArrayList<>());

        if (places.isEmpty())
            throw new NotFoundException("User " + userCurrent.getUserId() + " has no places in vicinity " + dto + ".");

        return places;
    }

    @Override
    public @NotNull Place createPlace(UUID authToken, @NotNull PlaceCreationDTO placeCreationDTO) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Place place = new Place(placeCreationDTO, userCurrent.getUserId());

        List<Tag> tags = tagsRepository.findAllById(placeCreationDTO.getTagIds());
        List<PlaceTag> placeTags = new ArrayList<>();
        for (Tag tag : tags) {
            if (!tag.getUserId().equals(userCurrent.getUserId()) && tokensRepository.findByUserIdAndTagId(userCurrent.getUserId(), tag.getTagId()).isEmpty())
                throw new NotFoundException("User " + userCurrent.getUserId() + " has no access token to tag " + tag.getTagId() + ".");
            placeTags.add(new PlaceTag(place, tag));
        }
        placesRepository.saveAndFlush(place);

        place.setPlaceTags(placeTags);

        placesTagsRepository.saveAllAndFlush(placeTags);

        return place;
    }

    @Override
    public void deletePlace(UUID authToken, @NotNull Long id) {
        Place place = checkPlaceAccessRights(authToken, id);

        placesTagsRepository.deleteAll(place.getPlaceTags());

        placesRepository.delete(place);
    }

    @Override
    public @NotNull Place updatePlace(UUID authToken, @NotNull Long id, @NotNull PlaceUpdateDTO place) {
        Place existingPlace = checkPlaceAccessRights(authToken, id);

        if (place.getName() != null)
            existingPlace.setName(place.getName());
        if (place.getDescription() != null)
            existingPlace.setDescription(place.getDescription());
        if (place.getLongitude() != null)
            existingPlace.setLongitude(place.getLongitude());
        if (place.getLatitude() != null)
            existingPlace.setLatitude(place.getLatitude());
        if (place.getImageId() != null)
            existingPlace.setImageId(place.getImageId());
        List<PlaceTag> placeTags = new ArrayList<>();
        if (place.getTagIds() != null) {
            for (Long tagId : place.getTagIds()) {
                if (existingPlace.getPlaceTags().stream().noneMatch(placeTag -> placeTag.getTag().getTagId().equals(tagId))) {
                    Tag tag = tagsRepository.findById(tagId).orElseThrow(NotFoundException::new);
                    placeTags.add(new PlaceTag(existingPlace, tag));
                }
            }
        }
        placesTagsRepository.saveAllAndFlush(placeTags);
        existingPlace.getPlaceTags().addAll(placeTags);

        return placesRepository.saveAndFlush(existingPlace);
    }

    @Override
    public Place getPlacesByPlaceTag(UUID authToken, Long id) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        PlaceTag placeTag = placesTagsRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!placeTag.getPlace().getUserId().equals(userCurrent.getUserId())) {
            List<Token> tokens = tokensRepository.findByUserIdAndTagId(userCurrent.getUserId(), placeTag.getTag().getTagId());
            if (tokens.isEmpty()) throw new UnauthorizedException(userCurrent.getNickname(), "get place of user " + placeTag.getPlace().getUserId());
        }

        return placeTag.getPlace();
    }

    @NotNull
    private Place checkPlaceAccessRights(UUID authToken, @NotNull Long id) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Place existingPlace = placesRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!userCurrent.getUserId().equals(existingPlace.getUserId()))
            throw new NotFoundException();
        return existingPlace;
    }
}

