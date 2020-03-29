package com.widget.service.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
@Getter @Setter
public class Widget {
    @Id
    @GeneratedValue
    private Long id;
    private int x;
    private int y;
    private int z;
    private double width;
    private double height;
    private ZonedDateTime lastModified;
}
