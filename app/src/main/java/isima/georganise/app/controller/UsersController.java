package isima.georganise.app.controller;

import isima.georganise.app.entity.dao.User;
import isima.georganise.app.entity.dto.GetUserNicknameDTO;
import isima.georganise.app.entity.dto.UserCreationDTO;
import isima.georganise.app.entity.dto.UserLoginDTO;
import isima.georganise.app.entity.dto.UserUpdateDTO;
import isima.georganise.app.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UsersController {

    private static final String AUTH_TOKEN_COOKIE_NAME = "authToken";
    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "", produces = "application/json")
    public @NotNull ResponseEntity<Iterable<User>> getUsers(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken) {
        return ResponseEntity.ok(userService.getAllUsers(authToken));
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public @NotNull ResponseEntity<GetUserNicknameDTO> getUserById(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(authToken, id));
    }

    @GetMapping(path = "/me", produces = "application/json")
    public @NotNull ResponseEntity<User> getMe(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken) {
        return ResponseEntity.ok(userService.getMe(authToken));
    }

    @PostMapping(path = "", consumes = "application/json", produces = "application/json")
    public @NotNull ResponseEntity<Void> createUser(@RequestBody UserCreationDTO user, @NotNull HttpServletResponse response) {

        Cookie cookie = new Cookie(AUTH_TOKEN_COOKIE_NAME, userService.createUser(user).toString());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/{id}", consumes = "application/json", produces = "application/json")
    public @NotNull ResponseEntity<User> updateUser(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id, @RequestBody UserUpdateDTO user) {
        return ResponseEntity.ok(userService.updateUser(authToken, id, user));
    }

    @DeleteMapping(path = "/{id}")
    public @NotNull ResponseEntity<Void> deleteUser(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @PathVariable("id") Long id) {
        userService.deleteUser(authToken, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/login", consumes = "application/json")
    public @NotNull ResponseEntity<Void> login(@RequestBody UserLoginDTO user, @NotNull HttpServletResponse response) {

        Cookie cookie = new Cookie(AUTH_TOKEN_COOKIE_NAME, userService.login(user).toString());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/logout")
    public @NotNull ResponseEntity<Void> logout(@CookieValue(AUTH_TOKEN_COOKIE_NAME) UUID authToken, @NotNull HttpServletResponse response) {
        userService.logout(authToken);

        Cookie cookie = new Cookie(AUTH_TOKEN_COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

}
