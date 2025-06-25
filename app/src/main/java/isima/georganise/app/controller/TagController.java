package isima.georganise.app.controller;

import isima.georganise.app.entity.dao.Tag;
import isima.georganise.app.entity.dto.RemovePlaceFromTagDTO;
import isima.georganise.app.entity.dto.TagCreationDTO;
import isima.georganise.app.entity.dto.TagUpdateDTO;
import isima.georganise.app.service.tag.TagService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tag")
public class TagController {

    private static final String AUTH_TOKEN_COOKIE_NAME = "authToken";
    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(path = "", produces = "application/json")
    public @NotNull ResponseEntity<Iterable<Tag>> getTags(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken) {
        return ResponseEntity.ok(tagService.getAllTags(authToken));
    }

    @GetMapping(path = "/keyword/{keyword}", produces = "application/json")
    public @NotNull ResponseEntity<Iterable<Tag>> getTagsByKeyword(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("keyword") String keyword) {
        return ResponseEntity.ok(tagService.getTagsByKeyword(authToken, keyword));
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public @NotNull ResponseEntity<Tag> getTagById(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id) {
        return ResponseEntity.ok(tagService.getTagById(authToken, id));
    }

    @GetMapping(path = "/placeTag/{id}", produces = "application/json")
    public @NotNull ResponseEntity<Tag> getTagByPlaceId(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id) {
        return ResponseEntity.ok(tagService.getTagByPlaceId(authToken, id));
    }

    @PostMapping(path = "", consumes = "application/json", produces = "application/json")
    public @NotNull ResponseEntity<Tag> createTag(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @RequestBody TagCreationDTO tag) {
        return ResponseEntity.ok(tagService.createTag(authToken, tag));
    }

    @PutMapping(path = "/{id}", consumes = "application/json", produces = "application/json")
    public @NotNull ResponseEntity<Tag> updateTag(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id, @RequestBody TagUpdateDTO tag) {
        return ResponseEntity.ok(tagService.updateTag(authToken, id, tag));
    }

    @DeleteMapping(path = "/{id}")
    public @NotNull ResponseEntity<Void> deleteTag(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id) {
        tagService.deleteTag(authToken, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/removeFrom/{id}")
    public @NotNull ResponseEntity<Void> removePlaceFromTag(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id, @RequestBody RemovePlaceFromTagDTO place) {
        tagService.removePlaceFromTag(authToken, id, place);
        return ResponseEntity.ok().build();
    }
}
