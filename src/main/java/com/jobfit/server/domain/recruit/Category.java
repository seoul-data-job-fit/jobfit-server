package com.jobfit.server.domain.recruit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.Id;
import static lombok.AccessLevel.PROTECTED;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Category {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "category_id")
  private Long id;
  private String wages;
  private String position;
  private String year;
  private int people;
  private String location;

  public Category(
    String wages,
    String position,
    String year,
    int people,
    String location
  ) {
    this.wages = wages;
    this.position = position;
    this.year = year;
    this.people = people;
    this.location = location;
  }
}
