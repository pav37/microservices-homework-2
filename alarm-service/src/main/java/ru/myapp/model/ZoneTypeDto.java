package ru.myapp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
public class ZoneTypeDto {
  private Long id;
  private String name;
  private String code;
}