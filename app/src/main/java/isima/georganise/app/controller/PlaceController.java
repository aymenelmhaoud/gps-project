package isima.georganise.app.controller;

import isima.georganise.app.entity.dao.Place;
import isima.georganise.app.entity.dto.GetPlaceVicinityDTO;
import isima.georganise.app.entity.dto.PlaceCreationDTO;
import isima.georganise.app.entity.dto.PlaceUpdateDTO;
import isima.georganise.app.service.place.PlaceService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    private static final String AUTH_TOKEN_COOKIE_NAME = "authToken";
    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping(path = "", produces = "application/json")
    public @NotNull ResponseEntity<Iterable<Place>> getPlaces(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken) {
        return ResponseEntity.ok(placeService.getAllPlaces(authToken));
    }

    @GetMapping(path = "/user/{id}", produces = "application/json")
    public @NotNull ResponseEntity<Iterable<Place>> getPlacesByUser(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id) {
        return ResponseEntity.ok(placeService.getPlacesByUser(authToken, id));
    }

    @GetMapping(path = "/tag/{id}", produces = "application/json")
    public @NotNull ResponseEntity<Iterable<Place>> getPlacesByTag(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id) {
        return ResponseEntity.ok(placeService.getPlacesByTag(authToken, id));
    }

    @GetMapping(path = "/keyword/{keyword}", produces = "application/json")
    public @NotNull ResponseEntity<Iterable<Place>> getPlacesByKeyword(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("keyword") String keyword) {
        return ResponseEntity.ok(placeService.getPlacesByKeyword(authToken, keyword));
    }

    @GetMapping(path = "/around", produces = "application/json")
    public @NotNull ResponseEntity<Iterable<Place>> getPlacesByVicinity(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @RequestBody GetPlaceVicinityDTO dto) {
        return ResponseEntity.ok(placeService.getPlacesByVicinity(authToken, dto));
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public @NotNull ResponseEntity<Place> getPlaceById(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id) {
        return ResponseEntity.ok(placeService.getPlaceById(authToken, id));
    }

    @GetMapping(path = "/placeTag/{id}", produces = "application/json")
    public @NotNull ResponseEntity<Place> getPlacesByPlaceTag(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id) {
        return ResponseEntity.ok(placeService.getPlacesByPlaceTag(authToken, id));
    }

    @PostMapping(path = "", consumes = "application/json", produces = "application/json")
    public @NotNull ResponseEntity<Place> createPlace(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @RequestBody PlaceCreationDTO place) {
        return ResponseEntity.ok(placeService.createPlace(authToken, place));
    }

    @PutMapping(path = "/{id}", consumes = "application/json", produces = "application/json")
    public @NotNull ResponseEntity<Place> updatePlace(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id, @RequestBody PlaceUpdateDTO place) {
        return ResponseEntity.ok(placeService.updatePlace(authToken, id, place));
    }

    @DeleteMapping(path = "/{id}")
    public @NotNull ResponseEntity<Void> deletePlace(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id) {
        placeService.deletePlace(authToken, id);
        return ResponseEntity.ok().build();
    }
}
