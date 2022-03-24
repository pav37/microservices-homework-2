package ru.myapp.controller;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.myapp.entity.User;
import ru.myapp.model.TokenInfo;
import ru.myapp.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@Timed
@Slf4j
//@Endpoint(id = "management")
public class UserController {

	@Autowired
	private UserService userService;

	@ReadOperation
	@GetMapping("/management/users")
	@ResponseBody
	public List<User> users(@AuthenticationPrincipal Jwt jwt, Principal principal) {
		return userService.getUsers();
	}

	@ReadOperation
	@GetMapping("/management/currentUser")
	@ResponseBody
	public String currentUser(@AuthenticationPrincipal Jwt jwt, Principal principal) {
		return principal.getName();
	}
}
