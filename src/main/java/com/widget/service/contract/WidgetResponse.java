package com.widget.service.contract;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
public class WidgetResponse {
    private UUID id;
    private int x;
    private int y;
    private int z;
    private double width;
    private double height;
    private ZonedDateTime lastModified;
}
