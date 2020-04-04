package com.widget.service.model;

import com.widget.service.model.listener.WidgetListener;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Getter @Setter
@EntityListeners(WidgetListener.class)
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
