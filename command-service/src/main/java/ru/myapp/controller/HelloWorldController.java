package ru.myapp.controller;

import java.security.Principal;
import java.util.concurrent.atomic.AtomicLong;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.myapp.model.Greeting;
import ru.myapp.model.TokenInfo;

@Controller
@Timed
@Slf4j
@Endpoint(id = "hello-world")
public class HelloWorldController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@ReadOperation
	@GetMapping("/hello")
	@ResponseBody
	public Greeting sayHello(@AuthenticationPrincipal Jwt jwt, Principal principal) {
		log.debug("Token Received: ");
		log.debug("" + jwt);
		log.debug("" + principal);
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		log.debug("" + currentUser);
		TokenInfo tokenInfo = null;
		if (currentUser != null) {
			log.debug(currentUser.getName());
			log.debug("" + currentUser.getPrincipal());
			Jwt token = (Jwt) currentUser.getPrincipal();
			log.debug("" + token.getClaims());
			log.debug(token.getTokenValue());
			log.debug(token.getIssuer().toString());
			log.debug(token.getExpiresAt().toString());
			tokenInfo = new TokenInfo(token);
		}
		return new Greeting(counter.incrementAndGet(),
				String.format(template, currentUser.getName()), tokenInfo);
	}

}
