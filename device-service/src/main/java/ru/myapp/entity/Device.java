package ru.myapp.entity;

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
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID id;
  @Column(name = "name")
  private String name;
  @Column(name = "is_test")
  private Boolean isTest;
  @ManyToOne
  private DeviceGroup deviceGroup;
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "device")
  private List<Sensor> sensors;
}