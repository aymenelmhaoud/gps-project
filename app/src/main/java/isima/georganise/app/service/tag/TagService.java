package isima.georganise.app.service.tag;

import isima.georganise.app.entity.dao.Tag;
import isima.georganise.app.entity.dto.RemovePlaceFromTagDTO;
import isima.georganise.app.entity.dto.TagCreationDTO;
import isima.georganise.app.entity.dto.TagUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TagService {

    List<Tag> getAllTags(UUID authToken);

    Tag getTagById(UUID authToken, Long id);

    Iterable<Tag> getTagsByKeyword(UUID authToken, String keyword);

    Tag createTag(UUID authToken, TagCreationDTO tag);

    void deleteTag(UUID authToken, Long id);

    Tag updateTag(UUID authToken, Long id, TagUpdateDTO tag);

    void removePlaceFromTag(UUID authToken, Long id, RemovePlaceFromTagDTO placeId);

    Tag getTagByPlaceId(UUID authToken, Long id);
}
