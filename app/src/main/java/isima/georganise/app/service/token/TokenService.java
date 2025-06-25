package isima.georganise.app.service.token;

import isima.georganise.app.entity.dao.Token;
import isima.georganise.app.entity.dto.TokenCreationDTO;
import isima.georganise.app.entity.dto.TokenUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TokenService {

    List<Token> getAllTokens(UUID authToken);

    Token getTokenById(UUID authToken, Long id);

    Iterable<Token> getTokensByUser(UUID authToken, Long id, boolean isCreator);

    Iterable<Token> getTokensByTag(UUID authToken, Long id);

    Token createToken(UUID authToken, TokenCreationDTO token);

    void deleteToken(UUID authToken, Long id);

    Token updateToken(UUID authToken, Long id, TokenUpdateDTO token);

    Token addTokenToUser(UUID authToken, Long id);
}
