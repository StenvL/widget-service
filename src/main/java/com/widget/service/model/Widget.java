package com.widget.service.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;

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

    @Min(0)
    private double width;

    @Min(0)
    private double height;
    private ZonedDateTime lastModified;
}
