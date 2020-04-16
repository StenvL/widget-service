package com.widget.service.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

import java.time.ZonedDateTime;

@Getter @Setter
public class Widget extends Entity {
    @Min(0)
    private int x;

    @Min(0)
    private int y;

    private Integer z = null;

    @Min(0)
    private double width;

    @Min(0)
    private double height;
    private ZonedDateTime lastModified;
}
