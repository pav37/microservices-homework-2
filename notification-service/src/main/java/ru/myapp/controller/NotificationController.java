package ru.myapp.controller;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.myapp.entity.Notification;
import ru.myapp.service.NotificationService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@Timed
@Slf4j
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

	@GetMapping("/notifications")
	@ResponseBody
	public List<Notification> getNotifications(Principal principal) {
		return StreamSupport.stream(notificationService.getNotifications().spliterator(), false)
//				.filter(a -> a.getUsername().equalsIgnoreCase(principal.getName()))
				.collect(Collectors.toList());
	}

	@GetMapping("/notifications/clear")
	@ResponseBody
	public List<Notification> clearNotifications() {
		notificationService.deleteNotifications();
		return StreamSupport.stream(notificationService.getNotifications().spliterator(), false)
				.collect(Collectors.toList());
	}
}
