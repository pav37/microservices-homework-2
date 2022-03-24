package ru.myapp.controller;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.myapp.entity.User;
import ru.myapp.model.UserDto;
import ru.myapp.model.UserRegistrationDto;
import ru.myapp.service.UserService;

import java.net.URI;
import java.util.List;

@RestController("/api")
@Timed(percentiles = {0.5, 0.95, 0.99, 1})
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @Counted(extraTags = {"method", "endpoint", "status"})
    @GetMapping("/users")
    List<UserDto> all() {
        return userService.findAll();
    }

    @PostMapping("/register")
    ResponseEntity<UserDto> register(@RequestBody UserRegistrationDto userDto) {
        UserDto user = userService.registerUser(userDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(location).body(user);
    }

    @PostMapping("/users")
    UserDto newUser(@RequestBody UserRegistrationDto userDto) {
        return userService.registerUser(userDto);
    }

    @GetMapping("/users/{id}")
    UserDto one(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PutMapping("/users/{id}")
    UserDto replaceUser(@RequestBody User newUser, @PathVariable Long id) {
        return userService.updateUser(newUser, id);
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}