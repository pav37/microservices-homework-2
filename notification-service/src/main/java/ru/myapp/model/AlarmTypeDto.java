package ru.myapp.model;

import lombok.Data;

@Data
public class AlarmTypeDto {
  private Long id;
  private String name;
  private String parameterName;
  private Double minValue;
  private Double maxValue;
  private ZoneTypeDto zoneType;
}