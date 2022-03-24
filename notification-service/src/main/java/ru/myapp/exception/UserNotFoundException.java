package ru.myapp.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(Long id) {
    super("Not found any user for send notification " + id);
  }

  public UserNotFoundException(Object obj) {
    super("Not found any user for send notification " + obj);
  }
}