package ru.myapp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "alarm_types")
public class AlarmType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "name")
  private String name;
  @Column(name = "parameter_name")
  private String parameterName;
  @Column(name = "min_value")
  private Double minValue;
  @Column(name = "max_value")
  private Double maxValue;
  @ManyToOne
  private ZoneType zoneType;
}