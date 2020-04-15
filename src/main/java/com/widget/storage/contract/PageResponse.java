package com.widget.storage.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> records;
    private int total;

    public static PageResponse empty() {
        return new PageResponse(new ArrayList<>(), 0);
    }
}
