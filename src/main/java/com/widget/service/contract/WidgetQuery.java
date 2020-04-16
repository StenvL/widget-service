package com.widget.service.contract;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetQuery {
    private Double x1;
    private Double y1;
    private Double x2;
    private Double y2;

    /**
     * Checks if object is empty.
     */
    public boolean isEmpty() {
        return x1 == null && y1 == null && x2 == null && y2 == null;
    }

    /**
     * Check if object is valid.
     */
    public boolean isValid() {
        return x1 != null && y1 != null && x2 != null && y2 != null && x1 < x2 && y1 < y2;
    }
}
