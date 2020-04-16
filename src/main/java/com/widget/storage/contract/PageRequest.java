package com.widget.storage.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@AllArgsConstructor
public class PageRequest {
    @Min(0)
    private Integer page;

    @Min(0)
    @Max(500)
    private Integer perPage;

    public PageRequest() {
        page = 0;
        perPage = 10;
    }
}
