package isima.georganise.app.service.token;


import isima.georganise.app.entity.dao.Tag;
import isima.georganise.app.entity.dao.Token;
import isima.georganise.app.entity.dao.User;
import isima.georganise.app.entity.dto.TokenCreationDTO;
import isima.georganise.app.entity.dto.TokenUpdateDTO;
import isima.georganise.app.exception.NotFoundException;
import isima.georganise.app.exception.NotLoggedException;
import isima.georganise.app.exception.UnauthorizedException;
import isima.georganise.app.repository.TagsRepository;
import isima.georganise.app.repository.TokensRepository;
import isima.georganise.app.repository.UsersRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private final @NotNull TokensRepository tokensRepository;

    private final @NotNull UsersRepository usersRepository;

    private final @NotNull TagsRepository tagsRepository;

    @Autowired
    public TokenServiceImpl(@NotNull UsersRepository usersRepository, @NotNull TokensRepository tokensRepository, @NotNull TagsRepository tagsRepository) {
        Assert.notNull(tokensRepository, "Tokens repository must not be null");
        Assert.notNull(usersRepository, "Users repository must not be null");
        Assert.notNull(tagsRepository, "Tags repository must not be null");
        this.usersRepository = usersRepository;
        this.tokensRepository = tokensRepository;
        this.tagsRepository = tagsRepository;
    }

    @Override
    public @NotNull List<Token> getAllTokens(UUID authToken) {
        usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        return tokensRepository.findAll();
    }

    @Override
    public @NotNull Token getTokenById(UUID authToken, @NotNull Long id) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Token token = tokensRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!token.getUserId().equals(currentUser.getUserId()))
            throw new NotFoundException("Token not found");

        return token;
    }

    @Override
    public @NotNull Iterable<Token> getTokensByTag(UUID authToken, Long id) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        List<Token> tokens = tokensRepository.findByUserIdAndTagId(currentUser.getUserId(), id);

        if (tokens.isEmpty())
            throw new NotFoundException("No tokens found for tag " + id);

        return tokens;
    }

    @Override
    public @NotNull Token createToken(UUID authToken, @NotNull TokenCreationDTO token) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        if (Objects.isNull(token.getAccessRight()))
            throw new IllegalArgumentException("Access right is required");
        if (Objects.isNull(token.getTagId()))
            throw new IllegalArgumentException("Tag id is required");

        Tag tag = tagsRepository.findById(token.getTagId()).orElseThrow(NotFoundException::new);

        if (!tag.getUserId().equals(currentUser.getUserId()))
            throw new UnauthorizedException(currentUser.getNickname(), "create token for tag " + tag.getTagId());

        return tokensRepository.saveAndFlush(new Token(token, currentUser.getUserId()));
    }

    @Override
    public void deleteToken(UUID authToken, @NotNull Long id) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Token token = tokensRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!token.getCreatorId().equals(currentUser.getUserId()))
            throw new UnauthorizedException(currentUser.getNickname(), "delete token " + id);

        tokensRepository.delete(token);
    }

    @Override
    public @NotNull Token updateToken(UUID authToken, @NotNull Long id, @NotNull TokenUpdateDTO token) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Token tokenToUpdate = tokensRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!tokenToUpdate.getCreatorId().equals(currentUser.getUserId()))
            throw new UnauthorizedException(currentUser.getNickname(), "update token " + id);

        if (token.getAccessRight() != null)
            tokenToUpdate.setAccessRight(token.getAccessRight());

        return tokensRepository.saveAndFlush(tokenToUpdate);
    }

    @Override
    public @NotNull Token addTokenToUser(UUID authToken, @NotNull Long id) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);
        Token tokenToUpdate = tokensRepository.findById(id).orElseThrow(NotFoundException::new);

        if (tokenToUpdate.getUserId() != null)
            throw new IllegalArgumentException("Token already has a user assigned");

        tokenToUpdate.setUserId(currentUser.getUserId());

        return tokensRepository.saveAndFlush(tokenToUpdate);
    }

    @Override
    public @NotNull Iterable<Token> getTokensByUser(UUID authToken, Long id, boolean isCreator) {
        User currentUser = usersRepository.findByAuthToken(authToken).orElseThrow(NotLoggedException::new);

        if (!currentUser.getUserId().equals(id))
            throw new UnauthorizedException(currentUser.getNickname(), "get tokens of user " + id);

        Iterable<Token> tokens;
        if (isCreator) {
            tokens = tokensRepository.findByCreatorId(id).orElseThrow(NotFoundException::new);
        } else {
            tokens = tokensRepository.findByUserId(id).orElseThrow(NotFoundException::new);
        }

        if (!tokens.iterator().hasNext())
            throw new NotFoundException("No tokens found for user " + id);

        return tokens;
    }
}

