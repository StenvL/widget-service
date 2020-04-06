package com.widget.service.contract;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
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
    @Min(0)
    private Double width;

    @NotNull(message = "Height must be present.")
    @Min(0)
    private Double height;
}
