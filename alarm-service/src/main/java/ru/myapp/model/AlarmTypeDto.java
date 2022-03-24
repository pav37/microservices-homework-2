package ru.myapp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.myapp.entity.ZoneType;

import javax.persistence.*;

@Data
public class AlarmTypeDto {
  private Long id;
  private String name;
  private String parameterName;
  private Double minValue;
  private Double maxValue;
  private ZoneTypeDto zoneType;
}