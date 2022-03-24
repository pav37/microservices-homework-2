package ru.myapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Table(name = "sensors")
public class Sensor {
  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID id;

  @ManyToOne
  private Parameter parameter;

  @Column(name = "value")
  private Double value;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="device_id", nullable=false)
  @JsonIgnore
  @ToString.Exclude
  private Device device;

}