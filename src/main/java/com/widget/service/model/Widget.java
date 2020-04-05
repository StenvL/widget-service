package com.widget.service.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Getter @Setter
public class Widget {
    @Id
    @GeneratedValue
    private Long id;
    private int x;
    private int y;
    private Integer z = null;
    private double width;
    private double height;
    private ZonedDateTime lastModified;
}
