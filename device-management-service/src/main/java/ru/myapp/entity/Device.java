package ru.myapp.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@Table(name = "devices")
public class Device {
  @Id
  private UUID id;
  @Column(name = "name")
  private String name;
  @Column(name = "is_test")
  private Boolean isTest;
  @ManyToOne
  private DeviceGroup deviceGroup;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "device", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Sensor> sensors;
}