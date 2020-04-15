package com.widget.storage.contract;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
public class PageRequest {
    @Min(0)
    private Integer page = 0;

    @Min(0)
    @Max(500)
    private Integer perPage = 20;
}
