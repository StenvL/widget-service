package com.widget.service.contracts;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class WidgetRequest {
    @NotNull(message = "X coordinate must be present.")
    private Integer x;

    @NotNull(message = "Y coordinate must be present.")
    private Integer y;

    private Integer z = null;

    @NotNull(message = "Width must be present.")
    private Double width;

    @NotNull(message = "Width must be present.")
    private Double height;
}
