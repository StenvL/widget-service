package com.widget.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={
	"com.widget.service",
	"com.widget.storage"
})
public class WidgetServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(WidgetServiceApp.class, args);
	}

}