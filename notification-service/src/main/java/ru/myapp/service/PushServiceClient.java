package ru.myapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.myapp.exception.UserNotFoundException;

import java.sql.SQLException;

@Service
@Slf4j
public class PushServiceClient {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${app.push.port}")
    private Integer appPort;
    @Value("${app.push.host}")
    private String appHost;
    private final String path = "push";

    @Retryable(value = UserNotFoundException.class, maxAttemptsExpression = "${app.push.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${app.push.retry.maxDelay}"))
    public void send(Object entity) throws UserNotFoundException {
        log.info("Send {}", entity);
        ResponseEntity<Void> response = restTemplate.postForEntity(String.format("http://%s:%s/%s", appHost, appPort, path),
                entity, Void.class);
        log.info("Response Status: {}", response.getStatusCode());
        if (response.getStatusCode().equals(HttpStatus.FAILED_DEPENDENCY)) {
            throw new UserNotFoundException(entity);
        }
    }

    @Recover
    void recover(SQLException e, Object entity) {
        log.error("Could not send notification {}", entity, e);
    }

}
