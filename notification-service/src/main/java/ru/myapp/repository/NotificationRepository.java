package ru.myapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.myapp.entity.Notification;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
}