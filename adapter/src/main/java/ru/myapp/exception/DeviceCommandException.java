package ru.myapp.exception;

import java.util.UUID;

public class DeviceCommandException extends RuntimeException {

  public DeviceCommandException(UUID id) {
    super("Could not execute transaction " + id);
  }

}