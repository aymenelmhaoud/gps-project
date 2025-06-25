package isima.georganise.app.service.tag;


import isima.georganise.app.entity.dao.*;
import isima.georganise.app.entity.dto.RemovePlaceFromTagDTO;
import isima.georganise.app.entity.dto.TagCreationDTO;
import isima.georganise.app.entity.dto.TagUpdateDTO;
import isima.georganise.app.entity.util.Right;
import isima.georganise.app.exception.ConflictException;
import isima.georganise.app.exception.NotFoundException;
import isima.georganise.app.exception.NotLoggedException;
import isima.georganise.app.exception.UnauthorizedException;
import isima.georganise.app.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TagServiceImpl implements TagService {

    private final @NotNull TagsRepository tagsRepository;

    private final @NotNull UsersRepository usersRepository;

    private final @NotNull TokensRepository tokensRepository;

    private final @NotNull PlacesRepository placesRepository;

    private final @NotNull PlacesTagsRepository placesTagsRepository;

    @Autowired
    public TagServiceImpl(@NotNull PlacesRepository placesRepository, @NotNull TagsRepository tagsRepository, @NotNull UsersRepository usersRepository, @NotNull TokensRepository tokensRepository, @NotNull PlacesTagsRepository placesTagsRepository) {
        Assert.notNull(placesRepository, "placesRepository must not be null");
        Assert.notNull(tagsRepository, "tagsRepository must not be null");
        Assert.notNull(usersRepository, "usersRepository must not be null");
        Assert.notNull(tokensRepository, "tokensRepository must not be null");
        Assert.notNull(placesTagsRepository, "placesTagsRepository must not be null");
        this.placesRepository = placesRepository;
        this.tagsRepository = tagsRepository;
        this.usersRepository = usersRepository;
        this.tokensRepository = tokensRepository;
        this.placesTagsRepository = placesTagsRepository;
    }

    @Override
    public @NotNull List<Tag> getAllTags(UUID authToken) {
        User userCurrent = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        List<Tag> tags = tagsRepository.findAll();

        if (tags.isEmpty()) throw new NotFoundException();

        List<Tag> tagsToReturn = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getUserId().equals(userCurrent.getUserId())) {
                tagsToReturn.add(tag);
            } else {
                List<Token> tokens = tokensRepository.findByUserIdAndTagId(userCurrent.getUserId(), tag.getTagId());
                if (!tokens.isEmpty())
                    tagsToReturn.add(tag);
            }
        }

        return tagsToReturn;
    }

    @Override
    public @NotNull Tag getTagById(UUID authToken, @NotNull Long id) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Tag tag = tagsRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!currentUser.getUserId().equals(tag.getUserId()) && (tokensRepository.findByUserIdAndTagId(currentUser.getUserId(), id).isEmpty()))
            throw new UnauthorizedException(currentUser.getNickname(), "get tag of user " + tag.getUserId());

        return tag;
    }

    @Override
    public Iterable<Tag> getTagsByKeyword(UUID authToken, String keyword) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        List<Tag> tags = tagsRepository.findByKeyword(keyword);

        if (tags.isEmpty()) throw new NotFoundException();

        List<Tag> tagsToReturn = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getUserId().equals(currentUser.getUserId())) {
                tagsToReturn.add(tag);
                continue;
            }

            List<Token> tokens = tokensRepository.findByUserIdAndTagId(currentUser.getUserId(), tag.getTagId());
            if (!tokens.isEmpty()) tagsToReturn.add(tag);
        }

        return tagsToReturn;
    }

    @Override
    public @NotNull Tag createTag(UUID authToken, @NotNull TagCreationDTO tag) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        if (tagsRepository.findByTitle(tag.getTitle()).isPresent())
            throw new ConflictException("Tag with title " + tag.getTitle() + " already exists");
        return tagsRepository.saveAndFlush(new Tag(tag, currentUser.getUserId()));
    }

    @Override
    public void deleteTag(UUID authToken, @NotNull Long id) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Tag tag = checkTagAccessRight(id, currentUser, "delete tag of user ");

        placesTagsRepository.deleteAll(placesTagsRepository.findByTag_TagId(id));

        tokensRepository.deleteAll(tokensRepository.findByTagId(id));

        tagsRepository.delete(tag);
    }

    @Override
    public @NotNull Tag updateTag(UUID authToken, @NotNull Long id, @NotNull TagUpdateDTO tag) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Tag tagToUpdate = checkTagAccessRight(id, currentUser, "update tag of user ");

        if (tag.getDescription() != null) {
            tagToUpdate.setDescription(tag.getDescription());
        }

        return tagsRepository.saveAndFlush(tagToUpdate);
    }

    @Override
    public void removePlaceFromTag(UUID authToken, @NotNull Long id, @NotNull RemovePlaceFromTagDTO placeId) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        checkTagAccessRight(id, currentUser, "remove place from tag of user ");

        PlaceTag placeTag = placesTagsRepository.findByTag_TagIdAndPlace_PlaceId(id, placeId.getPlaceId()).orElseThrow(NotFoundException::new);
        placesTagsRepository.delete(placeTag);
    }

    @Override
    public Tag getTagByPlaceId(UUID authToken, Long id) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        PlaceTag placeTag = placesTagsRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!currentUser.getUserId().equals(placeTag.getTag().getUserId())) {
            List<Token> tokens = tokensRepository.findByUserIdAndTagId(currentUser.getUserId(), placeTag.getTag().getTagId());
            if (tokens.isEmpty()) throw new UnauthorizedException(currentUser.getNickname(), "get tag of user " + placeTag.getTag().getUserId());
        }

        return placeTag.getTag();
    }

    @NotNull
    private Tag checkTagAccessRight(@NotNull Long id, User currentUser, String x) {
        Tag tagToUpdate = tagsRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!currentUser.getUserId().equals(tagToUpdate.getUserId())) {
            List<Token> token = tokensRepository.findByUserIdAndTagId(currentUser.getUserId(), id);
            if (token.stream().noneMatch(t -> t.getAccessRight().equals(Right.WRITER)))
                throw new UnauthorizedException(currentUser.getNickname(), x + tagToUpdate.getUserId());
        }
        return tagToUpdate;
    }
}

